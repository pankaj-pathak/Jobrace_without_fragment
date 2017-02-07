package com.example.pankaj.jobrace_without_fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Get_technology_list_activity extends AppCompatActivity {

    Spinner spinner_technology;
    Button btn_next;
    List<String> list_technology_show;
    List<String> list_technology_complete;
    List<String> list_technology_type;
    ArrayAdapter<String> ada;
    private ProgressDialog pd;
    RadioGroup radio_group_question_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_technology_list_activity);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.toolbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);
        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(true);


        pd=new ProgressDialog(this);
        pd.setCancelable(false);


        radio_group_question_type= (RadioGroup) findViewById(R.id.radio_group_question_type);
        radio_group_question_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
               display_technology();
            }
        });
        btn_next= (Button) findViewById(R.id.btn_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTest();
            }
        });
        spinner_technology=(Spinner)findViewById(R.id.spinner_technology);
        list_technology_show=new ArrayList<>();
        list_technology_complete=new ArrayList<>();
        list_technology_type=new ArrayList<>();
        getTechnology();
        ada=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,list_technology_show);
        spinner_technology.setAdapter(ada);
    }

    public void getTechnology()
    {
        if(!Check_connectivity.is_connected(this))
            return;
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test ctest=retrofit.create(Custom_interface_test.class);
        Call<String> call=ctest.getTechnology();
        pd.show();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit)
            {
                if(response.code()==200) {
                    try {

                        list_technology_show.clear();
                        list_technology_type.clear();
                        list_technology_complete.clear();
                        JSONArray ar = new JSONArray(response.body());
                        String result = ar.getString(0);
                        if (result != null && result.equals("sucess")) {
                            int i = 1;
                            while (i < ar.length()) {
                                JSONObject obj = ar.getJSONObject(i);
                                list_technology_complete.add(obj.getString("QSubCategory"));
                                list_technology_type.add(obj.getString("QType"));
                                i++;
                            }
                            btn_next.setEnabled(true);
                        } else if (result.equals("fail"))
                            Toast.makeText(Get_technology_list_activity.this, "No technology available.", Toast.LENGTH_LONG).show();
                        else if (result.equals("Access Denied"))
                            Toast.makeText(Get_technology_list_activity.this, "Access Denied.", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    display_technology();
                }
                else
                {
                    Toast.makeText(Get_technology_list_activity.this, "Unable to connect with server.", Toast.LENGTH_LONG).show();
                }
                pd.hide();
            }

            @Override
            public void onFailure(Throwable t)
            {
                pd.hide();
                Toast.makeText(Get_technology_list_activity.this,"Some error occured",Toast.LENGTH_LONG).show();
            }
        });
    }
    public void display_technology()
    {
        list_technology_show.clear();
        list_technology_show.add("Select Technology");
        if(radio_group_question_type.getCheckedRadioButtonId()==R.id.radio_technocal)
        for(int i=0;i<list_technology_complete.size();i++)
        {
            if(list_technology_type.get(i).equals("Technical"))
            {
                list_technology_show.add(list_technology_complete.get(i));
            }
        }
        else
            for(int i=0;i<list_technology_complete.size();i++)
            {
                if(!list_technology_type.get(i).equals("Technical"))
                {
                    list_technology_show.add(list_technology_complete.get(i));
                }
            }
        ada.notifyDataSetChanged();
    }

    public void startTest()
    {
        int selecteditem=spinner_technology.getSelectedItemPosition();
        if(selecteditem<0)
        {
            Toast.makeText(Get_technology_list_activity.this, "Please refresh your page.", Toast.LENGTH_SHORT).show();
            return;
        }
        else if(selecteditem==0)
        {
            Toast.makeText(Get_technology_list_activity.this, "Please select any technology.", Toast.LENGTH_SHORT).show();
            return;
        }
        if(Check_connectivity.is_connected(Get_technology_list_activity.this)) {
            sendTestInfo(selecteditem+1);
        }
    }
    public void sendTestInfo(final int selecteditem)
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(Get_technology_list_activity.this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.startTest(new Shared_preference_data_class(Get_technology_list_activity.this).getUserEmail(),"",list_technology_show.get(selecteditem-1));
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
                            Online_test_start_page.technology_name=list_technology_show.get(selecteditem-1);
                            Intent it=new Intent(Get_technology_list_activity.this,Online_test_start_page.class);
                            startActivity(it);
                            finish();
                            /*
                            FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_home_page,online_test_start_page);
                            fragmentTransaction.commit();
                            */
                        }
                        else if(result.equals("fail"))
                            Toast.makeText(Get_technology_list_activity.this,"Some error occured",Toast.LENGTH_LONG).show();
                        else if(result.equals("Access Denied"))
                            Toast.makeText(Get_technology_list_activity.this,"Access Denied.",Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(Get_technology_list_activity.this,"Unable to connect with server",Toast.LENGTH_LONG).show();
                }
                pd.hide();
            }

            @Override
            public void onFailure(Throwable t) {
                pd.hide();
                Toast.makeText(Get_technology_list_activity.this,"Some error occured",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }
}
