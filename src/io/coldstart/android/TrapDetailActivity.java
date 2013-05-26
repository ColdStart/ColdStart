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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import com.bugsense.trace.BugSenseHandler;

/**
 * An activity representing a single Trap detail screen. This activity is only
 * used on handset devices. On tablet-size devices, item details are presented
 * side-by-side with a list of items in a {@link TrapListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link TrapDetailFragment}.
 */
public class TrapDetailActivity extends FragmentActivity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);

        BugSenseHandler.initAndStartSession(TrapDetailActivity.this, "b569cf15");

		setContentView(R.layout.activity_trap_detail);

		// Show the Up button in the action bar.
		getActionBar().setDisplayHomeAsUpEnabled(true);

		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) 
		{
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putString(TrapDetailFragment.ARG_ITEM_ID, getIntent().getStringExtra(TrapDetailFragment.ARG_ITEM_ID));
			arguments.putString(TrapDetailFragment.ARG_HOSTNAME, getIntent().getStringExtra(TrapDetailFragment.ARG_HOSTNAME));
			arguments.putString(TrapDetailFragment.ARG_IPADDR, getIntent().getStringExtra(TrapDetailFragment.ARG_IPADDR));
			arguments.putBoolean(TrapDetailFragment.ARG_READ, getIntent().getBooleanExtra(TrapDetailFragment.ARG_READ,false));
			arguments.putString(TrapDetailFragment.ARG_TRAP, getIntent().getStringExtra(TrapDetailFragment.ARG_TRAP));
			arguments.putInt(TrapDetailFragment.ARG_TRAP_ID, getIntent().getIntExtra(TrapDetailFragment.ARG_TRAP_ID,0));
			arguments.putString(TrapDetailFragment.ARG_UPTIME, getIntent().getStringExtra(TrapDetailFragment.ARG_UPTIME));
			
			TrapDetailFragment fragment = new TrapDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.trap_detail_container, fragment).commit();
		}
	}

    public void exitOnDelete()
    {
        Intent in = new Intent();
        setResult(TrapDetailFragment.ARG_QUITONDELETE,in);
        finish();
    }

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
			NavUtils.navigateUpTo(this,
					new Intent(this, TrapListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
