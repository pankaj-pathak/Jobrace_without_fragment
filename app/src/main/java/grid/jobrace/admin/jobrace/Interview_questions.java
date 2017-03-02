package grid.jobrace.admin.jobrace;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import grid.jobrace.admin.jobrace.data_classes.Shared_preference_data_class;
import grid.jobrace.admin.jobrace.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class Interview_questions extends AppCompatActivity implements View.OnClickListener{

    TextView tv_question, tv_answer, tv_current_question_no, tv_technology_name,tv_display_answer;
    Button btn_previous, btn_next;
    String SKILLS = "JAVA/J2EE";
    List<String> list_questions, list_answers;
    public int CURRENT_QUESTION_NO=0;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_questions);
        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCancelable(false);

        list_questions = new ArrayList<>();
        list_answers = new ArrayList<>();
        tv_question = (TextView) findViewById(R.id.tv_question);
        tv_answer = (TextView) findViewById(R.id.tv_answer);
        tv_current_question_no = (TextView) findViewById(R.id.tv_current_question_no);
        tv_technology_name = (TextView) findViewById(R.id.tv_technology_name);
        tv_technology_name.setText(SKILLS);

        btn_previous = (Button) findViewById(R.id.btn_previous);
        btn_next = (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(this);
        btn_previous.setOnClickListener(this);
        tv_display_answer = (TextView) findViewById(R.id.tv_display_answer);
        tv_display_answer.setOnClickListener(this);
        getInterviewQuestions();
    }
    @Override
    public void onClick(View v) {
        if(v==btn_next)
        {
            CURRENT_QUESTION_NO++;
            setQuestion();
        }
        else if(v==btn_previous)
        {
            CURRENT_QUESTION_NO--;
            setQuestion();
        }
        else if(v==tv_display_answer)
        {
            tv_answer.setVisibility(View.VISIBLE);
        }

    }

    public void getInterviewQuestions()
    {
        if(Check_connectivity.is_connected(Interview_questions.this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(new Shared_preference_data_class(this).getUrl())
                    .addConverterFactory(new ToStringConverterFactory())
                    .build();
            Custom_interface_test custom_interface_test = retrofit.create(Custom_interface_test.class);
            Call<String> call = custom_interface_test.getInterviewQuestions(SKILLS);
            pd.show();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                    if(response.code()==200)
                    {
                        try {
                            JSONArray jsonArray = new JSONArray(response.body());
                            String reply = jsonArray.getString(0);
                            if (reply.equalsIgnoreCase("sucess"))
                            {
                                int i=1;
                                for (i = 1; i < jsonArray.length(); i++) {
                                    JSONObject obj=jsonArray.getJSONObject(i);
                                    list_questions.add(obj.getString("Question"));
                                    list_answers.add(obj.getString("Answer"));
                                }
                                if(i>1)
                                {
                                    CURRENT_QUESTION_NO++;
                                    setQuestion();
                                }
                            }
                            else
                            {
                                Toast.makeText(Interview_questions.this,"Questions not available for this technology",Toast.LENGTH_LONG).show();
                            }
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(Interview_questions.this,R.string.server_error,Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(Interview_questions.this,R.string.server_error,Toast.LENGTH_LONG).show();
                    }
                    pd.hide();
                }
                @Override
                public void onFailure(Throwable t) {
                    pd.hide();
                    Toast.makeText(Interview_questions.this,R.string.server_error,Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    public void setQuestion()
    {
        tv_question.setText(list_questions.get(CURRENT_QUESTION_NO-1));
        tv_answer.setText(list_answers.get(CURRENT_QUESTION_NO-1));
        tv_current_question_no.setText(CURRENT_QUESTION_NO+"/"+list_questions.size());
        if(CURRENT_QUESTION_NO>1) btn_previous.setEnabled(true);
        else btn_previous.setEnabled(false);
        if(CURRENT_QUESTION_NO<list_questions.size()) btn_next.setEnabled(true);
        else btn_next.setEnabled(false);
        tv_answer.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pd!=null)
            pd.dismiss();
    }
}
