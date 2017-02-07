package com.example.pankaj.jobrace_without_fragment.data_classes;


import com.example.pankaj.jobrace_without_fragment.adapter.Nevigation_drawer_recycler_view_adapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pankaj on 12/9/2016.
 */

public class Centeral_class
{
    //public static String BASE_URL="http://jobrace.grid.net.in/jobraceapi/index.php/";
    //public static Activity activity;
    /*
    private static String BASE_URL;
    public static String getUrl(Activity activity)
    {
        BASE_URL=new Shared_preference_data_class(activity).getUrl();
    return BASE_URL;
    }
    */
    public static List<String> menu_list=new ArrayList();
    public static Nevigation_drawer_recycler_view_adapter ada;
}
