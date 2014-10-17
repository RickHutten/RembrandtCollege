package rickhutten.rembrandtcollege;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


public class NewsActivity extends ActionBarActivity {

    final private static String XML_URL = "http://www.rembrandt-college.nl/rss.php";
    final private static String REMBRANDT_URL = "http://www.rembrandt-college.nl";
    final private static String FACEBOOK_URL = "https://www.facebook.com/RembrandtCollege";
    final private static String TWITTER_URL = "https://twitter.com/Rembrandt_Coll";
    final private static String MAGISTER_URL = "https://rembrandt.swp.nl/5.6.25/Magister.aspx";
    final private static String ITSLEARNING_URL = "http://itslearning.mobi/logOn/logOn.aspx?ReturnUrl=%2f";

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

        RelativeLayout magister_knop = (RelativeLayout) findViewById(R.id.magister_knop);
        magister_knop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInternetActivity(MAGISTER_URL);
            }
        });

        RelativeLayout itslearning_knop = (RelativeLayout) findViewById(R.id.itslearning_knop);
        itslearning_knop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startInternetActivity(ITSLEARNING_URL);
            }
        });

        android.support.v4.app.FragmentTransaction fragment_transition = getSupportFragmentManager().beginTransaction();
        rickhutten.rembrandtcollege.ListFragment list_fragment = new rickhutten.rembrandtcollege.ListFragment();
        fragment_transition.add(R.id.content_frame, list_fragment);
        fragment_transition.commit();

        DownloadWebPageTask download_web_page_task = new DownloadWebPageTask(this, list_fragment);
        download_web_page_task.execute(XML_URL);
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
}


