package com.sarahrobinson.finalyearproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class OnboardingActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnFinishOnboarding;

    // for getting user info to pass to home activity
    private String uid;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // getting uid for logged in user
        uid = getIntent().getExtras().getString("user_id");
        // getting imageUrl for logged in user
        imageUrl = getIntent().getExtras().getString("profile_picture");

        btnFinishOnboarding = (Button)findViewById(R.id.btnFinishOnboarding);

        // onclick listeners
        btnFinishOnboarding.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // update first time pref
        Utils.saveSharedSetting(OnboardingActivity.this, LoginActivity.PREF_USER_FIRST_TIME, "false");
        // pass user info to home activity
        Intent intentHome = new Intent(OnboardingActivity.this, HomeActivity.class);
        intentHome.putExtra("user_id", uid);
        intentHome.putExtra("profile_picture", imageUrl);
        // go to home screen
        startActivity(intentHome);
    }
}
