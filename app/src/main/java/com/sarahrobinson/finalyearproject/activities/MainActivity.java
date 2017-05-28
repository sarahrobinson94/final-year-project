package com.sarahrobinson.finalyearproject.activities;

import android.Manifest;
import android.app.ProgressDialog;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sarahrobinson.finalyearproject.classes.CircleTransform;
import com.sarahrobinson.finalyearproject.classes.Friendship;
import com.sarahrobinson.finalyearproject.classes.GetPlaceDetails;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.classes.User;
import com.sarahrobinson.finalyearproject.fragments.EventFragment;
import com.sarahrobinson.finalyearproject.fragments.EventsFragment;
import com.sarahrobinson.finalyearproject.fragments.EventsFragmentTabPending;
import com.sarahrobinson.finalyearproject.fragments.EventsFragmentTabUpcoming;
import com.sarahrobinson.finalyearproject.fragments.FavouritesFragment;
import com.sarahrobinson.finalyearproject.fragments.FindFriendsFragment;
import com.sarahrobinson.finalyearproject.fragments.FriendsFragment;
import com.sarahrobinson.finalyearproject.fragments.FriendsFragmentTabFriends;
import com.sarahrobinson.finalyearproject.fragments.FriendsFragmentTabRequests;
import com.sarahrobinson.finalyearproject.fragments.HomeFragment;
import com.sarahrobinson.finalyearproject.fragments.PlaceFragment;
import com.sarahrobinson.finalyearproject.fragments.SettingsFragment;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.sarahrobinson.finalyearproject.fragments.HomeFragment.editTextSearchLocation;

public class MainActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener{

    private static final String TAG = "MainActivity ******* ";

    private static Context mContext;

    private ProgressDialog progressDialog;

    FragmentManager fragmentManager;
    // for indicating which fragment is navigating to PlaceFragment
    public static String fromFragmentString;

    // to store a public reference to firebase
    public static Firebase firebaseRef;

    // to store instances of firebaseAuth and current firebaseUser
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    public static DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();

    // to store id of current user
    public static String currentUserId;

    // to store id of selected list items
    public static String selectedFavPlaceId;
    public static String selectedEventId;

    // to store a list of friends for event invitee modal
    public static ArrayList<String> dialogFriendIdList = new ArrayList<>();
    // friend details
    private String theFriendId, friendId, friendName, friendPhoto;

    // to store id of selected friend request user
    String friendRequestUserId;

    // to store id of selected email user to send friend request
    String emailUserId;

    // layout to be inflated with friend items
    private LinearLayout layoutFriendList;

    // to store list of event invitees
    public static List<String> eventInviteeList = new ArrayList<>();

    private NavigationView navigationView;
    private ImageView navHeaderProfilePic;
    private TextView navHeaderUserName;
    private TextView navHeaderUserEmail;

    // to store instance of current fragment
    public static HomeFragment homeFragment;
    public static FavouritesFragment favouritesFragment;
    public static PlaceFragment placeFragment;
    public static FriendsFragment friendsFragment;
    public static FriendsFragmentTabFriends friendsFragmentTabFriends;
    public static FriendsFragmentTabRequests friendsFragmentTabRequests;
    public static FindFriendsFragment findFriendsFragment;
    public static EventsFragment eventsFragment;
    public static EventsFragmentTabUpcoming eventsFragmentTabUpcoming;
    public static EventsFragmentTabPending eventsFragmentTabPending;
    public static EventFragment eventFragment;
    public static SettingsFragment settingsFragment;

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

        fragmentManager = getSupportFragmentManager();

        progressDialog = new ProgressDialog(this);

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
            Picasso.with(getApplicationContext())
                    .load(photoUrl)
                    .placeholder(R.drawable.circle_white)
                    .resize(150, 150)
                    .transform(new CircleTransform())
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

