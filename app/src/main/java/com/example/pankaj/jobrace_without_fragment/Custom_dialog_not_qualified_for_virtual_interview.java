package com.example.pankaj.jobrace_without_fragment;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class Custom_dialog_not_qualified_for_virtual_interview  extends Dialog
{
    private Activity activity;
    private Button btn_ok;
    public Custom_dialog_not_qualified_for_virtual_interview(final Activity activity)
    {
        super(activity);
        this.activity=activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_not_qualified_for_virtual_interview);
        btn_ok= (Button) findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Custom_dialog_not_qualified_for_virtual_interview.this.dismiss();
                activity.finish();
            }
        });
    }
}
