package com.sarahrobinson.finalyearproject.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.firebase.client.Firebase;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.sarahrobinson.finalyearproject.classes.GetPlaceDetails;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.fragments.EventFragment;
import com.sarahrobinson.finalyearproject.fragments.EventsFragment;
import com.sarahrobinson.finalyearproject.fragments.FavouritesFragment;
import com.sarahrobinson.finalyearproject.fragments.FriendsFragment;
import com.sarahrobinson.finalyearproject.fragments.HomeFragment;
import com.sarahrobinson.finalyearproject.fragments.PlaceFragment;
import com.sarahrobinson.finalyearproject.fragments.SettingsFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.sarahrobinson.finalyearproject.fragments.HomeFragment.editTextSearchLocation;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MainActivity ******* ";

    private static Context mContext;

    FragmentManager fragmentManager;
    // for indicating which fragment is navigating to PlaceFragment
    public static String fromFragmentString;

    // public reference to firebase for adding favourite places
    public static Firebase firebaseRef;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // to store id of current user
    public static String currentUserId;

    // to store id of selected favourite place
    public static String selectedFavPlaceId;

    // to store list of event invitees
    public static List<String> eventInviteeList = new ArrayList<>();

    private NavigationView navigationView;
    private ImageView navHeaderProfilePic;
    private TextView navHeaderUserName;
    private TextView navHeaderUserEmail;

    // to store instance of current fragment
    public static HomeFragment homeFragment;
    public static FavouritesFragment favouritesFragment;
    public static FriendsFragment friendsFragment;
    public static EventsFragment eventsFragment;
    public static SettingsFragment settingsFragment;
    public static PlaceFragment placeFragment;
    public static EventFragment eventFragment;

    // location services
    private int REQUEST_LOCATION;
    private GoogleMap googleMap;
    Double latitude, longitude;
    public static GoogleApiClient googleApiClient;
    public static Location location;
    public static LocationRequest locationRequest;
    public static Boolean permissionsGranted = false;
    public static String currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = getApplicationContext();

        ///// firebase /////

        firebaseRef = new Firebase("https://final-year-project-12698.firebaseio.com/");
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        currentUserId = (firebaseUser.getUid().toString());

        ///// navigation drawer /////

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        navHeaderProfilePic = (ImageView)navigationView.getHeaderView(0).findViewById(R.id.navHeaderProfilePic);
        navHeaderUserName = (TextView)navigationView.getHeaderView(0).findViewById(R.id.navHeaderUserName);
        navHeaderUserEmail = (TextView)navigationView.getHeaderView(0).findViewById(R.id.navHeaderUserEmail);

        if (firebaseUser.getPhotoUrl() != null){
            String photoUrl = firebaseUser.getPhotoUrl().toString();
            //new MainActivity.ImageLoadTask(imageUrl, navHeaderProfilePic).execute();
            Picasso.with(getApplicationContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.circle_white)
                    .resize(100, 100)
                    //.transform(new CircleTransform())
                    .centerCrop()
                    .into(navHeaderProfilePic);
        }else {
            Log.d(TAG, "onStart: no profile picture");
        }

        navHeaderUserName.setText(firebaseUser.getDisplayName());
        navHeaderUserEmail.setText(firebaseUser.getEmail());

        ///// location services /////

        REQUEST_LOCATION = 2;

        checkGooglePlayServices();

        // creating the LocationRequest object
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(500)        // 0.5 seconds, in milliseconds
                .setFastestInterval(250); // 0.25 second, in milliseconds

        initializeGooglePlayServices();

        ///// home fragment initialization /////

        fragmentManager = getSupportFragmentManager();
        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.content_main, homeFragment).commit();
    }

    public static Context getContext() {
        return mContext;
    }

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    // Method for getting location address from co-ordinates
    public String Geocoder(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        String addressToShow;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(addresses != null){
            String addressLine1 = addresses.get(0).getAddressLine(0);
            String addressLine2 = addresses.get(0).getAddressLine(1);
            String addressLine3 = addresses.get(0).getAddressLine(2);
            addressToShow = addressLine1 + ", " + addressLine2;
        }else{
            addressToShow = "";
        }
        return addressToShow;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                                   NAVIGATION                                  //
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }else if (getFragmentManager().getBackStackEntryCount() > 0 ){
            getFragmentManager().popBackStack();
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // handling navigation view item clicks
        int id = item.getItemId();
        if (id == R.id.nav_find) {
            homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, homeFragment).commit();
        } else if (id == R.id.nav_favs) {
            favouritesFragment = new FavouritesFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, favouritesFragment).commit();
        } else if (id == R.id.nav_friends) {
            friendsFragment = new FriendsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, friendsFragment).commit();
        } else if (id == R.id.nav_events) {
            eventsFragment = new EventsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, eventsFragment).commit();
        } else if (id == R.id.nav_settings) {
            settingsFragment = new SettingsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, settingsFragment).commit();
        } else if (id == R.id.nav_logout) {
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
            // start loginActivity
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            // end action_settings activity
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                              GOOGLE PLAY SERVICES                             //
    ///////////////////////////////////////////////////////////////////////////////////


    // Check if Google Play Services is available
    private boolean checkGooglePlayServices() {
        Log.d(TAG, "checkGooglePlayServices");
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        // checking if connection successful
        if (result != ConnectionResult.SUCCESS) {
            // connection unsuccessful
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result, 0).show();
            }
            return false;
        }
        // connection successful
        return true;
    }

    // Initialize Google Play Services
    private void initializeGooglePlayServices(){
        Log.d(TAG, "initializeGooglePlayServices");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = true;
                buildGoogleApiClient();
            }
            else
            {
                // requests permissions if they are not yet given
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
        } else {
            buildGoogleApiClient();
        }
    }

    // Builder method for initializing Google Play Services
    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient");
        googleApiClient = new GoogleApiClient.Builder(this)  // for configuring client
                .addConnectionCallbacks(this)                // called when client is connected or disconnected
                .addOnConnectionFailedListener(this)         // handles failed connection attempt
                .addApi(LocationServices.API).build();       // adds the LocationServices API endpoint from Google Play Services
        googleApiClient.connect();                           // ensures client is connected before executing any operation
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                         CONNECTION TO LOCATION SERVICES                       //
    ///////////////////////////////////////////////////////////////////////////////////


    // Method for regularly updating the user's current location
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected");
        // Get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        // If last location is available
        if (location != null) {
            Log.d(TAG, "onConnected: last location is available");
            Log.d(TAG, "Location: " + location);
        // If last location is not available
        }else{
            // If permissions have been granted
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onConnected: Permission granted");
                permissionsGranted = true;
                // get current location
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest,
                        (LocationListener) this);
                location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            // If permissions have not been granted
            }else{
                Log.d(TAG, "onConnected: Permission not granted");
                // request permission
                checkLocationPermission();
            }
        }
        // Populating location search field (home screen) with user's current location
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        if (latitude != null && longitude != null){
            Log.d(TAG, "latitude: " + latitude);
            Log.d(TAG, "longitude: " + longitude);
            currentLocation = Geocoder(latitude, longitude);
            editTextSearchLocation.setText(currentLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this,"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this,"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                               LOCATION PERMISSIONS                            //
    ///////////////////////////////////////////////////////////////////////////////////


    // Asking user for permission to access location
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        // TODO: 16/04/2017 more needed here ??
        Log.d(TAG, "checkLocationPermission");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //permission denied
            return false;
        } else {
            //permission granted
            permissionsGranted = true;
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "onRequestPermissionResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission granted
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        permissionsGranted = true;
                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                    }
                } else {
                    // Permission denied
                    Toast.makeText(this, "Permission denied. You will not be able to use location-based" +
                            "features of this app.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                             GETTING PLACE DETAILS                             //
    ///////////////////////////////////////////////////////////////////////////////////


    public void getDetails(String placeId, Fragment fromFragment){
        Log.d(TAG, "getPlaceDetails");

        String url = getUrl(placeId);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = url;
        DataTransfer[1] = fromFragment;
        GetPlaceDetails getPlaceDetails = new GetPlaceDetails();
        getPlaceDetails.execute(DataTransfer);
    }

    private String getUrl(String placeId) {
        StringBuilder placeDetailsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        placeDetailsUrl.append("placeid=" + placeId);
        placeDetailsUrl.append("&key=" + "AIzaSyDqO1XsZmh6XI1rqPbiaa2zEqqG7InpDCI");
        Log.d(TAG, "getUrl: " + placeDetailsUrl.toString());
        return (placeDetailsUrl.toString());
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                                ON CLICK METHODS                               //
    ///////////////////////////////////////////////////////////////////////////////////


    // fav places list item onclick
    public void favPlaceOnClick(View view){
        View parent = (LinearLayout)view.getParent().getParent();
        TextView tvId = (TextView)parent.findViewById(R.id.favsListItemPlaceId);
        String placeId = tvId.getText().toString();
        selectedFavPlaceId = placeId;

        fromFragmentString = "FavouritesFragment";

        placeFragment = new PlaceFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, placeFragment)
                .addToBackStack(null)
                .commit();
    }

    // action bar create event button onclick
    public void createEvent(MenuItem menuItem){
        fromFragmentString = "Create event";
        eventFragment = new EventFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, eventFragment)
                .addToBackStack(null)
                .commit();
    }

    // select friends dialog
    public void selectFriends(View view){

        eventInviteeList.clear();

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.dialog_select_friends, null);

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        dialogBuilder.setTitle("Select friends to add");
        dialogBuilder.setView(dialogView);


        ////////// for loop /////////

        LinearLayout friendWrapper = (LinearLayout)dialogView.findViewById(R.id.dialogFriendWrapper);

        final CheckBox chkFriend = (CheckBox)friendWrapper.findViewById(R.id.friendCheckbox);

        final TextView tvFriendName = (TextView)friendWrapper.findViewById(R.id.friendTextView);

        // set TextView text (friend name)

        /////////////////////////////

        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Invite friends",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // invite friends to event
                                Log.d(TAG, "Friends invited");

                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });

        AlertDialog alertDialog = dialogBuilder.create();

        alertDialog.show();

    }

    // event invitee list toggle invite
    public void toggleEventInvite(View view) {
        int id = view.getId();

        CheckBox chkFriend = (CheckBox)view;

        if (chkFriend.isChecked()) {
            // add to invite list
            Log.d(TAG, "Added to invite list");
        } else {
            // remove from invite list
            Log.d(TAG, "Removed from invite list");
        }

    }
}