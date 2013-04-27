package io.coldstart.android;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.MenuItem;

public class SettingsFragment extends PreferenceActivity {

	SharedPreferences prefs;

	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        addPreferencesFromResource(R.xml.preferences);
        
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("ColdStart - Settings");
        ab.setSubtitle("Configure customisations for ColdStart");
        
        if(Build.VERSION.SDK_INT >= 14)
        {
        	ab.setHomeButtonEnabled(true);
        }
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
	    switch (item.getItemId()) 
	    {
		    case android.R.id.home:
	        {
	        	Intent in = new Intent();
	    		setResult(20,in);
	    		finish();
	            return true;
	        }
        
        default:
            return super.onOptionsItemSelected(item);
	    }
	}
}
