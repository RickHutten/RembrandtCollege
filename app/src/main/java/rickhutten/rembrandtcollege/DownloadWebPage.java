package rickhutten.rembrandtcollege;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWebPage extends AsyncTask<String, Void, String> {

    final private static String FILE_NAME = "XML";
    final private static String XML_URL = "http://www.rembrandt-college.nl/rss.php";

    Context context;
    Fragment fragment;

    public DownloadWebPage(Context context, Fragment fragment) {
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    protected String doInBackground(String... urls) {
        String msg;
        if (hasInternetConnection(context)) {
            try {
                Boolean file_downloaded = downloadFromUrl(XML_URL);
                if (file_downloaded) {
                    msg = "File downloaded";
                } else {
                    msg = "File not downloaded";
                }
            } catch (IOException e) {
                e.printStackTrace();
                msg = context.getResources().getString(R.string.unable_to_retrieve_webpage);
            }
        } else {
            msg = context.getResources().getString(R.string.no_internet_connection);
        }
        return msg;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        Log.i("doInBackGround result", result);
        if (!result.equals("File downloaded") && !result.equals("File not downloaded")) {
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, result, duration);
            toast.show();
            ListFragment list_fragment = (ListFragment) fragment;
            list_fragment.swipe_layout.setRefreshing(false);
            System.out.println("Stop refreshing anim");
            return;
        }
        if (result.equals("File downloaded")) {
            // If a new version of the XML file is downloaded, set the adapter again
            ListFragment list_fragment = (ListFragment) fragment;
            Parser parser = new Parser(context, fragment);
            list_fragment.setAdapter(parser.parseXml());
        } else {
            ListFragment list_fragment = (ListFragment) fragment;
            list_fragment.swipe_layout.setRefreshing(false);
            System.out.println("Stop refreshing anim");
        }
    }

    private boolean downloadFromUrl(String url_string) throws IOException {
        // returns true if a new XML file is downloaded, returns false if not
        InputStream is = null;
        try {
            URL url = new URL(url_string);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            // Start the connection
            conn.connect();
            long date = conn.getLastModified();

            SharedPreferences shared_preferences;
            shared_preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

            if (shared_preferences.contains("date")) {
                long cached_XML_date = shared_preferences.getLong("date", 0);
                if (date <= cached_XML_date) {
                    // Don't download the xml file again (no new version available)
                    System.out.println("No new version available");
                    return false;
                }
            }
            System.out.println("Downloading new XML file");
            // There is a new version of the xml file online
            // Or it is the first time opening the app
            shared_preferences.edit().putLong("date", date).commit();

            is = conn.getInputStream();

            File file = new File(context.getFilesDir(), FILE_NAME);
            FileOutputStream fileOutput = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int bufferLength;

            while ((bufferLength = is.read(buffer)) > 0) {
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
        return true;
    }

    private boolean hasInternetConnection(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}
