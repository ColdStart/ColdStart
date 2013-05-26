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

    public TrapDetailListAdapter(Context context, List<Trap> listOfTraps, OnClickListener trapOnClick)
    {
        this.context = context;
        this.listOfTraps = listOfTraps;
        this.trapOnClick = trapOnClick;
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
        String trapDescription = "";
        try
        {
            trapDescription = trap.getPayloadAsString();
        }
        catch(Exception e)
        {
            trapDescription = "Unable to decode trap";
        }

        ((TextView) convertView.findViewById(R.id.TrapDetail)).setText(trapDescription);

        ((TextView) convertView.findViewById(R.id.TrapDate)).setTypeface(Typeface.createFromAsset((context).getAssets(), "fonts/MavenPro-Regular.ttf"));
        ((TextView) convertView.findViewById(R.id.TrapDate)).setText(trap.date);

        if(trapOnClick != null)
        {
            convertView.setTag(position);
            convertView.setOnClickListener((OnClickListener) trapOnClick);
        }

		return convertView;
	}
}
