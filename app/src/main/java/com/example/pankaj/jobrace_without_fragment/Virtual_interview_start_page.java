package com.example.pankaj.jobrace_without_fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Virtual_interview_start_page extends AppCompatActivity {
    Button btn_start_test;
    ArrayList<String> list_questions_id;
    private String technology_name;
    private String skills;
    private ProgressDialog pd;
    private String job_id;
    private String employer_email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Check_connectivity.is_connected(this,false))
        {
            setContentView(R.layout.layout_not_connected_with_internet);
            ((Button)findViewById(R.id.btn_try_again)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Check_connectivity.is_connected(Virtual_interview_start_page.this,false))
                    {
                        Intent it=getIntent();
                        finish();
                        startActivity(it);
                    }
                }
            });
            return;
        }
        setContentView(R.layout.activity_virtual_interview_start_page);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.toolbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);
        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(true);


        btn_start_test= (Button) findViewById(R.id.btn_start_test);
        Bundle bd=getIntent().getExtras();
        technology_name=bd.getString("technology");
        skills=bd.getString("skills");
        job_id=bd.getString("jobid");
        employer_email=bd.getString("employer_email");
        ((TextView)findViewById(R.id.tv_technology)).setText(technology_name+" Virtual Interview");
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        btn_start_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Check_connectivity.is_connected(Virtual_interview_start_page.this))
                    return;
                sendVirtualInterviewInfo();
            }
        });
        btn_start_test.setEnabled(false);
        list_questions_id=new ArrayList<>();
        getQuestionsId();
    }
   //    skills=skills.substring(0,skills.indexOf("Aptitude"))+skills.substring(skills.substring(skills.indexOf("Aptitude")).indexOf(",")+1);
    public void getQuestionsId()
    {
        if(!Check_connectivity.is_connected(this))
            return;
        Shared_preference_data_class sp=new Shared_preference_data_class(this);
        String experience=sp.getExperience();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.getQuestionsIdForVirtualInterview(skills,experience);
        pd.show();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.code()==200) {
                    try {
                        list_questions_id.clear();
                        JSONArray ar = new JSONArray(response.body());
                        String result=ar.getString(0);
                        if(result!=null && result.equals("sucess"))
                        {
                            JSONObject obj=ar.getJSONObject(1);
                            for (int i = 0; i < obj.length(); i++) {
                                list_questions_id.add(obj.getString("qus"+(i)));
                            }
                            if(list_questions_id.size()>0)
                                btn_start_test.setEnabled(true);
                            else
                            {
                                Toast.makeText(Virtual_interview_start_page.this,"Complete questions are not available for this technology..",Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                        else if(result.equals("fail"))
                        {
                            Toast.makeText(Virtual_interview_start_page.this,"No questions available",Toast.LENGTH_LONG).show();
                            //notSufficientQuestions();
                        }
                        else if(result.equals("Access Denied"))
                        {
                            Toast.makeText(Virtual_interview_start_page.this,"Access Denied.",Toast.LENGTH_LONG).show();
                           // notSufficientQuestions();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(Virtual_interview_start_page.this,"Some error occured.",Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(Virtual_interview_start_page.this,"Unable to connect with server.",Toast.LENGTH_LONG).show();
                    //notSufficientQuestions();
                }
                pd.hide();
            }

            @Override
            public void onFailure(Throwable t) {
                pd.hide();
                Toast.makeText(Virtual_interview_start_page.this,"Some problem occured.",Toast.LENGTH_LONG).show();
                }
        });
    }
    public void sendVirtualInterviewInfo()
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.startVirtualInterview(new Shared_preference_data_class(this).getUserEmail(),job_id,technology_name);
        pd.show();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.code() == 200)
                {
                    try {
                        JSONArray ar=new JSONArray(response.body());
                        String result=ar.getString(0);
                        if(result!=null && result.equals("sucess"))
                        {
                            Intent it=new Intent(Virtual_interview_start_page.this, Online_test.class);
                            Bundle bd=new Bundle();
                            bd.putStringArrayList("questions_list",list_questions_id);
                            bd.putBoolean("isVirtualInterview",true);
                            bd.putString("job_id",job_id);
                            bd.putString("technology",technology_name);
                            bd.putString("employer_email",employer_email);
                            it.putExtras(bd);
                            Date d=new Date();
                            String today=(d.getDate()<10?"0"+d.getDate():d.getDate())+"-"+(d.getMonth()<9?"0"+(d.getMonth()+1):(d.getMonth()+1))+"-"+(1900+d.getYear());
                            Shared_preference_data_class sp=new Shared_preference_data_class(Virtual_interview_start_page.this);
                            sp.initializeEditor();
                            sp.setLastVirtualInterviewAppliedDate(today);
                            sp.commitChanges();
                            startActivity(it);
                            finish();
                        }
                        else if(result.equals("fail"))
                            Toast.makeText(Virtual_interview_start_page.this,"Some error occured...",Toast.LENGTH_LONG).show();
                        else if(result.equals("Access Denied"))
                            Toast.makeText(Virtual_interview_start_page.this,"Access Denied.",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(Virtual_interview_start_page.this,"Unable to connect with server",Toast.LENGTH_LONG).show();
                }
                pd.hide();
            }

            @Override
            public void onFailure(Throwable t) {
                pd.hide();
                Toast.makeText(Virtual_interview_start_page.this,"Some error occured",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }
/*
    public void notSufficientQuestions()
    {

        Get_technology_list_fragment get_technology_list_fragment=new Get_technology_list_fragment();
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_home_page,get_technology_list_fragment);
        transaction.commit();
    }*/
}
