package io.coldstart.android;

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
		
		Log.e("GCMIntentService","onMessage");
		
		//GCM Payload
		String alertCount = intent.getExtras().getString("alertCount");
		String maxSeverity = intent.getExtras().getString("alertSeverity");
		String alertTime = intent.getExtras().getString("alertTime");
		String alertType = intent.getExtras().getString("alertType");
		String payloadJSON = intent.getExtras().getString("payload");
		
		//Stuff from the payload
		String hostname = "---", TrapDetails = "---", IP = "", Date = "", Uptime = "";
		JSONArray payloadDetails = null;
		
		//0 = a single alert
		if(alertType.equals("0"))
		{
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
					payloadDetails = payload.getJSONArray("trapdetails");
				}
				catch(Exception e)
				{
					payloadDetails = null;
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
				e.printStackTrace();
			}
			
			if(Build.VERSION.SDK_INT >= 16)
			{
				SendInboxStyleNotification(alertCount,alertTime,hostname,payloadDetails);
			}
			else
			{
				SendCombinedNotification(alertCount);
			}

			Trap trap = new Trap(hostname,IP);
			trap.date = Date;
			trap.uptime = Uptime;
			
			trap.trap = payloadDetails.toString();
			
			datasource = new TrapsDataSource(this);
		    datasource.open();
		    datasource.addRecentTrap(trap);
		    datasource.close();

            //If the app is in the foreground we should instruct it to refresh
            Intent broadcast = new Intent();
            broadcast.setAction(API.BROADCAST_ACTION);
            sendBroadcast(broadcast);
		}
		//1 = an indicative notification
		else
		{
			
		}
		
	}

	@Override
	protected void onRegistered(Context arg0, String arg1) 
	{
		Log.e("GCMIntentService","onRegistered");
		
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) 
	{
		Log.e("GCMIntentService","onUnregistered");
	}

	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	private void SendInboxStyleNotification(String alertCount, String alertTime, String hostname, JSONArray payloadDetails)
	{
		Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		
		String Line1 = "", Line2 = "", Line3 = "", Line4 = "", Line5 = "";
		
		int payloadLength = payloadDetails.length();
		for(int i = 0; i < payloadLength; i++)
		{
			try
			{
				switch(i)
				{
					case 0:
					{
						Line1 = payloadDetails.getString(i);
					}
					break;
					
					case 1:
					{
						Line2 = payloadDetails.getString(i);
					}
					break;
					
					case 2:
					{
						Line3 = payloadDetails.getString(i);
					}
					break;
					
					case 3:
					{
						Line4 = payloadDetails.getString(i);
					}
					break;
					
					case 4:
					{
						Line5 = payloadDetails.getString(i);
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
			      .setBigContentTitle(alertCount + " new SNMP traps")
			      .setSummaryText("Launch ColdStart.io to Manage These Events")
			      .addLine(Line1).addLine(Line2).addLine(Line3).addLine(Line4).addLine(Line5)
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
