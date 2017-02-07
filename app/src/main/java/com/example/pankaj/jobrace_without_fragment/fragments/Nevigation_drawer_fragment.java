package com.example.pankaj.jobrace_without_fragment.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.pankaj.jobrace_without_fragment.Homepage;
import com.example.pankaj.jobrace_without_fragment.Nevigation_drawer_item_click_listener;
import com.example.pankaj.jobrace_without_fragment.R;
import com.example.pankaj.jobrace_without_fragment.ToStringConverterFactory;
import com.example.pankaj.jobrace_without_fragment.adapter.Nevigation_drawer_recycler_view_adapter;
import com.example.pankaj.jobrace_without_fragment.data_classes.Centeral_class;
import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;
import com.example.pankaj.jobrace_without_fragment.interfaces.Custom_interface_test;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class Nevigation_drawer_fragment extends Fragment
{
    RecyclerView rec_view;
    TextView tv_nevigation_drawer_title;
    TextView tv_nevigation_drawer_sub_title;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActiomBarDrawerToggle;
    Nevigation_drawer_recycler_view_adapter ada;
    //static Activity activity;
    public Nevigation_drawer_fragment()
    {

    }

    Nevigation_drawer_item_click_listener listener=new Nevigation_drawer_item_click_listener() {
        @Override
        public void clicked(String value)
        {
            ((Homepage)getActivity()).changeFragment(value);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nevigation_drawer_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        rec_view= (RecyclerView) getActivity().findViewById(R.id.rec_view_nevigation_options1);
        tv_nevigation_drawer_title = (TextView) getActivity().findViewById(R.id.tv_nevigation_drawer_title);
        tv_nevigation_drawer_sub_title = (TextView) getActivity().findViewById(R.id.tv_nevigation_drawer_sub_title);
        Shared_preference_data_class sp=new Shared_preference_data_class(getActivity());
        tv_nevigation_drawer_title.setText(sp.getUserName());
        tv_nevigation_drawer_sub_title.setText(sp.getUserEmail());


        List<String> list_options=new ArrayList<>();
        /*
        list_options.add("Home");
        list_options.add("My Job");
        list_options.add("My Interview");
        list_options.add("My Resume");
        list_options.add("My Performance");
        list_options.add("Interview Questions");
        list_options.add("Today's Interview");
        list_options.add("Test My Skills");
        list_options.add("Settings");
        List<Integer>list_image_id=new ArrayList<>();
        list_image_id.add(R.drawable.list_options_icon);
        list_image_id.add(R.drawable.list_options_icon);
        list_image_id.add(R.drawable.list_options_icon);
        list_image_id.add(R.drawable.list_options_icon);
        list_image_id.add(R.drawable.list_options_icon);
        list_image_id.add(R.drawable.list_options_icon);
        list_image_id.add(R.drawable.list_options_icon);
        list_image_id.add(R.drawable.list_options_icon);
        list_image_id.add(R.drawable.list_options_icon);
        */
        //ada=new Nevigation_drawer_recycler_view_adapter(list_options,list_image_id,getActivity());
        ada=new Nevigation_drawer_recycler_view_adapter(getActivity());
        ada.listener=listener;
        rec_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        rec_view.setAdapter(ada);
        Centeral_class.ada=ada;
    }
    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar)
    {
        this.mDrawerLayout=drawerLayout;

        /*
        mActiomBarDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,toolbar,R.string.drawer_opened,R.string.drawer_closed)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                //getMenuList();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

        };
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActiomBarDrawerToggle.syncState();
            }
        });
        */
        mActiomBarDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,R.string.drawer_opened,R.string.drawer_closed)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
                //getMenuList();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mActiomBarDrawerToggle.syncState();
            }
        });
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void getMenuList()
    {
        Log.d("in get menu","in getttttttt menuuuuuuuu---------------------------------------------1111111111111111111111");
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(getActivity()).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.getMenuList();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                Log.d("menu======","menu lis==============="+response.body());

            }

            @Override
            public void onFailure(Throwable t) {

            }
        });

        Log.d("in get menu","in getttttttt menuuuuuuuu---------------------------------------------");
    }

}
