package io.coldstart.android;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class Trap implements Parcelable 
{
	int trapID = 0;
	String date = "";
	String uptime = "";
	String Hostname = "localhost.localdomain";
	public String IP = "127.0.0.1";
	String trap = "";
	Boolean read = false;
	
	public Trap(String a, String b)
	{
		this.Hostname = a;
		this.IP = b;
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		
	}
	
	public Trap(Cursor cursor)
	{
		//{ "trapID","trapHostName","trapIP","trapDate","trapUptime","trapPayload","trapRead" };
		this.trapID = cursor.getInt(0);
		this.Hostname = cursor.getString(1);
		this.IP = cursor.getString(2);
		this.date = cursor.getString(3);
		this.uptime = cursor.getString(4);
		this.trap = cursor.getString(5);
		
		int read = cursor.getInt(6);
		if(read == 0)
		{
			this.read = false;
		}
		else
		{
			this.read = true;
		}
	}

}
