package com.example.pankaj.jobrace_without_fragment;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Result_activity extends AppCompatActivity {

    TextView tv_correct_answer;
    TextView tv_incorrect_answer;
    TextView tv_questions_not_attempted;
    TextView tv_display_result;
    private int total_questions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_activity);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Result");
        actionBar.setDisplayHomeAsUpEnabled(true);

        total_questions=getIntent().getExtras().getInt("question_size");

        tv_display_result=(TextView)findViewById(R.id.tv_display_result);
        tv_correct_answer=(TextView)findViewById(R.id.tv_correct_answer);
        tv_incorrect_answer=(TextView)findViewById(R.id.tv_incorrect_answer);
        tv_questions_not_attempted=(TextView)findViewById(R.id.tv_questions_not_attempted);
        getResult();
    }
    public void getResult()
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())//.addConverterFactory(GsonConverterFactory.create())
                .build();

        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.getSkillTestResult(new Shared_preference_data_class(this).getUserEmail());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.code() == 200)
                {
                    try {
                        JSONArray ar=new JSONArray(response.body());
                        if(ar.getString(0).equals("sucess"))
                        {
                            JSONObject obj = ar.getJSONObject(1);
                            int correct=obj.getInt("correct_answer");
                            int incorrect=obj.getInt("incorrect_answer");
                            tv_correct_answer.setText(correct+"");
                            tv_incorrect_answer.setText(incorrect+"");
                            tv_questions_not_attempted.setText(total_questions-correct-incorrect+"");
                            String message="";
                            int correct_percentage=correct*100/total_questions;
                            Log.d("correct ====","correct percentageeeeeeeeeeee===s====="+correct+"================="+total_questions+"========"+correct_percentage);
                            if(correct_percentage>=80)message=getString(R.string.excellent_level);
                            else if(correct_percentage>=65)message=getString(R.string.good_level);
                            else if(correct_percentage>=40)message=getString(R.string.average_level);
                            else message=getString(R.string.basic_level);
                            tv_display_result.setText(message);

                        }
                        else if(ar.getString(0).equals("fail"))
                        {
                            Toast.makeText(Result_activity.this,"No data available..",Toast.LENGTH_LONG).show();
                        }
                        else if(ar.getString(0).equals("No Result"))
                        {
                            Toast.makeText(Result_activity.this,"No result available.",Toast.LENGTH_LONG).show();
                        }
                        else if(ar.getString(0).equals("Access Denied"))
                            Toast.makeText(Result_activity.this,"Access Denied.",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(Result_activity.this,"Unable to connect with server.",Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Throwable t)
            {
                t.printStackTrace();
                Toast.makeText(Result_activity.this,"Some error occured",Toast.LENGTH_LONG).show();
            }
        });



    }
}
