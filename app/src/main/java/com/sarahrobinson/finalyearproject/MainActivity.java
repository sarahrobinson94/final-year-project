package com.sarahrobinson.finalyearproject;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.internal.LoginAuthorizationType;
import com.facebook.login.LoginManager;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import static com.sarahrobinson.finalyearproject.HomeFragment.editTextSearchLocation;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final String TAG = "MainActivity ******* ";

    FragmentManager fragmentManager;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private NavigationView navigationView;

    private ImageView navHeaderProfilePic;
    private TextView navHeaderUserName;
    private TextView navHeaderUserEmail;

    // location services
    private int REQUEST_LOCATION;
    private GoogleMap googleMap;
    double latitude, longitude;
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

        ///// navigation drawer /////

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

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

    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    // TODO: 11/04/2017 delete if not needed (using Picasso instead)
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

    // Method for getting location address from co-ordinates
    public String Geocoder(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String addressLine1 = addresses.get(0).getAddressLine(0);
        String addressLine2 = addresses.get(0).getAddressLine(1);
        String addressLine3 = addresses.get(0).getAddressLine(2);
        String addressToShow = addressLine1 + ", " + addressLine2;
        return addressToShow;
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                   NAVIGATION                                  //
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_find) {
            HomeFragment homeFragment = new HomeFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, homeFragment).commit();
        } else if (id == R.id.nav_favs) {
            FavouritesFragment favouritesFragment = new FavouritesFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, favouritesFragment).commit();
        } else if (id == R.id.nav_friends) {
            FriendsFragment friendsFragment = new FriendsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, friendsFragment).commit();
        } else if (id == R.id.nav_events) {
            EventsFragment eventsFragment = new EventsFragment();
            fragmentManager.beginTransaction().replace(R.id.content_main, eventsFragment).commit();
        } else if (id == R.id.nav_settings) {
            SettingsFragment settingsFragment = new SettingsFragment();
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
            // end main activity
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
        currentLocation = Geocoder(latitude, longitude);
        editTextSearchLocation.setText(currentLocation);
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
    //                               FRAGMENT INTERACTION                            //
    ///////////////////////////////////////////////////////////////////////////////////


    // Handle interaction between fragments
    @Override
    public void onFragmentInteraction(String location) {
        MapFragment mapFragment = (MapFragment)getSupportFragmentManager().findFragmentById(R.id.layoutFragmentMap);
        mapFragment.getSearchData(location);
    }
}
