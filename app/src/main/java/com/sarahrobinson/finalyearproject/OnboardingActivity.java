package com.sarahrobinson.finalyearproject;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;

public class OnboardingActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnFinishOnboarding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
    }

    @Override
    protected void onStart() {
        super.onStart();

        btnFinishOnboarding = (Button)findViewById(R.id.btnFinishOnboarding);

        // onclick listeners
        btnFinishOnboarding.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // TODO: 25/03/2017 set user_first_time_pref to false & go to home activity 
    }
}
