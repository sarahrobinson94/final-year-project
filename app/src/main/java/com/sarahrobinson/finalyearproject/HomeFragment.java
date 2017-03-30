package com.sarahrobinson.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private Firebase firebaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // google sign in
    private GoogleApiClient googleApiClient;

    private TextView welcomeMessage;
    private ImageView profilePicture;
    private Button btnLogOut;

    private static final String TAG = "HomeFragment ******* ";

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebaseRef = new Firebase("https://final-year-project-12698.firebaseio.com/");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // starting login activity if user is not logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }else {
            Log.d(TAG, "User email: " + firebaseUser.getEmail());
            Log.d(TAG, "User display name: " + firebaseUser.getDisplayName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        welcomeMessage = (TextView)rootView.findViewById(R.id.textViewHomeWelcome);
        welcomeMessage.setText("Welcome " + firebaseUser.getDisplayName());
        profilePicture = (ImageView)rootView.findViewById(R.id.profilePicture);
        btnLogOut = (Button)rootView.findViewById(R.id.btnLogOut);
        btnLogOut.setOnClickListener(this);

        // getting uid & image of logged in user
        String uid = firebaseUser.getUid();
        if (firebaseUser.getPhotoUrl() != null){
            String imageUrl = firebaseUser.getPhotoUrl().toString();
            new HomeFragment.ImageLoadTask(imageUrl, profilePicture).execute();
        }else {
            Log.d(TAG, "onStart: no profile picture");
        }

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)

        return rootView;
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
            // google sign out - seems to work without this code?
            if (googleApiClient != null){
                Log.d(TAG, "onClick: signing out google user");
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(
                        new ResultCallback<Status>() {
                            @Override
                            public void onResult(Status status) {
                                Log.d(TAG, "onClick: sign out successful");
                            }
                        });
            }
            // firebase user facebook sign out
            LoginManager.getInstance().logOut();

            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }

}
