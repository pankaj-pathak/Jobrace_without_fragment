package com.example.pankaj.jobrace_without_fragment;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


import com.example.pankaj.jobrace_without_fragment.data_classes.Check_credentials;

import java.util.ArrayList;
import java.util.List;

public class Check_required_detail_first_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_required_detail_first_activity);
        checkPermissions();
    }
    public void checkPermissions()
    {
        int currentapiVersion = Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.M){
            List<String> permissions=new ArrayList<>();
            permissions.add(Manifest.permission.INTERNET);
            permissions.add(Manifest.permission.ACCESS_NETWORK_STATE);
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.READ_PHONE_STATE);
            permissions.add(Manifest.permission.INSTALL_SHORTCUT);
            permissions= Check_credentials.hasGrantedPermissions(this,permissions);
            if(permissions.size()==0)
            {
                Intent it=new Intent(this,Homepage.class);
                startActivity(it);
                finish();
            }
            else
            {
                Get_permissions_activity.permissions=permissions;
                Intent it=new Intent(this,Get_permissions_activity.class);
                startActivity(it);
                finish();
            }
        } else{
            Intent it=new Intent(this,Homepage.class);
            startActivity(it);
            finish();
        }

    }

}
