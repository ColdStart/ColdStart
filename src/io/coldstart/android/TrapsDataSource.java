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

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class TrapsDataSource 
{
	private SQLiteDatabase database;
	private TrapsDBOpenHelper dbHelper;
	private String[] trapAllColumns = { "trapID","trapHostName","trapIP","trapDate","trapUptime","trapPayload","trapRead" };
	private String[] trapAllColumnswCount = { "trapID","trapHostName","trapIP","trapDate","trapUptime","trapPayload","trapRead","count(1) as trapCount" };
    private String[] hostDetails = {"Location", "Contact", "Description"};

    public TrapsDataSource(Context context)
	{
		dbHelper = new TrapsDBOpenHelper(context);
	}
	
	public void open() throws SQLException 
	{
		database = dbHelper.getWritableDatabase();
	}
	
	public void close() 
	{
		dbHelper.close();
	}
	
	public boolean addRecentTrap(Trap trap) 
	  {
        ContentValues values = new ContentValues();

        values.put("trapHostName",trap.Hostname);
        values.put("trapIP",trap.IP);
        values.put("trapDate",trap.date);
        values.put("trapUptime",trap.uptime);
        values.put("trapPayload",trap.trap);
        values.put("trapRead",trap.read);


        long insertId = database.insert(TrapsDBOpenHelper.TABLE_NAME, null, values);

        if(insertId > -1)
        {
            return true;
        }
        else
        {
            return false;
        }
	  }

    public boolean addHostDetails(ColdStartHost host)
    {
        ContentValues values = new ContentValues();

        values.put("HostName",host.HostName);
        values.put("IP",host.IP);
        values.put("Location",host.Location);
        values.put("Contact",host.Contact);
        values.put("Description",host.Description);


        long insertId = database.insert("host_details", null, values);

        if(insertId > -1)
        {
            return true;
        }
        else
        {
            return false;
        }
    }


    public ColdStartHost getHostDetails(String IPAddress)
    {
        //						 query(String table, 				String[] columns, 	String selection, 	            String[] selectionArgs, String groupBy, String having, String orderBy)
        Cursor cursor = database.query("host_details",              hostDetails,        "IP = \""+IPAddress+"\"", 	    null, 					null, 			null, 			null);

        ColdStartHost dbHost = new ColdStartHost();
        if(cursor.moveToFirst())
        {
            dbHost.Location = cursor.getString(0);
            dbHost.Contact = cursor.getString(1);
            dbHost.Description = cursor.getString(2);
        }
        else
        {
            Log.e("getHostDetails", "Unable to move to first!");
        }

        // Make sure to close the cursor
        if (!cursor.isClosed() ||cursor != null)
        {
            cursor.close();
            cursor=null;
        }
        return dbHost;
    }

	  public void deleteSingleTrap(Trap trap)
	  {
	    int id = trap.trapID;
	    
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(TrapsDBOpenHelper.TABLE_NAME, "trapID" + " = " + id, null);
	  }

    public void deleteHost(String IPAddress)
    {
        //System.out.println("Comment deleted with id: " + id);
        database.delete(TrapsDBOpenHelper.TABLE_NAME, "trapIP" + " = \"" + IPAddress +"\"", null);
        database.delete("host_details", "IP" + " = \"" + IPAddress +"\"", null);
    }

	  public List<Trap> getRecentTraps() 
	  {
	    List<Trap> recentTags = new ArrayList<Trap>();

	    /*Cursor cursor = database.query(TrapsDBOpenHelper.TABLE_NAME, allColumns, null, null, null, null, "trapID DESC");

	    cursor.moveToFirst();
	    
	    while (!cursor.isAfterLast()) 
	    {
	    	Trap thisTrap =  new Trap(cursor);
	    	recentTags.add(thisTrap);
	    	cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return recentTags;*/
	    
	    //"trapID","trapHostName","trapIP","trapDate","trapUptime","trapPayload","trapRead"
	    //						 query(String table, 				String[] columns, 	String selection, 	String[] selectionArgs, String groupBy, String having, String orderBy)
	    Cursor cursor = database.query(TrapsDBOpenHelper.TABLE_NAME, trapAllColumnswCount, 		null, 				null, 					"trapIP", 			null, 			"trapID DESC");

	    cursor.moveToFirst();
	    
	    while (!cursor.isAfterLast()) 
	    {
	    	Trap thisTrap =  new Trap(cursor);
	    	recentTags.add(thisTrap);
	    	cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    //cursor.close();
        if (!cursor.isClosed() ||cursor != null)
        {
          cursor.close();
          cursor=null;
        }
	    return recentTags;
	  }

    public List<Trap> getTrapsforHost(String IPAddress)
    {
        List<Trap> trapsFromHost = new ArrayList<Trap>();

        //						 query(String table, 				String[] columns, 	String selection, 	            String[] selectionArgs, String groupBy, String having, String orderBy)
        Cursor cursor = database.query(TrapsDBOpenHelper.TABLE_NAME, trapAllColumns,  "trapIP = \""+IPAddress+"\"", 	null, 					null, 			null, 			"trapID DESC");

        cursor.moveToFirst();

        while (!cursor.isAfterLast())
        {
            Trap thisTrap =  new Trap(cursor,true);
            trapsFromHost.add(thisTrap);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        //cursor.close();
        if (!cursor.isClosed() ||cursor != null)
        {
            cursor.close();
            cursor=null;
        }
        return trapsFromHost;
    }
}
