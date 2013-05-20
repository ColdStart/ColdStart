package io.coldstart.android;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

import java.util.List;

public class BatchDownloadReceiver extends BroadcastReceiver
{
    public static String BROADCAST_ACTION = "io.coldstart.android.broadcast.batchdownload";

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(43524);

        Log.e("onReceive", "I'm a broadcast receiver!");
        Log.e("onReceive", "Downloading Batch");

        ((Thread) new Thread()
        {
            public void run()
            {
                API api = new API();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

                TrapsDataSource datasource = null;

                try
                {
                    List<Trap> batchedTraps = api.getBatch(settings.getString("APIKey", ""), settings.getString("keyPassword", ""), API.md5(Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID)));

                    if(null == batchedTraps)
                    {
                        Log.e("BatchDownloadReceiver", "BatchedTraps was null");
                        return;
                    }

                    datasource = new TrapsDataSource(context);
                    datasource.open();
                    int i = batchedTraps.size();
                    for(int x = 0; x < i; x++)
                    {
                        datasource.addRecentTrap(batchedTraps.get(x));
                    }

                    //If the app is in the foreground we should instruct it to refresh
                    Intent broadcast = new Intent();
                    broadcast.setAction(API.BROADCAST_ACTION);
                    context.sendBroadcast(broadcast);
                }
                catch(Exception e)
                {
                    //TODO this is probably pretty bad!
                    e.printStackTrace();
                }
                finally
                {
                    try
                    {
                        if(null != datasource)
                            datasource.close();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }
}