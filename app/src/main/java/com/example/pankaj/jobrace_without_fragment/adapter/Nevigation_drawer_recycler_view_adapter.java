package com.example.pankaj.jobrace_without_fragment.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.pankaj.jobrace_without_fragment.Nevigation_drawer_item_click_listener;
import com.example.pankaj.jobrace_without_fragment.R;
import com.example.pankaj.jobrace_without_fragment.data_classes.Centeral_class;

import java.util.List;

/**
 * Created by Pankaj on 11/18/2016.
 */

public class Nevigation_drawer_recycler_view_adapter extends RecyclerView.Adapter<Nevigation_drawer_recycler_view_adapter.Rec_view_holder>
{
    List<String> list1;
    List<Integer> list2;
    Context context;
    public Nevigation_drawer_item_click_listener listener;
    //public Nevigation_drawer_recycler_view_adapter(List<String>list1, List<Integer>list2, Context context)
    public Nevigation_drawer_recycler_view_adapter(Context context)
    {
     //   this.list1=list1;
       // this.list2=list2;
        this.list1= Centeral_class.menu_list;
        this.context=context;

    }

    @Override
    public Rec_view_holder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        return new Rec_view_holder(LayoutInflater.from(context).inflate(R.layout.rec_view_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(Rec_view_holder holder, final int position)
    {
        //holder.iv_nevigation_icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),list2.get(position)));
        holder.iv_nevigation_icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.list_options_icon));
        holder.tv_nevigation_option_name.setText(list1.get(position));
        holder.v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                listener.clicked(list1.get(position));
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return list1.size();
    }

    public class Rec_view_holder extends RecyclerView.ViewHolder
    {

        ImageView iv_nevigation_icon;
        TextView tv_nevigation_option_name;
        View v;
        public Rec_view_holder(View itemView)
        {
            super(itemView);
            iv_nevigation_icon= (ImageView) itemView.findViewById(R.id.iv_nevigation_icon);
            tv_nevigation_option_name= (TextView) itemView.findViewById(R.id.tv_nevigation_option_name);
            this.v=itemView;
        }
    }
}
