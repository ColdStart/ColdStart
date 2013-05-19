package io.coldstart.android;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Gareth on 19/05/13.
 */
public class PendingDeleteFragment extends Fragment
{
    public static final String ARG_HOSTNAME = "hostname";
    String hostname = "localhost.localdomain";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PendingDeleteFragment()
    {
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_HOSTNAME))
        {
            hostname = getArguments().getString(ARG_HOSTNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_host_deleted,	container, false);

        ((TextView) rootView.findViewById(R.id.hostname)).setText(hostname);

        return rootView;
    }
}