package com.example.pankaj.jobrace_without_fragment.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pankaj.jobrace_without_fragment.R;
import com.example.pankaj.jobrace_without_fragment.View_job_details_activity;
import com.example.pankaj.jobrace_without_fragment.data_classes.Check_credentials;
import com.example.pankaj.jobrace_without_fragment.data_classes.Job_details_data;
import com.example.pankaj.jobrace_without_fragment.data_classes.Shared_preference_data_class;

import java.util.List;


/**
 * Created by Pankaj on 11/16/2016.
 */

public class Jobs_list_adapter extends RecyclerView.Adapter<Jobs_list_adapter.Custom_view_holder>
{

    List<Job_details_data> list_of_jobs;
    LayoutInflater inflater;
    Context context;
    public Jobs_list_adapter(List<Job_details_data> list_of_jobs, Context context)
    {
        this.context=context;
        this.list_of_jobs=list_of_jobs;
        this.inflater=LayoutInflater.from(context);
    }

    @Override
    public Custom_view_holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v=inflater.inflate(R.layout.jobs_list_item_layout,parent,false);
        return new Custom_view_holder(v);
    }

    @Override
    public void onBindViewHolder(Custom_view_holder holder, int position)
    {
        final Job_details_data jd=list_of_jobs.get(position);
        holder.tv_job_designation.setText(jd.getJob_title());
        holder.tv_experience_required.setText(jd.getExp_req_min()+"-"+jd.getExp_req_max());
        holder.tv_interview_location.setText(jd.getInterview_location());
        holder.tv_job_eligibility.setText(jd.getJob_eligibility());
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(Check_credentials.isEligibleToViewJob((Activity)context))
                {

                    Intent it = new Intent(context, View_job_details_activity.class);
                    View_job_details_activity.jd = jd;
                    context.startActivity(it);
                    Shared_preference_data_class sp=new Shared_preference_data_class((Activity)context);
                    sp.initializeEditor();
                    sp.setNoOfJobsOpened(Integer.parseInt(sp.getNoOfJobsOpened())+1+"");
                    sp.commitChanges();
                }

            }
        });

    }

    @Override
    public int getItemCount()
    {
        return list_of_jobs.size();
    }

    class Custom_view_holder extends RecyclerView.ViewHolder
    {
        TextView tv_job_designation;
        TextView tv_experience_required;
        TextView tv_interview_location;
        TextView tv_job_eligibility;
        View v=null;
        public Custom_view_holder(View v)
        {
            super(v);
            tv_job_designation= (TextView) v.findViewById(R.id.tv_job_designation);
            tv_experience_required= (TextView) v.findViewById(R.id.tv_experience_required);
            tv_interview_location= (TextView) v.findViewById(R.id.tv_interview_location);
            tv_job_eligibility= (TextView) v.findViewById(R.id.tv_job_eligibility);
            this.v=v;
        }

    }
}
