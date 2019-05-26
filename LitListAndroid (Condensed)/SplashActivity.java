package com.litlist;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    final String key = "HAS_SAVED_DATA";
    Intent intent;

    TextView titleTextView, subtitleTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        prefs = getSharedPreferences("com.litlist", Context.MODE_PRIVATE);
        editor = prefs.edit();

        if (prefs.getBoolean(key, false)) {
            intent = new Intent(this, NavigationActivity.class);
        } else {
            intent = new Intent(this, StartupActivity.class);
        }

        titleTextView = (TextView)findViewById(R.id.litlist_textview_id);
        subtitleTextView = (TextView)findViewById(R.id.subtitle_textview_id);
        subtitleTextView.setVisibility(View.INVISIBLE);
        final AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f),
                       animation2 = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                animation2.setDuration(1000);
                animation2.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        subtitleTextView.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                subtitleTextView.startAnimation(animation2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        titleTextView.startAnimation(animation);
    }
}
