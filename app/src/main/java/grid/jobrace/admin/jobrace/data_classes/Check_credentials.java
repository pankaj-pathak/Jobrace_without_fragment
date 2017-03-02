package grid.jobrace.admin.jobrace.data_classes;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.widget.Toast;


import grid.jobrace.admin.jobrace.Activation_custom_dialog;
import grid.jobrace.admin.jobrace.dialogs.Custom_dialog_for_message;

import java.util.Date;
import java.util.List;

/**
 * Created by Pankaj on 11/23/2016.
 */

public class Check_credentials
{
   public static boolean isActivated(Activity activity)
    {
        SharedPreferences sp=activity.getSharedPreferences("current_user_details",activity.MODE_PRIVATE);
        if(sp.getString("activation_key","").trim().equals(""))
        {
            Activation_custom_dialog dialog=new Activation_custom_dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.show();
            return false;
        }
        return true;
    }
    public static boolean isEligibleToViewJob(Activity activity) {
        Shared_preference_data_class sp=new Shared_preference_data_class(activity);
        Date d=new Date();
        String today=(d.getDate()<10?"0"+d.getDate():d.getDate())+"-"+(d.getMonth()<9?"0"+(d.getMonth()+1):(d.getMonth()+1))+"-"+(1900+d.getYear());
        if(sp.getCardType().equals("Free"))
        {
            if(sp.getLastJobViewdDate().equals(today) && Integer.parseInt(sp.getNoOfJobsOpened().trim())>=1)
            {
                Custom_dialog_for_message custom_dialog_for_message=new Custom_dialog_for_message(activity,"You can view only one job every day.Update Your Card to increase the limit.");
                custom_dialog_for_message.show();
                return false;
            }
        }
        else if(sp.getCardType().equals("Brownze"))
        {
            if(sp.getLastJobViewdDate().equals(today) && Integer.parseInt(sp.getNoOfJobsOpened().trim())>=3)
            {
                Custom_dialog_for_message custom_dialog_for_message=new Custom_dialog_for_message(activity,"You can view only one job every day.Update Your Card to increase the limit.");
                custom_dialog_for_message.show();
                return false;
            }
        }

        return true;
    }

    public static boolean isEligibleToGiveVirtualInterview(Activity activity) {
        Shared_preference_data_class sp=new Shared_preference_data_class(activity);
        Date d=new Date();
        String today=(d.getDate()<10?"0"+d.getDate():d.getDate())+"-"+(d.getMonth()<9?"0"+(d.getMonth()+1):(d.getMonth()+1))+"-"+(1900+d.getYear());
        if(sp.getLastVirtualInterviewAppliedDate().equals(today)) {
            Toast.makeText(activity, "You can apply for only one test every today.", Toast.LENGTH_LONG).show();
            return false;
        }
        else if(sp.getPremiumEndDate().compareTo(new StringBuffer(today).reverse().toString())<=0) {
            Toast.makeText(activity, "You card validity is expired. Please recharge your card.", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    public static List<String> hasGrantedPermissions(Activity activity,List<String> permissions)
    {
        int i=0;
        while(i<permissions.size())
        {
            if(ContextCompat.checkSelfPermission(activity,permissions.get(i))==PackageManager.PERMISSION_GRANTED)
            {
                permissions.remove(i);
                i--;
            }
            i++;
        }
        return permissions;
    }

    public static void askForPermissions(Activity activity,List<String> permissions)
    {
        String per[]=new String[permissions.size()];
        for(int i=0;i<permissions.size();i++)
        {
            per[i]=permissions.get(i);
        }
        ActivityCompat.requestPermissions(activity,
                per,
                123);
    }
}
