package grid.jobrace.admin.jobrace;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.SeekBar;

public class Splash_screen extends AppCompatActivity {

    SeekBar seekBar;
    Handler handler = new Handler();
    Runnable runnable;
    int k=10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        seekBar= (SeekBar) findViewById(R.id.seekBar);
        startAnimations();
    }
    private void startAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l = (LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);
        seekBar.setMax(1000);
        runnable = new Runnable() {
            @Override
            public void run() {
                seekBar.setProgress(k+=8);
             if(k>1000) {
                 Intent it = new Intent(Splash_screen.this, Check_required_detail_first_activity.class);
                 startActivity(it);
                 finish();
             }
                else handler.postDelayed(this,5);
            }
        };
        handler.postDelayed(runnable,0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(handler!=null){
            handler.removeCallbacks(runnable);
            handler=null;
        }
    }
}