package io.coldstart.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class TrapsDBOpenHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "recent_traps";

    TrapsDBOpenHelper(Context context) 
    {
        super(context, "traps", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) 
    {
        db.execSQL("CREATE TABLE \"recent_traps\"(\"trapID\" INTEGER PRIMARY KEY  NOT NULL, " +
        		"\"trapHostName\" TEXT, " +
        		"\"trapIP\" TEXT, " +
        		"\"trapDate\" TEXT, " +
        		"\"trapUptime\" TEXT, " +
        		"\"trapPayload\" TEXT, " +
        		"\"trapRead\" INTEGER)");

        db.execSQL("CREATE TABLE \"host_details\"(\"detailID\" INTEGER PRIMARY KEY  NOT NULL, " +
                "\"HostName\" TEXT, " +
                "\"IP\" TEXT, " +
                "\"Location\" TEXT, " +
                "\"Contact\" TEXT, " +
                "\"Description\" TEXT)");
    }


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
        Log.i("onUpgrade", "Upgrading DBs");
        if(newVersion == 2 && oldVersion == 1)
        {
            db.execSQL("CREATE TABLE \"host_details\"(\"detailID\" INTEGER PRIMARY KEY  NOT NULL, " +
                    "\"HostName\" TEXT, " +
                    "\"IP\" TEXT, " +
                    "\"Location\" TEXT, " +
                    "\"Contact\" TEXT, " +
                    "\"Description\" TEXT)");
        }
		
	}
}
