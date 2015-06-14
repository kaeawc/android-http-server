package io.kaeawc.httpwidgets.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import de.greenrobot.event.EventBus;
import io.kaeawc.httpwidgets.network.WidgetServer;
import io.kaeawc.httpwidgets.network.events.NetworkRequest;
import io.kaeawc.httpwidgets.network.events.NetworkResponse;
import timber.log.Timber;

public class BackgroundService extends Service {

    private WidgetServer mServer;
    OkHttpClient client = new OkHttpClient();
    private static BackgroundService sInstance = null;
    private static boolean running = false;
    private final IBinder mBinder = new LocalBinder();

    public static boolean isInstanceCreated() {
        return (sInstance != null && running);
    }


    @Override
    public void onCreate() {
        super.onCreate();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        init();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy() {

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }

        try {
            mServer.stop();
        } catch (Exception exception) {
            Timber.v(exception, "Failed to stop web server");
        }


        super.onDestroy();
    }

    public class LocalBinder extends Binder {
        public BackgroundService getService() {
            // Return this instance of LocalService so clients can call public
            // methods
            Timber.i("Broadcast received");
            return BackgroundService.this;
        }
    }

    private void init() {

        if (running) {
            Timber.v("Already running");
            return;
        }

        mServer = new WidgetServer();

        try {
            mServer.start();
        } catch (Exception exception) {
            Timber.v(exception, "Failed to start web server");
            return;
        }

        running = true;
    }

    private String getUrl(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private String getWidget() {
        try {
            return getUrl("http://localhost:8080");
        } catch (IOException exception) {
            Timber.v(exception, "");
            return null;
        }
    }

    /**
     * EventBus subscriber for network requests
     * @param request Network request event
     */
    @SuppressWarnings("unused")
    public void onEventAsync(NetworkRequest request) {
        String response = getWidget();
        boolean success = response != null;
        EventBus.getDefault().post(new NetworkResponse(request, success, response));
    }
}
