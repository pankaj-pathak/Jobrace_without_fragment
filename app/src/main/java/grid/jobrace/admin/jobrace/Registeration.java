package grid.jobrace.admin.jobrace;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import grid.jobrace.admin.jobrace.data_classes.Shared_preference_data_class;
import grid.jobrace.admin.jobrace.interfaces.Custom_interface_test;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class Registeration extends AppCompatActivity {

    EditText et_name,et_email,et_pass,et_phone;
    Spinner spinner_technology,spinner_year,spinner_month;
    Button btn_send;
    List<String> list_technology;
    List<String> list_year;
    List<String> list_month;

    ArrayAdapter<String> ada_technology,ada_year,ada_month;
    private ProgressDialog pd;
    TelephonyManager telephonyManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Check_connectivity.is_connected(this,false))
        {
            setContentView(R.layout.layout_not_connected_with_internet);
            ((Button)findViewById(R.id.btn_try_again)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Check_connectivity.is_connected(Registeration.this,false))
                    {
                        Intent it=getIntent();
                        finish();
                        startActivity(it);
                    }
                }
            });
            return;
        }
        setContentView(R.layout.activity_registeration);
        pd=new ProgressDialog(Registeration.this);
        pd.setCancelable(false);
        telephonyManager= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        et_name= (EditText) findViewById(R.id.et_name);
        et_email= (EditText) findViewById(R.id.et_email);
        et_pass= (EditText) findViewById(R.id.et_pass);
        spinner_technology= (Spinner) findViewById(R.id.spinner_technology);
        spinner_year= (Spinner) findViewById(R.id.spinner_year);
        spinner_month= (Spinner) findViewById(R.id.spinner_month);
        et_phone= (EditText) findViewById(R.id.et_phone);
        btn_send= (Button) findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(!Check_connectivity.is_connected(Registeration.this))
                return;
                if(isCompleteDetailInserted()) {
               register();
            }
            }
        });
        getSupportActionBar().setTitle("Registeration");
        list_technology=new ArrayList<>();
        getTechnology();
        ada_technology=new ArrayAdapter<String>(this,R.layout.spinner_item_layout,list_technology);
        spinner_technology.setAdapter(ada_technology);

        list_month=new ArrayList<>();
        list_year=new ArrayList<>();
        list_year.add("Select Year");
        list_month.add("Select Month");
        for(int i=0;i<=15;i++)
        {
            if(i<12)
                list_month.add(i+"");
            list_year.add(i+"");
        }
        ada_year=new ArrayAdapter<String>(this,R.layout.spinner_layout,list_year);
        ada_month=new ArrayAdapter<String>(this,R.layout.spinner_layout,list_month);
        spinner_year.setAdapter(ada_year);
        spinner_month.setAdapter(ada_month);
    }
    public void getTechnology()
    {
        if(!Check_connectivity.is_connected(Registeration.this))
            return;
        Retrofit retrofit=new Retrofit.Builder()
                .baseUrl(new Shared_preference_data_class(Registeration.this).getUrl())
                .addConverterFactory(new ToStringConverterFactory())
                .build();
        Custom_interface_test ctest=retrofit.create(Custom_interface_test.class);
        Call<String> call=ctest.getRegistrationTechnology();
        pd.show();
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Response<String> response, Retrofit retrofit)
            {
                if(response.code()==200) {
                    try {

                        list_technology.clear();
                        list_technology.add("Select Technology");
                        JSONArray ar = new JSONArray(response.body());
                        String result = ar.getString(0);
                        if (result != null && result.equals("sucess")) {
                            int i = 1;
                            while (i < ar.length()) {
                                JSONObject obj = ar.getJSONObject(i);
                                list_technology.add(obj.getString("QCategory"));
                                i++;
                            }
                            btn_send.setEnabled(true);
                        } else if (result.equals("fail"))
                            Toast.makeText(Registeration.this, R.string.server_error, Toast.LENGTH_LONG).show();
                        else if (result.equals("Access Denied"))
                            Toast.makeText(Registeration.this, "Access Denied.", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ada_technology.notifyDataSetChanged();
                }
                else
                {
                    Toast.makeText(Registeration.this, R.string.server_error, Toast.LENGTH_LONG).show();
                }
                pd.hide();
            }

            @Override
            public void onFailure(Throwable t)
            {
                Toast.makeText(Registeration.this,R.string.server_error,Toast.LENGTH_LONG).show();
                pd.hide();
            }
        });
    }
    public void register() {
        if (Check_connectivity.is_connected(Registeration.this)) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(new Shared_preference_data_class(this).getUrl())
                    .addConverterFactory(new ToStringConverterFactory())
                    .build();
            Custom_interface_test custom_interface_test = retrofit.create(Custom_interface_test.class);
            Call<String> call = custom_interface_test.registerUser(et_name.getText().toString(), et_email.getText().toString(), et_pass.getText().toString(), list_technology.get(spinner_technology.getSelectedItemPosition()), et_phone.getText().toString(),telephonyManager.getDeviceId()+"",spinner_year.getSelectedItem()+"."+spinner_month.getSelectedItem());
            pd.show();
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Response<String> response, Retrofit retrofit) {
                    if (response.code() == 200) {
                        try {
                            JSONArray ar = new JSONArray(response.body());
                            String result = ar.getString(0);
                            if (result != null && result.equals("sucess")) {
                                Toast.makeText(Registeration.this, "Registered Successfully.", Toast.LENGTH_LONG).show();
                                store_data();
                                Intent it = new Intent(Registeration.this, Homepage.class);
                                startActivity(it);
                                finish();
                            }
                            else if (result.equals("already registered"))
                                Toast.makeText(Registeration.this, "You are already registered.", Toast.LENGTH_LONG).show();
                            else if (result.equals("fail"))
                                Toast.makeText(Registeration.this, "You are not registered.", Toast.LENGTH_LONG).show();
                            else if (result.equals("Access Denied"))
                                Toast.makeText(Registeration.this, "Access Denied.", Toast.LENGTH_LONG).show();
                            else if(result.equals("Other account registered"))
                                Toast.makeText(Registeration.this, "Another account is already registered in this phone.", Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(Registeration.this, R.string.server_error, Toast.LENGTH_LONG).show();
                    }
                    pd.hide();
                }

                @Override
                public void onFailure(Throwable t) {
                    Toast.makeText(Registeration.this, R.string.server_error, Toast.LENGTH_LONG).show();
                    pd.hide();
                }
            });
        }
    }
    public void store_data()
    {
        Shared_preference_data_class sp=new Shared_preference_data_class(this);
        sp.initializeEditor();
        sp.setUserName(et_name.getText().toString());
        sp.setUserPassword(et_pass.getText().toString());
        sp.setUserEmail(et_email.getText().toString());
        sp.setTechnology(list_technology.get(spinner_technology.getSelectedItemPosition()));
        sp.setActivationKey(telephonyManager.getDeviceId()+"");
        sp.setExperience(spinner_year.getSelectedItem()+"."+spinner_month.getSelectedItem());
        sp.commitChanges();
    }
    public boolean isCompleteDetailInserted()
    {
        int selecteditem=spinner_technology.getSelectedItemPosition();
        if(et_name.getText().toString()==null || et_name.getText().toString().trim().equals(""))
        {
            showMessage("Please enter name.");
            return false;
        }
        else if(selecteditem<0)
        {
            Toast.makeText(Registeration.this, "Please refresh your page to load technology.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(selecteditem==0)
        {
            Toast.makeText(Registeration.this, "Please select any technology.", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(et_phone.getText().toString()==null || et_phone.getText().toString().trim().equals(""))
        {
            showMessage("Please enter phone number.");
            return false;
        }
        else if(et_phone.getText().toString().trim().length()<10)
        {
            showMessage("Please enter a valid phone number.");
            return false;
        }

        else if(et_email.getText().toString()==null || et_email.getText().toString().trim().equals(""))
        {
            showMessage("Please enter email.");
            return false;
        }
        else if(!isValidEmail(et_email.getText().toString()))
        {
            showMessage("Please enter a valid email id.");
            return false;
        }
        else if(et_pass.getText().toString()==null || et_pass.getText().toString().trim().equals(""))
        {
            showMessage("Please enter the password.");
            return false;
        }
        else if(!isContainsNumberOnly(et_phone.getText().toString().trim()))
        {
            showMessage("Mobile number should contain number only.");
            return false;
        }
        else if(spinner_year.getSelectedItemPosition()==0)
        {
            Toast.makeText(Registeration.this, "Please select experience ", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(spinner_month.getSelectedItemPosition()==0)
        {
            Toast.makeText(Registeration.this, "Please select experience", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }
    public boolean isContainsNumberOnly(String s)
    {
        for(int i=0;i<s.length();i++)
        {
            if(((int)s.charAt(i))>=58 || ((int)s.charAt(i))<48)
            {
                return false;
            }
        }
        return true;
    }
    public void showMessage(String message)
    {
        Toast.makeText(Registeration.this,message,Toast.LENGTH_LONG).show();
    }
    public boolean isValidEmail(String email)
    {
        if(!email.contains("@") || !email.contains("."))
            return false;
        else if(email.lastIndexOf("@")>email.lastIndexOf("."))
            return false;
        return true;
    }
    @Override
    public void onDestroy() {
        if(pd!=null)
            pd.dismiss();
        super.onDestroy();
    }
}
