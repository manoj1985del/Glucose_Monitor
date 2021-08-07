package com.app.glucosemonitor.baseactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.app.glucosemonitor.R;
import com.app.glucosemonitor.auth.LoginActivity;
import com.app.glucosemonitor.model.User;
import com.app.glucosemonitor.utils.CommonVariables;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivity extends AppCompatActivity {
    Boolean splashLoaded = false;
    Handler handler;
    View mContentView;
    ImageView splashAnim;
    FirebaseAuth auth;
    FirebaseFirestore db;
    CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (!splashLoaded) {
            setContentView(R.layout.activity_splash);
            splashAnim = findViewById(R.id.splash_img);
            mContentView = splashAnim;
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            handler = new Handler();
            handler.postDelayed(this::startTimer, 0);
            splashLoaded = true;
        }
    }

    private void startTimer() {
        timer = new CountDownTimer(3500, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {

                if (auth.getCurrentUser() != null) {
                    getUserDetails();
                } else {
                    LaunchLoginActivity();
                }
            }
        }.start();
    }

    public void getUserDetails() {
        CommonVariables.FirebaseUserId = auth.getUid();
        DocumentReference docRef = db.collection("users").document(CommonVariables.FirebaseUserId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    CommonVariables.loggedInUserDetails = document.toObject(User.class);
                    LaunchHomeActivity();
                }
            } else {
                auth.signOut();
                LaunchLoginActivity();
                Log.d("TAG", "get failed with ", task.getException());
            }
        });
    }


    private void LaunchLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void LaunchHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}