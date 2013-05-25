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
import io.coldstart.android.R.color;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
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
		
		//((TextView) convertView.findViewById(R.id.IPAddress)).setText(trap.IP);
        ((TextView) convertView.findViewById(R.id.IPAddress)).setText(trap.trap);

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date trapDate = null;
        try
        {
            trapDate = format.parse(trap.date.substring(0,10));
            System.out.println(trapDate);
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        try
        {
            if(new Date().before(trapDate))
            {
                ((TextView) convertView.findViewById(R.id.TrapDate)).setText(
                        trap.date.substring(8,10)
                                + "/" +
                                trap.date.substring(5,7)
                                + "/" +
                                trap.date.substring(2,4)
                );
            }
            else
            {
                ((TextView) convertView.findViewById(R.id.TrapDate)).setText(trap.date.substring(11,19));
            }
        }
        catch(Exception e)
        {
            //e.printStackTrace();
        }

		((TextView) convertView.findViewById(R.id.TrapCount)).setText(Integer.toString(trap.trapCount));

        ((TextView) convertView.findViewById(R.id.TrapDate)).setTypeface(Typeface.createFromAsset((context).getAssets(), "fonts/MavenPro-Regular.ttf"));
        ((TextView) convertView.findViewById(R.id.TrapCount)).setTypeface(Typeface.createFromAsset((context).getAssets(), "fonts/MavenPro-Regular.ttf"));
        hostname.setTypeface(Typeface.createFromAsset((context).getAssets(), "fonts/MavenPro-Regular.ttf"));
		return convertView;
	}
}
