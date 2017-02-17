package com.example.pankaj.jobrace_without_fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pankaj.jobrace_without_fragment.data_classes.Check_credentials;
import com.example.pankaj.jobrace_without_fragment.data_classes.Job_details_data;
import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class View_job_details_activity extends AppCompatActivity
{

    Toolbar toolbar;
    TextView tv_job_title;
    TextView tv_company_name;
    TextView tv_experience_required;
    TextView tv_interview_location;
    TextView tv_salary_offered;
    TextView tv_job_created_date;
    TextView tv_job_description;
    TextView tv_interview_date;
    TextView tv_apply_before;
    TextView tv_job_eligibility;
    TextView tv_employer_email;
    TextView tv_is_active;
    ImageView iv_company_logo;

    public static Job_details_data jd;

    ScrollView sv;
    Button btn_apply_for_job;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_job_details_activity);
        getSupportActionBar().setTitle("Job Description");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sv= (ScrollView) findViewById(R.id.scroll_view);
        sv.setVerticalScrollBarEnabled(false);
        sv.setHorizontalScrollBarEnabled(false);

        btn_apply_for_job=(Button)findViewById(R.id.btn_apply_for_job);

        tv_job_title=(TextView)findViewById(R.id.tv_job_title);
        tv_company_name=(TextView)findViewById(R.id.tv_company_name);
        tv_experience_required=(TextView)findViewById(R.id.tv_experience_required);
        tv_interview_location=(TextView)findViewById(R.id.tv_interview_location);
        tv_salary_offered=(TextView)findViewById(R.id.tv_salary_offered);
        tv_job_created_date=(TextView)findViewById(R.id.tv_job_created_date);
        tv_job_description=(TextView)findViewById(R.id.tv_job_description);
        tv_interview_date=(TextView)findViewById(R.id.tv_interview_date);
        tv_apply_before=(TextView)findViewById(R.id.tv_apply_before);
        tv_job_eligibility=(TextView)findViewById(R.id.tv_job_eligibility);
        tv_employer_email=(TextView)findViewById(R.id.tv_employer_email);
        tv_is_active=(TextView)findViewById(R.id.tv_is_active);
        iv_company_logo= (ImageView) findViewById(R.id.iv_company_logo);
        tv_job_title.setText(jd.getJob_title());
        tv_company_name.setText(jd.getCompany_name());
        tv_experience_required.setText(jd.getExp_req_min()+"-"+jd.getExp_req_max());
        tv_interview_location.setText(jd.getInterview_location());
        tv_salary_offered.setText(jd.getSalary_offer_min()+"-"+jd.getExp_req_max());
        tv_job_created_date.setText(jd.getCreated_date());
        tv_job_description.setText(jd.getJob_description());
        tv_interview_date.setText(jd.getDate_time_of_interview());
        tv_apply_before.setText(jd.getApply_before());
        tv_job_eligibility.setText(jd.getJob_eligibility());
        tv_employer_email.setText(jd.getEmployer_email());
        if(jd.getIs_active()!=null && !jd.getIs_active().trim().equals("N"))
            tv_is_active.setText("Active");
        else {
            tv_is_active.setText("Not Active");
            btn_apply_for_job.setEnabled(false);
        }
        btn_apply_for_job.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(Check_credentials.isEligibleToGiveVirtualInterview(View_job_details_activity.this))
                {
                    getTotalInterviewsGiven();

                }
            }
        });

        String imageurl="";
        Picasso.with(this)
                .load((imageurl=new Shared_preference_data_class(this).getUrl()).substring(0,imageurl.indexOf("jobraceapi"))+"company_logo/"+jd.getCompany_name()+".png")
                .placeholder(R.drawable.jobs_logo)
                .error(R.drawable.jobrace_icon)
                .resize(30, 30)
                .rotate(90)
                .into(iv_company_logo);
    }

    public void getTotalInterviewsGiven() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())//.addConverterFactory(GsonConverterFactory.create())
                .build();

        Custom_interface_test custom_interface_test = retrofit.create(Custom_interface_test.class);
        Call<String> call = custom_interface_test.getTotalVirtualInterviewsGiven(new Shared_preference_data_class(this).getUserEmail());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.code() == 200) {
                    try {
                        JSONArray ar = new JSONArray(response.body());
                        if (ar.getString(0).equals("sucess")) {
                            JSONObject obj = ar.getJSONObject(1);
                            int total_interviews = Integer.parseInt(obj.getString("TotalInterviews").trim());
                            if (total_interviews >= new Shared_preference_data_class(View_job_details_activity.this).getTotalInterviews()) {
                                Toast.makeText(View_job_details_activity.this, "Your interview limit is expired. Please recharge your card for more interviews.", Toast.LENGTH_LONG).show();
                            }
                            else
                                startVirtualInterview();
                        }
                        else{
                            Toast.makeText(View_job_details_activity.this, "Not connected with server", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(View_job_details_activity.this, "Unable to connect with server.", Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(View_job_details_activity.this, "Some error occured", Toast.LENGTH_LONG).show();
            }

            public void startVirtualInterview()
            {
                Intent it=new Intent(View_job_details_activity.this,Virtual_interview_start_page.class);
                Bundle bd=new Bundle();
                String tech=jd.getTechnology();
                String skills=jd.getSkills();
                    /*
                    if(skills==null || skills.trim().equals(""))
                    {
                        Toast.makeText(View_job_details_activity.this,"Recruiter has not defined any skills.",Toast.LENGTH_LONG).show();
                        return;
                    }
                    */
                bd.putString("technology",tech);
                bd.putString("skills",skills);
                bd.putString("jobid",jd.getJobid());
                bd.putString("employer_email",jd.getEmployer_email());
                it.putExtras(bd);

                startActivity(it);
            }
        });


    }
        }

