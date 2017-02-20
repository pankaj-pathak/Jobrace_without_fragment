package com.example.pankaj.jobrace_without_fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pankaj.jobrace_without_fragment.data_classes.Check_credentials;
import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Resume_builder_activity extends AppCompatActivity {

    EditText et_full_name;
    EditText et_email;
    EditText et_phone;
    EditText et_summary;
    EditText et_educational_detail;
    EditText et_certification;
    EditText et_projects;
    EditText et_work_experience;
    EditText et_other_details;
    EditText et_personal_details;
    Button btn_send;
    String email;
    private ProgressDialog pd;
    String IS_UPDATE="0";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Check_connectivity.is_connected(this,false))
        {
            setContentView(R.layout.layout_not_connected_with_internet);
            ((Button)findViewById(R.id.btn_try_again)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Check_connectivity.is_connected(Resume_builder_activity.this,false))
                    {
                        Intent it=getIntent();
                        finish();
                        startActivity(it);
                    }
                }
            });
            return;
        }
        setContentView(R.layout.activity_resume_builder_activity);
        getSupportActionBar().setTitle("Resume");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pd=new ProgressDialog(Resume_builder_activity.this);
        pd.setCancelable(false);
        et_full_name= (EditText) findViewById(R.id.et_full_name);
        et_email=(EditText)findViewById(R.id.et_email);
        et_phone=(EditText)findViewById(R.id.et_phone);
        et_summary=(EditText)findViewById(R.id.et_summary);
        et_educational_detail=(EditText)findViewById(R.id.et_educational_detail);
        et_certification=(EditText)findViewById(R.id.et_certification);
        et_projects=(EditText)findViewById(R.id.et_projects);
        et_work_experience=(EditText)findViewById(R.id.et_work_experience);
        et_other_details=(EditText)findViewById(R.id.et_other_details);
        et_personal_details=(EditText)findViewById(R.id.et_personal_details);

        Shared_preference_data_class sp=new Shared_preference_data_class(this);
        et_email.setText(email=sp.getUserEmail());
        et_email.setEnabled(false);
        et_full_name.setText(sp.getUserName());
        et_phone.setText(sp.getContactNo());
        btn_send=(Button)findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_validation();
            }
        });
        getResumeDetail();

    }
    public void getResumeDetail()
    {
        if (Check_connectivity.is_connected(this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(new Shared_preference_data_class(this).getUrl())
                    .addConverterFactory(new ToStringConverterFactory())
                    .build();
            Custom_interface_test custom_interface_test = retrofit.create(Custom_interface_test.class);
            Call<String> call = custom_interface_test.getResumeDetail(email);
            pd.show();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Response<String> response, Retrofit retrofit) {
                    if (response.code() == 200) {
                        try {
                            Log.d("response===","response==================="+response.body());
                            JSONArray ar = new JSONArray(response.body());
                            String result = ar.getString(0);
                            if (result != null && result.equals("sucess")) {
                                setData(ar.getJSONObject(1));
                                IS_UPDATE="1";
                            }
                            btn_send.setEnabled(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(Resume_builder_activity.this, "Unable to connect with server.Please try again.", Toast.LENGTH_LONG).show();
                    }
                    pd.hide();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(Resume_builder_activity.this, "Unable to connect with server.Please try again.", Toast.LENGTH_LONG).show();
                    pd.hide();
                }
            });
        }
    }
    public void setData(JSONObject obj)
    {
        try
        {
        et_full_name.setText(obj.getString("Name"));
        et_phone.setText(obj.getString("ContactNo"));
        et_summary.setText(obj.getString("Summary"));
        et_educational_detail.setText(obj.getString("Educationaldetails"));
        et_certification.setText(obj.getString("Certification"));
        et_projects.setText(obj.getString("Projects"));
        et_work_experience.setText(obj.getString("Workexperience"));
        et_other_details.setText(obj.getString("Otherdetail"));
        et_personal_details.setText(obj.getString("Personaldetails"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void check_validation()
    {
        if (et_full_name.getText().toString().trim().length() == 0) {
            et_full_name.setError("Name cannot be Blank");
            return;
        } else if (et_email.getText().toString().trim().length() == 0) {
            et_email.setError("Email cannot be Blank");
            return;
        } else if (!isValidEmail(et_email.getText().toString())) {
            et_email.setError("Email is no valid");
            return;
        } else if (et_phone.getText().toString().trim().length() == 0) {
            et_phone.setError("Phone cannot be Blank");
            return;
        } else if (et_phone.getText().toString().trim().length() != 10) {
            et_phone.setError("Phone should be of 10 digits");
            return;
        } else if (et_phone.getText().toString().trim().length() != 0) {
            String number = et_phone.getText().toString().trim();
            int k = number.charAt(0);
            if (k > 57 || k < 55) {
                et_phone.setError("Phone should start with 7,8,9");
                return;
            }
        }
        if (et_summary.getText().toString().trim().length() == 0) {
            et_summary.setError("Summary cannot be Blank");
            return;
        } else if (et_educational_detail.getText().toString().trim().length() == 0) {
            et_educational_detail.setError("Educational details cannot be Blank");
            return;
        } else if (et_projects.getText().toString().trim().length() == 0) {
                et_projects.setError("Projects details cannot be Blank");
                return;
        } else if (et_work_experience.getText().toString().trim().length() == 0) {
            et_work_experience.setError("Work experience details cannot be Blank");
            return;
        } else if (et_personal_details.getText().toString().trim().length() == 0) {
            et_personal_details.setError("Personal details cannot be Blank");
            return;
        }
        sendResumeToServer();
    }
    private boolean isValidEmail(String e_email) {
        if (!e_email.contains("@") || !e_email.contains(".")) {
            return false;
        } else if (e_email.lastIndexOf("@") > e_email.lastIndexOf("."))
            return false;
        return true;
    }
    public void sendResumeToServer()
    {
        if (Check_connectivity.is_connected(this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(new Shared_preference_data_class(this).getUrl())
                    .addConverterFactory(new ToStringConverterFactory())
                    .build();
            Custom_interface_test custom_interface_test = retrofit.create(Custom_interface_test.class);
            Call<String> call = custom_interface_test.updateResume(
                    et_full_name.getText().toString().trim()
                    ,email
                    ,et_phone.getText().toString().trim()
                    ,et_summary.getText().toString().trim()
                    ,et_educational_detail.getText().toString().trim()
                    ,et_certification.getText().toString().trim()
                    ,et_projects.getText().toString().trim()
                    ,et_work_experience.getText().toString().trim()
                    ,et_other_details.getText().toString().trim()
                    ,et_personal_details.getText().toString().trim()
                    ,IS_UPDATE);

            pd.show();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Response<String> response, Retrofit retrofit) {
                    if (response.code() == 200) {
                        try {
                            JSONArray ar = new JSONArray(response.body());
                            String result = ar.getString(0);
                            if (result != null && result.equals("sucess")) {
                                Toast.makeText(Resume_builder_activity.this, "Resume updated successfully.", Toast.LENGTH_LONG).show();
                            }
                            else
                                Toast.makeText(Resume_builder_activity.this, "Resume not updated please try again.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(Resume_builder_activity.this, "Unable to connect with server.Please try again.", Toast.LENGTH_LONG).show();
                    }
                    pd.hide();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(Resume_builder_activity.this, "Unable to connect with server.Please try again.", Toast.LENGTH_LONG).show();
                    pd.hide();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if(pd!=null)
            pd.dismiss();
        super.onDestroy();
    }
}
