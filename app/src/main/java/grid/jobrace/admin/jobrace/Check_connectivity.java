package grid.jobrace.admin.jobrace;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import grid.jobrace.admin.jobrace.dialogs.Custom_dialog_connect_with_internet;

public class Check_connectivity
{
	public static boolean is_connected(Activity activity,boolean... show_toast)
	{

        String message="Internet connection is not available. Please connect with internet.";
		NetworkInfo info=null;
		ConnectivityManager connectivity = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnectedOrConnecting())
                return true;
        }
        if(show_toast.length<=0)
        new Custom_dialog_connect_with_internet(activity,message).show();
        return false;

	}

}
