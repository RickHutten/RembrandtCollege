package rickhutten.rembrandtcollege;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import rickhutten.rembrandtcollege.net.GETRequest;
import rickhutten.rembrandtcollege.net.ResponseListener;


public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    final private static String TAG = "ListFragment";
    final private static String FILE_NAME = "XML";
    final private static String XML_URL = "https://www.rembrandt-college.nl/rss2.php";

    ListView list_view;
    SwipeRefreshLayout swipe_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, container, false);
        list_view = view.findViewById(R.id.list);

        swipe_layout = view.findViewById(R.id.swipe_container);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setSize(SwipeRefreshLayout.DEFAULT);
        swipe_layout.setColorSchemeResources(
                R.color.blue,
                R.color.yellow,
                R.color.green,
                R.color.red);

        Bundle bundle = this.getArguments();

        if (bundle.containsKey("onCreate")) {
            Log.i(TAG, "Called from onCreate in activity");
            bundle.clear();
            Parser parser = new Parser(getActivity());
            setAdapter(parser.parseXml());
            onRefresh();
        } else if (new File(getActivity().getFilesDir(), FILE_NAME).exists()) {
            Log.i(TAG, "Not refreshing");
            Parser parser = new Parser(getActivity());
            setAdapter(parser.parseXml());
        } else {
            onRefresh();
        }
        return view;
    }

    public void setAdapter(final ArrayList<ArrayList<String>> entries) {

        ListAdapter adapter = new ListItemAdapter(getActivity(), entries);
        Log.i(TAG, "Set Adapter");
        list_view.setAdapter(adapter);

        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = entries.size() - position - 1;
                FragmentTransaction fragment_transaction = getFragmentManager().beginTransaction();
                fragment_transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                        R.anim.slide_in_left, R.anim.slide_out_right);
                DetailFragment detail_fragment = new DetailFragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("item", entries.get(position));
                detail_fragment.setArguments(bundle);
                fragment_transaction.replace(R.id.content_frame, detail_fragment);
                fragment_transaction.addToBackStack(null);
                fragment_transaction.commit();
            }
        });
        swipe_layout.setRefreshing(false);
        Log.i(TAG, "Stop refreshing anim");
    }

    @Override
    public void onRefresh() {
        // This function downloads the XML file again and sets the adapter
        // (if new version is available)
        swipe_layout.setRefreshing(true);
        Log.i(TAG, "Start refreshing anim");
        Log.i(TAG, "Refreshing..");
        final ListFragment listFragment = this;
        new GETRequest(XML_URL, new ResponseListener() {
            @Override
            public void onResponse(String response) {
                File file = new File(getContext().getFilesDir(), FILE_NAME);
                try {
                    FileOutputStream fileOutput = new FileOutputStream(file);
                    fileOutput.write(response.getBytes());
                    fileOutput.close();

                    // Parse file and set adapter again
                    // need to run on UI thread to touch the views
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Parser parser = new Parser(getContext());
                            listFragment.setAdapter(parser.parseXml());
                        }
                    });
                } catch (IOException e) {
                    // Not gonna happen but the IDE wants me to
                    e.printStackTrace();
                }

            }
        });
    }
}
