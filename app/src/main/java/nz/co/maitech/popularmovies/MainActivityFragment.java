package nz.co.maitech.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private MoviePosterAdapter moviePosterAdapter;
    private ArrayList<Movie> movieList = new ArrayList<>();
    private String SAVE_STATE_MOVIE_LIST_KEY = "movies";
    private String SAVE_STATE_SEARCH_TERM_KEY = "search";
    private final String LOG_TAG = this.getClass().getSimpleName();
    private String currentSearchTerm;

    private Realm realm;


    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }


    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String prefSearchTerm = prefs.getString(getString(R.string.search_terms_key), getString(R.string.search_terms_default));
        if (currentSearchTerm == null || !currentSearchTerm.equals(prefSearchTerm)) {
            currentSearchTerm = prefSearchTerm;
            retrieveMovies();
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_STATE_SEARCH_TERM_KEY)) {
            currentSearchTerm = new String(savedInstanceState.getCharArray(SAVE_STATE_SEARCH_TERM_KEY));
        }

        // Create a new empty instance of Realm.
        realm = Realm.getDefaultInstance();

        // Query the database for movies in storage.
        RealmResults<Movie> results = realm.where(Movie.class).findAll();

        // If there is the correct number of movies, and they are recently added, add them to movieList.
        if (results.size() == 20 && (System.currentTimeMillis() - results.first().getTimeStamp()) < 1200000) {
            for (Movie movie : results) {
                movieList.add(movie);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        moviePosterAdapter = new MoviePosterAdapter(getActivity(), movieList);
        GridView moviePosterView = (GridView) rootView.findViewById(R.id.movie_poster_gridview);
        moviePosterView.setAdapter(moviePosterAdapter);
        if (movieList.size() < 20) {
            retrieveMovies();;
        }
        moviePosterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), MovieSynopsis.class);
                intent.putExtra("movie", moviePosterAdapter.getItem(position).getTitle());
                startActivity(intent);
            }
        });
        return rootView;
    }

    private void retrieveMovies() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String searchTerm = prefs.getString(getString(R.string.search_terms_key), getString(R.string.search_terms_default));
        FetchMoviesTask fetchMovies = new FetchMoviesTask();
        fetchMovies.execute(searchTerm);
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
        private Uri.Builder uriBuilder = new Uri.Builder();
        final String POPULAR_SEARCH_URL = "https://api.themoviedb.org/3/movie/popular?";
        final String TOP_RATED_SEARCH_URL = "https://api.themoviedb.org/3/movie/top_rated?";
        final String APPID_QUERY = "api_key";  // This is the query term for the http GET request. Key is a string stored in secure_keys.xml

        @Override
        protected Movie[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String movieReviewsJsonStr = null;

            try {
                // Build the URL
                uriBuilder = Uri.parse(params[0]).buildUpon();
                uriBuilder.appendQueryParameter(APPID_QUERY, getString(R.string.appid_key)); // Key appid_key is a string stored in secure_keys.xml
                URL url = new URL(uriBuilder.toString());

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the return data.
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null; // Nothing received, stop here
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    line += "\n";
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    return null; // Stream was empty, just stop here.
                }

                movieReviewsJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error Closing Stream, e");
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieReviewsJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        private Movie[] getMovieDataFromJson(String movieReviewsJsonStr) throws JSONException {
            final String MAPI_OVERVIEW = "overview";
            final String MAPI_TITLE = "original_title";
            final String MAPI_RELEASE_DATE = "release_date";
            final String MAPI_RATING = "vote_average";
            final String MAPI_POSTER = "poster_path";
            final String MAPI_MOVIES = "results";
            final String MAPI_ID = "id";

            JSONObject movieResult = new JSONObject(movieReviewsJsonStr);
            JSONArray movieJsonArray = movieResult.getJSONArray(MAPI_MOVIES);

            Movie[] movieArray = new Movie[movieJsonArray.length()];
            long timeStamp = System.currentTimeMillis();
            for (int i = 0; i < movieJsonArray.length(); i++) {
                JSONObject jsonMovie = movieJsonArray.getJSONObject(i);
                Movie movie = new Movie();
                movie.setId(jsonMovie.getString(MAPI_ID));
                movie.setTitle(jsonMovie.getString(MAPI_TITLE));
                movie.setOverview(jsonMovie.getString(MAPI_OVERVIEW));
                movie.setPosterPath(jsonMovie.getString(MAPI_POSTER));
                movie.setRating(jsonMovie.getString(MAPI_RATING));
                movie.setReleaseDate(jsonMovie.getString(MAPI_RELEASE_DATE));
                movie.setTimeStamp(timeStamp);
                movieArray[i] = movie;
            }
            return movieArray;
        }

        @Override
        protected void onPostExecute(Movie[] result) {
            if (result != null) {
                deleteCurrentMoviesFromRealm();
                moviePosterAdapter.clear();
                for (Movie movie : result) {
                    moviePosterAdapter.add(movie);
                }
                saveMoviesToRealm(result);
            }
        }

        private void saveMoviesToRealm(Movie[] result) {

            realm.beginTransaction();
            realm.copyToRealm(new ArrayList<Movie>(Arrays.asList(result)));
            realm.commitTransaction();
        }

        private void deleteCurrentMoviesFromRealm() {
            RealmResults<Movie> results = realm.where(Movie.class).findAll();
            realm.beginTransaction();
            results.deleteAllFromRealm();
            realm.commitTransaction();
        }
    }

}
