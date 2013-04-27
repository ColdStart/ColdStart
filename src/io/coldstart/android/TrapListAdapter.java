package io.coldstart.android;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TrapListAdapter extends BaseAdapter
{

	private Context context;
    private List<Trap> listOfTraps;
    OnClickListener trapOnClick = null;
    OnLongClickListener trapOnLongClick = null;
 
    public TrapListAdapter(Context context, List<Trap> listOfTraps) 
    {
        this.context = context;
        this.listOfTraps = listOfTraps;
    }
    
    @Override
	public int getCount() 
    {
    	return this.listOfTraps.size();
	}
    
	@Override
	public Object getItem(int position) 
	{
		return listOfTraps.get(position);
	}
	
	@Override
	public long getItemId(int position) 
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		Trap trap = listOfTraps.get(position);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		convertView = inflater.inflate(R.layout.trap_list_item, null);
		
		//((TextView) convertView.findViewById(R.id.TopicName)).setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/d0.ttf"));
		TextView hostname = ((TextView) convertView.findViewById(R.id.Hostname));
		
		hostname.setText(trap.Hostname);
		
		if(trap.read)
		{
			hostname.setTypeface(Typeface.DEFAULT);
		}
		
		((TextView) convertView.findViewById(R.id.IPAddress)).setText(trap.IP);
		
		((TextView) convertView.findViewById(R.id.TrapDate)).setText(trap.date);
		
		return convertView;
	}
}
