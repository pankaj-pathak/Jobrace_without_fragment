package com.example.pankaj.jobrace_without_fragment.services;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.pankaj.jobrace_without_fragment.Custom_dialog_new_apk_available;


/**
 * Created by Pankaj on 12/16/2016.
 */

public class DownloadApkReceiver extends BroadcastReceiver
{

    public static final String PROCESS_RESPONSE = "com.as400samplecode.intent.action.PROCESS_RESPONSE";
    Activity activity;
    String apk_url="";
    public static Custom_dialog_new_apk_available custom_dialog_new_apk_available;
    //private long downloadReference;
    public DownloadApkReceiver(Activity activity,String apk_url)
    {
        this.activity=activity;
        this.apk_url=apk_url;

    }
    @Override
    public void onReceive(Context context, Intent intent) {

        //String reponseMessage = intent.getStringExtra(Download_apk_service.RESPONSE_MESSAGE);
        //Log.v("response message===", reponseMessage);

        //parse the JSON response
        //JSONObject responseObj;
        try {
            //responseObj = new JSONObject(reponseMessage);
            //boolean success =responseObj.getBoolean("success");
            //if the reponse was successful check further
            //if(success){
                //get the latest version from the JSON string
                //int latestVersion = responseObj.getInt("latestVersion");
                //get the lastest application URI from the JSON string
                //appURI = responseObj.getString("appURI");
                //check if we need to upgrade?
                //if(latestVersion > versionCode){
               // if(true){
                    //oh yeah we do need an upgrade, let the user know send an alert message
            //Log.d("current version====","current  in recieverrrrrrrrrr=======================");
            custom_dialog_new_apk_available=new Custom_dialog_new_apk_available(activity,apk_url);
            custom_dialog_new_apk_available.setCancelable(false);
            custom_dialog_new_apk_available.show();

            /*
            final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("There is newer version of this application available, click OK to upgrade now?")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                //if the user agrees to upgrade
                                public void onClick(DialogInterface dialog, int id) {
                                    ((Homepage)activity).downloadManager = (DownloadManager)activity.getSystemService(activity.DOWNLOAD_SERVICE);
                                    Uri URI = Uri.parse(apk_url);
                                    Log.d("current version====","current  in recirrrrrrrrrr==============URI==========="+apk_url);

                                    DownloadManager.Request request = new DownloadManager.Request(URI);
                                    //request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
                                    //request.setAllowedOverRoaming(false);
                                    request.setTitle("Updating Jobrace");
                                    request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS,"Jobrace.apk");
                                    ((Homepage)activity).downloadReference = ((Homepage)activity).downloadManager.enqueue(request);
                                }
                            })
                            .setNegativeButton("Remind Later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // User cancelled the dialog
                                }
                            });
                    //show the alert message
            builder.setCancelable(false);
            builder.create().show();
*/
              //  }

           // }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
