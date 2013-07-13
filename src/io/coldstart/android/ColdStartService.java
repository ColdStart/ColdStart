package io.coldstart.android;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;


public class ColdStartService extends Service
{
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate()
    {
        Log.i("ColdStartService", "I exist so you can receive messages");
    }

    @Override
    public void onDestroy()
    {
        Log.e("ColdStartService", "Being destroyed, this is bad!");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        // We always want to be alive
        return START_STICKY;
    }
}
