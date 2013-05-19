package io.coldstart.android;
import android.R.menu;
import android.app.*;
import android.app.backup.BackupManager;
import android.content.*;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;

/**
 * An activity representing a list of Traps. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link TrapDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link TrapListFragment} and the item details (if present) is a
 * {@link TrapDetailFragment}.
 * <p>
 * This activity also implements the required {@link TrapListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class TrapListActivity extends FragmentActivity implements TrapListFragment.Callbacks
{

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;
	SharedPreferences settings = null;
	DialogFragment dialogFragment = null;
	MenuItem gcmStatus = null;
	String regId = "";
	View abprogress = null;
	boolean gcmSuccess = false;
	String securityID = "";
	final static int DISPLAY_SETTINGS = 1;
    final static int LAUNCHDETAILACTIVITY = 2;
    boolean instantRefresh = false;

    String selectedIP = "";
    String selectedHost = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_trap_list);

		securityID = API.md5(Settings.Secure.getString(getContentResolver(),Settings.Secure.ANDROID_ID));
		
		ActionBar ab = getActionBar();
        ab.setTitle("ColdStart.io");
        ab.setSubtitle("Instant SNMP Trap Alerting");
		
		//GCM stuff
		GCMRegistrar.checkDevice(this);
		GCMRegistrar.checkManifest(this);
		regId = GCMRegistrar.getRegistrationId(this);
		
		if (regId.equals("")) 
		{
			Log.v("GCM", "Registering");
			GCMRegistrar.register(this, API.SENDER_ID);
			
			/*if(null != gcmStatus)
				gcmStatus.setIcon(R.drawable.ic_action_gcm_success);*/
		} 
		else 
		{
			Log.v("GCM", "Already registered");
			
			/*if(null != gcmStatus)
				gcmStatus.setIcon(R.drawable.ic_action_gcm_success);*/
		}
		
		Log.e("GCMID",GCMRegistrar.getRegistrationId(this));
		
		settings = PreferenceManager.getDefaultSharedPreferences(this);
		
		if(settings.getBoolean("firstRun", true))
		{
			if(dialogFragment != null)
		    {
		    	dialogFragment.dismiss();
		    }
		    
		    // DialogFragment.show() will take care of adding the fragment
		    // in a transaction.  We also want to remove any currently showing
		    // dialog, so make our own transaction and take care of that here.
		    FragmentTransaction ft = getFragmentManager().beginTransaction();
		    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		    if (prev != null) 
		    {
		        ft.remove(prev);
		        Log.i("prev","Removing");
		    }
		    ft.addToBackStack(null);

		    // Create and show the dialog.
		    //dialogFragment = StartupProcessDialog.newInstance(GCMRegistrar.getRegistrationId(this));
		    dialogFragment = StartupChooseDialog.newInstance();
		    dialogFragment.setCancelable(true);
		    dialogFragment.show(ft, "dialog");
		}
		else
		{
			subscribeToMessages();
		}
				
		
		if (findViewById(R.id.trap_detail_container) != null) 
		{
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((TrapListFragment) getSupportFragmentManager().findFragmentById(R.id.trap_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	public void subscribeToMessages()
	{
		LayoutInflater inflater2 = (LayoutInflater) getSystemService(TrapListActivity.LAYOUT_INFLATER_SERVICE);
		abprogress = inflater2.inflate(R.layout.progress_wheel, null);
		
		if(null != gcmStatus)
		{
			gcmStatus.setActionView(abprogress);
		}
		else
		{
			Log.e("gcmStatus","gcmStatus was null");
		}
		
		((Thread) new Thread()
    	{
			public void run()
			{
				while(null == gcmStatus)
				{
					Log.i("gcmStatus","waiting");
					try 
					{
						Thread.sleep(1000);
	                } 
					catch (InterruptedException e) 
					{
	                    // TODO Auto-generated catch block
	                    e.printStackTrace();
	                }
				}
				
				Log.i("OnLaunch","Just updating our GCM ID");
				
				API api = new API();
				
				try
				{
					if(api.updateGCMAccount(settings.getString("APIKey", ""), settings.getString("keyPassword", ""), GCMRegistrar.getRegistrationId(TrapListActivity.this), securityID))
					{
						Log.i("updateGCMAccount","Success");
						
						if(null != gcmStatus)
						{
							gcmSuccess = true;
							
							runOnUiThread(new Runnable() {
							    public void run() {
							    	gcmStatus.setIcon(R.drawable.ic_action_gcm_success);
							    	TrapListActivity.this.invalidateOptionsMenu();
							    }
							});
						}
						else
						{
							Log.e("gcmStatus","Icon was null");
						}
					}
					else
					{
						Log.i("updateGCMAccount","Fail");
						gcmSuccess = false;
						if(null != gcmStatus)
						{
							runOnUiThread(new Runnable() {
							    public void run() {
							    	gcmStatus.setIcon(R.drawable.ic_action_gcm_failed);
							    }
							});
						}
						
						//TODO Popup to the user that there was a problem
					}

				
					runOnUiThread(new Runnable() {
					    public void run() {
					    	TrapListActivity.this.invalidateOptionsMenu();
					    }
					});
				}
				catch(Exception e)
				{
					//TODO this is probably pretty bad!
					e.printStackTrace();
				}
			}
    	}).start();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		
		gcmStatus = menu.findItem(R.id.GCMStatus);
		
		if(gcmSuccess)
		{
			gcmStatus.setIcon(R.drawable.ic_action_gcm_success);
		}
		else
		{
			gcmStatus.setIcon(R.drawable.ic_action_gcm_failed);
		}
		/*if(regId.equals(""))
		{
			LayoutInflater inflater2 = (LayoutInflater) getSystemService(TrapListActivity.LAYOUT_INFLATER_SERVICE);
			View abprogress = inflater2.inflate(R.layout.progress_wheel, null);
			gcmStatus.setActionView(abprogress);
		}
		else
		{
			gcmStatus.setIcon(R.drawable.ic_action_gcm_success);
		}*/
		 
		 
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) 
		{
			case R.id.GCMStatus:
			{
				subscribeToMessages();
				return true;
			}

			case R.id.Settings:
			{
				Intent SettingsIntent = new Intent(TrapListActivity.this, SettingsFragment.class);
				this.startActivityForResult(SettingsIntent, DISPLAY_SETTINGS);
				return true;
			}

			case R.id.SeeAPIKey:
			{
				/*ClipData clip = ClipData.newPlainText("ColdStart API Key",settings.getString("APIKey", ""));
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				clipboard.setPrimaryClip(clip);*/
				
				if(dialogFragment != null)
			    	dialogFragment.dismiss();
			    
			    FragmentTransaction ft = getFragmentManager().beginTransaction();
			    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
			    if (prev != null) 
			    {
			        ft.remove(prev);
			        Log.i("prev","Removing");
			    }
			    ft.addToBackStack(null);

			    // Create and show the dialog.
			    dialogFragment = ViewAPIKeyDialog.newInstance(settings.getString("APIKey", ""));
			    dialogFragment.setCancelable(true);
			    dialogFragment.show(ft, "dialog");
			    
			    return true;
			}
		}
		
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{
        Log.e("onActivityResult", "requestCode" + requestCode + " / resultCode" + resultCode);
		//Check what the result was from the Settings Activity
		switch(requestCode)
		{
			case DISPLAY_SETTINGS:
			{
				BackupManager bm = new BackupManager(this);
				bm.dataChanged();
				
				((Thread) new Thread()
		    	{
					public void run()
					{
						try
						{
							API api = new API();
							boolean updateTest = api.updateAccountSettings(securityID, settings.getBoolean("allowBundling", true), settings.getString("bundleDelay","30"));
							
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
                    		    	}).start();
			}
			break;

            //TODO remove as the onResume of the fragment handles the refresh
            case LAUNCHDETAILACTIVITY:
            {
                if(resultCode == TrapDetailFragment.ARG_QUITONDELETE)
                {
                    /*Intent broadcast = new Intent();
                    broadcast.setAction(API.BROADCAST_ACTION);
                    sendBroadcast(broadcast);*/
                }
            }
            break;
		}
	}

	/**
	 * Callback method from {@link TrapListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	//public void onItemSelected(String id) 
	@Override
	public void onItemSelected(Trap trap) 
	{
		//Trap trap = ((TrapListFragment) getFragmentManager().findFragmentByTag("dialog")).listOfTraps.get(location)
        selectedIP = trap.IP;
        selectedHost = trap.Hostname;

		if (mTwoPane) 
		{
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(TrapDetailFragment.ARG_HOSTNAME, trap.Hostname);
			arguments.putString(TrapDetailFragment.ARG_IPADDR, trap.IP);
			arguments.putBoolean(TrapDetailFragment.ARG_READ, trap.read);
			arguments.putString(TrapDetailFragment.ARG_TRAP, trap.trap);
			arguments.putInt(TrapDetailFragment.ARG_TRAP_ID, trap.trapID);
			arguments.putString(TrapDetailFragment.ARG_UPTIME, trap.uptime);
            arguments.putBoolean(TrapDetailFragment.ARG_2PANE, true);

			TrapDetailFragment fragment = new TrapDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.trap_detail_container, fragment).commit();

		}
		else 
		{
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, TrapDetailActivity.class);
			detailIntent.putExtra(TrapDetailFragment.ARG_HOSTNAME, trap.Hostname);
			detailIntent.putExtra(TrapDetailFragment.ARG_IPADDR, trap.IP);
			detailIntent.putExtra(TrapDetailFragment.ARG_READ, trap.read);
			detailIntent.putExtra(TrapDetailFragment.ARG_TRAP, trap.trap);
			detailIntent.putExtra(TrapDetailFragment.ARG_TRAP_ID, trap.trapID);
			detailIntent.putExtra(TrapDetailFragment.ARG_UPTIME, trap.uptime);
            detailIntent.putExtra(TrapDetailFragment.ARG_2PANE, false);

            startActivityForResult(detailIntent,LAUNCHDETAILACTIVITY);
		}
	}

	public void ShowRegisterDialog() 
	{
		if(dialogFragment != null)
	    	dialogFragment.dismiss();
	    
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) 
	    {
	        ft.remove(prev);
	        Log.i("prev","Removing");
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    dialogFragment = StartupRegisterDialog.newInstance(GCMRegistrar.getRegistrationId(this));
	    dialogFragment.setCancelable(true);
	    dialogFragment.show(ft, "dialog");
	}

	public void ShowLoginDialog() 
	{
	    if(dialogFragment != null)
	    	dialogFragment.dismiss();
	    
	    FragmentTransaction ft = getFragmentManager().beginTransaction();
	    Fragment prev = getFragmentManager().findFragmentByTag("dialog");
	    if (prev != null) 
	    {
	        ft.remove(prev);
	        Log.i("prev","Removing");
	    }
	    ft.addToBackStack(null);

	    // Create and show the dialog.
	    dialogFragment = StartupLoginDialog.newInstance(GCMRegistrar.getRegistrationId(this));
	    dialogFragment.setCancelable(true);
	    dialogFragment.show(ft, "dialog");
	}
}
