package com.sarahrobinson.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
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

import static com.sarahrobinson.finalyearproject.MainActivity.currentLocation;
import static com.sarahrobinson.finalyearproject.MainActivity.location;
import static com.sarahrobinson.finalyearproject.MainActivity.permissionsGranted;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "HomeFragment ******* ";

    private FragmentManager fragmentManager;
    private OnFragmentInteractionListener mListener;

    private Firebase firebaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public static EditText editTextSearchRadius;
    public static EditText editTextSearchLocation;
    private Button btnFindPlaces;

    // values added to search
    public static double sRadius;
    public static String sLocation;
    public static String sName;

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

        REQUEST_LOCATION = 2;

        editTextSearchRadius = (EditText)rootView.findViewById(R.id.editTextHomeRadius);

        editTextSearchLocation = (EditText)rootView.findViewById(R.id.editTextHomeLocation);
        editTextSearchLocation.setText(currentLocation);

        btnFindPlaces = (Button)rootView.findViewById(R.id.btnFind);
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
                // get search location
                sLocation = editTextSearchLocation.getText().toString();
                // get search radius (set to default if none specified)
                String stringRadius = editTextSearchRadius.getText().toString();
                if (stringRadius.equals("") || stringRadius.equals(null)){
                    Log.d(TAG, "Radius: default");
                    sRadius = 20000.0;
                }else{
                    Log.d(TAG, "Radius: custom");
                    Log.d(TAG, "Radius: " + stringRadius);
                    sRadius = Double.parseDouble(stringRadius);
                }
                MapFragment mapFragment = new MapFragment();
                fragmentManager.beginTransaction().replace(R.id.content_main, mapFragment).commit();
            }
        }
    }

    // interface for passing data to other fragments via MainActivity
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String location, double radius);
    }
}
