package nz.co.maitech.popularmovies;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieSynopsisFragment extends Fragment {

    private Movie movie;
    private String LOG_TAG = this.getClass().getSimpleName();
    private String posterURL = "http://image.tmdb.org/t/p/w185";
    private Realm realm;

    public MovieSynopsisFragment() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_synopsis, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("movie")) {
            realm = Realm.getDefaultInstance();
            RealmQuery<Movie> query = realm.where(Movie.class).contains("title", intent.getStringExtra("movie"));
            movie = query.findFirst();
            TextView textView = (TextView) rootView.findViewById(R.id.synopsis_title_text_view);
            textView.setText(movie.getTitle());
            textView = (TextView) rootView.findViewById(R.id.synopsis_year_text_view);
            textView.setText(movie.getYear());
            textView = (TextView) rootView.findViewById(R.id.synopsis_rating_text_view);
            textView.setText(movie.getRating());
            textView = (TextView) rootView.findViewById(R.id.synopsis_overview_text_view);
            textView.setText(movie.getOverview());
            ImageView imageView = (ImageView) rootView.findViewById(R.id.synopsis_poster_image_view);
            Picasso.with(getContext()).load(posterURL + movie.getPosterPath()).into(imageView);
            retrieveTrailersAndReviews();
        } else {
            Log.e(LOG_TAG, "No movie synopsis received");
        }
        return rootView;
    }

    private void retrieveTrailersAndReviews() {
        FetchTrailersAndReviewsTask fetchTrailersAndReviews = new FetchTrailersAndReviewsTask();
        fetchTrailersAndReviews.execute(movie.getId());
    }

    private void addTrailers(RealmList<Trailer> trailers) {
        realm.beginTransaction();
        for (Trailer trailer : trailers) {
            movie.trailers.add(trailer);
            Log.v(LOG_TAG, "Review by " + trailer.getName());
        }
        realm.copyToRealmOrUpdate(movie);
        realm.commitTransaction();
    }

    private void addReviews(RealmList<Review> reviews) {
        realm.beginTransaction();
        for (Review review : reviews) {
            movie.reviews.add(review);
            Log.v(LOG_TAG, "Review by " + review.getAuthor());
        }
        realm.copyToRealmOrUpdate(movie);
        realm.commitTransaction();

    }

    public class FetchTrailersAndReviewsTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchTrailersAndReviewsTask.class.getSimpleName();
        private Uri.Builder uriBuilder = new Uri.Builder();
        final String MAPI_BASE_SEARCH_URL = "https://api.themoviedb.org/3/movie";
        final String TRAILER_SEARCH_URL = "videos";
        final String REVIEWS_SEARCH_URL = "reviews";
        final String APPID_QUERY = "api_key";
        RealmList<Trailer> trailers;
        RealmList<Review> reviews;

        @Override
        protected Void doInBackground(String... params) {
            String trailerJsonStr = mApiRequest(params[0], TRAILER_SEARCH_URL);
            String reviewsJsonStr = mApiRequest(params[0], REVIEWS_SEARCH_URL);
            getMovieDataFromJson(trailerJsonStr, reviewsJsonStr, params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            addTrailers(trailers);
            addReviews(reviews);
        }

        private Void getMovieDataFromJson(String trailerJsonStr, String reviewsJsonStr, String movieId) {
            final String MAPI_TRAILER_KEY = "key";
            final String MAPI_TRAILER_NAME = "name";
            final String MAPI_VIDEO_TYPE = "type";
            final String MAPI_TRAILER_SITE = "site";
            final String MAPI_REVIEW_AUTHOR = "author";
            final String MAPI_REVIEW_CONTENT = "content";
            final String MAPI_TRAILERS = "results";
            final String MAPI_REVIEWS = "results";


            try {
                JSONArray jsonTrailerArray = new JSONObject(trailerJsonStr).getJSONArray(MAPI_TRAILERS);
                JSONArray jsonReviewsArray = new JSONObject(reviewsJsonStr).getJSONArray(MAPI_REVIEWS);

                trailers = new RealmList<>();
                for (int i = 0; i < jsonTrailerArray.length(); i++) {
                    Trailer trailer = new Trailer();
                    JSONObject jsonTrailer = jsonTrailerArray.getJSONObject(i);
                    trailer.setKey(jsonTrailer.getString(MAPI_TRAILER_KEY));
                    trailer.setName(jsonTrailer.getString(MAPI_TRAILER_NAME));
                    trailer.setType(jsonTrailer.getString(MAPI_VIDEO_TYPE));
                    trailer.setSite(jsonTrailer.getString(MAPI_TRAILER_SITE));
                    trailers.add(trailer);
                }

                reviews = new RealmList<>();
                for (int i = 0; i < jsonReviewsArray.length(); i++) {
                    Review review = new Review();
                    JSONObject jsonReview = jsonReviewsArray.getJSONObject(i);
                    review.setAuthor(jsonReview.getString(MAPI_REVIEW_AUTHOR));
                    review.setContent(jsonReview.getString(MAPI_REVIEW_CONTENT));
                    reviews.add(review);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
                return null;
            }
            return null;
        }

        private String mApiRequest(String movieId, String mApiSearchType) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String jsonStr = null;

            try {
                // Build the URL
                uriBuilder = Uri.parse(MAPI_BASE_SEARCH_URL).buildUpon();
                uriBuilder.appendPath(movieId);
                uriBuilder.appendPath(mApiSearchType);
                uriBuilder.appendQueryParameter(APPID_QUERY, getString(R.string.appid_key)); // Key appid_key is a string stored in secure_keys.xml
                URL url = new URL(uriBuilder.toString());
                Log.v(LOG_TAG, "Attempting URL : " + url.toString());

                // Create the request and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the return data.
                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    line += "\n";
                    buffer.append(line);
                }

                if (buffer.length() == 0) {
                    return null;
                }

                jsonStr = buffer.toString();
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
            return jsonStr;
        }
    }

}
