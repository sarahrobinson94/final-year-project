package com.sarahrobinson.finalyearproject.activities;

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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.FacebookSdk;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.classes.User;

import java.util.Arrays;
import java.util.List;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    public User user;

    private Firebase firebaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseUser firebaseUser;
    private Firebase userRef;

    // facebook log in
    private CallbackManager callbackManager;

    // google sign in
    private GoogleApiClient googleApiClient;
    private static int RC_SIGN_IN = 289;

    private ProgressDialog progressDialog;

    private EditText editTextLogInEmail;
    private EditText editTextLogInPassword;
    private Button btnLogIn;
    private Button btnLogInFacebook;
    private SignInButton btnLogInGoogle;
    private TextView btnSignUpPrompt;

    private static final String TAG = "LoginActivity ******* ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initializing facebook sdk before setContentView
        FacebookSdk.sdkInitialize(getApplicationContext());

        setContentView(R.layout.activity_login);

        // setting android context before using firebase
        Firebase.setAndroidContext(this);

        firebaseRef = new Firebase("https://final-year-project-12698.firebaseio.com/");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // checking if user is already logged in
        if (firebaseUser != null){
            // user is logged in
            Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
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
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
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

        // configuring google login
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        progressDialog.hide();
                    }
                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart()");

        editTextLogInEmail = (EditText)findViewById(R.id.editTextLoginEmail);
        editTextLogInPassword = (EditText)findViewById(R.id.editTextLoginPassword);
        btnLogIn = (Button)findViewById(R.id.btnLogIn);
        btnLogInFacebook = (Button) findViewById(R.id.btnFbLoginLarge);
        btnLogInGoogle = (SignInButton) findViewById(R.id.btnGoogleLoginLarge);
        btnSignUpPrompt = (TextView)findViewById(R.id.btnSignUpPrompt);
        progressDialog = new ProgressDialog(this);

        // onclick listeners
        btnLogIn.setOnClickListener(this);
        btnLogInFacebook.setOnClickListener(this);
        btnLogInGoogle.setOnClickListener(this);
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

                            // getting user info
                            firebaseUser = firebaseAuth.getCurrentUser();
                            final String uid = firebaseUser.getUid();
                            final String name = firebaseUser.getDisplayName();
                            final String email = firebaseUser.getEmail();
                            final String image;
                            if (firebaseUser.getPhotoUrl() != null){
                                image = firebaseUser.getPhotoUrl().toString();
                            }else {
                                image = "";
                            }

                            // checking if user's first time logging in
                            userRef = firebaseRef.child("users").child(firebaseUser.getUid() + "/");
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // user already exists
                                        Log.d(TAG, "existing user logging in");
                                        // TODO: 20/03/2017 take user to home screen
                                        Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                        progressDialog.dismiss();
                                        finish();
                                        startActivity(homeIntent);
                                    } else {
                                        // new user
                                        Log.d(TAG, "user's first time logging in");
                                        // setting up user on firebase database
                                        setUpUser(name, email, image);
                                        // saving user to firebase database
                                        onAuthenticationSuccess(firebaseUser);
                                        // taking user to onboarding screens
                                        Intent intentOnboarding = new Intent(LoginActivity.this,OnboardingActivity.class);
                                        startActivity(intentOnboarding);
                                    }
                                }
                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    Log.d(TAG, "ValueEventListener: OnCancelled");
                                }
                            });
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

    // result for facebook and google sign in
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // result returned from googleSignInIntent
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // google sign in was successful, authenticate with firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // google sign in failed, show error message
                // ...
            }
            // result returned from facebook log in
        } else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    // handling facebook log in (called on facebook success callback)
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
                            Log.d(TAG, "facebook login successful");
                            // sign in succeeded...
                            // the auth state listener will be notified and logic to handle
                            // the signed in user can be handled in the listener
                            final String uid = task.getResult().getUser().getUid();
                            final String name = task.getResult().getUser().getDisplayName();
                            final String email = task.getResult().getUser().getEmail();
                            // TODO: 25/03/2017 may need to check if photoURL exists and if not set image to empty string
                            final String image = task.getResult().getUser().getPhotoUrl().toString();

                            firebaseUser = firebaseAuth.getCurrentUser();
                            userRef = firebaseRef.child("users").child(firebaseUser.getUid() + "/");
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // user already exists
                                        Log.d(TAG, "existing user logging in");
                                        // TODO: 20/03/2017 take user to home screen
                                        Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                        homeIntent.putExtra("user_id", uid);
                                        homeIntent.putExtra("profile_picture", image);
                                        progressDialog.dismiss();
                                        finish();
                                        startActivity(homeIntent);
                                    } else {
                                        // new user
                                        Log.d(TAG, "user's first time logging in");
                                        // setting up user on firebase database
                                        setUpUser(name, email, image);
                                        // saving user to firebase database
                                        onAuthenticationSuccess(firebaseUser);
                                        // take user to onboarding screens
                                        Intent intentOnboarding = new Intent(LoginActivity.this,OnboardingActivity.class);
                                        intentOnboarding.putExtra("user_id", uid);
                                        intentOnboarding.putExtra("profile_picture", image);
                                        startActivity(intentOnboarding);
                                    }
                                }
                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    Log.d(TAG, "ValueEventListener: OnCancelled");
                                }
                            });
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

    // handling google sign in
    private void googleSignIn() {
        progressDialog.setMessage("Loading...");
        progressDialog.show();
        Intent googleSignInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(googleSignInIntent, RC_SIGN_IN);
    }

    // authenticating google user with firebase
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        progressDialog.setMessage("Logging in...");
        progressDialog.show();
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        if (task.isSuccessful()) {
                            // sign in succeeded...
                            // the auth state listener will be notified and logic to handle
                            // the signed in user can be handled in the listener
                            final String uid = task.getResult().getUser().getUid();
                            final String name = task.getResult().getUser().getDisplayName();
                            final String email = task.getResult().getUser().getEmail();
                            // TODO: 25/03/2017 may need to check if photoURL exists and if not set image to empty string
                            final String image = task.getResult().getUser().getPhotoUrl().toString();

                            firebaseUser = firebaseAuth.getCurrentUser();

                            userRef = firebaseRef.child("users").child(firebaseUser.getUid() + "/");
                            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        // user already exists
                                        Log.d(TAG, "existing user logging in");
                                        // TODO: 20/03/2017 take user to home screen
                                        Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                                        homeIntent.putExtra("user_id", uid);
                                        homeIntent.putExtra("profile_picture", image);
                                        progressDialog.dismiss();
                                        finish();
                                        startActivity(homeIntent);
                                    } else {
                                        // new user
                                        Log.d(TAG, "user's first time logging in");
                                        // setting up user on firebase database
                                        setUpUser(name, email, image);
                                        // saving user to firebase database
                                        onAuthenticationSuccess(firebaseUser);
                                        // take user to onboarding screens
                                        Intent intentOnboarding = new Intent(LoginActivity.this,OnboardingActivity.class);
                                        intentOnboarding.putExtra("user_id", uid);
                                        intentOnboarding.putExtra("profile_picture", image);
                                        startActivity(intentOnboarding);
                                    }
                                }
                                @Override
                                public void onCancelled(FirebaseError firebaseError) {
                                    Log.d(TAG, "ValueEventListener: OnCancelled");
                                }
                            });
                        } else {
                            // sign in failed...
                            progressDialog.dismiss();
                            // displaying error message to user
                            Log.w(TAG, "signInWithCredential", task.getException());
                            // TODO: 22/03/2017 fix this so user can't log in with google if email address already in use
                            if(task.getException().toString().contains("FirebaseAuthUserCollisionException")){
                                Toast.makeText(LoginActivity.this, "An account already exists with the same email address. " +
                                                "Please disable this account before logging in with Google.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });
    }

    //////////// SAVING USER TO FIREBASE REALTIME DATABASE ////////////

    // setting up user on firebase realtime database
    protected void setUpUser(String name, String email, String image) {
        user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setImage(image);
    }

    // successful firebase authentication
    private void onAuthenticationSuccess(FirebaseUser firebaseUser){
        saveNewUser(firebaseUser.getUid(), user.getName(), user.getEmail(), user.getImage());
    }

    // saving new user to firebase realtime database
    private void saveNewUser(String userId, String name, String email, String image) {
        User user = new User(userId, name, email, image, null, null);
        firebaseRef.child("users").child(userId).setValue(user);
    }

    ///////////////////////////////////////////////////////////////////

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick: " + view.toString());
        // log in button clicked
        if (view == btnLogIn){
            userLogin();
        }
        // facebook log in button clicked
        if (view == btnLogInFacebook){
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile", "user_friends"));
        }
        // google sign in button clicked
        if (view == btnLogInGoogle){
            googleSignIn();
        }
        // sign up button clicked
        if (view == btnSignUpPrompt){
            finish();
            Intent i = new Intent(LoginActivity.this,SignUpActivity.class);
            startActivity(i);
        }
    }
}
