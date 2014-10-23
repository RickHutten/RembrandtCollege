package rickhutten.rembrandtcollege;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ListFragment extends Fragment {

    final private static String XML_URL = "http://www.rembrandt-college.nl/rss.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DownloadWebPageTask download_web_page_task = new DownloadWebPageTask(getActivity(), this);
        download_web_page_task.execute(XML_URL);
        return inflater.inflate(R.layout.list_view, container, false);
    }
}
