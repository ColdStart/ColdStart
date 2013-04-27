package io.coldstart.android;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class TrapsDataSource 
{
	private SQLiteDatabase database;
	private TrapsDBOpenHelper dbHelper;
	private String[] allColumns = { "trapID","trapHostName","trapIP","trapDate","trapUptime","trapPayload","trapRead" };
	
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
		/*Cursor cursor = database.query(TrapsDBOpenHelper.TABLE_NAME, new String[] { "trapID" }, "trapID = " + Integer.toString(trap.trapID), null, null, null, null);
		
		if(cursor.getCount() > 0)
		{
			cursor.close();
			return false;
		}
		else
		{*/
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
		/*}*/
	  }

	  public void deleteComment(Trap trap) 
	  {
	    int id = trap.trapID;
	    
	    System.out.println("Comment deleted with id: " + id);
	    database.delete(TrapsDBOpenHelper.TABLE_NAME, "trapID" + " = " + id, null);
	  }

	  public List<Trap> getRecentTraps() 
	  {
	    List<Trap> recentTags = new ArrayList<Trap>();

	    Cursor cursor = database.query(TrapsDBOpenHelper.TABLE_NAME, allColumns, null, null, null, null, "trapID DESC");

	    cursor.moveToFirst();
	    
	    while (!cursor.isAfterLast()) 
	    {
	    	Trap thisTrap =  new Trap(cursor);
	    	recentTags.add(thisTrap);
	    	cursor.moveToNext();
	    }
	    // Make sure to close the cursor
	    cursor.close();
	    return recentTags;
	  }
}
