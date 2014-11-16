package rickhutten.rembrandtcollege;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.Calendar;


public class NewsActivity extends ActionBarActivity {

    final private static String REMBRANDT_URL = "http://www.rembrandt-college.nl";
    final private static String FACEBOOK_APP_URL = "fb://profile/593801583967937";
    final private static String FACEBOOK_URL = "https://www.facebook.com/RembrandtCollege";
    final private static String TWITTER_URL = "https://twitter.com/Rembrandt_Coll";
    final private static String MAGISTER_URL = "https://rembrandt.swp.nl/5.6.25/Magister.aspx";
    final private static String ITSLEARNING_URL = "http://itslearning.mobi/logOn/logOn.aspx?ReturnUrl=%2f";

    protected DrawerLayout drawer_layout;
    private ActionBarDrawerToggle drawer_toggle;
    private float down_point_x;
    private float down_point_y;
    public SharedPreferences shared_preferences;

    Handler handler = new Handler();
    Runnable runnable;

    View.OnTouchListener touch_listener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(final View view, MotionEvent arg1) {
            switch (arg1.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    down_point_x = arg1.getX();
                    down_point_y = arg1.getY();
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            view.setBackgroundResource(R.color.background_blue);
                        }
                    };
                    handler.postDelayed(runnable, 50);
                    break;
                }
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:{
                    handler.removeCallbacks(runnable);
                    float up_point_x = arg1.getX();
                    float up_point_y = arg1.getY();
                    view.setBackgroundResource(R.color.white);
                    // The difference between up and down can be 20 pixels max
                    final int TAP_SIZE = (int) getResources().getDimension(R.dimen.tap_size);
                    if (TAP_SIZE > Math.abs(down_point_x-up_point_x) &&
                            TAP_SIZE > Math.abs(down_point_y-up_point_y)) {
                        if (view.getTag().equals(FACEBOOK_APP_URL)) {
                            try{
                                startInternetActivity((String) view.getTag());
                            } catch (Exception e){
                                // Facebook app is not found
                                startInternetActivity(FACEBOOK_URL);
                            }
                        } else {
                            startInternetActivity((String) view.getTag());
                        }
                    }
                    break;
                }
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_news);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer_toggle = new ActionBarDrawerToggle(this, drawer_layout, 0, 0) {

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
        rembrandt_knop.setTag(REMBRANDT_URL);
        rembrandt_knop.setOnTouchListener(touch_listener);

        RelativeLayout facebook_knop = (RelativeLayout) findViewById(R.id.facebook_knop);
        facebook_knop.setTag(FACEBOOK_APP_URL);
        facebook_knop.setOnTouchListener(touch_listener);

        RelativeLayout twitter_knop = (RelativeLayout) findViewById(R.id.twitter_knop);
        twitter_knop.setTag(TWITTER_URL);
        twitter_knop.setOnTouchListener(touch_listener);

        RelativeLayout magister_knop = (RelativeLayout) findViewById(R.id.magister_knop);
        magister_knop.setTag(MAGISTER_URL);
        magister_knop.setOnTouchListener(touch_listener);

        RelativeLayout itslearning_knop = (RelativeLayout) findViewById(R.id.itslearning_knop);
        itslearning_knop.setTag(ITSLEARNING_URL);
        itslearning_knop.setOnTouchListener(touch_listener);

        if (getSupportFragmentManager().getFragments() == null) {
            FragmentTransaction fragment_transaction = getSupportFragmentManager().beginTransaction();
            ListFragment list_fragment = new ListFragment();
            Bundle bundle = new Bundle();
            bundle.putString("onCreate", null);
            list_fragment.setArguments(bundle);
            fragment_transaction.add(R.id.content_frame, list_fragment);
            fragment_transaction.commit();
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawer_toggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        shared_preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        shared_preferences.edit().putBoolean("in_foreground", true).commit();
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

    @Override
    protected void onPause() {
        super.onPause();
        shared_preferences = getSharedPreferences("prefs", Context.MODE_PRIVATE);
        shared_preferences.edit().putBoolean("in_foreground", false).commit();
        System.out.println("Alarm set");
        // Set the alarm to start at approx 14:00
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 14);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        long repeat_time = AlarmManager.INTERVAL_DAY;

        long time_now = System.currentTimeMillis();
        long next_alarm_time = calendar.getTimeInMillis();
        if (time_now < next_alarm_time) {
            // The alarm has yet to go off
            // No need to change the alarm time
            System.out.println("The alarm has yet to go off");
        } else {
            // The alarm should have already gone off
            // change the next_alarm_time
            System.out.println("The alarm should have already gone off");
            next_alarm_time = next_alarm_time + repeat_time;
        }

        AlarmManager alarm_manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent alarm_intent = PendingIntent.getBroadcast(
                this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarm_manager.setRepeating(AlarmManager.RTC, next_alarm_time,
                repeat_time, alarm_intent);
    }
}