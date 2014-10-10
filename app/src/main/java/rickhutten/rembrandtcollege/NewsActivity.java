package rickhutten.rembrandtcollege;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class NewsActivity extends ActionBarActivity {

    final private static String XML_URL = "http://www.rembrandt-college.nl/rss.php";
    final private static String FILE_NAME = "XML";
    final private static String REMBRANDT_URL = "http://www.rembrandt-college.nl";
    final private static String FACEBOOK_URL = "https://www.facebook.com/RembrandtCollege";
    final private static String TWITTER_URL = "https://twitter.com/Rembrandt_Coll";

    ArrayList<ArrayList<String>> entries = new ArrayList<ArrayList<String>>();
    XmlPullParser parser;
    protected DrawerLayout drawer_layout;
    private ActionBarDrawerToggle drawer_toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_toggle = new ActionBarDrawerToggle(this, drawer_layout, R.drawable.ic_drawer_dark,
                0, 0) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        // Set the drawer toggle as the DrawerListener
        drawer_layout.setDrawerListener(drawer_toggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        RelativeLayout rembrandt_knop = (RelativeLayout) findViewById(R.id.rembrandt_knop);
        rembrandt_knop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInternetActivity(REMBRANDT_URL);
            }
        });

        RelativeLayout facebook_knop = (RelativeLayout) findViewById(R.id.facebook_knop);
        facebook_knop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInternetActivity(FACEBOOK_URL);
            }
        });

        RelativeLayout twitter_knop = (RelativeLayout) findViewById(R.id.twitter_knop);
        twitter_knop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInternetActivity(TWITTER_URL);
            }
        });

        new DownloadWebPageTask().execute(XML_URL);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawer_toggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawer_toggle.onConfigurationChanged(newConfig);
    }

    public void startInternetActivity(String url) {
        drawer_layout.closeDrawers();
        Intent internet_intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        NewsActivity.this.startActivity(internet_intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    private class DownloadWebPageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            String msg;
            if (hasInternetConnection(getApplicationContext())) {
                // params comes from the execute() call: params[0] is the url.
                try {
                    downloadFromUrl(urls[0]);
                    msg = "No errors";
                } catch (IOException e) {
                    e.printStackTrace();
                    msg = getResources().getString(R.string.unable_to_retrieve_webpage);
                }
            } else {
                msg = getResources().getString(R.string.no_internet_connection);
            }
            try {
                entries = getEntries();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return msg;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.i("Internet status", result);
            if (!result.equals("No errors")) {
                int duration = Toast.LENGTH_LONG;
                Toast toast = Toast.makeText(getApplicationContext(), result, duration);
                toast.show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (drawer_toggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private void downloadFromUrl(String url_string) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(url_string);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            String encoding = conn.getContentEncoding();
            Log.i("Encoding:", "" + encoding);
            int response = conn.getResponseCode();
            Log.i("Connection", "The response is: " + response);
            is = conn.getInputStream();

            File file = new File(getCacheDir(), FILE_NAME);
            FileOutputStream fileOutput = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int bufferLength;

            while ( (bufferLength = is.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
            }

            fileOutput.close();

        } finally {
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
            if (is != null) {
                is.close();

            }
        }
    }

    private boolean hasInternetConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private ArrayList<ArrayList<String>> getEntries() throws XmlPullParserException, IOException {

        ArrayList<ArrayList<String>> entries = new ArrayList<ArrayList<String>>();
        File file = new File(getCacheDir(), FILE_NAME);
        InputStream is = new FileInputStream(file);
        try {
            XmlPullParserFactory xml_pull_parser_factory = XmlPullParserFactory.newInstance();
            parser = xml_pull_parser_factory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, false);
            parser.setInput(is, null);
            parser.defineEntityReplacementText("&eacute", "é");
            parser.nextTag();
            parser.nextTag();

            parser.require(XmlPullParser.START_TAG, null, "channel");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                // Starts by looking for the title tag
                if (name.equals("item")) {
                    ArrayList<String> a = getItem(parser);
                    entries.add(a);
                } else {
                    skip(parser);
                }
            }
        } finally {
            is.close();
        }

        return entries;
    }

    private ArrayList<String> getItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<String> items = new ArrayList<String>();
        parser.require(XmlPullParser.START_TAG, null, "item");
        String title;
        String link = null;
        String pub_date = null;
        String guid = null;
        String content = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = getTitle(parser);
                items.add(title);
            } else {
                skip(parser);
            }
        }

        return items;
    }

    private String getTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }
    // Copied from "http://developer.android.com/training/basics/network-ops/xml.html"
    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    // Copied from "http://developer.android.com/training/basics/network-ops/xml.html"
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.nextToken()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}


