package com.example.pankaj.jobrace_without_fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pankaj.jobrace_without_fragment.adapter.Jobs_list_adapter;
import com.example.pankaj.jobrace_without_fragment.data_classes.Job_details_data;
import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class Todays_interview extends AppCompatActivity {

    EditText et_job_search;
    ImageButton btn_job_search;
    RecyclerView rec_view;
    List<Job_details_data> list_of_jobs;
    Jobs_list_adapter ada;
    public ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_interview);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Today's Interview");
        btn_job_search= (ImageButton) findViewById(R.id.btn_search_job);
        et_job_search= (EditText) findViewById(R.id.et_search_job);
        et_job_search.clearFocus();
        rec_view = (RecyclerView) findViewById(R.id.rec_view);
        rec_view.requestFocus();

        btn_job_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_job_search.getText().toString().trim().equals(""))
                    Toast.makeText(Todays_interview.this,"Please enter valid technology.",Toast.LENGTH_LONG).show();
                else {
                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(btn_job_search.getWindowToken(), 0);
                    getJobs(et_job_search.getText().toString().trim());

                }
            }
        });

        //rec_view.stopScroll();
        rec_view.setLayoutManager(new LinearLayoutManager(this));
        list_of_jobs = new ArrayList<>();
        ada = new Jobs_list_adapter(list_of_jobs,this);
        rec_view.setAdapter(ada);
        get_Jobs_From_Server();
    }

    public void get_Jobs_From_Server()
    {
        Shared_preference_data_class sp = new Shared_preference_data_class(this);
        String technologies = sp.getTechnology();
        et_job_search.setText(technologies);
        getJobs(technologies);

    }
    public void getJobs(String technologies)
    {
        if(!Check_connectivity.is_connected(this))
        {
            return;
        }
        list_of_jobs.clear();
        Shared_preference_data_class sp=new Shared_preference_data_class(this);
        String cardType=sp.getCardType();
        if (!Check_connectivity.is_connected(this))
            return;
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.get_todays_jobs(technologies,cardType,list_of_jobs.size());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit.Response<String> response, Retrofit retrofit)
            {
                if(response.code()==200)
                {
                    try {
                        JSONArray ar = new JSONArray(response.body());
                        String result=ar.getString(0);
                        if(result!=null && result.equals("sucess")) {
                            setData(ar);
                            if(list_of_jobs.size()>0)
                                ada.notifyDataSetChanged();
                        }
                        else if(result.equals("fail")) {
                            Toast.makeText(Todays_interview.this, "No interviews available.", Toast.LENGTH_LONG).show();
                            rec_view.removeAllViews();
                        }
                        else if(result.equals("Access Denied"))
                            Toast.makeText(Todays_interview.this,"Access Denied.",Toast.LENGTH_LONG).show();

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(Todays_interview.this,"Not connected with server.",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(Todays_interview.this, "Some error occured", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void setData(JSONArray ar)
    {
        int i=1;
        for(;i<ar.length();i++)
        {
            try {
                ImageView iv;
                JSONObject obj=ar.getJSONObject(i);
                Job_details_data jd=new Job_details_data();
                jd.setJobid(obj.getString("id"));
                jd.setJob_title(obj.getString("JobTitle"));
                jd.setJob_description(obj.getString("JobDescription"));
                jd.setSalary_offer_min(obj.getString("SalaryOfferedMin"));
                jd.setSalary_offer_max(obj.getString("SalaryOfferedMax"));
                jd.setExp_req_min(obj.getString("ExperienceRequiredMin"));
                jd.setExp_req_max(obj.getString("ExperienceRequiredMax"));
                jd.setInterview_location(obj.getString("InterviewLocation"));
                jd.setDate_time_of_interview(obj.getString("DateTimeOfInterview"));
                jd.setApply_before(obj.getString("ApplyBefore"));
                jd.setJob_eligibility(obj.getString("JobEligibility"));
                jd.setIs_active(obj.getString("IsActive"));
                jd.setCreated_date(obj.getString("CreatedDate"));
                jd.setEmployer_email(obj.getString("EmployerEmail"));
                jd.setTechnology(obj.getString("Technology"));
                jd.setSkills(obj.getString("Skills"));
                jd.setCompany_name(obj.getString("CompanyName"));
                list_of_jobs.add(jd);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        pd.dismiss();
        super.onDestroy();

    }
}
