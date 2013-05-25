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

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class HostFilterDialog extends DialogFragment
{
	SharedPreferences settings = null;

    public interface EditNameDialogListener
    {
        void onFinishEditDialog(String inputText);
    }

    private EditText mEditText;

    public static HostFilterDialog newInstance()
    {
    	HostFilterDialog frag = new HostFilterDialog();
        /*Bundle args = new Bundle();
        args.putInt("title", title);
        frag.setArguments(args);*/
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_dialog_filters, container);
        mEditText = (EditText) view.findViewById(R.id.hostEditText);
        getDialog().setTitle("ColdStart Alert Filtering");

        /*mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mEditText.setOnEditorActionListener(this);*/

        return view;
    }

    /*@Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        if (EditorInfo.IME_ACTION_DONE == actionId)
        {
            TrapListActivity activity = (TrapListActivity) getActivity();
            activity.onFinishEditDialog(mEditText.getText().toString());
            this.dismiss();
            return true;
        }
        return false;
    }*/
}