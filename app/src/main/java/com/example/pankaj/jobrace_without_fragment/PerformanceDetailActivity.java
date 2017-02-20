package com.example.pankaj.jobrace_without_fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class PerformanceDetailActivity extends AppCompatActivity {
    GraphView graphView;
    List<String> list_technology;
    List<String> list_date_time;
    List<Integer> list_correct_answer;
    List<Integer> list_test_id;
    LinearLayout linear_technology;
    ArrayAdapter ada;
    Set<String> set_technology;
    List<String> list_show_technology;
    static int k=0;
    int i=0;
    Menu option_menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!Check_connectivity.is_connected(this,false))
        {
            setContentView(R.layout.layout_not_connected_with_internet);
            ((Button)findViewById(R.id.btn_try_again)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Check_connectivity.is_connected(PerformanceDetailActivity.this,false))
                    {
                        Intent it=getIntent();
                        finish();
                        startActivity(it);
                    }
                }
            });
            return;
        }
        setContentView(R.layout.activity_performance_detail);
        getSupportActionBar().setTitle("Performance Detail");
        //getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        graphView= (GraphView)findViewById(R.id.graph_view);
        linear_technology= (LinearLayout) findViewById(R.id.linear_technology);
        list_technology=new ArrayList<>();
        list_test_id=new ArrayList<>();
        list_correct_answer=new ArrayList<>();
        list_date_time=new ArrayList<>();
        list_show_technology=new ArrayList<>();
        set_technology=new HashSet<>();
        ada=new ArrayAdapter(this,android.R.layout.simple_list_item_1,list_show_technology);
        /*spinner_technology.setAdapter(ada);
        spinner_technology.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int positiom, long l) {
                                String selected_technology=list_show_technology.get(positiom);
                display_graph(selected_technology);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        */
        getProgressReport();
    }
    public void getProgressReport()
    {
        String baseurl=new Shared_preference_data_class(PerformanceDetailActivity.this).getUrl();
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(baseurl)
                .addConverterFactory(new ToStringConverterFactory())//.addConverterFactory(GsonConverterFactory.create())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.getProgressReportOfCandidate(new Shared_preference_data_class(PerformanceDetailActivity.this).getUserEmail());
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if (response.code() == 200)
                {
                    try {
                        JSONArray ar=new JSONArray(response.body());
                        if(ar.getString(0).equals("sucess"))
                        {
                            int i=1;
                            for(;i<ar.length();i++) {
                                JSONObject obj = ar.getJSONObject(i);
                                list_test_id.add(obj.getInt("test_id"));
                                list_date_time.add(obj.getString("date_time"));
                                int corr=obj.getInt("correct_answer");
                                list_correct_answer.add(corr);
                                list_technology.add(obj.getString("technology"));
                                set_technology.add(obj.getString("technology"));
                            }
                            list_show_technology.addAll(set_technology);

                            if(i>1)
                                display_graph(list_show_technology.get(0));
                            else
                                Toast.makeText(PerformanceDetailActivity.this,"No result Present",Toast.LENGTH_LONG).show();
                        }
                        else if(ar.getString(0).equals("fail"))
                        {
                            Toast.makeText(PerformanceDetailActivity.this,"No result available.",Toast.LENGTH_LONG).show();
                        }
                        else if(ar.getString(0).equals("No Result"))
                        {
                            Toast.makeText(PerformanceDetailActivity.this,"No result available.",Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Toast.makeText(PerformanceDetailActivity.this,"Unable to connect with server.",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
                Toast.makeText(PerformanceDetailActivity.this,"Some error occured",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void showGraph(List<Integer>list_correct_answer)
    {
        graphView.clearAnimation();
        DataPoint dp[]=new DataPoint[list_correct_answer.size()];
        for(int i=0;i<list_correct_answer.size();i++)
        {
            dp[i]=new DataPoint(i+1,list_correct_answer.get(i));
        }
        BarGraphSeries barGraphSeries=new BarGraphSeries(dp);
        barGraphSeries.setValueDependentColor(new ValueDependentColor() {
            @Override
            public int get(DataPointInterface data)
            {
                if(data.getY()>16)
                    return Color.GREEN;
                else if(data.getY()>12)
                    return Color.YELLOW;
                else if(data.getY()>7)
                    return Color.MAGENTA;
                else
                    return Color.RED;
            }
        });

        Viewport viewport=graphView.getViewport();
        viewport.setMaxY(20);
        viewport.setScalable(true);
        viewport.setScrollable(true);
        viewport.setYAxisBoundsManual(true);
        viewport.setMaxX(15);
        GridLabelRenderer glr = graphView.getGridLabelRenderer();
        glr.setPadding(32);
        glr.setVerticalAxisTitle("Score");
        glr.setHorizontalAxisTitle("Test");
        glr.setHorizontalLabelsVisible(false);
        barGraphSeries.setDrawValuesOnTop(true);
        barGraphSeries.setValuesOnTopColor(Color.BLACK);
        barGraphSeries.setValuesOnTopSize(15);
        barGraphSeries.setSpacing(20);
        graphView.removeAllSeries();
        graphView.addSeries(barGraphSeries);

        //ada.notifyDataSetChanged();
    }
    public void display_graph(String technology)
    {
        show_tab();
        List<Integer> list_show_answer=new ArrayList<>();
        for(int i=0;i<list_correct_answer.size();i++)
        {
            if(list_technology.get(i).equals(technology))
            {
                list_show_answer.add(list_correct_answer.get(i));
            }
        }
        showGraph(list_show_answer);

    }
    public void show_tab()
    {
        /*
        linear_technology.removeAllViews();

        for(i=0;i<list_show_technology.size();i++)
        {
            final Button tv=new Button(this);
            tv.setText(list_show_technology.get(i));
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                    ActionBar.LayoutParams.WRAP_CONTENT,
                    ActionBar.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(param);
            tv.setTextSize(10);
            tv.setBackgroundColor(Color.TRANSPARENT);
            tv.setId(i);
            if(k==i)
                tv.setTextColor(Color.BLUE);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tv.setBackgroundColor(Color.BLUE);
                    k=tv.getId();
                    display_graph(tv.getText().toString());

                }
            });
            linear_technology.addView(tv,i);
        }
        */
        option_menu.clear();
        for(i=0;i<list_show_technology.size();i++)
        {
            option_menu.add(i,i,i,list_show_technology.get(i));
            if(k==i)option_menu.getItem(i).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
          option_menu=menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()!=android.R.id.home) {
            k = item.getItemId();
            display_graph(item.getTitle().toString());
            item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            return super.onOptionsItemSelected(item);
        }
        else return false;
    }
}
