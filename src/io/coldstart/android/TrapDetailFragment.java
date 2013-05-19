package io.coldstart.android;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.widget.ListView;
import android.widget.TextView;

import android.widget.Toast;
import com.google.android.gcm.GCMRegistrar;
import io.coldstart.android.dummy.DummyContent;

import java.util.List;

/**
 * A fragment representing a single Trap detail screen. This fragment is either
 * contained in a {@link TrapListActivity} in two-pane mode (on tablets) or a
 * {@link TrapDetailActivity} on handsets.
 */
public class TrapDetailFragment extends Fragment 
{
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";
	
	public static final String ARG_TRAP_ID = "trapid";
	public static final String ARG_HOSTNAME = "hostname";
	public static final String ARG_IPADDR = "ipaddr";
	public static final String ARG_UPTIME = "uptime";
	public static final String ARG_TRAP = "trap";
	public static final String ARG_READ = "read";
    public static final String ARG_2PANE = "2pane";

    public static final int ARG_QUITONDELETE = 9;
    public static final String ARG_LAUNCHDETAILACTIVITY = "2pane";

    public static String BROADCAST_ACTION = "io.coldstart.android.broadcast.removepane";

	String hostname = "localhost.localdomain";
	int trapid = 0;
	String ipaddr = "127.0.0.1";
	String uptime = "";
	String trap = "{\"error\":\"nothing passed\"";
	boolean read = false;
    boolean twoPane = false;

    TrapDetailListAdapter adapter = null;
    TrapsDataSource datasource = null;
    public List<Trap> listOfTraps = null;
    ListView list = null;
    private BroadcastReceiver receiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            Log.e("onRecieve","Got a broadcast");
            getData();
        }
    };
	
	/**
	 * The dummy content this fragment is presenting.
	 */
	private DummyContent.DummyItem mItem;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public TrapDetailFragment()
	{
	}

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		if (getArguments().containsKey(ARG_TRAP_ID)) 
		{
			trapid = getArguments().getInt(ARG_TRAP_ID);
		}
		
		if (getArguments().containsKey(ARG_HOSTNAME)) 
		{
			hostname = getArguments().getString(ARG_HOSTNAME);
		}
		
		if (getArguments().containsKey(ARG_IPADDR)) 
		{
			ipaddr = getArguments().getString(ARG_IPADDR);
		}
		
		if (getArguments().containsKey(ARG_UPTIME)) 
		{
			uptime = getArguments().getString(ARG_UPTIME);
		}
		
		if (getArguments().containsKey(ARG_TRAP)) 
		{
			trap = getArguments().getString(ARG_TRAP);
		}
		
		if (getArguments().containsKey(ARG_READ)) 
		{
			read = getArguments().getBoolean(ARG_READ);
		}

        if (getArguments().containsKey(ARG_2PANE))
        {
            twoPane = getArguments().getBoolean(ARG_2PANE);
        }

        setHasOptionsMenu(true);
	}

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        getData();
    }


    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_trap_detail,	container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) 
		{
			((TextView) rootView.findViewById(R.id.hostname)).setText(mItem.content);
		}
		
		((TextView) rootView.findViewById(R.id.hostname)).setText(hostname);
        ((TextView) rootView.findViewById(R.id.hostname)).setTypeface(Typeface.createFromAsset((getActivity()).getAssets(), "fonts/MavenPro-Regular.ttf"));

        ((TextView) rootView.findViewById(R.id.IPAddress)).setText(ipaddr);

        //TODO: We don't have any way of storing this data yet *AND* the phone interface doesn't have them either
        /*((TextView) rootView.findViewById(R.id.Location)).setText("Unknown Location");
        ((TextView) rootView.findViewById(R.id.Contact)).setText("No Contact information");
        ((TextView) rootView.findViewById(R.id.Description)).setText("...");*/

        list = (ListView) rootView.findViewById(R.id.trap_list);
		return rootView;
	}

    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater)
    {
        inflater.inflate(R.menu.view_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.ImportSNMP:
            {
                Log.e("onOptionsItemSelected", "Importing SNMP");
                return true;
            }

            case R.id.DeleteTraps:
            {
                new AlertDialog.Builder(getActivity())
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete Traps for selected host?")
                        .setMessage("Are you sure you want to delete all the traps from host " + hostname + "?")
                        .setPositiveButton("Yes, Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("onOptionsItemSelected", "Deleting Traps");
                                (new Thread() {
                                    public void run() {
                                        datasource = new TrapsDataSource(getActivity());
                                        datasource.open();
                                        datasource.deleteHost(ipaddr);
                                        datasource.close();

                                        if(twoPane)
                                        {
                                            Intent broadcast = new Intent();
                                            broadcast.setAction(API.BROADCAST_ACTION);
                                            getActivity().sendBroadcast(broadcast);

                                            Bundle arguments = new Bundle();
                                            arguments.putString(TrapDetailFragment.ARG_HOSTNAME, hostname);

                                            PendingDeleteFragment fragment = new PendingDeleteFragment();
                                            fragment.setArguments(arguments);
                                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.trap_detail_container, fragment).commit();
                                        }
                                        else
                                        {
                                            ((TrapDetailActivity) getActivity()).exitOnDelete();
                                        }
                                    }
                                }).start();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();

                return true;
            }
        }

        return false;
    }

    private void getData()
    {
        Log.i("getData","getting data");
        datasource = new TrapsDataSource(getActivity());
        datasource.open();

        listOfTraps = datasource.getTrapsforHost(ipaddr);

        datasource.close();

        adapter = new TrapDetailListAdapter(getActivity(),listOfTraps);

        if(null != list)
        {
            list.setAdapter(adapter);
        }
        else
        {
            Toast.makeText(getActivity(),"There was an error addressing the UI",Toast.LENGTH_SHORT).show();
        }
    }
}
