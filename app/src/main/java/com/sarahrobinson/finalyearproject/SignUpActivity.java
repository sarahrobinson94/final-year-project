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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import org.w3c.dom.Text;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;

    private EditText editTextSignUpName;
    private EditText editTextSignUpEmail;
    private EditText editTextSignUpPassword;
    private Button btnSignUp;
    private TextView btnLogInPrompt;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firebaseAuth = FirebaseAuth.getInstance();

        // checking if user is already logged in
        if (firebaseAuth.getCurrentUser() != null){
            // TODO: 16/03/2017 take user to home screen (create new fragment)
            finish();
            // start HomeActivity
            Intent i = new Intent(SignUpActivity.this,HomeActivity.class);
            startActivity(i);
        }

        editTextSignUpName = (EditText)findViewById(R.id.editTextSignUpName);
        editTextSignUpEmail = (EditText)findViewById(R.id.editTextSignUpEmail);
        editTextSignUpPassword = (EditText)findViewById(R.id.editTextSignUpPassword);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnLogInPrompt = (TextView)findViewById(R.id.btnLogInPrompt);

        progressDialog = new ProgressDialog(this);

        btnSignUp.setOnClickListener(this);
        btnLogInPrompt.setOnClickListener(this);

    }

    private void registerUser(){
        final String name = editTextSignUpName.getText().toString().trim();
        String email = editTextSignUpEmail.getText().toString().trim();
        String password = editTextSignUpPassword.getText().toString().trim();

        if(TextUtils.isEmpty(name)){
            // name field is empty
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            // stopping function from executing further
            return;
        }

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
        progressDialog.setMessage("Creating your account...");
        progressDialog.show();

        // registering user to firebase server
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // adding display name to user profile
                            final FirebaseUser user = firebaseAuth.getCurrentUser();
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            user.updateProfile(profileUpdates)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.d("SignUpActivity", "User profile updated with display name");
                                            }
                                        }
                                    });

                            progressDialog.dismiss();
                            Toast.makeText(SignUpActivity.this, "Your account has been created",
                                    Toast.LENGTH_SHORT).show();

                            // TODO: 12/03/2017 take user to login screen
                            finish();
                            // start LoginActivity
                            Intent login = new Intent(SignUpActivity.this,LoginActivity.class);
                            startActivity(login);

                        }else{
                            progressDialog.dismiss();
                            // TODO: 16/03/2017 check why signup was unsuccessful and notify user, e.g. invalid password
                            Toast.makeText(SignUpActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        if (view == btnSignUp){
            registerUser();
        }

        if (view == btnLogInPrompt){
            // end this activity
            finish();
            // start LoginActivity
            Intent i = new Intent(SignUpActivity.this,LoginActivity.class);
            startActivity(i);
        }
    }
}
