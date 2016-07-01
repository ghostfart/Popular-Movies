package nz.co.maitech.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.util.ArrayList;

import io.realm.Realm;
import nz.co.maitech.popularmovies.data.Movie;
import nz.co.maitech.popularmovies.data.MovieDB;
import nz.co.maitech.popularmovies.data.Review;
import nz.co.maitech.popularmovies.data.Trailer;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieSynopsisFragment extends Fragment {


    private String LOG_TAG = this.getClass().getSimpleName();
    private final int SYNOPSIS_CHILD_COUNT = 4;

    private Movie movie;
    private String posterURL = "http://image.tmdb.org/t/p/w185";
    private Realm realm;
    private LinearLayout mSynopsisLayout;
    private Context mContext;

    public MovieSynopsisFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        realm = Realm.getDefaultInstance();
        if (movie != null && movie.trailers.size() < 1) {
            retrieveTrailersAndReviews();
        }
        if (movie != null && movie.trailers != null && mSynopsisLayout.getChildCount() <=  SYNOPSIS_CHILD_COUNT) {
            for (Trailer trailer : movie.trailers) {
                addTrailerToLayout(trailer);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public void onStop() {
        super.onStop();
        realm.close();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_synopsis, container, false);
        Intent intent = getActivity().getIntent();
        Bundle args = getArguments();
        mSynopsisLayout = (LinearLayout) rootView.findViewById(R.id.synopsis_linear_layout);
        mContext = getContext();

        Button favButton = (Button) rootView.findViewById(R.id.favorite_button);
        favButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (movie != null) {
                    MovieDB movieDB = new MovieDB(mContext);
                    movieDB.saveMovieToFavorite(movie);
                }
            }
        });

        MovieDB movieDB = new MovieDB(mContext);
        if (intent != null && intent.hasExtra(Movie.MOVIE_ID)) {
            movie = movieDB.getMovie(intent.getStringExtra(Movie.MOVIE_ID));
            setSynopsis(rootView);

        } else if (args != null && args.getString(Movie.MOVIE_ID) != null){
            movie = movieDB.getMovie(args.getString(Movie.MOVIE_ID));
            setSynopsis(rootView);
        } else {
            movie = movieDB.getDefaultMovie();
            return rootView;
        }
        return rootView;
    }

    private void setSynopsis(View rootView) {
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
    }

    private void retrieveTrailersAndReviews() {
        FetchTrailersAndReviewsTask fetchTrailersAndReviews = new FetchTrailersAndReviewsTask();
        fetchTrailersAndReviews.execute(movie.getId());
    }

    private void displayTrailers() {
        MovieDB movieDB = new MovieDB(mContext);
        ArrayList<Trailer> trailers = movieDB.getTrailers(movie.getId());
        for (Trailer trailer : trailers) {
            addTrailerToLayout(trailer);
        }
    }

    private void addTrailerToLayout(final Trailer trailer) {
        LinearLayout trailerLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item_trailer, mSynopsisLayout, false);
        TextView trailerTextView = (TextView) trailerLayout.getChildAt(1);
        trailerTextView.setText(trailer.getName());
        mSynopsisLayout.addView(trailerLayout);
        trailerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewTrailerIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + trailer.getKey()));
                startActivity(viewTrailerIntent);
            }
        });
    }

    private void displayReviews() {
        MovieDB movieDB = new MovieDB(mContext);
        ArrayList<Review> reviews = movieDB.getReviews(movie.getId());
        for (Review review : reviews) {
            addReviewToLayout(review);
        }
    }

    private void addReviewToLayout(final Review review) {
        LinearLayout reviewLayout = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.list_item_review, mSynopsisLayout, false);
        TextView reviewTextView = (TextView) reviewLayout.getChildAt(0);
        reviewTextView.setText(review.getContent());
        TextView authorTextView = (TextView) reviewLayout.getChildAt(1);
        authorTextView.setText(review.getAuthor());
        mSynopsisLayout.addView(reviewLayout);
    }


    public class FetchTrailersAndReviewsTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchTrailersAndReviewsTask.class.getSimpleName();
        private Uri.Builder uriBuilder = new Uri.Builder();
        final String MAPI_BASE_SEARCH_URL = "https://api.themoviedb.org/3/movie";
        final String TRAILER_SEARCH_URL = "videos";
        final String REVIEWS_SEARCH_URL = "reviews";
        final String APPID_QUERY = "api_key";
        ArrayList<Trailer> trailers;
        ArrayList<Review> reviews;

        @Override
        protected Void doInBackground(String... params) {
            String trailerJsonStr = mApiRequest(params[0], TRAILER_SEARCH_URL);
            String reviewsJsonStr = mApiRequest(params[0], REVIEWS_SEARCH_URL);
            getMovieDataFromJson(trailerJsonStr, reviewsJsonStr, params[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            MovieDB movieDB = new MovieDB(mContext);
            movieDB.saveTrailers(trailers, movie);
            movieDB.saveReviews(reviews, movie);
            displayTrailers();
            displayReviews();
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

                trailers = new ArrayList<>();
                for (int i = 0; i < jsonTrailerArray.length(); i++) {
                    Trailer trailer = new Trailer();
                    JSONObject jsonTrailer = jsonTrailerArray.getJSONObject(i);
                    trailer.setKey(jsonTrailer.getString(MAPI_TRAILER_KEY));
                    trailer.setName(jsonTrailer.getString(MAPI_TRAILER_NAME));
                    trailer.setType(jsonTrailer.getString(MAPI_VIDEO_TYPE));
                    trailer.setSite(jsonTrailer.getString(MAPI_TRAILER_SITE));
                    trailers.add(trailer);
                }

                reviews = new ArrayList<>();
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
