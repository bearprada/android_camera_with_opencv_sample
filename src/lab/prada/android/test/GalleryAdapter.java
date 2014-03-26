package lab.prada.android.test;

import java.io.File;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.BitmapAjaxCallback;

public class GalleryAdapter extends ArrayAdapter<File> {

    public GalleryAdapter(Context context) {
        super(context, 0, 0);
    }

    @Override
    public View getView(int position, View convertView , ViewGroup parent) {
        File file = getItem(position);
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = new ImageView(getContext());
        }
        AQuery aq = new AQuery(view);
        aq.image(new BitmapAjaxCallback().file(file));
        return view;
    }
}
