package com.example.pankaj.jobrace_without_fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by Pankaj on 12/18/2016.
 */

public class Custom_dialog_new_apk_available  extends Dialog
{
    Activity activity;
    Button btn_cancel;
    Button btn_activate;
    String apk_url;
    public Custom_dialog_new_apk_available(final Activity activity,String apk_url)
    {
        super(activity);
        this.activity=activity;
        this.apk_url=apk_url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_new_apk_available);
        btn_activate= (Button) findViewById(R.id.btn_activate);
        btn_cancel= (Button) findViewById(R.id.btn_cancel);
        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

               ((Homepage)activity).downloadManager = (DownloadManager)activity.getSystemService(activity.DOWNLOAD_SERVICE);
                Uri URI = Uri.parse(apk_url);
                DownloadManager.Request request = new DownloadManager.Request(URI);
                request.setTitle("Updating Jobrace");
                request.setDestinationInExternalFilesDir(activity, Environment.DIRECTORY_DOWNLOADS,"Jobrace.apk");
                ((Homepage)activity).downloadReference = ((Homepage)activity).downloadManager.enqueue(request);
                Custom_dialog_new_apk_available.this.dismiss();
                ((Homepage)activity).pd.show();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Custom_dialog_new_apk_available.this.dismiss();
                activity.finish();
            }
        });

    }
}
