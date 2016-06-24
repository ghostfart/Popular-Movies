package nz.co.maitech.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import io.realm.Realm;
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

//            movie = intent.getParcelableExtra("movie");
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
        } else {
            Log.e(LOG_TAG, "No movie synopsis received");
        }
        return rootView;
    }
}
