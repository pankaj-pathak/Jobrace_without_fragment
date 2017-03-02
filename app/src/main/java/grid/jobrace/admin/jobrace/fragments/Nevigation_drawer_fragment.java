package grid.jobrace.admin.jobrace.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import grid.jobrace.admin.jobrace.Homepage;
import grid.jobrace.admin.jobrace.Nevigation_drawer_item_click_listener;
import grid.jobrace.admin.jobrace.R;
import grid.jobrace.admin.jobrace.adapter.Nevigation_drawer_recycler_view_adapter;
import grid.jobrace.admin.jobrace.data_classes.Centeral_class;
import grid.jobrace.admin.jobrace.data_classes.Shared_preference_data_class;

import java.util.ArrayList;
import java.util.List;


public class Nevigation_drawer_fragment extends Fragment
{
    RecyclerView rec_view;
    TextView tv_nevigation_drawer_title;
    TextView tv_nevigation_drawer_sub_title;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActiomBarDrawerToggle;
    Nevigation_drawer_recycler_view_adapter ada;
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
        ada=new Nevigation_drawer_recycler_view_adapter(getActivity());
        ada.listener=listener;
        rec_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        rec_view.setAdapter(ada);
        Centeral_class.ada=ada;

}


    public void setUp(DrawerLayout drawerLayout, Toolbar toolbar)
    {
        this.mDrawerLayout=drawerLayout;

        mActiomBarDrawerToggle=new ActionBarDrawerToggle(getActivity(),drawerLayout,R.string.drawer_opened,R.string.drawer_closed)
        {
            @Override
            public void onDrawerOpened(View drawerView)
            {
                super.onDrawerOpened(drawerView);
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


}


