package grid.jobrace.admin.jobrace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import grid.jobrace.admin.jobrace.data_classes.Shared_preference_data_class;
import grid.jobrace.admin.jobrace.interfaces.Custom_interface_test;

import org.json.JSONArray;

import java.util.Random;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;


public class Forgot_password extends AppCompatActivity {
    Button btn_change_password;
    EditText et_otp;
    EditText et_pass1;
    EditText et_pass2;
    String OTP;
    ProgressDialog pd;
    public Forgot_password() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if(!Check_connectivity.is_connected(this,false))
        {
            setContentView(R.layout.layout_not_connected_with_internet);
            ((Button)findViewById(R.id.btn_try_again)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Check_connectivity.is_connected(Forgot_password.this,false))
                    {
                        Intent it=getIntent();
                        finish();
                        startActivity(it);
                    }
                }
            });
            return;
        }
        setContentView(R.layout.activity_forgot_password);
        et_otp= (EditText) findViewById(R.id.et_otp);
        et_pass1= (EditText)findViewById(R.id.et_pass1);
        et_pass2= (EditText)findViewById(R.id.et_pass2);
        pd=new ProgressDialog(this);
        pd.setCancelable(false);
        btn_change_password= (Button)findViewById(R.id.btn_change_password);
        btn_change_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Check_connectivity.is_connected(Forgot_password.this))
                    check_otp_pass_match();
            }
        });
        sendOtp();
     }


    public void sendOtp()
    {
        Random random=new Random();
        final String otp=random.nextInt(899999)+100000+"";
        Shared_preference_data_class sp=new Shared_preference_data_class(Forgot_password.this);
        if(sp.getUserEmail()==null || sp.getUserEmail().trim().equals(""))
        {
            Toast.makeText(getApplicationContext(), "Sorry you are not logged in this device.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if(!Check_connectivity.is_connected(this))
        {
            finish();
            return;

        }

        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.sendOtp(sp.getUserEmail(),otp+"");
        pd.show();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit)
            {
                if(response.code()==200)
                {
                    try {
                        JSONArray ar = new JSONArray(response.body());
                        String result=ar.getString(0);
                        if(result!=null && result.equals("sucess")) {
                            Toast.makeText(Forgot_password.this, "OTP has sended to your account.", Toast.LENGTH_SHORT).show();
                            OTP=otp;
                        }
                        else if(result.equals("fail"))
                            Toast.makeText(Forgot_password.this,"You are not registered.",Toast.LENGTH_LONG).show();
                        else if(result.equals("Access Denied"))
                            Toast.makeText(Forgot_password.this,"Access Denied.",Toast.LENGTH_LONG).show();

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(Forgot_password.this, R.string.server_error,Toast.LENGTH_LONG).show();
                }
                pd.hide();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(Forgot_password.this, R.string.server_error, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                pd.hide();
            }
        });
    }
    public void check_otp_pass_match()
    {
        String otp=et_otp.getText().toString();
        String pass1=et_pass1.getText().toString();
        String pass2=et_pass2.getText().toString();
        if(otp==null || otp.trim().equals(""))
        {
            Toast.makeText(Forgot_password.this,"Please enter the otp",Toast.LENGTH_LONG).show();
            return;
        }
        else if(pass1==null || pass1.trim().equals(""))
        {
            Toast.makeText(Forgot_password.this,"Please enter the new password",Toast.LENGTH_LONG).show();
            return;
        }
        else if(pass2==null || pass2.trim().equals(""))
        {
            Toast.makeText(Forgot_password.this,"Please reenter the otp",Toast.LENGTH_LONG).show();
            return;
        }
        else if(!otp.equals(OTP))
        {
            Toast.makeText(Forgot_password.this,"Please enter correct OTP",Toast.LENGTH_LONG).show();
            return;
        }
        else if(!pass1.equals(pass2))
        {
            Toast.makeText(Forgot_password.this,"Both password should be same",Toast.LENGTH_LONG).show();
            return;
        }
        change_password(pass1);

    }

    public void change_password(String newPassword)
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Shared_preference_data_class sp=new Shared_preference_data_class(Forgot_password.this);
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.changePassword(sp.getUserEmail(),newPassword);
        pd.show();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit) {
                if(response.code()==200)
                {
                    try {
                        JSONArray ar = new JSONArray(response.body());
                        String result=ar.getString(0);
                        if(result!=null && result.equals("sucess")) {
                            Toast.makeText(Forgot_password.this, "Password has been changed successfully", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else if(result.equals("fail"))
                            Toast.makeText(Forgot_password.this,"You are not registered.",Toast.LENGTH_LONG).show();
                        else if(result.equals("Access Denied"))
                            Toast.makeText(Forgot_password.this,"Access Denied.",Toast.LENGTH_LONG).show();

                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else
                {
                    Toast.makeText(Forgot_password.this, R.string.server_error,Toast.LENGTH_LONG).show();
                }
                pd.hide();
            }

            @Override
            public void onFailure(Throwable t) {
                Toast.makeText(Forgot_password.this,  R.string.server_error, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
                pd.hide();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pd!=null)
            pd.dismiss();
    }
}
