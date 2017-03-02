package grid.jobrace.admin.jobrace;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import grid.jobrace.admin.jobrace.data_classes.Shared_preference_data_class;

public class Setting_activity extends AppCompatActivity {
    Switch switch_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_activity);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        LinearLayout actionBarLayout = (LinearLayout)getLayoutInflater().inflate(R.layout.toolbar_layout, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.LEFT);

        actionBar.setCustomView(actionBarLayout, params);
        actionBar.setDisplayHomeAsUpEnabled(true);
        switch_btn= (Switch) findViewById(R.id.switch_btn);
        switch_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Shared_preference_data_class sp=new Shared_preference_data_class(Setting_activity.this);
                sp.initializeEditor();
                sp.setIsAutoLogin(b);
                sp.commitChanges();
            }
        });
        Shared_preference_data_class sp=new Shared_preference_data_class(Setting_activity.this);
        switch_btn.setChecked(sp.getIsAutologin());
    }
}
