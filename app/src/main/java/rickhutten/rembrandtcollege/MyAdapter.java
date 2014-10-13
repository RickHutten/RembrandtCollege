package rickhutten.rembrandtcollege;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

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

        ImageView image_view = (ImageView) convert_view.findViewById(R.id.adapter_image);

        int width;
        int height = (int) context.getResources().getDimension(R.dimen.list_item_height);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        if (android.os.Build.VERSION.SDK_INT >= 13) {
            Point size = new Point();
            display.getSize(size);
            width = size.x;
        } else {
            width = display.getWidth();  // deprecated
        }

        String url = URL_START + entries.get(position).get(GUID) + ".jpg";
        Picasso picasso = Picasso.with(context);
        RequestCreator request_creator = picasso.load(url);
        request_creator.resize(width, height).centerCrop().into(image_view);

        TextView text_view = (TextView) convert_view.findViewById(R.id.adapter_title);
        text_view.setText(entries.get(position).get(TITLE));

        return convert_view;
    }
}
