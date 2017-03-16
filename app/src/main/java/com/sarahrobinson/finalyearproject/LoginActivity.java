package com.sarahrobinson.finalyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private EditText editTextLogInEmail;
    private EditText editTextLogInPassword;
    private Button btnLogIn;
    private TextView btnSignUpPrompt;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        /*
        // checking if user is already logged in
        if (firebaseAuth.getCurrentUser() != null){
            // TODO: 16/03/2017 take user to home screen (create new fragment)
            finish();
            // start HomeActivity
            Intent i = new Intent(LoginActivity.this,HomeActivity.class);
            startActivity(i);
        }
        */

        editTextLogInEmail = (EditText)findViewById(R.id.editTextLoginEmail);
        editTextLogInPassword = (EditText)findViewById(R.id.editTextLoginPassword);
        btnLogIn = (Button)findViewById(R.id.btnLogIn);
        btnSignUpPrompt = (TextView)findViewById(R.id.btnSignUpPrompt);

        progressDialog = new ProgressDialog(this);

        btnLogIn.setOnClickListener(this);
        btnSignUpPrompt.setOnClickListener(this);
    }

    private void userLogin(){
        String email = editTextLogInEmail.getText().toString().trim();
        String password = editTextLogInPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            // email field is empty
            Toast.makeText(this, "Email address is required", Toast.LENGTH_SHORT).show();
            // stopping function from executing further
            return;
        }

        if(TextUtils.isEmpty(password)){
            // password field is empty
            Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show();
            // stopping function from executing further
            return;
        }

        // entries valid, showing progress dialog
        progressDialog.setMessage("Logging in...");
        progressDialog.show();

        // logging in
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            // TODO: 16/03/2017 take user to onboarding or home screen

                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            Log.d("LoginActivity", "Display name: " + user.getDisplayName());

                            finish();
                            // start HomeActivity
                            Intent i = new Intent(LoginActivity.this,HomeActivity.class);
                            startActivity(i);
                        }else{
                            progressDialog.dismiss();
                            // TODO: 16/03/2017 check why login was unsuccessful and notify user, e.g. invalid password
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogIn){
            // log in
            userLogin();
        }

        if (view == btnSignUpPrompt){
            finish();
            // start SignUpActivity
            Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(i);
        }
    }
}
