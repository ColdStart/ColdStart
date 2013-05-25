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

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.google.android.gcm.GCMRegistrar;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class StartupLoginDialog extends DialogFragment 
{
    int processStage;
    
    private static final Long REGISTER_SUCCESS = (long) 100;
    private static final Long REGISTER_FAIL = (long) 101;
    
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static StartupLoginDialog newInstance(String GCMID) 
    {
    	StartupLoginDialog f = new StartupLoginDialog();
    	
        Bundle args = new Bundle();
        args.putString("GCMID", GCMID);
        f.setArguments(args);

        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        // Pick a style based on the num.
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        /*switch ((mNum-1)%6) {
            case 1: style = DialogFragment.STYLE_NO_TITLE; break;
            case 2: style = DialogFragment.STYLE_NO_FRAME; break;
            case 3: style = DialogFragment.STYLE_NO_INPUT; break;
            case 4: style = DialogFragment.STYLE_NORMAL; break;
            case 5: style = DialogFragment.STYLE_NORMAL; break;
            case 6: style = DialogFragment.STYLE_NO_TITLE; break;
            case 7: style = DialogFragment.STYLE_NO_FRAME; break;
            case 8: style = DialogFragment.STYLE_NORMAL; break;
        }
        switch ((mNum-1)%6) {
            case 4: theme = android.R.style.Theme_Holo; break;
            case 5: theme = android.R.style.Theme_Holo_Light_Dialog; break;
            case 6: theme = android.R.style.Theme_Holo_Light; break;
            case 7: theme = android.R.style.Theme_Holo_Light_Panel; break;
            case 8: theme = android.R.style.Theme_Holo_Light; break;
        }*/
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	View v = inflater.inflate(R.layout.fragment_dialog_startup_login, container, false);
    	
    	getDialog().setTitle("Configure ColdStart Alerts");
    	
        /*View tv = v.findViewById(R.id.text);
        ((TextView)tv).setText("Dialog #" + mNum + ": using style "
                + getNameForNum(mNum));
         */
    	
        // Watch for button clicks.
        Button button = (Button)v.findViewById(R.id.SaveButton);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) 
            {
                // When button is clicked, call up to owning activity.
                //((FragmentDialog) getActivity()).showDialog();
            	EditText APIKeyET = (EditText) getDialog().findViewById(R.id.APIKey);
            	EditText keyPasswordET = (EditText) getDialog().findViewById(R.id.keyPassword);
            	
            	if(APIKeyET.getText().toString().equals(""))
            	{
            		Toast.makeText(getActivity(), "Ensure the API Key is complete", Toast.LENGTH_SHORT).show();
            	}
            	else
            	{
            		APIKey thisKey = new APIKey();
            		thisKey.APIKey = APIKeyET.getText().toString();
            		thisKey.Password = keyPasswordET.getText().toString();
            		
            		new RegisterAccount().execute(thisKey);
            	}
            }
        });

        return v;
    }
    
    private class RegisterAccount extends AsyncTask<APIKey, Integer, Long> 
    {
    	APIKey thisKey = null;
    	
    	@Override
    	protected void onPreExecute()
    	{
    		((EditText) getDialog().findViewById(R.id.APIKey)).setEnabled(false);
        	((EditText) getDialog().findViewById(R.id.keyPassword)).setEnabled(false);
        	((Button) getDialog().findViewById(R.id.SaveButton)).setEnabled(false);
        	((ProgressBar) getDialog().findViewById(R.id.registeringProgressBar)).setVisibility(View.VISIBLE);
    	}
    	
    	@Override
		protected Long doInBackground(APIKey... params) 
    	{
    		thisKey = params[0];
    		
    		API api = new API();
    		
    		try 
    		{
				if(api.updateGCMAccount(thisKey.APIKey, thisKey.Password, GCMRegistrar.getRegistrationId(getActivity()),Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID)))
				{
					Log.e("login","Success on logging in");
					return REGISTER_SUCCESS;
				}
				else
				{
					return REGISTER_FAIL;
				}
			} 
    		catch (Exception e) 
    		{
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
    		
			
		}

        protected void onPostExecute(Long result) 
        {
        	((EditText) getDialog().findViewById(R.id.APIKey)).setEnabled(true);
        	((EditText) getDialog().findViewById(R.id.keyPassword)).setEnabled(true);
        	((Button) getDialog().findViewById(R.id.SaveButton)).setEnabled(true);
        	((ProgressBar) getDialog().findViewById(R.id.registeringProgressBar)).setVisibility(View.INVISIBLE);
        	
        	if(result == REGISTER_SUCCESS)
        	{
	        	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
	        	editor.putBoolean("firstRun", false);
	        	editor.putString("APIKey", thisKey.APIKey);
	    		editor.putString("keyPassword", thisKey.Password);
	    		editor.commit();
	    		getDialog().dismiss();
        	}
        	else
        	{
        		Toast.makeText(getActivity(), "There was an issue registering that account", Toast.LENGTH_SHORT).show();
        	}
        }
		
    }
}