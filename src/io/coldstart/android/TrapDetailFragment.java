package io.coldstart.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import io.coldstart.android.dummy.DummyContent;

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
	
	String hostname = "localhost.localdomain";
	int trapid = 0;
	String ipaddr = "127.0.0.1";
	String uptime = "";
	String trap = "{\"error\":\"nothing passed\"";
	boolean read = false;
	
	
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View rootView = inflater.inflate(R.layout.fragment_trap_detail,	container, false);

		// Show the dummy content as text in a TextView.
		if (mItem != null) 
		{
			((TextView) rootView.findViewById(R.id.trap_detail)).setText(mItem.content);
		}
		
		((TextView) rootView.findViewById(R.id.trap_detail)).setText(hostname);
		
		return rootView;
	}
}
