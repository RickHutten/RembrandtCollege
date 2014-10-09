package rickhutten.rembrandtcollege;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class InternetActivity extends Activity {

    WebView web_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internet_activity);

        web_view = (WebView) findViewById(R.id.web_view);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.loadUrl("http://www.rembrandt-college.nl/");
        web_view.setWebViewClient(new WebViewClient());
        web_view.setInitialScale(1);
        //web_view.getSettings().setBuiltInZoomControls(true);
        web_view.getSettings().setUseWideViewPort(true);
    }

    @Override
    public void onBackPressed() {
        if (web_view.canGoBack()) {
            web_view.goBack();
        } else {
            this.finish();
        }
    }
}
