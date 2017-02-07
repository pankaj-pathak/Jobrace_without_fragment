package com.example.pankaj.jobrace_without_fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.pankaj.jobrace_without_fragment.data_classes.Check_credentials;

import java.util.List;

public class Get_permissions_activity extends AppCompatActivity {

    public static List<String> permissions;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_permissions_activity);
        ((Button)findViewById(R.id.btn_next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Check_credentials.askForPermissions(Get_permissions_activity.this,permissions);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode)
        {
            case 123:
            {
                if (grantResults.length > 0)
                {
                    int k=0;
                    int temp=0;
                    while(k<permissions.length)
                    {
                        if(grantResults[k] != PackageManager.PERMISSION_GRANTED)
                        {
                            Toast.makeText(this,"Allow all permissions to run this application",Toast.LENGTH_LONG).show();
                            temp=1;
                            finish();
                            break;
                        }
                        k++;
                    }
                    if(temp==0)
                    {
                        Intent it=new Intent(this,Homepage.class);
                        startActivity(it);
                        finish();
                    }
                }
                else
                {
                    Toast.makeText(this,"Allow all permissions to run this application",Toast.LENGTH_LONG).show();
                    finish();
                }
                return;
            }
        }
    }
}
