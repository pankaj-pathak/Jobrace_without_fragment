package com.example.pankaj.jobrace_without_fragment.services;

/**
 * Created by Pankaj on 12/16/2016.
 */

import android.app.IntentService;
import android.content.Intent;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class Download_apk_service extends IntentService{

    public static final String REQUEST_STRING = "myRequest";
    public static final String RESPONSE_STRING = "myResponse";
    public static final String RESPONSE_MESSAGE = "myResponseMessage";

    public Download_apk_service() {
        super("Download_apk_service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String responseMessage = "";
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(DownloadApkReceiver.PROCESS_RESPONSE);
        broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
        broadcastIntent.putExtra(RESPONSE_MESSAGE, responseMessage);
        sendBroadcast(broadcastIntent);
    }

}