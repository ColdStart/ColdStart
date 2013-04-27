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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class ViewAPIKeyDialog extends DialogFragment 
{
	public String APIKey = "";
	
    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    static ViewAPIKeyDialog newInstance(String APIKey) 
    {
    	ViewAPIKeyDialog f = new ViewAPIKeyDialog();
    	
        Bundle args = new Bundle();
        args.putString("APIKey", APIKey);
        f.setArguments(args);

        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        int style = DialogFragment.STYLE_NORMAL, theme = 0;
        setStyle(style, theme);
        APIKey = getArguments().getString("APIKey");
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
    {
    	View v = inflater.inflate(R.layout.fragment_apikey, container, false);
    	
    	getDialog().setTitle("ColdStart API Key / SNMP Community");
    	
    	((TextView) v.findViewById(R.id.APIKeyLabel)).setText(APIKey);
    	
    	((TextView) v.findViewById(R.id.APIKeyLabel)).setOnClickListener(new OnClickListener() {
            public void onClick(View v) 
            {
            	ClipData clip = ClipData.newPlainText("ColdStart API Key",APIKey);
				ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setPrimaryClip(clip);
				
				Toast.makeText(getActivity(), "Your API Key has been added to the clipboard.", Toast.LENGTH_LONG).show();
            }});
    	
    	
        // Watch for button clicks.
        Button button = (Button)v.findViewById(R.id.closeButton);
        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) 
            {
				getDialog().cancel();
            }
        });

        return v;
    }
 
}