package com.smartbus.app.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.smartbus.app.R;
import com.smartbus.app.utils.SessionManager;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private static final long SPLASH_DELAY_MS = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logoImage = findViewById(R.id.iv_logo);
        TextView appNameText = findViewById(R.id.tv_app_name);

        Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_logo_pop_in);
        Animation textAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_text_slide_fade);

        logoImage.startAnimation(logoAnimation);
        appNameText.startAnimation(textAnimation);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            SessionManager sessionManager = new SessionManager(this);
            Intent intent;

            if (sessionManager.isLoggedIn()) {
                intent = new Intent(SplashActivity.this, MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }, SPLASH_DELAY_MS);
    }
}
