package grid.jobrace.admin.jobrace.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import grid.jobrace.admin.jobrace.dialogs.Custom_dialog_new_apk_available;


/**
 * Created by Pankaj on 12/16/2016.
 */

public class DownloadApkReceiver extends BroadcastReceiver
{

    public static final String PROCESS_RESPONSE = "com.as400samplecode.intent.action.PROCESS_RESPONSE";
    Activity activity;
    String apk_url="";
    public static Custom_dialog_new_apk_available custom_dialog_new_apk_available;
    public DownloadApkReceiver(Activity activity,String apk_url)
    {
        this.activity=activity;
        this.apk_url=apk_url;

    }
    @Override
    public void onReceive(Context context, Intent intent) {
   try {
            custom_dialog_new_apk_available=new Custom_dialog_new_apk_available(activity,apk_url);
            custom_dialog_new_apk_available.setCancelable(false);
            custom_dialog_new_apk_available.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
