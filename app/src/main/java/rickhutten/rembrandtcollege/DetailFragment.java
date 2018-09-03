package rickhutten.rembrandtcollege;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class DetailFragment extends Fragment {

    private static final int TITLE = 0;
    private static final int GUID = 1;
    private static final int DESCRIPTION = 3;
    private static final int PUBDATE = 2;
    private static final String URL_START = "https://www.rembrandt-college.nl/_db/news/";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        Bundle bundle = this.getArguments();
        ArrayList<String> item = bundle.getStringArrayList("item");

        // Set the text for the title
        TextView title_text = view.findViewById(R.id.detail_title);
        title_text.setTypeface(null, Typeface.BOLD);
        Spanned title_clean = Html.fromHtml(item.get(TITLE));
        title_text.setText(title_clean);

        // Set the text for the date
        TextView date_text = view.findViewById(R.id.detail_date);
        String date_clean = Html.fromHtml(item.get(PUBDATE)).toString();
        String formatted_date = formatDate(date_clean);
        date_text.setText(formatted_date);

        // Set the text for the large textview
        TextView description_text = view.findViewById(R.id.detail_text);
        description_text.setMovementMethod(LinkMovementMethod.getInstance());

        // Stupid shitdicks
        String description_with_linebreaks = item.get(DESCRIPTION).replace("<p> </p>", "");

        Spanned description_clean = Html.fromHtml(description_with_linebreaks);
        description_text.setText(description_clean);

        // Set the image in the imageview
        ImageView image_view = view.findViewById(R.id.detail_image);
        String url = URL_START + item.get(GUID) + ".jpg";

        Picasso picasso = Picasso.get();
        RequestCreator request_creator = picasso.load(url);
        request_creator.fit().centerCrop()
                .error(getResources().getDrawable(R.drawable.error))
                .into(image_view);

        return view;
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
