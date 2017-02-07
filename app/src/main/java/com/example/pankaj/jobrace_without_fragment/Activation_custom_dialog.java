package com.example.pankaj.jobrace_without_fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Pankaj on 11/24/2016.
 */

public class Activation_custom_dialog extends Dialog
{
    Activity activity;
    Button btn_cancel;
    Button btn_activate;
    EditText et_activation_key;
    TextView tv_show_wrong_key;

    public Activation_custom_dialog(final Activity activity)
    {
        super(activity);
        this.activity=activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activation_custom_dialog_layout);

        tv_show_wrong_key= (TextView) findViewById(R.id.tv_show_wrong_key);
        btn_activate= (Button) findViewById(R.id.btn_activate);
        btn_cancel= (Button) findViewById(R.id.btn_cancel);
        et_activation_key= (EditText) findViewById(R.id.et_activation_key);
        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                //Toast.makeText(activity,,Toast.LENGTH_LONG).show();
                isUserActivated(et_activation_key.getText().toString());
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Activation_custom_dialog.this.dismiss();
            }
        });

    }
    public void isUserActivated(final String activation_key)
    {
        Shared_preference_data_class sp=new Shared_preference_data_class(activity);
        String email=sp.getUserEmail();
/*
        Converter<JSONObject,String> converter=new Converter<JSONObject, String>() {
            @Override
            public String convert(JSONObject value) throws IOException {
                String data=null;
                try {
                    data=value.getString("result");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return data;
            }
        };
        retrofit.Converter.Factory factory=new retrofit.Converter.Factory() {
            @Override
            public retrofit.Converter<ResponseBody, ?> fromResponseBody(Type type, Annotation[] annotations) {
                return super.fromResponseBody(type, annotations);
            }
        };
        */
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(activity).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.verify_user(email,activation_key);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit)
            {
                if(response.code()==200)
                {
                    try {
                        JSONObject obj=new JSONObject(response.body());
                        if(obj.getString("result").equals("sucess"))
                        {
                            Shared_preference_data_class sp=new Shared_preference_data_class(activity);
                            sp.initializeEditor();
                            sp.setActivationKey(activation_key);
                            sp.commitChanges();
                            Intent it=new Intent(activity,Homepage.class);
                            activity.startActivity(it);
                            Activation_custom_dialog.this.dismiss();

                        }
                        else
                        {
                            tv_show_wrong_key.setText("Please enter a valid key.");
                            et_activation_key.setText("");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                    Log.d("responce===","resoponce== is no correct==================================="+response.code());

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });
    }
    private class Responseclass
    {
        String result;
    }
}
