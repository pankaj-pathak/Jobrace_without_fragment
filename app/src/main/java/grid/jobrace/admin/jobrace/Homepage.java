package grid.jobrace.admin.jobrace;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import grid.jobrace.admin.jobrace.adapter.Jobs_list_adapter;
import grid.jobrace.admin.jobrace.data_classes.Centeral_class;
import grid.jobrace.admin.jobrace.data_classes.Job_details_data;
import grid.jobrace.admin.jobrace.data_classes.Shared_preference_data_class;
import grid.jobrace.admin.jobrace.dialogs.Custom_dialog_for_message;
import grid.jobrace.admin.jobrace.fragments.Nevigation_drawer_fragment;
import grid.jobrace.admin.jobrace.interfaces.Custom_interface_test;
import grid.jobrace.admin.jobrace.services.DownloadApkReceiver;
import grid.jobrace.admin.jobrace.services.Download_apk_service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class Homepage extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    static int exit=0;
    static int isLoggegIn=0;
    public ProgressDialog pd;
    private DownloadApkReceiver receiver;
    public DownloadManager downloadManager;
    public long downloadReference;
    private boolean isRegistered=false;
    public static Custom_dialog_for_message custom_dialog_for_message=null;

    EditText et_job_search;
    ImageButton btn_job_search;
    RecyclerView rec_view;
    List<Job_details_data> list_of_jobs;
    Jobs_list_adapter ada;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);
       if(!Check_connectivity.is_connected(this,false))
        {
            setContentView(R.layout.layout_not_connected_with_internet);
            ((Button)findViewById(R.id.btn_try_again)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Check_connectivity.is_connected(Homepage.this,false))
                    {
                        Intent it=getIntent();
                        finish();
                        startActivity(it);
                    }
                }
            });
            return;
        }


        if(check_Currently_Logged_In()) {
            setContentView(R.layout.activity_homepage);
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

            LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.toolbar_layout, null);
            ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT,
                    ActionBar.LayoutParams.MATCH_PARENT,
                    Gravity.LEFT);

            actionBar.setCustomView(actionBarLayout, params);
            actionBar.setDisplayHomeAsUpEnabled(true);



            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            Nevigation_drawer_fragment nevigation_drawer_fragment = (Nevigation_drawer_fragment) getSupportFragmentManager().findFragmentById(R.id.nevigation_drawer_fragment);
            nevigation_drawer_fragment.setUp(drawerLayout,toolbar);
            Shared_preference_data_class sp=new Shared_preference_data_class(this);
            sp.initializeEditor();
            sp.setActivationKey("");
            Date d=new Date();
            String today=(d.getDate()<10?"0"+d.getDate():d.getDate())+"-"+(d.getMonth()<9?"0"+(d.getMonth()+1):(d.getMonth()+1))+"-"+(1900+d.getYear());
            if(!today.equals(sp.getLastJobViewdDate())) {
                sp.setLastJobViewedDate(today);
                sp.setNoOfJobsOpened("0");
            }
            sp.commitChanges();


            btn_job_search= (ImageButton) findViewById(R.id.btn_search_job);
            et_job_search= (EditText) findViewById(R.id.et_search_job);
            et_job_search.clearFocus();
            rec_view = (RecyclerView) findViewById(R.id.rec_view);
            rec_view.requestFocus();

            btn_job_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(et_job_search.getText().toString().trim().equals(""))
                        Toast.makeText(Homepage.this,"Please enter valid technology.",Toast.LENGTH_LONG).show();
                    else {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(btn_job_search.getWindowToken(), 0);
                        getJobs(et_job_search.getText().toString().trim());

                    }
                }
            });
            rec_view.setLayoutManager(new LinearLayoutManager(this));
            list_of_jobs = new ArrayList<>();
            ada = new Jobs_list_adapter(list_of_jobs,this);
            rec_view.setAdapter(ada);
            get_Jobs_From_Server();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean("isReloaded",true);
        super.onSaveInstanceState(outState);
    }
    public boolean check_Currently_Logged_In()
    {
        boolean result=false;
        Shared_preference_data_class sp=new Shared_preference_data_class(Homepage.this);
        if(sp.getUserEmail().trim().equals("") || sp.getUserPassword().trim().equals("") || (!sp.getIsAutologin() && isLoggegIn==0))
        {
            getUrl();
            Intent it=new Intent(this,Login.class);
            startActivity(it);
            finish();
        }
        else
        {
            getUrl();
            getStudentData(sp.getUserEmail(),sp.getUserPassword());
            result= true;
        }
        return  result;
    }

    public boolean getUrl()
    {

        if(Check_connectivity.is_connected(Homepage.this))
        {
            Retrofit retrofit=new Retrofit.Builder()
                    .baseUrl(new Shared_preference_data_class(this).getUrl())
                    .addConverterFactory(new ToStringConverterFactory())
                    .build();
            Shared_preference_data_class sp=new Shared_preference_data_class(Homepage.this);
            Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
            Call<String> call=custom_interface_test.getUrl();
            pd.show();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                    if(response.code()==200)
                    {
                        try {
                            JSONArray ar=new JSONArray(response.body());
                            String result=ar.getString(0);
                            if(result.equals("sucess")) {
                                JSONObject obj =ar.getJSONObject(1);
                                String res = obj.getString("URL");
                                Shared_preference_data_class sp = new Shared_preference_data_class(Homepage.this);
                                sp.initializeEditor();
                                sp.setUrl("http://" + res + "/jobraceapi/");
                                sp.commitChanges();
                                getMenuList();
                            }
                            else if(result.equals("fail"))
                                Toast.makeText(Homepage.this,R.string.server_error,Toast.LENGTH_LONG).show();
                            else if(result.equals("Access Denied"))
                                Toast.makeText(Homepage.this,R.string.server_error,Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    pd.hide();
                }

                @Override
                public void onFailure(Throwable t) {
                    t.printStackTrace();
                    Toast.makeText(Homepage.this,R.string.server_error,Toast.LENGTH_LONG).show();
                    pd.hide();
                }
            });
        }
        return false;
    }
    public void getMenuList()
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(Homepage.this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.getMenuList();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                JSONArray ar= null;
                try {
                    ar = new JSONArray(response.body());
                    String result=ar.getString(0);
                    if(result!=null && result.equals("sucess")) {
                        ArrayList<String> list = new ArrayList<String>();
                        for (int i = 1; i < ar.length(); i++) {
                            try {
                                JSONObject obj = ar.getJSONObject(i);
                                list.add(obj.getString("Munulist").trim());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        Centeral_class.menu_list.clear();
                        Centeral_class.menu_list.addAll(list);
                        if (Centeral_class.ada != null)
                            Centeral_class.ada.notifyDataSetChanged();

                    }
                    else if(result.equals("fail"))
                        Toast.makeText(Homepage.this,R.string.server_error,Toast.LENGTH_LONG).show();
                    else if(result.equals("Access Denied"))
                        Toast.makeText(Homepage.this,"Access Denied.",Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(Homepage.this,R.string.server_error,Toast.LENGTH_LONG).show();
            }
        });

    }
    public void getStudentData(String user_email,String password)
    {
        if(Check_connectivity.is_connected(Homepage.this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(new Shared_preference_data_class(this).getUrl())
                    .addConverterFactory(new ToStringConverterFactory())
                    .build();
            Custom_interface_test custom_interface_test = retrofit.create(Custom_interface_test.class);
            Call<String> call = custom_interface_test.getCandidateInfo(user_email,password);
            pd.show();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                    if(response.code()==200)
                    {
                        try {
                            JSONArray ar= new JSONArray(response.body());
                            String result=ar.getString(0);
                            if(result!=null && result.equals("sucess")) {
                                JSONObject obj = ar.getJSONObject(1);
                                Shared_preference_data_class sp = new Shared_preference_data_class(Homepage.this);
                                sp.initializeEditor();
                                sp.setTechnology(obj.getString("Technology"));
                                sp.setExperience(obj.getString("Experience"));
                                sp.setUserName(obj.getString("FullName"));
                                sp.setContactNo(obj.getString("ContactNo"));
                                sp.setPremiumEndDate(obj.getString("PremiumEndtDate"));
                                String s=obj.getString("TotalInterviews");
                                try {sp.setTotalInterviews(Integer.parseInt(s.trim()));}catch(Exception e){sp.setTotalInterviews(0);}
                                String ct=obj.getString("CardType");
                                String imei_no=obj.getString("IMEINumber");
                                if(!imei_no.equals(((TelephonyManager) getSystemService(TELEPHONY_SERVICE)).getDeviceId()))
                                {
                                    sp.setIsAutoLogin(false);
                                    Intent it=new Intent(Homepage.this,Login.class);
                                    startActivity(it);
                                    finish();
                                }
                                sp.setCardType(ct);
                                sp.commitChanges();
                            }
                            else if(result.equals("fail"))
                                Toast.makeText(Homepage.this,"You are not registered.",Toast.LENGTH_LONG).show();
                            else if(result.equals("Access Denied"))
                                Toast.makeText(Homepage.this,"Access Denied.",Toast.LENGTH_LONG).show();

                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(Homepage.this,R.string.server_error,Toast.LENGTH_LONG).show();
                    }
                    pd.hide();
                }
                @Override
                public void onFailure(Throwable t) {
                    pd.hide();
                    Toast.makeText(Homepage.this,R.string.server_error,Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void checkNewVersionAvailable(String version,String apk_url)
    {
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String current_version = pInfo.versionName;
        if(version!=null && !version.trim().equals("") && !current_version.trim().equals(version.trim()))
        {
            getNewApk(apk_url);
        }
    }
    public void getNewApk(String url)
    {
        IntentFilter filter = new IntentFilter(DownloadApkReceiver.PROCESS_RESPONSE);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new DownloadApkReceiver(this,url);
        registerReceiver(receiver, filter);

        filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(downloadReceiver, filter);
        isRegistered=true;
        if(Check_connectivity.is_connected(this))
        {
            Intent msgIntent = new Intent(this, Download_apk_service.class);
            msgIntent.putExtra(Download_apk_service.REQUEST_STRING, "http://demo.mysamplecode.com/Servlets_JSP/CheckAppVersion");
            startService(msgIntent);
        }
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if(downloadReference == referenceId){
                pd.hide();
                //Log.v("complete", "Downloading of the new app version complete");
                Intent installIntent = new Intent(Intent.ACTION_VIEW);
                installIntent.setDataAndType(downloadManager.getUriForDownloadedFile(downloadReference),
                        "application/vnd.android.package-archive");
                installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(installIntent);
            }

        }
    };
    public void changeFragment(String value)
    {
        if(value.equals("Job Search"))
        {
            /*
            Jobs_list_fragment fragment = new Jobs_list_fragment();
            setFragment(fragment);
            */
            drawerLayout.closeDrawers();
            //Intent it=new Intent(this,Homepage.class);
            //startActivity(it);
        }
        else if(value.equals("My Job"))
        {
            drawerLayout.closeDrawers();
            Intent it=new Intent(this,My_job_list_activity.class);
            startActivity(it);
        }
        else if(value.equals("Test My Skills"))
        {
            drawerLayout.closeDrawers();
            Intent it=new Intent(this,Get_technology_list_activity.class);
            //Intent it=new Intent(this,Result_activity.class);
            startActivity(it);
        }

        else if(value.equals("Settings"))
        {
            drawerLayout.closeDrawers();
            Intent it=new Intent(this,Setting_activity.class);
            startActivity(it);
        }
        else if(value.trim().equals("Interview Questions"))
        {
            drawerLayout.closeDrawers();
            Intent it=new Intent(this,Interview_questions.class);
            startActivity(it);
        }
        else if(value.trim().equals("My Performance"))
        {
            drawerLayout.closeDrawers();
            Intent it=new Intent(this,PerformanceDetailActivity.class);
            startActivity(it);
        }
        else if(value.trim().equals("Today's Interview"))
        {
            drawerLayout.closeDrawers();
            Intent it=new Intent(this,Todays_interview.class);
            startActivity(it);
        }

        else if(value.trim().equals("My Resume"))
        {
            drawerLayout.closeDrawers();
            Intent it=new Intent(this,Resume_builder_activity.class);
            startActivity(it);
        }
        else
        {
            custom_dialog_for_message=new Custom_dialog_for_message(Homepage.this,"You are not subscribed for this service.Upgrade your card to get benifit of this service.");
            custom_dialog_for_message.show();
        }

    }
    @Override
    public void onBackPressed() {

        exit++;
        if(exit==1) {
            Toast.makeText(getApplicationContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
            Handler handler = new Handler();

            Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    exit=0;
                }
            };
            handler.postDelayed(runnable,3000);
        }
        else if(exit==2)
            super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onPause();
        /*
        if(changeFragment==1)
        {
            changeFragment=0;
            Result_fragment result_fragment=new Result_fragment();
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_home_page,result_fragment);
            transaction.commit();
        }
        if(changeFragment==2)
        {
            changeFragment=0;
            Jobs_list_fragment jobs_list_fragment=new Jobs_list_fragment();
            FragmentManager fragmentManager=getSupportFragmentManager();
            FragmentTransaction transaction=fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_home_page,jobs_list_fragment);
            transaction.commit();
        }
        */
    }

    @Override
    public void onDestroy() {
        if(DownloadApkReceiver.custom_dialog_new_apk_available!=null)
            DownloadApkReceiver.custom_dialog_new_apk_available.dismiss();
        if(receiver!=null && isRegistered)
            this.unregisterReceiver(receiver);
        if(downloadReceiver!=null && isRegistered)
            this.unregisterReceiver(downloadReceiver);
        pd.dismiss();
        if(custom_dialog_for_message!=null)
            custom_dialog_for_message.dismiss();
        super.onDestroy();
    }



    public void get_Jobs_From_Server()
    {
        Shared_preference_data_class sp = new Shared_preference_data_class(this);
        String technologies = sp.getTechnology();
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
        Call<String> call=custom_interface_test.get_jobs(technologies,cardType,list_of_jobs.size());
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
                            Toast.makeText(Homepage.this, "No Jobs available.", Toast.LENGTH_LONG).show();
                            rec_view.removeAllViews();
                        }
                        else if(result.equals("Access Denied"))
                            Toast.makeText(Homepage.this,"Access Denied.",Toast.LENGTH_LONG).show();

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(Homepage.this,R.string.server_error,Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(Homepage.this, R.string.server_error, Toast.LENGTH_LONG).show();
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawers();
                }
                else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

