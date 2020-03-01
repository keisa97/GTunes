package com.example.musicwithnav.ui.MyAccount.ui.myaccount;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.musicwithnav.MainActivity;
import com.example.musicwithnav.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {


    private FirebaseAuth mAuth = null;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //time of splash screen till move to wanted activity
    private final int SPLASH_DISPLAY_LENGTH = 1000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null)
            actionBar.hide();
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                mAuth = FirebaseAuth.getInstance();
               // mAuthListener = new FirebaseAuth.AuthStateListener() {

                    //public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // User is signed in, send to mainmenu
                           // Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        } else {
                            // User is signed out, send to register/login
                            startActivity(new Intent(SplashScreenActivity.this, MyAccount_activity.class));
                        }
                  //  }
               // };
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
