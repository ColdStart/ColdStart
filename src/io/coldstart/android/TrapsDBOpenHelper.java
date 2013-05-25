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
