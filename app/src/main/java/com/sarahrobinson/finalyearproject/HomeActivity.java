package com.sarahrobinson.finalyearproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private TextView textViewHomeWelcome;
    private Button btnLogOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseAuth = FirebaseAuth.getInstance();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        // start login activity if user is not logged in
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
        }else{
            Log.d("HomeActivity", "User email: " + user.getEmail());
            Log.d("HomeActivity", "User display name: " + user.getDisplayName());
        }

        textViewHomeWelcome = (TextView)findViewById(R.id.textViewHomeWelcome);
        textViewHomeWelcome.setText("Welcome " + user.getDisplayName());

        btnLogOut = (Button)findViewById(R.id.btnLogOut);

        btnLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogOut){
            firebaseAuth.signOut();
            finish();
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}
