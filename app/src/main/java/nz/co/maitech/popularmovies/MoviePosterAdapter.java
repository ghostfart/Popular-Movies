package nz.co.maitech.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * MoviePosterAdapter is a custom implementation of ArrayAdapter<>
 */
public class MoviePosterAdapter extends ArrayAdapter<Movie> {

    /**
     * Basic constructor, doesn't reference a layout, as creates a customer layout.
     * @param context The context where the adapter is placed.
     * @param movies The list of data submitted.
     */
    public MoviePosterAdapter(Activity context, List<Movie> movies) {
        super(context, 0, movies);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Movie movie = getItem(position);
        String posterURL =  "http://image.tmdb.org/t/p/w342/";
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_poster, parent, false);
        }
        ImageView posterView = (ImageView) convertView.findViewById(R.id.movie_poster_image_view);
        Picasso.with(getContext()).load(posterURL + movie.getPosterPath()).into(posterView);
        return convertView;
    }
}
