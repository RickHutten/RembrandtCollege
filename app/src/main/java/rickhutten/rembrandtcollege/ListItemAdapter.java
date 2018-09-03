package rickhutten.rembrandtcollege;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Transformation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

class ListItemAdapter extends BaseAdapter {

    private static final int TITLE = 0;
    private static final int GUID = 1;
    private static final int PUBDATE = 2;
    private static final String URL_START = "https://www.rembrandt-college.nl/_db/news/";

    private Context context;
    private ArrayList<ArrayList<String>> entries;
    private int entries_size;

    ListItemAdapter(Context context, ArrayList<ArrayList<String>> entries) {
        this.context = context;
        this.entries = entries;
        entries_size = entries.size();
    }

    @Override
    public int getCount() {
        return entries_size;
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
        position = entries_size - position - 1;
        if (convert_view == null) {
            // Inflate layout
            LayoutInflater layout_inflater = ((Activity) context).getLayoutInflater();
            convert_view = layout_inflater.inflate(R.layout.list_item_layout, parent, false);
        }
        ImageView image_view = convert_view.findViewById(R.id.adapter_image);

        String url = URL_START + entries.get(position).get(GUID) + ".jpg";

        Picasso picasso = Picasso.get();
        RequestCreator request_creator = picasso.load(url);
        request_creator.fit().centerCrop()
                .error(context.getResources().getDrawable(R.drawable.error))
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        int radius = Math.round(6 * (context.getResources().getDisplayMetrics().xdpi / DisplayMetrics.DENSITY_DEFAULT));
                        int margin = 0;
                        final Paint paint = new Paint();
                        paint.setAntiAlias(true);
                        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));

                        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(output);
                        RectF rect = new RectF(margin, margin, source.getWidth() - margin, source.getHeight() - margin);
                        canvas.drawRoundRect(rect, radius, radius, paint);
                        canvas.drawRect(rect.left, rect.bottom - radius, rect.right, rect.bottom, paint);
                        if (source != output) {
                            source.recycle();
                        }
                        return output;
                    }

                    @Override
                    public String key() {
                        return "rounded";
                    }
                })
                .into(image_view);

        TextView text_date = convert_view.findViewById(R.id.adapter_date);
        String date = entries.get(position).get(PUBDATE);
        text_date.setText(formatDate(date));

        TextView text_view = convert_view.findViewById(R.id.adapter_title);
        String title_with_html_entities = entries.get(position).get(TITLE);
        Spanned title = Html.fromHtml(title_with_html_entities);
        text_view.setText(title);

        return convert_view;
    }

    private String formatDate(String date) {
        SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.US);
        Date dateStr;
        try {
            dateStr = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        SimpleDateFormat out = new SimpleDateFormat("EEEE d MMMM yyyy 'om' h:mm", Locale.getDefault());
        return out.format(dateStr);
    }
}
