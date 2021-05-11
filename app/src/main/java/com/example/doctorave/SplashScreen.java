package com.example.doctorave;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.example.doctorave.HomeActivity.HomeActivity;
import com.example.doctorave.LoginFragments.RegisterFragment;
import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {

            if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
            } else
                startActivity(new Intent(SplashScreen.this, HomeActivity.class));
            finish();
        }, 2000);
    }
}