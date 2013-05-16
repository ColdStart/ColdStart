package io.coldstart.android;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import com.google.android.gcm.GCMRegistrar;

import android.app.DialogFragment;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;


public class StartupRegisterDialog extends DialogFragment 
{
    int processStage;
    String APIKey = "";
    boolean lock = false;
    
    private static final Long REGISTER_SUCCESS = (long) 100;
    private static final Long REGISTER_FAIL = (long) 101;
    
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static StartupRegisterDialog newInstance(String GCMID) 
    {
    	StartupRegisterDialog f = new StartupRegisterDialog();
    	
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
        setStyle(style, theme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	View v = inflater.inflate(R.layout.fragment_dialog_startup_register, container, false);
    	
    	getDialog().setTitle("Generate a ColdStart API key");
    	
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
            	EditText emailAddressET = (EditText) getDialog().findViewById(R.id.emailAddress);
            	EditText keyPasswordET = (EditText) getDialog().findViewById(R.id.keyPassword);
            	
            	/*if(userNameET.getText().toString().equals("") ||
            			userEmailET.getText().toString().equals("") ||
            			userPassword.getText().toString().equals(""))
            	{
            		Toast.makeText(getActivity(), "Ensure all fields are complete", Toast.LENGTH_SHORT).show();
            	}
            	else
            	{*/
            		APIKey thisKey = new APIKey();
            		thisKey.Email = emailAddressET.getText().toString();
            		thisKey.Password = keyPasswordET.getText().toString();
            		
            		new RegisterAccount().execute(thisKey);
            	//}
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
    		((EditText) getDialog().findViewById(R.id.emailAddress)).setEnabled(false);
        	((EditText) getDialog().findViewById(R.id.keyPassword)).setEnabled(false);
        	((Button) getDialog().findViewById(R.id.SaveButton)).setEnabled(false);
        	((ProgressBar) getDialog().findViewById(R.id.registeringProgressBar)).setVisibility(View.VISIBLE);
    	}
    	
    	@Override
		protected Long doInBackground(APIKey... params) 
    	{
    		thisKey = params[0];
    		
    		API api = new API();
    		if(lock)
    		{
    			return null;
    		}
    		
    		lock = true;
    		
    		try 
    		{
    			APIKey = api.createGCMAccount(thisKey.Email, thisKey.Password, GCMRegistrar.getRegistrationId(getActivity()),Settings.Secure.getString(getActivity().getContentResolver(),Settings.Secure.ANDROID_ID)); 
    			if(APIKey != null && !APIKey.equals(""))
				{
    				try
    				{
    					Looper.prepare();
	    				ClipData clip = ClipData.newPlainText("ColdStart API Key",APIKey);
	    				ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
	    				clipboard.setPrimaryClip(clip);
    				}
    				catch(Exception e)
    				{
    					e.printStackTrace();
    				}
    			
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
        	((EditText) getDialog().findViewById(R.id.emailAddress)).setEnabled(true);
        	((EditText) getDialog().findViewById(R.id.keyPassword)).setEnabled(true);
        	
        	((Button) getDialog().findViewById(R.id.SaveButton)).setEnabled(true);
        	((ProgressBar) getDialog().findViewById(R.id.registeringProgressBar)).setVisibility(View.INVISIBLE);
        	
        	if(result == REGISTER_SUCCESS)
        	{
	        	SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
	        	editor.putBoolean("firstRun", false);
	        	editor.putString("APIKey", APIKey);
	    		editor.putString("keyPassword", thisKey.Password);
	    		
	    		editor.commit();
	    		getDialog().dismiss();
	    		
	    		Toast.makeText(getActivity(), "Your API Key is: " + APIKey +"\r\nIt has been added to your clipboard.", Toast.LENGTH_LONG).show();
        	}
        	else
        	{
        		Toast.makeText(getActivity(), "There was an issue registering for an API Key", Toast.LENGTH_SHORT).show();
        	}
        }
		
    }
}