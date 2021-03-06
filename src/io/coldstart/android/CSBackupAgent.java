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

import android.app.backup.BackupAgentHelper;
import android.app.backup.SharedPreferencesBackupHelper;

public class CSBackupAgent extends BackupAgentHelper 
{
	// The name of the SharedPreferences file
	static final String PREFS = "user_preferences";
	
	// A key to uniquely identify the set of backup data
	static final String PREFS_BACKUP_KEY = "prefs";
	
	// Allocate a helper and add it to the backup agent
	@Override
	public void onCreate() 
	{
	    SharedPreferencesBackupHelper helper = new SharedPreferencesBackupHelper(this,"io.coldstart_preferences");
	    addHelper(PREFS_BACKUP_KEY, helper);
	}
}
