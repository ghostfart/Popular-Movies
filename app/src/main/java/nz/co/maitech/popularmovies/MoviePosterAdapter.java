package nz.co.maitech.popularmovies;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * MoviePosterAdapter is a custom implementation of ArrayAdapter<>
 */
public class MoviePosterAdapter extends ArrayAdapter<Integer> {

    /**
     * Basic constructor, doesn't reference a layout, as creates a customer layout.
     * @param context The context where the adapter is placed.
     * @param mThumbs The list of data submitted.
     */
    public MoviePosterAdapter(Activity context, List<Integer> mThumbs) {
        super(context, 0, mThumbs);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Integer thumbNailImageResource = getItem(position);
        View rootView;

        if (convertView == null) {
            rootView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_poster, parent, false);
        } else {
            rootView = convertView;
        }
        ((ImageView) rootView.findViewById(R.id.movie_poster_image_view)).setImageResource(thumbNailImageResource);

        return rootView;
    }


}
