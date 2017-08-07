package rickhutten.rembrandtcollege.net;

import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class GETRequest {
    final private String TAG = getClass().toString();

    public GETRequest(String url) {
        this(url, null);
    }

    public GETRequest(String url, @Nullable final ResponseListener listener) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    // If HTML response.code() is between 200-299
                    Log.w(TAG, "Unexpected code " + response);
                } else if (listener != null) {
                    listener.onResponse(response.body().string());
                }
            }
        });
    }
}
