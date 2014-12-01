package rickhutten.rembrandtcollege;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;

import java.net.HttpURLConnection;
import java.net.URL;

public class CheckVersion extends AsyncTask<String, Boolean, Boolean>{

    final private static String XML_URL = "http://www.rembrandt-college.nl/rss.php";
    private Context context;

    public CheckVersion(Context context) {
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... strings) {
        return isNewVersionAvailale();
    }

    // The params from onPostExecute come from the result of doInBackground
    @Override
    protected void onPostExecute(Boolean new_version) {
        SharedPreferences shared_preferences;
        shared_preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        final boolean in_foreground;
        if (shared_preferences.contains("in_foreground")) {
            in_foreground = shared_preferences.getBoolean("in_foreground", false);
        } else {
            in_foreground = false;
        }

        if (!in_foreground) {
            System.out.println("Not in foreground check if new version");

            if (new_version) {
                System.out.println("New version available, show notification");
                long when = System.currentTimeMillis();
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);

                Intent notificationIntent = new Intent(context, NewsActivity.class);
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                        notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
                        context).setSmallIcon(R.drawable.logo)
                        .setContentTitle(context.getResources().getText(R.string.notification_title))
                        .setContentText(context.getResources().getText(R.string.notification_text))
                        .setAutoCancel(true).setWhen(when)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(pendingIntent);
                notificationManager.notify(1, mNotifyBuilder.build());
            } else {
                System.out.println("No new version available, don't show notification");
            }
        } else {
            System.out.println("In foreground, don't show notification");
        }
    }

    public boolean isNewVersionAvailale() {
        // Returns true is there is a new version available
        try {
            URL url = new URL(XML_URL);
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
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occured while running newVersionAvailale");
            return false;
        }
        return true;
    }
}
