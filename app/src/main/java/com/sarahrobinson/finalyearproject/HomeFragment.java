package com.sarahrobinson.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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

import static com.sarahrobinson.finalyearproject.MainActivity.permissionsGranted;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "HomeFragment ******* ";

    private FragmentManager fragmentManager;
    private OnFragmentInteractionListener mListener;

    private Firebase firebaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private Button btnFindPlaces;
    private EditText editTextSearchLocation;

    private int REQUEST_LOCATION;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getFragmentManager();

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
        // inflating layout
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        // changing actionBar title
        getActivity().setTitle("Find");

        ///// places search /////

        editTextSearchLocation = (EditText)rootView.findViewById(R.id.editTextHomeLocation);
        btnFindPlaces = (Button)rootView.findViewById(R.id.btnFind);

        REQUEST_LOCATION = 2;

        if (permissionsGranted == true){
            // TODO: 17/04/2017 set location to user's current location
            editTextSearchLocation.setText("Current location");
        }

        btnFindPlaces.setOnClickListener(this);

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == btnFindPlaces){
            // request permissions or go to map
            if (permissionsGranted == false){
                ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }else{
                String location = editTextSearchLocation.getText().toString();
                if (mListener != null) {
                    mListener.onFragmentInteraction(location);
                }
                MapFragment mapFragment = new MapFragment();
                fragmentManager.beginTransaction().replace(R.id.content_main, mapFragment).commit();
            }
        }
    }

    // interface for passing data to other fragments via MainActivity
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String location);
    }
}
