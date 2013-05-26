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

import android.preference.PreferenceManager;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class GCMIntentService extends com.google.android.gcm.GCMBaseIntentService 
{
	private NotificationManager mNM;
	TrapsDataSource datasource = null;
	
	public GCMIntentService() 
	{
        super(API.SENDER_ID);
    }
	
	@Override
	protected void onError(Context arg0, String arg1) 
	{
		Log.e("GCMIntentService","Error");
		
	}

	@Override
	protected void onMessage(Context arg0, Intent intent) 
	{
		String ns = Context.NOTIFICATION_SERVICE;
		mNM = (NotificationManager) arg0.getSystemService(ns);
		
		Log.e("GCMIntentService","onMessageonMessageonMessageonMessage");
		
		//GCM Payload
		String alertCount = intent.getExtras().getString("alertCount");
		String maxSeverity = intent.getExtras().getString("alertSeverity");
		String alertTime = intent.getExtras().getString("alertTime");
		String alertType = intent.getExtras().getString("alertType");
		String payloadJSON = intent.getExtras().getString("payload");
		
		//Stuff from the payload
		String hostname = "---", TrapDetails = "---", IP = "", Date = "", Uptime = "", payloadDetails = "Unknown payload";
		//JSONArray payloadDetails = null;
		
		//0 = a single alert
		if(alertType.equals(API.MSG_TRAP))
		{
            if( PreferenceManager.getDefaultSharedPreferences(this).getBoolean("allowBundling",false))
            {
                Log.i("TRAP","Received a trap notification but I'm bundling");
                return;
            }

			try
			{
				JSONObject payload = new JSONObject(payloadJSON);
				
				Log.i("payload",payload.toString(3));
				
				try
				{
					hostname = payload.getJSONObject("source").getString("hostname");
				}
				catch(Exception e)
				{
					hostname = "Unknown host";
				}
				
				try
				{
					//payloadDetails = payload.getJSONArray("trapdetails");
                    payloadDetails = payload.getString("trapdetails");
				}
				catch(Exception e)
				{
                    //e.printStackTrace();
					payloadDetails = "Unknown Trap payload";
				}
				
				try
				{
					IP = payload.getJSONObject("source").getString("ip");
				}
				catch(Exception e)
				{
					IP = "127.0.0.1";
				}
				
				try
				{
					Date = payload.getString("date");
				}
				catch(Exception e)
				{
					Date = "01/01/1970";
				}
				
				try
				{
					Uptime = payload.getString("uptime");
				}
				catch(Exception e)
				{
					Uptime = "";
				}
			}
			catch(Exception e)
			{
				//e.printStackTrace();
			}

            Trap trap = new Trap(hostname,IP);
            trap.date = Date;
            trap.uptime = Uptime;
            trap.trap = payloadDetails;

			if(Build.VERSION.SDK_INT >= 16)
			{
				SendInboxStyleNotification(alertCount,alertTime,hostname,trap.getPayloadAsString());
			}
			else
			{
				SendCombinedNotification(alertCount);
			}

			datasource = new TrapsDataSource(this);
		    datasource.open();
		    datasource.addRecentTrap(trap);
		    datasource.close();

            //If the app is in the foreground we should instruct it to refresh
            Intent broadcast = new Intent();
            broadcast.setAction(API.BROADCAST_ACTION);
            sendBroadcast(broadcast);
		}
        //1 = an generic notification
        //TODO Create generic handler

        //2 = batch (a bit like rate limit but on user choice)
        else if(alertType.equals(API.MSG_BATCH))
        {
            //Check we are ones after a bundled alert (many people share this GCM ID)
            if( PreferenceManager.getDefaultSharedPreferences(this).getBoolean("allowBundling",false))
            {
                sendBatchNotification(intent.getExtras().getString("alertCount"));
            }
            else
            {
                Log.i("BATCHING","Batching isn't enabled at the moment");
            }
        }
        //3 = Zenoss (for use with Rhybudd / coldstart HTTP API)
        else if(alertType.equals(API.MSG_ZENOSS))
        {
            Intent broadcast = new Intent();
            broadcast.setAction(API.ZENOSS_BROADCAST_ACTION);
            sendBroadcast(broadcast);
        }
        //4 = rate limit hit
        else if(alertType.equals(API.MSG_RATELIMIT))
        {
            sendRateLimitNotification(intent.getExtras().getString("ratelimit"));
        }
		else
		{
			//Do nothing
            //TODO Log an error / inform the user they need to update?
		}
		
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) 
	{
		//Log.e("GCMIntentService","onRegistered");
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) 
	{
		//Log.e("GCMIntentService","onUnregistered");
	}

    private void sendBatchNotification(String batchCount)
    {
        if(null == batchCount)
            batchCount = "1+";

        Intent intent = new Intent(this, TrapListActivity.class);
        intent.putExtra("forceDownload",true);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent broadcastDownload = new Intent();
        broadcastDownload.setAction(BatchDownloadReceiver.BROADCAST_ACTION);
        PendingIntent pBroadcastDownload = PendingIntent.getBroadcast(this,0,broadcastDownload,0);

        Intent broadcastIgnore = new Intent();
        broadcastIgnore.setAction(BatchIgnoreReceiver.BROADCAST_ACTION);
        PendingIntent pBroadcastIgnore = PendingIntent.getBroadcast(this,0,broadcastIgnore,0);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = null;

        if(Build.VERSION.SDK_INT >= 16)
        {
            notification = new Notification.InboxStyle(
                    new Notification.Builder(this)
                            .setContentTitle("A batch of Traps has been sent")
                            .setContentText("\"Batched traps are waiting to be downloaded")
                            .setSmallIcon(R.drawable.ic_stat_ratelimit)
                            .setVibrate(new long[] {0,100,200,300})
                            .setAutoCancel(true)
                            .setSound(uri)
                            .setTicker("A batch of Traps has been sent")
                            .addAction(R.drawable.ic_download_batch, "Get Batched Traps", pBroadcastDownload)
                            .addAction(R.drawable.ic_ignore, "Ignore Batch", pBroadcastIgnore))
                    .setBigContentTitle("A batch of Traps has been sent")
                    .setSummaryText("Launch ColdStart.io to Manage These Events")
                    .addLine("A number of traps have been sent and batched for delivery")
                    .addLine("The current number of items queued is " + batchCount)
                    .addLine(" ")
                    .addLine("Tap \"Get Batched Traps\" to download the cached traps")
                    .addLine("Tap \"Ignore Batch\" to delete them from the server.")

                    .build();
        }
        else
        {
            notification = new Notification.Builder(this)
                    .setContentTitle("A batch of Traps has been sent")
                    .setContentText("A number of traps have been sent and batched for delivery. The current number of items queued is " + batchCount +
                            "\nTap \"Get Alerts\" to batch download the outstanding traps or tap \"Ignore\" to delete them from the server.")
                    .setSmallIcon(R.drawable.ic_stat_ratelimit)
                    .setContentIntent(pIntent)
                    .setVibrate(new long[] {0,100,200,300})
                    .setAutoCancel(true)
                    .setSound(uri).build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(43524, notification);
    }


    private void sendRateLimitNotification(String rateLimitCount)
    {
        if(null == rateLimitCount)
            rateLimitCount = "0";

        Intent intent = new Intent(this, TrapListActivity.class);
        intent.putExtra("forceDownload",true);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Intent broadcastDownload = new Intent();
        broadcastDownload.setAction(BatchDownloadReceiver.BROADCAST_ACTION);
        PendingIntent pBroadcastDownload = PendingIntent.getBroadcast(this,0,broadcastDownload,0);

        Intent broadcastIgnore = new Intent();
        broadcastIgnore.setAction(BatchIgnoreReceiver.BROADCAST_ACTION);
        PendingIntent pBroadcastIgnore = PendingIntent.getBroadcast(this,0,broadcastIgnore,0);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification notification = null;

        if(Build.VERSION.SDK_INT >= 16)
        {
            notification = new Notification.InboxStyle(
                    new Notification.Builder(this)
                            .setContentTitle("Inbound Traps have been rate limited")
                            .setContentText("\"The number of traps being relayed to your phone has breeched the rate limit.")
                            .setSmallIcon(R.drawable.ic_stat_ratelimit)
                            .setVibrate(new long[] {0,100,200,300})
                            .setAutoCancel(true)
                            .setSound(uri)
                            .setTicker("Inbound Traps have been rate limited")
                            .addAction(R.drawable.ic_download_batch, "Get Batched Traps", pBroadcastDownload)
                            .addAction(R.drawable.ic_ignore, "Ignore Batch", pBroadcastIgnore))
                    .setBigContentTitle("Inbound Traps have been rate limited")
                    .setSummaryText("Launch ColdStart.io to Manage These Events")
                    .addLine("The number of traps relayed to you has breeched the rate limit.")
                    .addLine("The current number of items queued is " + rateLimitCount)
                    .addLine(" ")
                    .addLine("Tap \"Get Batched Traps\" to download the cached traps")
                    .addLine("Tap \"Ignore Batch\" to delete them from the server.")

                    .build();
        }
        else
        {
            notification = new Notification.Builder(this)
                .setContentTitle("Inbound Traps have been rate limited")
                .setContentText("The number of traps being relayed to your phone has breeched the rate limit. The current number of items queued is " + rateLimitCount +
                        "\nTap \"Get Alerts\" to batch download the outstanding traps or tap \"Ignore\" to delete them from the server.")
                .setSmallIcon(R.drawable.ic_stat_ratelimit)
                .setContentIntent(pIntent)
                .setVibrate(new long[] {0,100,200,300})
                .setAutoCancel(true)
                .setSound(uri).build();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.notify(43524, notification);
    }

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void SendInboxStyleNotification(String alertCount, String alertTime, String hostname, String payloadDetails)
	{
		Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		String Line1 = "", Line2 = "", Line3 = "", Line4 = "", Line5 = "";
        String[] separatedLines = payloadDetails.split("\n");

		int payloadLength = separatedLines.length;
        if(payloadLength > 5)
            payloadLength = 5;

		for(int i = 0; i < payloadLength; i++)
		{
			try
			{
				switch(i)
				{
					case 0:
					{
						Line1 = separatedLines[i];
					}
					break;
					
					case 1:
					{
						Line2 = separatedLines[i];
					}
					break;
					
					case 2:
					{
						Line3 = separatedLines[i];
					}
					break;
					
					case 3:
					{
						Line4 = separatedLines[i];
					}
					break;
					
					case 4:
					{
						Line5 = separatedLines[i];
					}
					break;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		Notification notification = new Notification.InboxStyle(
			      new Notification.Builder(this)
			         .setContentTitle("SNMP trap received")
			         .setContentText("TrapDetails")
			         .setSmallIcon(R.drawable.ic_stat_alert)
			         .setVibrate(new long[] {0,100,200,300})
			         .setAutoCancel(true)
			         .setSound(uri)
			         .setTicker("New SNMP traps have been received")
					 .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, TrapListActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("forceRefresh", true), 0)))
			      .setBigContentTitle("New SNMP traps have been received")
			      .setSummaryText("Launch ColdStart.io to Manage These Events")
			      .addLine(Line1).addLine(Line2).addLine(Line3).addLine(Line4)
			      .build();
		
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		mNM.notify(43523, notification); 
	}
	
	private void SendCombinedNotification(String EventCount)
	{
		Notification notification = new Notification(R.drawable.ic_stat_alert, EventCount + " new SNMP Traps!", System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		notification.ledARGB = 0xffff0000;

		notification.ledOnMS = 300;
		notification.ledOffMS = 1000;
		
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		Context context = getApplicationContext();
		Intent notificationIntent = new Intent(this, TrapListActivity.class);
		notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notificationIntent.putExtra("forceRefresh", true);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(context, EventCount + " SNMP Traps", "Click to launch ColdStart.io", contentIntent);
		mNM.notify(43523, notification);//NotificationID++ 
	}
	
	/*@Override
	protected void onRecoverableError(Context context, String errorId)
	{
		
	}*/
}
