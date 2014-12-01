package rickhutten.rembrandtcollege;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null) {
            System.out.println("Set notification");
            CheckVersion check_version = new CheckVersion(context);
            check_version.execute();
        } else if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            System.out.println("Setting alarm on boot");
            NewsActivity news_activity = new NewsActivity();
            news_activity.setAlarm(context);
        }
    }
}
