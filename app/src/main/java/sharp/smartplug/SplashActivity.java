package sharp.smartplug;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences prefs;
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        prefs = getSharedPreferences("preference", Context.MODE_PRIVATE);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(prefs.getBoolean("instance",false) == false)
                {
                    Intent i = new Intent(SplashActivity.this,MainActivity.class);
                    startActivity(i);
                }
                else {

                    Intent i = new Intent(SplashActivity.this, ProbeListActivity.class);
                    startActivity(i);
                }
                finish();
            }
        },3000);
    }
}
