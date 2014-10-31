package rickhutten.rembrandtcollege;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
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
    private static final int DESCRIPTION = 2;
    private static final String URL_START = "http://www.rembrandt-college.nl/_db/news/";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_fragment, container, false);
        Bundle bundle = this.getArguments();
        ArrayList<String> item = bundle.getStringArrayList("item");

        TextView title_text = (TextView) view.findViewById(R.id.detail_title);
        title_text.setTypeface(null, Typeface.BOLD);
        Spanned title_clean = Html.fromHtml(item.get(TITLE));
        title_text.setText(title_clean);

        TextView description_text = (TextView) view.findViewById(R.id.detail_text);
        Spanned description_clean = Html.fromHtml(item.get(DESCRIPTION));
        description_text.setText(description_clean);

        ImageView image_view = (ImageView) view.findViewById(R.id.detail_image);

        String url = URL_START + item.get(GUID) + ".jpg";

        Picasso picasso = Picasso.with(getActivity());
        RequestCreator request_creator = picasso.load(url);
        request_creator.fit().centerCrop()
                .placeholder(getActivity().getResources().getDrawable(R.drawable.zandloper))
                .into(image_view);

        return view;
    }
}
