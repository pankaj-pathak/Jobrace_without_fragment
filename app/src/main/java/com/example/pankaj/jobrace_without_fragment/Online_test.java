package com.example.pankaj.jobrace_without_fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Online_test extends AppCompatActivity {

    private List<String> list_questions_id;
    int questionno = 1;
    EditText et_question_answer;
    Button btn_save;
    //TextView tv_question;
    TextView tv_question_no;
    TextView tv_question_id;
    ImageView iv_reload_question;
    String question = "";
    int time = 0;
    //ScrollView scrollView;
    TextView tv_timer;
    Handler handler;
    Runnable runnable;
    private String job_id;
    static int exit = 0;
    private boolean isVirtualInterview;
    private String employer_email;
    private String technology;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_test);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.toolbar_layout, null);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(getResources().getDimensionPixelSize(R.dimen.toolbar_image_size), LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0,0,0,0);
        ((ImageView)actionBarLayout.findViewById(R.id.toolbar_image)).setLayoutParams(lp);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);
        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(false);


        et_question_answer = (EditText) findViewById(R.id.et_question_answer);
        btn_save = (Button) findViewById(R.id.btn_save);
        iv_reload_question= (ImageView) findViewById(R.id.iv_reload_question);
        //tv_question = (TextView) findViewById(R.id.tv_question);
        et_question_answer = (EditText) findViewById(R.id.et_question_answer);
        tv_question_no = (TextView) findViewById(R.id.tv_question_no);
        tv_question_id = (TextView) findViewById(R.id.tv_question_id);
       /*
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        scrollView.setHorizontalScrollBarEnabled(false);
        scrollView.setVerticalScrollBarEnabled(false);
        */
        Bundle bd = getIntent().getExtras();
        list_questions_id = bd.getStringArrayList("questions_list");
        isVirtualInterview = bd.getBoolean("isVirtualInterview");
        job_id=bd.getString("job_id");
        if(isVirtualInterview)
        {
            employer_email=bd.getString("employer_email");
            technology=bd.getString("technology");
        }
        tv_timer = (TextView) findViewById(R.id.tv_timer);
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String answer = et_question_answer.getText().toString().trim();
                if (answer == null || answer.equals("")) {
                    Toast.makeText(Online_test.this, "Please enter answer first..", Toast.LENGTH_LONG).show();
                    return;
                }
                btn_save.setEnabled(false);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btn_save.getWindowToken(), 0);
                questionno++;
                if (handler != null) {
                    handler.removeCallbacks(runnable);
                    handler = null;
                }
                tv_timer.setText("");
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(new Shared_preference_data_class(Online_test.this).getUrl())
                        .addConverterFactory(new ToStringConverterFactory())//.addConverterFactory(GsonConverterFactory.create())
                        .build();
                /*
                if (isVirtualInterview) {
                   /*
                    if ((questionno - 1) == 40) {
                        isSecondRoundCleared();
                    }
                    else
                    *\/
                        saveAndGetData(retrofit);
                }*/
                saveAndGetData(retrofit);

            }
        });
        iv_reload_question.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_question_answer.setText(question);
            }
        });
        getQuestion();
    }
    public void saveAndGetData(Retrofit retrofit)
    {
        if (questionno > list_questions_id.size()) {
            if(isVirtualInterview)
            {
                sendAnswer(retrofit);
                sendMailToEmployer();
                Toast.makeText(Online_test.this,"Your result will be sent to Recruiter.",Toast.LENGTH_LONG).show();
                finish();
            }
            else{
                sendAnswer(retrofit);
                Intent it = new Intent(Online_test.this, Result_activity.class);
                startActivity(it);
                finish();
            }
            return;
        } else {
            sendAnswer(retrofit);
            getQuestion();
            return;
        }
    }

    public void sendMailToEmployer()
    {
        Shared_preference_data_class sp=new Shared_preference_data_class(Online_test.this);
        String email=sp.getUserEmail();
        String name=sp.getUserName();
        String contactNo=sp.getContactNo();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(Online_test.this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())//.addConverterFactory(GsonConverterFactory.create())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.sendMailToEmployer(name,email,contactNo,technology,employer_email,job_id,1,list_questions_id.size());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.code() == 200)
                {
                    try {
                        JSONArray ar=new JSONArray(response.body());
                        String result=ar.getString(0);
                        if(result.equals("sucess"))
                        {
                            Toast.makeText(Online_test.this,ar.getJSONObject(1).getString("message"),Toast.LENGTH_LONG).show();
                        }
                        else if(result.equals("fail"))
                            //Toast.makeText(Online_test.this,"Some error occured in send answer.",Toast.LENGTH_LONG).show();
                            ;
                        else if(result.equals("Access Denied"))
                            Toast.makeText(Online_test.this,"Access Denied.",Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(Online_test.this, "Unable to connect with server", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(Online_test.this,"Some error occured.",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void isSecondRoundCleared()
    {

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(Online_test.this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())//.addConverterFactory(GsonConverterFactory.create())
                .build();

        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.getVirtualInterviewResult(new Shared_preference_data_class(Online_test.this).getUserEmail(),job_id,11,40);

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
                            if(correct>=21)
                                saveAndGetData(retrofit);
                            else
                            {
                                new Custom_dialog_not_qualified_for_virtual_interview(Online_test.this).show();
                            }
                        }
                        else if(ar.getString(0).equals("fail"))
                        {
                            Toast.makeText(Online_test.this,"No data available..",Toast.LENGTH_LONG).show();
                        }
                        else if(ar.getString(0).equals("No Result"))
                        {
                            Toast.makeText(Online_test.this,"No result available.",Toast.LENGTH_LONG).show();
                        }
                        else if(ar.getString(0).equals("Access Denied"))
                            Toast.makeText(Online_test.this,"Access Denied.",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(Online_test.this, "Unable to connect with server try again", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(Online_test.this, "Some error occured. Please try again", Toast.LENGTH_LONG).show();
            }
        });
    }
    public void getQuestion()
    {
        question="";
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(Online_test.this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())//.addConverterFactory(GsonConverterFactory.create())
                .build();


        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.getQuestions(list_questions_id.get(questionno-1));
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.code() == 200)
                {
                    try {
                        JSONArray ar=new JSONArray(response.body());
                        String result=ar.getString(0);
                        if(result!=null && result.equals("sucess")) {
                            JSONObject obj = ar.getJSONObject(1);
                            et_question_answer.setText(question = obj.getString("QuestionText"));
                            time = obj.getInt("TimeAllowed");
                            tv_question_no.setText(questionno + "/" + list_questions_id.size());
                            tv_question_id.setText(list_questions_id.get(questionno-1));
                            startTimer();
                        }
                        else if(result.equals("fail"))
                            Toast.makeText(Online_test.this,"Some error occured",Toast.LENGTH_LONG).show();
                        else if(result.equals("Access Denied"))
                            Toast.makeText(Online_test.this,"Access Denied.",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(Online_test.this, "Unable to connect with server", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });



    }

    public void sendAnswer(Retrofit retrofit)
    {
        String answer=et_question_answer.getText().toString().trim();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=null;
        if(isVirtualInterview)
            call=custom_interface_test.sendVirtualInterviewAnswer(new Shared_preference_data_class(Online_test.this).getUserEmail(),list_questions_id.get(questionno-2),answer,questionno-1+"");
        else
            call=custom_interface_test.sendAnswer(new Shared_preference_data_class(Online_test.this).getUserEmail(),list_questions_id.get(questionno-2),answer,questionno-1+"");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.code() == 200)
                {
                    try {
                        JSONArray ar=new JSONArray(response.body());
                        String result=ar.getString(0);
                        if(result.equals("sucess"))
                        {

                        }
                        else if(result.equals("fail"))
                            //Toast.makeText(Online_test.this,"Some error occured in send answer.",Toast.LENGTH_LONG).show();
                        ;
                        else if(result.equals("Access Denied"))
                            Toast.makeText(Online_test.this,"Access Denied.",Toast.LENGTH_LONG).show();
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(Online_test.this, "Unable to connect with server", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(Online_test.this,"Some error occured.",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void startTimer()
    {
        if(handler!=null) {
            handler.removeCallbacks(runnable);
            handler=null;
        }
        handler=new Handler();
        runnable=new Runnable() {
            @Override
            public void run()
            {
                //tv_timer.setText("Time : "+time+"sec");
                tv_timer.setText(time+"sec");

                if(time>0)
                {
                    handler.postDelayed(this,1000);
                }
                else
                {
                    questionno++;
                    getQuestion();
                }
                time--;

            }
        };
        handler.postDelayed(runnable,0);
        btn_save.setEnabled(true);
        et_question_answer.getParent().requestChildFocus(et_question_answer,et_question_answer);

    }

    @Override
    public void onBackPressed() {

        exit++;
        if(exit==1) {
            Toast.makeText(getApplicationContext(), "Press again to exit from test.", Toast.LENGTH_SHORT).show();
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
        {
            super.onBackPressed();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(handler!=null)
            handler.removeCallbacks(runnable);
    }
}
