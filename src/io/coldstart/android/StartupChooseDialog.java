package io.coldstart.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;


public class StartupChooseDialog extends DialogFragment implements OnCancelListener 
{
	SharedPreferences settings = null;
	
    public static StartupChooseDialog newInstance() 
    {
    	StartupChooseDialog frag = new StartupChooseDialog();
        /*Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);*/
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) 
    {
        return new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_choose)
                .setTitle("Register a new Account or Login")
                .setMessage("To get started with ColdStart Alerts you'll need to create a new API Key or login to an existing one.")
                .setPositiveButton("Create",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((TrapListActivity) getActivity()).ShowRegisterDialog();
                        }
                    }
                )
                .setNeutralButton("Login",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            ((TrapListActivity) getActivity()).ShowLoginDialog();
                        }
                    }
                )
                .create();
    }
    
    @Override
    public void onResume() 
    {
      super.onResume();
      Log.e("onResume","Resuming");
      settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
  	
      	if(settings.getBoolean("firstRun", true))
		{
	  		Log.e("onResume","Looks like firstrun was still true");
		}
      	else
      	{
      		Log.e("onResume","Dismissing");
      		Toast.makeText(getActivity(), "Successfully logged in. Syncing GCM now", Toast.LENGTH_SHORT).show();
    		((TrapListActivity) getActivity()).subscribeToMessages();
	  		getDialog().dismiss();
      	}
    }
    
    @Override
    public void onCancel(DialogInterface dialog)
    {
    	/*if(settings.getBoolean("firstRun", true))
		{*/
    		Toast.makeText(getActivity(), "ColdStart will not work without a registered API Key. Exiting...", Toast.LENGTH_SHORT).show();
	    	getActivity().finish();
		/*}
    	else
    	{
	    	Toast.makeText(getActivity(), "Successfully logged in. Syncing GCM now", Toast.LENGTH_SHORT).show();
    		((TrapListActivity) getActivity()).subscribeToMessages();
    	}*/
    }
}