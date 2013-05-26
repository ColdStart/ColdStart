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
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by Gareth on 26/05/13.
 */
public class EditOIDActivity extends FragmentActivity
{
    LinearLayout oidDescList = null;
    API api = new API();

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpTo(this, new Intent(this, TrapListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_oid);

        // Show the Up button in the action bar.
        getActionBar().setDisplayHomeAsUpEnabled(true);

        oidDescList = (LinearLayout) findViewById(R.id.OIDDescriptionHolder);

        //((TextView) findViewById(R.id.textView)).setText(getIntent().getStringExtra("payload"));
        JSONObject oidDescriptions = null;
        int i = 0;
        try
        {
            oidDescriptions = new JSONObject(getIntent().getStringExtra("payload"));

            Iterator<String> iter = oidDescriptions.keys();
            while (iter.hasNext())
            {
                String key = iter.next();
                try
                {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View convertView = inflater.inflate(R.layout.oid_edit, null);

                    ((TextView) convertView.findViewById(R.id.OID)).setText(key);
                    ((EditText) convertView.findViewById(R.id.OIDDescription)).setText((String) oidDescriptions.get(key));
                    ((EditText) convertView.findViewById(R.id.OIDDescription)).setTag("e_"+key);

                    ((Button) convertView.findViewById(R.id.submitDescriptionButton)).setTag(key);
                    ((Button) convertView.findViewById(R.id.submitDescriptionButton)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view)
                        {
                            //Log.e("onClick", ((EditText) ((View) oidDescList.findViewWithTag(view.getTag())).findViewWithTag("e_"+view.getTag())).getText().toString());

                            ((Thread) new Thread()
                            {
                                public void run()
                                {
                                    try
                                    {
                                        if(api.submitOIDEdit((String) view.getTag(),((EditText) ((View) oidDescList.findViewWithTag(view.getTag())).findViewWithTag("e_"+view.getTag())).getText().toString()))
                                        {
                                            runOnUiThread(new Runnable() {
                                                public void run()
                                                {
                                                    Toast.makeText(getApplicationContext(), "OID suggestion successful.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                        else
                                        {
                                            runOnUiThread(new Runnable() {
                                                public void run()
                                                {
                                                    Toast.makeText(getApplicationContext(), "OID suggestion was not successful.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                    catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();
                        }
                    });
                    convertView.setTag(key);
                    oidDescList.addView(convertView);
                    i++;
                }
                catch (Exception e)
                {
                    // Something went wrong!
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
