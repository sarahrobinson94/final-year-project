package com.sarahrobinson.finalyearproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    private Firebase firebaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // google sign in
    private GoogleApiClient googleApiClient;

    private TextView welcomeMessage;
    private ImageView profilePicture;
    private Button btnLogOut;

    private static final String TAG = "HomeActivity ******* ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseRef = new Firebase("https://final-year-project-12698.firebaseio.com/");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // starting login activity if user is not logged in
        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
        }else{
            Log.d(TAG, "User email: " + firebaseUser.getEmail());
            Log.d(TAG, "User display name: " + firebaseUser.getDisplayName());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        welcomeMessage = (TextView)findViewById(R.id.textViewHomeWelcome);
        welcomeMessage.setText("Welcome " + firebaseUser.getDisplayName());
        profilePicture = (ImageView)findViewById(R.id.profilePicture);
        btnLogOut = (Button)findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);

        // getting uid for logged in user
        String uid = getIntent().getExtras().getString("user_id");
        // getting imageUrl for logged in user
        String imageUrl = getIntent().getExtras().getString("profile_picture");

        if(imageUrl != null){
            new ImageLoadTask(imageUrl, profilePicture).execute();
        }else{
            Log.d(TAG, "onStart: no profile picture");
        }

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)
    }

    // getting profile picture if user logs in with facebook or google
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                // TODO: 19/03/2017 ensure InputStream is using correct import
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == btnLogOut){
            // firebase user sign out
            firebaseAuth.signOut();
            // google sign out
            if (googleApiClient != null){
                Log.d(TAG, "onClick: signing out google user");
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Log.d(TAG, "onClick: google sign out");
                            }
                        });
            }
            // TODO: 22/03/2017 logout facebook users too
            finish();
            Intent i = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(i);
        }
    }
}
