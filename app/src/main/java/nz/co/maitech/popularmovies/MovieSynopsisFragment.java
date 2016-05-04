package nz.co.maitech.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieSynopsisFragment extends Fragment {

    private Movie movie;
    private String LOG_TAG = this.getClass().getSimpleName();

    public MovieSynopsisFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_synopsis, container, false);
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("movie")) {
            movie = intent.getParcelableExtra("movie");
            Log.v(LOG_TAG, movie.getTitle());
        } else {
            Log.e(LOG_TAG, "No movie synopsis received");
        }
        return rootView;
    }
}
