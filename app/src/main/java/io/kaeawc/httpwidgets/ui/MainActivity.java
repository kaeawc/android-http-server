package io.kaeawc.httpwidgets.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import de.greenrobot.event.EventBus;
import io.kaeawc.httpwidgets.App;
import io.kaeawc.httpwidgets.R;
import io.kaeawc.httpwidgets.network.events.NetworkRequest;
import io.kaeawc.httpwidgets.network.events.NetworkResponse;
import io.kaeawc.httpwidgets.services.BackgroundService;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    TextView mHelloWorld;
    Button mHttpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelloWorld = (TextView) findViewById(R.id.hello_world);
        mHttpButton = (Button) findViewById(R.id.http_button);
        mHttpButton.setOnClickListener(onHttpButtonClicked);
        startBackgroundService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    private void startBackgroundService() {
        Intent intent = new Intent(this, BackgroundService.class);
        if (!BackgroundService.isInstanceCreated()) {
            if (startService(intent) != null) {
                Timber.d("BackgroundService was already started");
            } else {
                Timber.d("BackgroundService not running, starting up...");
            }
        } else {
            Timber.d("BackgroundService already started");
        }
    }

    private View.OnClickListener onHttpButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Timber.v("Http Clicked");
            EventBus.getDefault().post(new NetworkRequest());
        }
    };

    /**
     * EventBus subscriber for network responses
     * @param response NetworkResponse event
     */
    @SuppressWarnings("unused")
    public void onEventMainThread(NetworkResponse response) {
        if (!response.success) {
            Timber.v("Unsuccessful network request");
            return;
        }

        mHelloWorld.setText(response.response);
    }
}
