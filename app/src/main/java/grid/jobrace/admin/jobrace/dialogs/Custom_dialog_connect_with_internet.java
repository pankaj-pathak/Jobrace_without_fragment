package grid.jobrace.admin.jobrace.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import grid.jobrace.admin.jobrace.R;

/**
 * Created by Pankaj on 1/7/2017.
 */

public class Custom_dialog_connect_with_internet extends Dialog
{
    private String message;
    public Custom_dialog_connect_with_internet(final Activity activity, final String message)
    {
        super(activity);
        this.message=message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_connect_with_internet);
        TextView tv_message=(TextView) findViewById(R.id.tv_message);
        tv_message.setText(message);
        findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Custom_dialog_connect_with_internet.this.dismiss();

            }
        });
        findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Custom_dialog_connect_with_internet.this.dismiss();
            }
        });
    }
}
