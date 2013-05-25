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

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class Trap implements Parcelable 
{
	int trapID = 0;
	String date = "";
	String uptime = "";
	String Hostname = "localhost.localdomain";
	public String IP = "127.0.0.1";
	String trap = "";
	Boolean read = false;
	int trapCount = 0;
	
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
		
		try
		{
			this.trapCount = cursor.getInt(7);
			
			Log.e("TrapCount",Integer.toString(this.trapCount));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

    public Trap(Cursor cursor, boolean noCount)
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

    public String getPayloadAsString() throws JSONException
    {
        JSONObject jsonTrap = new JSONObject(this.trap);
        String trapDescription = "";

        Iterator<String> iter = jsonTrap.keys();
        while (iter.hasNext())
        {
            String key = iter.next();
            try
            {
                trapDescription += jsonTrap.get(key) + "\n";
            }
            catch (JSONException e)
            {
                // Something went wrong!
            }
        }

        return trapDescription;
    }

}
