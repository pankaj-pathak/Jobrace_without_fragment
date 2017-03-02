package grid.jobrace.admin.jobrace.data_classes;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * Created by Pankaj on 11/24/2016.
 */

public class Shared_preference_data_class
{
    private SharedPreferences.Editor edit;
    private SharedPreferences sp;
    public Shared_preference_data_class(Activity activity)
    {
        sp=activity.getSharedPreferences("current_user_details",Activity.MODE_PRIVATE);
    }
    public void initializeEditor()
    {
        edit=sp.edit();
    }
    public String getUserName()
    {
        return sp.getString("user_name","");
    }
    public void setUserName(String value)
    {
        edit.putString("user_name",value);
    }
    public String getExperience()
    {
        return sp.getString("experience","");
    }
    public void setExperience(String value)
    {
        edit.putString("experience",value);
    }
    public String getUserEmail()
    {
        return sp.getString("user_email","");
    }
    public void setUserEmail(String value)
    {
        edit.putString("user_email",value);
    }
    public String getUserPassword()
    {
        return sp.getString("user_password","");
    }
    public void setUserPassword(String value)
    {
        edit.putString("user_password",value);
    }
    public String getTechnology()
    {
        return sp.getString("technologies","");
    }
    public void setTechnology(String value)
    {
        edit.putString("technologies",value);
    }
    public String getActivationKey()
    {
        return sp.getString("activation_key","");
    }
    public void setActivationKey(String value)
    {
        edit.putString("activation_key",value);
    }
    public String getCardType()
    {
        return sp.getString("card_type","Free");
    }
    public void setCardType(String value)
    {
        edit.putString("card_type",value);
    }

    public String getLastJobViewdDate()
    {
        return sp.getString("last_job_viewed_date","0");
    }
    public void setLastJobViewedDate(String value)
    {
        edit.putString("last_job_viewed_date",value);
    }

    public String getNoOfJobsOpened()
    {
        return sp.getString("no_of_jobs_opened","0");
    }
    public void setNoOfJobsOpened(String value)
    {
        edit.putString("no_of_jobs_opened",value);
    }

    protected String getLastVirtualInterviewAppliedDate()
    {
        return sp.getString("last_virtual_interview_applied_date","");
    }
    public void setLastVirtualInterviewAppliedDate(String value)
    {
        edit.putString("last_virtual_interview_applied_date",value);

    }

    public String getUrl()
    {
        return sp.getString("url","http://jobrace.in/jobraceapi/");
    }
    public void setUrl(String value)
    {
        edit.putString("url",value);
    }


    public boolean getIsAutologin()
    {
        return sp.getBoolean("is_auto_login",false);
    }
    public void setIsAutoLogin(boolean value)
    {
        edit.putBoolean("is_auto_login",value);
    }
    public String getContactNo()
    {
        return sp.getString("ContactNo","");
    }
    public void setContactNo(String value)
    {
        edit.putString("ContactNo",value);
    }
    public int getTotalInterviews()
    {
        return sp.getInt("TotalInterviews",0);
    }
    public void setTotalInterviews(int value)
    {
        edit.putInt("TotalInterviews",value);
    }
    protected String getPremiumEndDate()
    {
        return sp.getString("PremiumEndDate","");
    }
    public void setPremiumEndDate(String value)
    {
        edit.putString("PremiumEndDate",value);
    }
    public void commitChanges()
    {
        edit.commit();
    }
    public void clearAllData()
    {
        setActivationKey("");
        setUserPassword("");
        setUserEmail("");
        setUserName("");
        setCardType("");
        setTechnology("");
    }

}
