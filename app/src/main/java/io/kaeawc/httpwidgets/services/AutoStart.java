package io.kaeawc.httpwidgets.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import timber.log.Timber;

public class AutoStart extends BroadcastReceiver
{
    public void onReceive(Context arg0, Intent arg1)
    {
        Timber.d("Completed bootup, starting BackgroundService");
        Intent intent = new Intent(arg0, BackgroundService.class);
        arg0.startService(intent);
    }
}