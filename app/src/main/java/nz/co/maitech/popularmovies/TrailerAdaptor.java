package nz.co.maitech.popularmovies;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by Grant on 26/06/2016.
 */
public class TrailerAdaptor extends RealmBaseAdapter<Trailer> implements ListAdapter{

    public TrailerAdaptor(Context context, OrderedRealmCollection<Trailer> trailers) {
        super(context, trailers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.v("Hi ", "I'm at get view");
        // Trailer trailer = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);
            Log.v("Hi ", "I'm at convert view is null");
//            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_trailer, parent, false);

        }
        Trailer trailer = adapterData.get(position);
        Log.v("Trailer Adaptor ", position + " : " + trailer.getName());
        TextView trailerView = (TextView) convertView.findViewById(R.id.trailer_name_text_view);
       trailerView.setText(trailer.getName());
        return convertView;
    }

}
