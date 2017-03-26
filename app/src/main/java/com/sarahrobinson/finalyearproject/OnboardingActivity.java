package com.sarahrobinson.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

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
        Intent intentHome = new Intent(OnboardingActivity.this, HomeActivity.class);
        startActivity(intentHome);
    }
}
