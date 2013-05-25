/*
* Copyright (C) 2013 - Gareth Llewellyn
*
* This file is part of ColdStart.io - https://github.com/ColdStart/ColdStart
*
* This program is free software: you can redistribute it and/or modify it
* under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful, but WITHOUT
* ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
* FOR A PARTICULAR PURPOSE. See the GNU General Public License
* for more details.
*
* You should have received a copy of the GNU General Public License along with
* this program. If not, see <http://www.gnu.org/licenses/>
*/
package io.coldstart.android;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

public class BatchIgnoreReceiver extends BroadcastReceiver
{
    public static String BROADCAST_ACTION = "io.coldstart.android.broadcast.batchignore";

    @Override
    public void onReceive(final Context context, Intent intent)
    {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(43524);

        Log.e("onReceive", "I'm a broadcast receiver!");
        Log.e("onReceive", "Ignoring Batch");

        ((Thread) new Thread()
        {
            public void run()
            {
                API api = new API();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);

                try
                {
                    if(api.ignoreBatch(settings.getString("APIKey", ""), settings.getString("keyPassword", ""), API.md5(Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID))))
                    {
                        Log.i("ignoreBatch","Success");
                    }
                    else
                    {
                        Log.i("ignoreBatch","Failure");
                    }
                }
                catch(Exception e)
                {
                    //TODO this is probably pretty bad!
                    e.printStackTrace();
                }
            }
        }).start();

    }
}