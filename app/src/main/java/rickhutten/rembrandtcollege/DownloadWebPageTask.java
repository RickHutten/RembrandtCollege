package rickhutten.rembrandtcollege;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.widget.ListAdapter;
import android.widget.ListView;
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

public class DownloadWebPageTask extends AsyncTask<String, Void, String> {

    final private static String FILE_NAME = "XML";

    Context context;
    ListView list_view;
    ArrayList<ArrayList<String>> entries = new ArrayList<ArrayList<String>>();
    XmlPullParser parser;
    String msg;

    public DownloadWebPageTask(Context context, ListView list_view) {
        this.context = context;
        this.list_view = list_view;
    }

    @Override
    protected String doInBackground(String... urls) {
        String msg;
        if (hasInternetConnection(context)) {
            // params comes from the execute() call: params[0] is the url.
            try {
                downloadFromUrl(urls[0]);
                msg = "No errors";
            } catch (IOException e) {
                e.printStackTrace();
                msg = context.getResources().getString(R.string.unable_to_retrieve_webpage);
            }
        } else {
            msg = context.getResources().getString(R.string.no_internet_connection);
        }
        try {
            entries = Entries();
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
            Toast toast = Toast.makeText(context, result, duration);
            toast.show();
        }
        ListAdapter my_adapter = new MyAdapter(context, entries);
        list_view.setAdapter(my_adapter);
    }

    public ArrayList<ArrayList<String>> getEntries() {
        return entries;
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

            File file = new File(context.getCacheDir(), FILE_NAME);
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
                context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    private ArrayList<ArrayList<String>> Entries() throws XmlPullParserException, IOException {

        ArrayList<ArrayList<String>> entries = new ArrayList<ArrayList<String>>();
        File file = new File(context.getCacheDir(), FILE_NAME);
        InputStream is = new FileInputStream(file);
        try {
            XmlPullParserFactory xml_pull_parser_factory = XmlPullParserFactory.newInstance();
            xml_pull_parser_factory.setValidating(false);
            xml_pull_parser_factory.setFeature(Xml.FEATURE_RELAXED, true);
            parser = xml_pull_parser_factory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(is, null);
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
                    ArrayList<String> item = getItem(parser);
                    entries.add(item);
                } else {
                    skip(parser);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            is.close();
        }
        Log.i("entries", "" + entries);
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
            } else if (name.equals("guid")) {
                guid = getGuid(parser);
                items.add(guid);
            } else if (name.equals("content:encoded")) {
                content = getContent(parser);
                items.add(content);
            } else {
                skip(parser);
            }
        }
        Log.i("items", "" + items);
        return items;
    }

    private String getTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "title");
        String title = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;
    }

    private String getGuid(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "guid");
        String guid = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "guid");
        return guid;
    }

    private String getContent(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, null, "content:encoded");
        String content = readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "content:encoded");
        return content;
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