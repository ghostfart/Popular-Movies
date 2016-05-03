package nz.co.maitech.popularmovies;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Grant on 3/05/2016.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();
    private Uri.Builder uriBuilder = new Uri.Builder();
    final String POPULAR_SEARCH_URL = "https://api.themoviedb.org/3/movie/popular?";
    final String TOP_RATED_SEARCH_URL = "https://api.themoviedb.org/3/movie/top_rated?";
    final String APPID_QUERY = "api_key";
    final String APPID_KEY = "7fed2f4065af40fb68914e264e1a07c7";
    // https://api.themoviedb.org/3/movie/popular?api_key=7fed2f4065af40fb68914e264e1a07c7

    @Override
    protected Movie[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieReviewsJsonStr = null;

        try {
            // Build the URL
            uriBuilder = Uri.parse(POPULAR_SEARCH_URL).buildUpon();
            uriBuilder.appendQueryParameter(APPID_QUERY, APPID_KEY);
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

        JSONObject movieResult = new JSONObject(movieReviewsJsonStr);
        JSONArray movieJsonArray = movieResult.getJSONArray(MAPI_MOVIES);

        Movie[] movieArray = new Movie[movieJsonArray.length()];
        for (int i = 0; i < movieJsonArray.length(); i++) {
            JSONObject jsonMovie = movieJsonArray.getJSONObject(i);

            Movie movie = new Movie();
            movie.setTitle(jsonMovie.getString(MAPI_TITLE));
            movie.setOverview(jsonMovie.getString(MAPI_OVERVIEW));
            movie.setPosterPath(jsonMovie.getString(MAPI_POSTER));
            movie.setRating(jsonMovie.getString(MAPI_RATING));
            movie.setReleaseDate(jsonMovie.getString(MAPI_RELEASE_DATE));

            movieArray[i] = movie;

            Log.v(LOG_TAG, movie.toString());
        }

        return movieArray;
    }


}