package com.example.pankaj.jobrace_without_fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_performance_detail);
        getSupportActionBar().setTitle("Performance Detail");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        graphView= (GraphView)findViewById(R.id.graph_view);
        list_technology=new ArrayList<>();
        list_test_id=new ArrayList<>();
        list_correct_answer=new ArrayList<>();
        list_date_time=new ArrayList<>();

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
                            }
                            if(i>1)
                                showGraph();
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
    public void showGraph()
    {

        DataPoint dp[]=new DataPoint[list_test_id.size()];
        for(int i=0;i<list_test_id.size();i++)
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

        barGraphSeries.setDrawValuesOnTop(true);
        barGraphSeries.setValuesOnTopColor(Color.BLACK);
        barGraphSeries.setValuesOnTopSize(15);

        barGraphSeries.setSpacing(20);
        graphView.addSeries(barGraphSeries);
    }
}
