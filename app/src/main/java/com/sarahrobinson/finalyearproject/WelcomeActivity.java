package com.sarahrobinson.finalyearproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSignUp;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this); // facebook analytics

        firebaseAuth = FirebaseAuth.getInstance();

        btnSignUp = (Button)findViewById(R.id.btnSignUpWelcome);
        btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        finish();
        Intent signUp = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(signUp);
    }
}
