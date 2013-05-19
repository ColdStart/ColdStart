package io.coldstart.android;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TrapDetailListAdapter extends BaseAdapter
{

	private Context context;
    private List<Trap> listOfTraps;
    OnClickListener trapOnClick = null;
    OnLongClickListener trapOnLongClick = null;

    public TrapDetailListAdapter(Context context, List<Trap> listOfTraps)
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
		
		convertView = inflater.inflate(R.layout.trap_detail_list_item, null);

        String TrapDetails = "";

        /*try
        {
            JSONArray TrapJSON = new JSONArray(trap.trap);

            int trapsCount = TrapJSON.length();

            for(int i = 0; i < trapsCount; i++)
            {
                String tmpPayload = TrapJSON.getString(i);

                if(!tmpPayload.equals(""))
                {
                    TrapDetails += tmpPayload + "\n\n";
                }
            }
        }
        catch (JSONException e)
        {
            TrapDetails = "Trap details failed to parse";
        }


        ((TextView) convertView.findViewById(R.id.TrapDetail)).setText(TrapDetails);*/

        ((TextView) convertView.findViewById(R.id.TrapDetail)).setText(trap.trap);

        ((TextView) convertView.findViewById(R.id.TrapDate)).setTypeface(Typeface.createFromAsset((context).getAssets(), "fonts/MavenPro-Regular.ttf"));
        ((TextView) convertView.findViewById(R.id.TrapDate)).setText(trap.date);

		return convertView;
	}
}
