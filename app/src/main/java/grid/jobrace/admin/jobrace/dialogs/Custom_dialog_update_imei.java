package grid.jobrace.admin.jobrace.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import grid.jobrace.admin.jobrace.Check_connectivity;
import grid.jobrace.admin.jobrace.R;
import grid.jobrace.admin.jobrace.ToStringConverterFactory;
import grid.jobrace.admin.jobrace.data_classes.Shared_preference_data_class;
import grid.jobrace.admin.jobrace.interfaces.Custom_interface_test;

import org.json.JSONArray;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by pankaj on 2/18/2017.
 */

public class Custom_dialog_update_imei  extends Dialog
{
    private  Activity activity;
    private Button btn_cancel;
    private Button btn_activate;
    private String email;
    private ProgressDialog pd;
    private Imeiregistered imeiregistered;
    public Custom_dialog_update_imei(final Activity activity, String email, ProgressDialog pd,Imeiregistered imeiregistered)
    {
        super(activity);
        this.activity=activity;
        this.email=email;
        this.pd=pd;
        this.imeiregistered=imeiregistered;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog_update_imei);
        btn_activate= (Button) findViewById(R.id.btn_activate);
        btn_cancel= (Button) findViewById(R.id.btn_cancel);
        btn_activate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                register();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Custom_dialog_update_imei.this.dismiss();
            }
        });

    }
    public void register() {
        if (Check_connectivity.is_connected(activity)) {
            Shared_preference_data_class sp=new Shared_preference_data_class(activity);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(sp.getUrl())
                    .addConverterFactory(new ToStringConverterFactory())
                    .build();
            Custom_interface_test custom_interface_test = retrofit.create(Custom_interface_test.class);
            Call<String> call = custom_interface_test.updateCandidateInfo( email,((TelephonyManager)activity.getSystemService(activity.TELEPHONY_SERVICE)).getDeviceId());
            pd.show();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Response<String> response, Retrofit retrofit) {
                    if (response.code() == 200) {
                        try {
                            JSONArray ar = new JSONArray(response.body());
                            String result = ar.getString(0);
                            if (result != null && result.equals("sucess")) {
                                imeiregistered.status(1);
                            }
                            else
                                Toast.makeText(activity, "Unable to connect with server. Please try again.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(activity, "Unable to connect with server. Please try again..."+response.code(), Toast.LENGTH_LONG).show();
                    }
                    pd.hide();
                    Custom_dialog_update_imei.this.dismiss();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(activity, "Some error occured", Toast.LENGTH_LONG).show();
                    pd.hide();
                    Custom_dialog_update_imei.this.dismiss();
                }
            });
        }
    }
    public interface Imeiregistered
    {
        public void status(int n);
    }
}
