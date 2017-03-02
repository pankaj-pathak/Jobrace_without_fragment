package grid.jobrace.admin.jobrace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import grid.jobrace.admin.jobrace.adapter.Jobs_list_adapter;
import grid.jobrace.admin.jobrace.data_classes.Job_details_data;
import grid.jobrace.admin.jobrace.data_classes.Shared_preference_data_class;
import grid.jobrace.admin.jobrace.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class My_job_list_activity extends AppCompatActivity {

    RecyclerView rec_view;
    List<Job_details_data> list_of_jobs;
    Jobs_list_adapter ada;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Check_connectivity.is_connected(this,false))
        {
            setContentView(R.layout.layout_not_connected_with_internet);
            ((Button)findViewById(R.id.btn_try_again)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Check_connectivity.is_connected(My_job_list_activity.this,false))
                    {
                        Intent it=getIntent();
                        finish();
                        startActivity(it);
                    }
                }
            });
            return;
        }
        setContentView(R.layout.activity_my_job_list_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.toolbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);

        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(true);

        pd = new ProgressDialog(this);
        pd.setMessage("Please Wait");
        pd.setCanceledOnTouchOutside(false);
        rec_view = (RecyclerView) findViewById(R.id.rec_view);
        rec_view.requestFocus();
        rec_view.stopScroll();
        rec_view.setLayoutManager(new LinearLayoutManager(this));
        list_of_jobs = new ArrayList<>();
        ada = new Jobs_list_adapter(list_of_jobs, this);
        rec_view.setAdapter(ada);
        get_Jobs_From_Server();

    }

    public void get_Jobs_From_Server() {
        Shared_preference_data_class sp = new Shared_preference_data_class(My_job_list_activity.this);
        String technologies = sp.getTechnology();
        getJobs(technologies);

    }

    public void getJobs(String technologies) {
        if (!Check_connectivity.is_connected(My_job_list_activity.this)) {
            return;
        }
        list_of_jobs.clear();
        Shared_preference_data_class sp = new Shared_preference_data_class(My_job_list_activity.this);
        String cardType = sp.getCardType();
        if (!Check_connectivity.is_connected(My_job_list_activity.this))
            return;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(My_job_list_activity.this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test = retrofit.create(Custom_interface_test.class);
        Call<String> call = custom_interface_test.get_jobs(technologies, cardType, list_of_jobs.size());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                if (response.code() == 200) {
                    try {
                        JSONArray ar = new JSONArray(response.body());
                        String result = ar.getString(0);
                        if (result != null && result.equals("sucess")) {
                            setData(ar);
                            if (list_of_jobs.size() > 0)
                                ada.notifyDataSetChanged();
                        } else if (result.equals("fail")) {
                            Toast.makeText(My_job_list_activity.this, "No Jobs available.", Toast.LENGTH_LONG).show();
                            rec_view.removeAllViews();
                        } else if (result.equals("Access Denied"))
                            Toast.makeText(My_job_list_activity.this, "Access Denied.", Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(My_job_list_activity.this,R.string.server_error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(My_job_list_activity.this, R.string.server_error, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setData(JSONArray ar) {
        int i = 1;
        for (; i < ar.length(); i++) {
            try {
                JSONObject obj = ar.getJSONObject(i);
                Job_details_data jd = new Job_details_data();
                jd.setJobid(obj.getString("Id"));
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
}