        HomeFragment homeFragment = new HomeFragment();
        fragmentManager.beginTransaction().replace(R.id.content_main, homeFragment).commit();
    }

    public static Context getContext() {
        return mContext;
    }

    protected void onStart() {
        super.onStart();
    }

    protected void onStop() {
        //googleApiClient.disconnect();
        super.onStop();
    }

    // Method for getting location address from co-ordinates
    public String Geocoder(double latitude, double longitude){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses = null;
        String addressToShow;
        // TODO: 27/05/2017 run try catch in a thread to stop home screen from freezing on startup?
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
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, homeFragment)
                    //.addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_favs) {
            favouritesFragment = new FavouritesFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, favouritesFragment)
                    //.addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_friends) {
            friendsFragment = new FriendsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, friendsFragment)
                    //.addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_events) {
            eventsFragment = new EventsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, eventsFragment)
                    //.addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_settings) {
            settingsFragment = new SettingsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, settingsFragment)
                    //.addToBackStack(null)
                    .commit();
        } else if (id == R.id.nav_logout) {
            // firebase user sign out
            firebaseAuth.signOut();
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
            // Populating location search field (home screen) with user's current location
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.d(TAG, "latitude: " + latitude);
            Log.d(TAG, "longitude: " + longitude);
            currentLocation = Geocoder(latitude, longitude);
            editTextSearchLocation.setText(currentLocation);
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
        getDetails(placeId, fromFragment, null);
    }

    public void getDetails(String placeId, Fragment fromFragment, GetPlaceDetails.Callback callback){
        Log.d(TAG, "getPlaceDetails method");

        String url = getUrl(placeId);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = url;
        DataTransfer[1] = fromFragment;
        GetPlaceDetails getPlaceDetails = new GetPlaceDetails(callback);
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
    //                                ONCLICK METHODS                                //
    ///////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////// FAVOURITE PLACES /////////////////////////////////


    // fav places list item -> view place details
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


    ///////////////////////////////////// EVENTS //////////////////////////////////////


    // events list item -> view event details
    public void eventOnClick(View view){
        View parent = (LinearLayout)view.getParent().getParent();
        TextView tvId = (TextView)parent.findViewById(R.id.eventsListItemId);
        String eventId = tvId.getText().toString();
        selectedEventId = eventId;

        fromFragmentString = "EventsFragment";

        eventFragment = new EventFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, eventFragment)
                .addToBackStack(null)
                .commit();
    }

    // action bar create event button -> create event
    public void createEvent(MenuItem menuItem){
        fromFragmentString = "Create event";
        eventFragment = new EventFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, eventFragment)
                .addToBackStack(null)
                .commit();
    }

    // invite friends to event -> select invitees dialog
    public void selectEventInvitees(final View view){

        // showing progress dialog
        progressDialog.setMessage("Retrieving friends");
        progressDialog.show();

        // inflating layout to be used as dialog
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View dialogView = layoutInflater.inflate(R.layout.dialog_event_invitees, null);

        // building dialog and setting custom layout
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Select friends to invite");
        dialogBuilder.setView(dialogView);

        // getting layout to be inflated with list items (event -> friends to invite list)
        layoutFriendList = (LinearLayout)dialogView.findViewById(R.id.layoutEventFriendList);
        //layoutFriendList = (LinearLayout)findViewById(R.id.layoutEventFriendList);

        layoutFriendList.removeAllViews();

        // TODO: 29/04/2017 if not a new event, tick already invited friends when dialog is opened


        dialogBuilder
                .setCancelable(false)
                .setPositiveButton("Invite",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                eventFragment.showTemporaryInviteeList(view);
                                dialogInterface.dismiss();
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

        if (dialogFriendIdList.size() > 0) {
            // already retrieved friend list, skip the first method
            setStrValues(alertDialog);
        } else {
            // retrieve friend list, call first method
            retrieveFriends(alertDialog);
        }
    }

    // event invitee list toggle invite
    public void toggleEventInvite(View view) {
        // checkbox
        CheckBox chkFriend = (CheckBox)view;
        // friend id
        View parent = (LinearLayout)view.getParent().getParent();
        TextView tvFriendId = (TextView)parent.findViewById(R.id.eventInviteeId);
        String strFriendId = tvFriendId.getText().toString();
        if (chkFriend.isChecked()) {
            // add friend to invite list
            eventInviteeList.add(strFriendId);
            Log.d(TAG, "Added to invite list");
        } else {
            // remove from invite list
            Log.d(TAG, "Removed from invite list");
            eventInviteeList.remove(strFriendId);
        }
    }


    //////////////////////////////////// FRIENDS /////////////////////////////////////


    // action bar find friends button -> find friends
    public void findFriends(MenuItem menuItem){
        fromFragmentString = "Find friends";
        findFriendsFragment = new FindFriendsFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.content_main, findFriendsFragment)
                .addToBackStack(null)
                .commit();
    }

    // find friend by email toggle add
    public void toggleEmailUserAdd(View view) {
        // checkbox
        CheckBox chkFriend = (CheckBox)view;
        // user id
        View parent = (LinearLayout)view.getParent().getParent();
        TextView tvEmailUserId = (TextView)parent.findViewById(R.id.tabEmailUserId);
        emailUserId = tvEmailUserId.getText().toString();
        if (chkFriend.isChecked()) {
            // send user a friend request
            sendFriendRequest();
            // disable uncheck
            chkFriend.setEnabled(false);
        }
    }

    // pending friend request toggle accept
    public void toggleFriendRequestAccept(View view) {
        // checkbox
        CheckBox chkFriendRequest = (CheckBox)view;
        // user id
        View parent = (LinearLayout)view.getParent().getParent();
        TextView tvUserId = (TextView)parent.findViewById(R.id.friendsListItemFriendId);
        friendRequestUserId = tvUserId.getText().toString();
        if (chkFriendRequest.isChecked()) {
            // getting current date
            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedCurrrentDate = df.format(c.getTime());
            /*
             accepting friend request
            */
            // updating friendship for current user
            firebaseRef.child("friendships").child(currentUserId).child(friendRequestUserId).
                    child("requestStatus").setValue("accepted");
            firebaseRef.child("friendships").child(currentUserId).child(friendRequestUserId).
                    child("acceptDate").setValue(formattedCurrrentDate);
            // updating friendship for friend user
            firebaseRef.child("friendships").child(friendRequestUserId).child(currentUserId).
                    child("requestStatus").setValue("accepted");
            firebaseRef.child("friendships").child(friendRequestUserId).child(currentUserId).
                    child("acceptDate").setValue(formattedCurrrentDate);
            // show confirmation message
            Toast.makeText(this,"Friend request accepted",Toast.LENGTH_SHORT).show();
        } else {
            // undo accept friend request
            Toast.makeText(this,"test undo accepted friend request",Toast.LENGTH_SHORT).show();
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                                  OTHER METHODS                                //
    ///////////////////////////////////////////////////////////////////////////////////


    //////////////////////////// EVENT INVITE FRIENDS DIALOG //////////////////////////


    // retreiving list of friends
    public void retrieveFriends(final AlertDialog alertDialog){
        Log.d(TAG, "retrieveFriends entered");

        Query queryFriends = databaseRef.child("friendships").child(currentUserId)
                .orderByChild("requestStatus").equalTo("accepted");

        queryFriends.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Getting friends by id");
                    // adding users ids to list
                    dialogFriendIdList.add(String.valueOf(snapshot.getKey()));
                    Log.d(TAG, "Friend ids: " + dialogFriendIdList);
                }
                // continue dialog creation
                setStrValues(alertDialog);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // getting each friend's id and name
    private void setStrValues(AlertDialog alertDialog){
        for (int i=0; i<dialogFriendIdList.size(); i++){

            theFriendId = dialogFriendIdList.get(i);
            DatabaseReference eventRef = databaseRef.child("users").child(theFriendId);

            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "Getting friend details");
                        // getting and storing friend details
                        User user = dataSnapshot.getValue(User.class);
                        friendId = dataSnapshot.getKey();
                        friendName = user.getName();
                        friendPhoto = user.getImage();
                        // inflate list with friend item
                        inflateFriendListItem(friendId, friendName);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        // show alert dialog when everything else is complete
        progressDialog.dismiss();
        alertDialog.show();
        // setting dialog width and height
        alertDialog.getWindow().setLayout(1000, 1300);
    }

    // inflating friends list (invite friends to event dialog)
    public void inflateFriendListItem(String friendId, String friendName){

        // inflating layout to be used as a list item
        LayoutInflater inflater = LayoutInflater.from(this);
        View listItem = inflater.inflate(R.layout.dialog_event_invitee_wrapper, layoutFriendList, false);

        // adding inflated item to list
        layoutFriendList.addView(listItem, layoutFriendList.getChildCount() - 1);

        TextView tvFriendName = (TextView)listItem.findViewById(R.id.eventInviteeTvName);
        // invisible textView for storing user id
        TextView tvFriendId = (TextView)listItem.findViewById(R.id.eventInviteeId);

        // populating views with friend name
        tvFriendName.setText(friendName);
        tvFriendId.setText(friendId);
    }


    //////////////////////////// FIND FRIENDS - EMAIL USER ////////////////////////////

    private void sendFriendRequest() {
        setFriendship();
        Toast.makeText(this,"Friend request sent",Toast.LENGTH_SHORT).show();
    }

    private void setFriendship() {
        // getting current date
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedCurrrentDate = df.format(c.getTime());
        // setting friendship
        Friendship friendship = new Friendship();
        friendship.setRequestBy(currentUserId);
        friendship.setRequestDate(formattedCurrrentDate);
        friendship.setRequestStatus("pending");
        // writing friendship
        writeNewFriendship(friendship);
    }

    private void writeNewFriendship(Friendship friendship) {
        // requester user node
        firebaseRef.child("friendships").child(currentUserId).child(emailUserId)
                .setValue(friendship);
        // requested user node
        firebaseRef.child("friendships").child(emailUserId).child(currentUserId)
                .setValue(friendship);
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
