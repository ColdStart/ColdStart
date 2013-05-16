package io.coldstart.android;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TrapsDBOpenHelper extends SQLiteOpenHelper
{
	private static final int DATABASE_VERSION = 1;
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
    }


	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		// TODO Auto-generated method stub
		
	}
}
