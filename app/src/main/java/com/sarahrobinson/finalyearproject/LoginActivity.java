package com.sarahrobinson.finalyearproject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.FirebaseException;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.widget.LoginButton;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    public User user;

    private Firebase firebaseRef;
    private FirebaseAuth firebaseAuth;
    //private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser firebaseUser;

    // facebook callbackManager
    private CallbackManager callbackManager;

    private ProgressDialog progressDialog;

    private EditText editTextLogInEmail;
    private EditText editTextLogInPassword;
    private Button btnLogIn;
    private LoginButton btnLogInFacebook;
    private Button btnLogInGoogle;
    private TextView btnSignUpPrompt;

    private static final String TAG = "LoginActivity ******* ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initializing facebook sdk before setContentView
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        // setting Android context before using Firebase
        Firebase.setAndroidContext(this);

        firebaseRef = new Firebase("https://final-year-project-12698.firebaseio.com/");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // checking if user is logged in
        if (firebaseUser != null){
            // user is logged in
            // TODO: 16/03/2017 take user to home screen (create new fragment)
            Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
            String uid = firebaseUser.getUid();

            // passing profile image to HomeActivity if the user has one
            if (firebaseUser.getPhotoUrl() != null){
                String image = firebaseUser.getPhotoUrl().toString();
                homeIntent.putExtra("profile_picture", image);
            }

            // passing user id to HomeActivity
            homeIntent.putExtra("user_id", uid);

            finish();
            startActivity(homeIntent);

            // TODO: 19/03/2017 add AuthStateListener ?? (see android bash blog post)
        }

        // facebook login
        callbackManager = CallbackManager.Factory.create();
        btnLogInFacebook = (LoginButton) findViewById(R.id.btnFbLoginLarge);
        btnLogInFacebook.setReadPermissions("email", "public_profile");
        btnLogInFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess " + loginResult);
                logInWithFacebook(loginResult.getAccessToken());
            }
            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }
            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError " + error);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart()");

        editTextLogInEmail = (EditText)findViewById(R.id.editTextLoginEmail);
        editTextLogInPassword = (EditText)findViewById(R.id.editTextLoginPassword);
        btnLogIn = (Button)findViewById(R.id.btnLogIn);
        btnSignUpPrompt = (TextView)findViewById(R.id.btnSignUpPrompt);
        btnLogInGoogle = (Button) findViewById(R.id.btnGoogleLoginLarge);
        progressDialog = new ProgressDialog(this);

        // onclick listeners
        btnLogIn.setOnClickListener(this);
        btnLogInFacebook.setOnClickListener(this);
        btnSignUpPrompt.setOnClickListener(this);

        //firebaseAuth.addAuthStateListener(firebaseAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "onStop()");
        /*
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    // called if user clicks normal login button (email & password login)
    private void userLogin(){
        Log.d(TAG, "userLogin()");

        String email = editTextLogInEmail.getText().toString().trim();
        String password = editTextLogInPassword.getText().toString().trim();

        // validate entries
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
                        if (task.isSuccessful()) {
                            //Log.d("LoginActivity", "Display name: " + firebaseUser.getDisplayName());

                            // setting up user on firebase database ??
                            setUpUser();

                            // TODO: 16/03/2017 take user to onboarding or home screen
                            Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            firebaseUser = firebaseAuth.getCurrentUser();
                            String uid = firebaseUser.getUid();
                            homeIntent.putExtra("user_id", uid);
                            progressDialog.dismiss();
                            finish();
                            startActivity(homeIntent);
                        }else{
                            Log.w(TAG, "signInWithEmail", task.getException());
                            progressDialog.dismiss();
                            // TODO: 16/03/2017 check why login was unsuccessful and notify user, e.g. invalid password
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    // setting up user on firebase database ??
    protected void setUpUser() {
        user = new User();
        user.setEmail(editTextLogInEmail.getText().toString().trim());
    }

    // called on facebook success callback
    private void logInWithFacebook(AccessToken token) {
        Log.d(TAG, "logInWithFacebook: " + token);

        progressDialog.setMessage("Logging in..."); // TODO: 20/03/2017 message may need to change
        progressDialog.show();

        AuthCredential credential = FacebookAuthProvider.getCredential((token.getToken()));
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete: " + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // sign in succeeded...
                            // the auth state listener will be notified and logic to handle
                            // the signed in user can be handled in the listener
                            String uid = task.getResult().getUser().getUid();
                            String name = task.getResult().getUser().getDisplayName();
                            String email = task.getResult().getUser().getEmail();
                            String image = task.getResult().getUser().getPhotoUrl().toString();

                            // TODO: 20/03/2017 does this save the user's changes from other times using the app ??
                            // create user and save in firebase database ??
                            user = new User(uid, name, email);
                            firebaseRef.child(uid).setValue(user);

                            // TODO: 20/03/2017 take user to onboarding or home screen
                            Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
                            homeIntent.putExtra("user_id", uid);
                            homeIntent.putExtra("profile_picture", image);

                            progressDialog.dismiss();
                            finish();
                            startActivity(homeIntent);
                        } else {
                            // sign in failed...
                            progressDialog.dismiss();
                            // displaying error message to user
                            Log.w(TAG, "signInWithCredential", task.getException());
                            // TODO: 22/03/2017 fix this so user can't log in with facebook if email address already in use
                            if(task.getException().toString().contains("FirebaseAuthUserCollisionException")){
                                Toast.makeText(LoginActivity.this, "An account already exists with the same email address. " +
                                        "Please disable this account before logging in with Facebook.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view.toString());
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
