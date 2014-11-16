package rickhutten.rembrandtcollege;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        System.out.println("Set notification");
        CheckVersion check_version = new CheckVersion(context);
        check_version.execute();
    }
}
