package rickhutten.rembrandtcollege;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;


public class ListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    final private static String FILE_NAME = "XML";

    ListView list_view;
    SwipeRefreshLayout swipe_layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.list_view, container, false);
        list_view = (ListView) view.findViewById(R.id.list);

        swipe_layout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipe_layout.setOnRefreshListener(this);
        swipe_layout.setSize(SwipeRefreshLayout.DEFAULT);
        swipe_layout.setColorSchemeResources(
                R.color.refresh_1,
                R.color.refresh_2,
                R.color.refresh_3,
                R.color.refresh_4);

        Bundle bundle = this.getArguments();

        if (bundle.containsKey("onCreate")) {
            System.out.println("Called from onCreate in activity");
            bundle.clear();
            Parser parser = new Parser(getActivity(), this);
            setAdapter(parser.parseXml());
            refresh();
        } else if (new File(getActivity().getFilesDir(), FILE_NAME).exists() ) {
            System.out.println("Not refreshing");
            Parser parser = new Parser(getActivity(), this);
            setAdapter(parser.parseXml());
        } else {
            refresh();
        }
        return view;
    }

    public void refresh() {
        // This function downloads the XML file again and sets the adapter
        // (if new version is available)
        swipe_layout.setRefreshing(true);
        System.out.println("Start refreshing anim");
        System.out.println("Refreshing..");
        DownloadWebPage download_web_page = new DownloadWebPage(getActivity(), this);
        download_web_page.execute();
    }

    public void setAdapter(final ArrayList<ArrayList<String>> entries) {

        ListAdapter adapter = new ListItemAdapter(getActivity(), entries);
        System.out.println("Set Adapter");
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
        System.out.println("Stop refreshing anim");
    }

    @Override
    public void onRefresh() {
        refresh();
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh();
    }
}
