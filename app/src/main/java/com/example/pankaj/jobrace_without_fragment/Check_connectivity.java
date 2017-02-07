package com.example.pankaj.jobrace_without_fragment;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class Check_connectivity
{
	public static boolean is_connected(Activity activity)
	{

		NetworkInfo info=null;
		ConnectivityManager connectivity = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnectedOrConnecting())
                return true;
        }
        Toast.makeText(activity, "You are not connected with internet.....", Toast.LENGTH_LONG).show();
        return false;

	}

}
