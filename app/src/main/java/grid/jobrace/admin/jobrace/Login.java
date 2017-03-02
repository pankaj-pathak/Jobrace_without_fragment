package grid.jobrace.admin.jobrace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import grid.jobrace.admin.jobrace.data_classes.Shared_preference_data_class;
import grid.jobrace.admin.jobrace.dialogs.Custom_dialog_update_imei;
import grid.jobrace.admin.jobrace.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Retrofit;

public class Login extends AppCompatActivity
{

    EditText et_email, et_pass;
    Button btn_login,btn_register,btn_forgot_password;
    String URL ="";
    TelephonyManager telephonyManager;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        URL= new Shared_preference_data_class(this).getUrl()+"/LoginNew";
        telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        et_email = (EditText) findViewById(R.id.et_email);
        et_pass = (EditText) findViewById(R.id.et_pass);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register= (Button) findViewById(R.id.btn_register);
        btn_forgot_password=(Button) findViewById(R.id.btn_forgot_password);
        btn_login.setOnClickListener(
                new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isValidEmail(et_email.getText().toString().trim())) {
                    Toast.makeText(Login.this, "Please enter the correct email id", Toast.LENGTH_LONG).show();
                return;
                }
                if(et_pass.getText().toString().trim().equals("")){
                    Toast.makeText(Login.this,"Please enter the Password",Toast.LENGTH_LONG).show();
                    return;
                }
                if(Check_connectivity.is_connected(Login.this) )
                {
                    pd.show();
                    login();
                }

            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(Login.this,Registeration.class);
                startActivity(it);
                finish();
            }
        });
        btn_forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it=new Intent(Login.this,Forgot_password.class);
                startActivity(it);
           }
        });
        pd=new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);

    }

    public boolean isValidEmail(String email)
    {
        if(!email.contains("@") || !email.contains("."))
            return false;
        else if(email.lastIndexOf("@")>email.lastIndexOf("."))
            return false;
        return true;
    }

    class Myclass extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... voids) {
            String data = null;
            try {
                java.net.URL url = new URL(URL);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                httpConn.setDoInput(true);
                httpConn.setAllowUserInteraction(false);
                httpConn.setInstanceFollowRedirects(true);
                httpConn.setRequestMethod("POST");
                httpConn.setDoInput(true);
                httpConn.setDoOutput(true);

                httpConn.setConnectTimeout(15000);
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("Email", voids[0])
                        .appendQueryParameter("Password", voids[1])
                        .appendQueryParameter("IMEINumber",telephonyManager.getDeviceId()+"");
                String query = builder.build().getEncodedQuery();

                OutputStream os = httpConn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                httpConn.connect();

                int resCode = httpConn.getResponseCode();

                if (resCode == HttpURLConnection.HTTP_OK) {
                    InputStream is = httpConn.getInputStream();
                    BufferedReader br = new BufferedReader(new InputStreamReader(is));
                    String s1;
                    StringBuffer sb = new StringBuffer();
                    while ((s1 = br.readLine()) != null) {
                        sb.append(s1);
                    }
                    data = sb.toString();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if(s!=null && !s.trim().equals(""))

                {
                    JSONArray ar=new JSONArray(s);
                    JSONObject obj = ar.getJSONObject(0);
                    s = obj.getString("result");
                    if (s != null && s.trim().equals("sucess"))
                    {
                        store_data();
                        Homepage.isLoggegIn=1;
                        Intent it = new Intent(Login.this, Homepage.class);
                        startActivity(it);
                        finish();
                    }
                    else if(s != null && s.trim().equals("imei incorrect"))
                    {
                        Toast.makeText(getApplicationContext(), "You are already registered in another device...", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(), R.string.server_error, Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Login failed.", Toast.LENGTH_LONG).show();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            pd.hide();
        }

    }
    public void login()
    {
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(Login.this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test custom_interface_test=retrofit.create(Custom_interface_test.class);
        Call<String> call=custom_interface_test.login(et_email.getText().toString(), et_pass.getText().toString(),telephonyManager.getDeviceId()+"");
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(retrofit.Response<String> response, Retrofit retrofit) {
                JSONArray ar= null;
                try {
                    ar = new JSONArray(response.body());
                    String result=ar.getString(0);
                    if(result!=null && result.equals("sucess"))
                    {
                        store_data();
                        Homepage.isLoggegIn=1;
                        Intent it = new Intent(Login.this, Homepage.class);
                        startActivity(it);
                        finish();
                    }
                    else if(result != null && result.trim().equals("imei incorrect")) {
                        Custom_dialog_update_imei.Imeiregistered imeiregistered=new Custom_dialog_update_imei.Imeiregistered() {
                            @Override
                            public void status(int n) {
                                if(n==1)
                                {
                                    store_data();
                                    Homepage.isLoggegIn=1;
                                    Intent it = new Intent(Login.this, Homepage.class);
                                    startActivity(it);
                                    finish();
                                }
                            }
                        };
                        Custom_dialog_update_imei custom_dialog_update_imei=new Custom_dialog_update_imei(Login.this,et_email.getText().toString(),pd,imeiregistered);
                        custom_dialog_update_imei.show();
                    }
                        else if(result.equals("fail"))
                        Toast.makeText(Login.this,"Username or password is incorrect.",Toast.LENGTH_LONG).show();
                    else if(result.equals("Access Denied"))
                        Toast.makeText(Login.this,"Access Denied.",Toast.LENGTH_LONG).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                pd.hide();

            }

            @Override
            public void onFailure(Throwable t)
            {
                Toast.makeText(Login.this,R.string.server_error,Toast.LENGTH_LONG).show();
                pd.hide();
            }
        });
    }

    public void store_data()
    {
        Shared_preference_data_class sp=new Shared_preference_data_class(this);
        sp.initializeEditor();
        sp.setUserEmail(et_email.getText().toString());
        sp.setUserPassword(et_pass.getText().toString());
        sp.commitChanges();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(pd!=null)
            pd.dismiss();
    }

}
