package com.sarahrobinson.finalyearproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;

public class WelcomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_welcome);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setProviders(
                        AuthUI.FACEBOOK_PROVIDER,
                        AuthUI.GOOGLE_PROVIDER)
                .build(), 1);

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
