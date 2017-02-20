package com.example.pankaj.jobrace_without_fragment;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
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

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Online_test_start_page extends AppCompatActivity {

    Button btn_start_test;
    private ArrayList<String> list_questions_id;
    public static String technology_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_test_start_page);

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


        ((TextView)findViewById(R.id.tv_technology)).setText(Html.fromHtml("<font color='red'>"+
                technology_name
                +"</font>")+" Questions");
        btn_start_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Online_test_fragment online_test_fragment=new Online_test_fragment();
                online_test_fragment.list_questions_id.addAll(list_questions_id);
                FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                FragmentTransaction transaction=fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_home_page,online_test_fragment);
                transaction.commit();
                */
                if(!Check_connectivity.is_connected(Online_test_start_page.this))
                    return;
                Intent it=new Intent(Online_test_start_page.this, Online_test.class);
                Bundle bd=new Bundle();
                bd.putStringArrayList("questions_list",list_questions_id);
                bd.putBoolean("isVirtualInterview",false);
                bd.putString("technology",technology_name);
                bd.putString("jobid","");
                it.putExtras(bd);
                startActivity(it);
                finish();
            }
        });
        btn_start_test.setEnabled(false);
        list_questions_id=new ArrayList<>();
        getQuestionsId();
    }

    public void getQuestionsId()
    {
        String urll=new Shared_preference_data_class(this).getUrl();
        Log.d("data======","url============"+urll+"OnebyonequestionsNew.php");
        if(!Check_connectivity.is_connected(this))
            return;
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.getQuestionsId(technology_name);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.code()==200) {
                    try {
                        list_questions_id.clear();
                        JSONArray ar = new JSONArray(response.body());
                        Log.d("data from server===","data==="+ar);
                        String result=ar.getString(0);
                        if(result!=null && result.equals("sucess"))
                        {
                            JSONObject obj=ar.getJSONObject(1);
                            for (int i = 0; i < obj.length(); i++) {
                                list_questions_id.add(obj.getString("qus"+(i+1)));
                            }
                            if(list_questions_id.size()<20)
                            {
                                Toast.makeText(Online_test_start_page.this,"Questions for this technology are not available.",Toast.LENGTH_LONG).show();
                                notSufficientQuestions();
                            }
                            else
                                btn_start_test.setEnabled(true);
                        }
                        else if(result.equals("fail"))
                        {
                            Toast.makeText(Online_test_start_page.this,"No questions available",Toast.LENGTH_LONG).show();
                            Log.d("no data===","no data availableeeeeeeee");
                            notSufficientQuestions();
                        }
                        else if(result.equals("Access Denied"))
                        {
                            Toast.makeText(Online_test_start_page.this,"Access Denied.",Toast.LENGTH_LONG).show();
                            notSufficientQuestions();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(Online_test_start_page.this,"Unable to connect with server.",Toast.LENGTH_LONG).show();
                    notSufficientQuestions();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(Online_test_start_page.this,"Some problem occured.",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void notSufficientQuestions()
    {

        Intent it=new Intent(this,Get_technology_list_activity.class);
        startActivity(it);
        finish();
        /*
        Get_technology_list_fragment get_technology_list_fragment=new Get_technology_list_fragment();
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        FragmentTransaction transaction=fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_home_page,get_technology_list_fragment);
        transaction.commit();
        */
    }
}
