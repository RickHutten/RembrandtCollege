package rickhutten.rembrandtcollege;

import android.graphics.Typeface;
import android.os.Bundle;
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

import java.util.ArrayList;


public class DetailFragment extends Fragment {

    private static final int TITLE = 0;
    private static final int GUID = 1;
    private static final int DESCRIPTION = 3;
    private static final int PUBDATE = 2;
    private static final String URL_START = "https://www.rembrandt-college.nl/_db/news/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        Bundle bundle = this.getArguments();
        ArrayList<String> item = bundle.getStringArrayList("item");

        // Set the text for the title
        TextView title_text = (TextView) view.findViewById(R.id.detail_title);
        title_text.setTypeface(null, Typeface.BOLD);
        Spanned title_clean = Html.fromHtml(item.get(TITLE));
        title_text.setText(title_clean);

        // Set the text for the date
        TextView date_text = (TextView) view.findViewById(R.id.detail_date);
        String date_clean = "" + Html.fromHtml(item.get(PUBDATE));
        String formatted_date = formatDate(date_clean);
        date_text.setText(formatted_date);

        // Set the text for the large textview
        TextView description_text = (TextView) view.findViewById(R.id.detail_text);
        description_text.setMovementMethod(LinkMovementMethod.getInstance());

        // Stupid shitdicks
        String description_with_linebreaks = item.get(DESCRIPTION).replace("<p> </p>", "");

        Spanned description_clean = Html.fromHtml(description_with_linebreaks);
        description_text.setText(description_clean);

        // Set the image in the imageview
        ImageView image_view = (ImageView) view.findViewById(R.id.detail_image);
        String url = URL_START + item.get(GUID) + ".jpg";

        Picasso picasso = Picasso.with(getActivity());
        RequestCreator request_creator = picasso.load(url);
        request_creator.fit().centerCrop()
                .placeholder(getActivity().getResources().getDrawable(R.drawable.zandloper))
                .into(image_view);

        return view;
    }

    private String formatDate(String date) {
        String[] date_array = date.split(" ");
        String day = date_array[0];
        String day_no = date_array[1];
        String month = date_array[2];
        String year = date_array[3];

        month = month.toLowerCase();

        // Translating
        if (month.equals("mar"))  {month = "mrt";}
        if (month.equals("may"))  {month = "mei";}
        if (month.equals("june")) {month = "jun";}
        if (month.equals("july")) {month = "jul";}
        if (month.equals("sept")) {month = "sep";}
        if (month.equals("oct"))  {month = "okt";}

        if (day.equals("Mon,"))   {day = "Ma,";}
        if (day.equals("Tue,"))   {day = "Di,";}
        if (day.equals("Wed,"))   {day = "Wo,";}
        if (day.equals("Thu,"))   {day = "Do,";}
        if (day.equals("Fri,"))   {day = "Vr,";}
        if (day.equals("Sat,"))   {day = "Za,";}
        if (day.equals("Sun,"))   {day = "Zo,";}

        if (day_no.startsWith("0")) {day_no = day_no.replace("0", "");}
        return day + " " + day_no + " " + month + ". " + year;
    }
}
