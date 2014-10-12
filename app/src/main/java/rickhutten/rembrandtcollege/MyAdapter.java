package rickhutten.rembrandtcollege;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private static final int TITLE = 0;
    private static final int GUID = 1;
    private static final String URL_START = "http://www.rembrandt-college.nl/_db/news/";


    Context context;
    ArrayList<ArrayList<String>> entries;

    public MyAdapter (Context context, ArrayList<ArrayList<String>> entries) {
        this.context = context;
        this.entries = entries;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convert_view, ViewGroup parent) {
        if (convert_view == null) {
            // Inflate layout
            LayoutInflater layout_inflater = ((Activity)context).getLayoutInflater();
            convert_view = layout_inflater.inflate(R.layout.list_item_layout, parent, false);
        }
        TextView text_view = (TextView) convert_view.findViewById(R.id.adapter_title);
        ImageView image_view = (ImageView) convert_view.findViewById(R.id.adapter_image);

        String url = URL_START + entries.get(position).get(GUID) + ".jpg";

        Picasso image = Picasso.with(context);
        image.setIndicatorsEnabled(true);
        image.load(url).into(image_view);

        text_view.setText(entries.get(position).get(TITLE) + " " + entries.get(position).get(GUID));

        return convert_view;
    }
}
