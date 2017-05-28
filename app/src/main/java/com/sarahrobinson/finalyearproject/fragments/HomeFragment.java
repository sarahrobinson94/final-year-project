package com.sarahrobinson.finalyearproject.fragments;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.ColorFilter;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarahrobinson.finalyearproject.activities.LoginActivity;
import com.sarahrobinson.finalyearproject.R;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentLocation;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.permissionsGranted;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "HomeFragment ******* ";

    private FragmentManager fragmentManager;
    private OnFragmentInteractionListener mListener;

    private Firebase firebaseRef;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ImageView ivOptionCafe, ivOptionRestaurant, ivOptionTakeaway, ivOptionBar;
    public static EditText editTextSearchRadius;
    public static EditText editTextSearchLocation;
    private Button btnFindPlaces;

    // values added to search
    public static double sRadius;
    public static String sLocation;
    public static String sName;

    // for changing state of place type filters
    public static boolean cafeSelected;
    public static boolean restaurantSelected;
    public static boolean takeawaySelected;
    public static boolean barSelected;
    public int blue;
    public int grey;

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

        blue = (ContextCompat.getColor(getActivity(),R.color.colorPrimary));
        grey = (ContextCompat.getColor(getActivity(),R.color.grey));

        cafeSelected = true;
        restaurantSelected = true;
        takeawaySelected = true;
        barSelected = true;

        REQUEST_LOCATION = 2;

        // getting views
        ivOptionCafe = (ImageView)rootView.findViewById(R.id.imageViewOptionCafe);
        ivOptionRestaurant = (ImageView)rootView.findViewById(R.id.imageViewOptionRestaurant);
        ivOptionTakeaway = (ImageView)rootView.findViewById(R.id.imageViewOptionTakeaway);
        ivOptionBar = (ImageView)rootView.findViewById(R.id.imageViewOptionBar);
        editTextSearchRadius = (EditText)rootView.findViewById(R.id.editTextHomeRadius);
        editTextSearchLocation = (EditText)rootView.findViewById(R.id.editTextHomeLocation);
        btnFindPlaces = (Button)rootView.findViewById(R.id.btnFind);

        // onclick listeners
        ivOptionCafe.setOnClickListener(this);
        ivOptionRestaurant.setOnClickListener(this);
        ivOptionTakeaway.setOnClickListener(this);
        ivOptionBar.setOnClickListener(this);
        btnFindPlaces.setOnClickListener(this);

        // setting text to current location
        editTextSearchLocation.setText(currentLocation);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == btnFindPlaces)
        {
            // request permissions or go to map
            if (permissionsGranted == false){
                ActivityCompat.requestPermissions(getActivity(), new String[]
                        {android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            }else if (cafeSelected == false && restaurantSelected == false
                        && takeawaySelected == false && barSelected == false) {
                Toast.makeText(getActivity(), "You must select a place type to perform a search",
                        Toast.LENGTH_LONG).show();
            } else {
                // get search location
                sLocation = editTextSearchLocation.getText().toString();
                // get search radius (set to default if none specified)
                String stringRadius = editTextSearchRadius.getText().toString();
                if (stringRadius.equals("") || stringRadius.equals(null)){
                    sRadius = 20000.0;
                }else{
                    sRadius = Double.parseDouble(stringRadius);
                }
                // go to map
                MapFragment mapFragment = new MapFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_main, mapFragment)
                        .addToBackStack(null)
                        .commit();
            }
        }
        else if (view == ivOptionCafe)
        {
            // toggle colour
            if (cafeSelected == false) {
                ivOptionCafe.setColorFilter(blue);
                cafeSelected = true;
            } else if (cafeSelected == true) {
                ivOptionCafe.setColorFilter(grey);
                cafeSelected = false;
            }
        }
        else if (view == ivOptionRestaurant)
        {
            // toggle colour
            if (restaurantSelected == false) {
                ivOptionRestaurant.setColorFilter(blue);
                restaurantSelected = true;
            } else if (restaurantSelected == true) {
                ivOptionRestaurant.setColorFilter(grey);
                restaurantSelected = false;
            }
        }
        else if (view == ivOptionTakeaway)
        {
            // toggle colour
            if (takeawaySelected == false) {
                ivOptionTakeaway.setColorFilter(blue);
                takeawaySelected = true;
            } else if (takeawaySelected == true) {
                ivOptionTakeaway.setColorFilter(grey);
                takeawaySelected = false;
            }
        }
        else if (view == ivOptionBar)
        {
            // toggle colour
            if (barSelected == false) {
                ivOptionBar.setColorFilter(blue);
                barSelected = true;
            } else if (barSelected == true) {
                ivOptionBar.setColorFilter(grey);
                barSelected = false;
            }
        }
    }

    // interface for passing data to other fragments via MainActivity
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(String location, double radius);
    }
}
