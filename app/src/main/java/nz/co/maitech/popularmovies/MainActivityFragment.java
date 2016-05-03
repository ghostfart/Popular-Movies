package nz.co.maitech.popularmovies;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private Movie[] movies = {
            new Movie(R.drawable.sample_2),
            new Movie(R.drawable.sample_3),
            new Movie(R.drawable.sample_4),
            new Movie(R.drawable.sample_5),
            new Movie(R.drawable.sample_6),
            new Movie(R.drawable.sample_7),
            new Movie(R.drawable.sample_0),
            new Movie(R.drawable.sample_1),
            new Movie(R.drawable.sample_2),
            new Movie(R.drawable.sample_3),
            new Movie(R.drawable.sample_4),
            new Movie(R.drawable.sample_5),
            new Movie(R.drawable.sample_6),
            new Movie(R.drawable.sample_7),
            new Movie(R.drawable.sample_0),
            new Movie(R.drawable.sample_1),
            new Movie(R.drawable.sample_2),
            new Movie(R.drawable.sample_3),
            new Movie(R.drawable.sample_4),
            new Movie(R.drawable.sample_5),
            new Movie(R.drawable.sample_6),
            new Movie(R.drawable.sample_7)
    };

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        List<Movie> thumbNailImages = new ArrayList<>(Arrays.asList(movies));

        GridView moviePosterView = (GridView) rootView.findViewById(R.id.movie_poster_gridview);
        moviePosterView.setAdapter(new MoviePosterAdapter(getActivity(), thumbNailImages));

        moviePosterView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                retrieveMovies();
                Toast.makeText(getContext(), "" + position, Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    private void retrieveMovies() {
        FetchMoviesTask fetchMovies = new FetchMoviesTask();
        fetchMovies.execute();
    }

}
