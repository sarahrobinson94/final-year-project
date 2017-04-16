package com.sarahrobinson.finalyearproject;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.Manifest;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

//import com.b00636938.com594.GetNearbyPlacesData;
//import com.b00636938.com594.PoiClass;
//import com.b00636938.com594.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;

public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    // Declaring global variables

    private GoogleMap googleMap;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private Location location;
    private Marker currLocationMarker;
    private Marker selectedMarker;

    private LatLng latLng;
    private double latitude, longitude;

    double PROXIMITY_RADIUS;

    private String placeType = "pharmacy";

    // values added to search
    private String sLocation;
    private String sName;

    private int REQUEST_LOCATION;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // setting the radius in meters for markers to be added to the map
        PROXIMITY_RADIUS = 2000.0;

        REQUEST_LOCATION = 2;

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // loading the map fragment on startup
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);

        checkGooglePlayServices();

        // creating the LocationRequest object
        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(500)        // 0.5 seconds, in milliseconds
                .setFastestInterval(250); // 0.25 second, in milliseconds
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                               MAP INITIALIZATION                              //
    ///////////////////////////////////////////////////////////////////////////////////


    // Check if Google Play Services is available
    private boolean checkGooglePlayServices() {
        Log.d("checkGooglePlayServices", "entered");
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(super.getActivity());
        // checking if connection successful
        if (result != ConnectionResult.SUCCESS) {
            // connection unsuccessful
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(super.getActivity(), result, 0).show();
            }
            return false;
        }
        // connection successful
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap mMap) {
        Log.d("onMapReady", "entered");

        //final int REQUEST_LOCATION = 2;
        googleMap = mMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(super.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                googleMap.setMyLocationEnabled(true);
            }
            else
            {
                //requests permissions if they are not yet given
                ActivityCompat.requestPermissions(super.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_LOCATION);
            }
        } else {
            buildGoogleApiClient();
            googleMap.setMyLocationEnabled(true);
        }

        //called when a marker is selected // TODO: 16/04/2017 needed ??
        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener(){
            @Override
            public boolean onMarkerClick(Marker marker){
                selectedMarker = marker;
                //getPlaceDetails();
                return true;
            }
        });
    }

    // Builder method for initializing Google Play Services
    protected synchronized void buildGoogleApiClient() {
        Log.d("buildGoogleApiClient", "entered");
        googleApiClient = new GoogleApiClient.Builder(super.getActivity())  // for configuring client
                .addConnectionCallbacks(this)                               // called when client is connected or disconnected
                .addOnConnectionFailedListener(this)                        // handles failed connection attempt
                .addApi(LocationServices.API).build();                      // adds the LocationServices API endpoint from Google Play Services
        googleApiClient.connect();                                          // ensures client is connected before executing any operation
    }

    // Method for regularly updating the user's current location
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("onConnected", "entered");
        // Get last location
        location = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        // If last location is available
        if (location != null) {
            Log.d("onConnected", "location is not null");
            // position marker
            setMarker(location);
        // If last location is not available
        }else{
            // If permissions have been granted
            if (ContextCompat.checkSelfPermission(super.getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d("onConnected", "PERMISSION GRANTED");
                // get current location
                LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
            // If permissions have not been granted
            }else{
                Log.d("onConnected", "PERMISSION NOT GRANTED");
                // request permission
                checkLocationPermission();
            }
        }
        findPlaces(location);
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                               HANDLING CONNECTION                             //
    ///////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(super.getActivity(),"onConnectionSuspended",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(super.getActivity(),"onConnectionFailed",Toast.LENGTH_SHORT).show();
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                            DISPLAYING CURRENT LOCATION                        //
    ///////////////////////////////////////////////////////////////////////////////////


    // add map marker and zoom to location
    public void setMarker(Location mLocation){
        location = mLocation;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                             HANDLING LOCATION CHANGE                          //
    ///////////////////////////////////////////////////////////////////////////////////


    // Method called when user's location changes
    @Override
    public void onLocationChanged(Location mLocation) {

        location = mLocation;

        findPlaces(location);

        // Remove previous location marker
        if (currLocationMarker != null) {
            currLocationMarker.remove();
        }

        // Place current location marker and move map camera
        setMarker(location);

        // Stop location updates
        if (googleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                               HANDLING PERMISSIONS                            //
    ///////////////////////////////////////////////////////////////////////////////////


    // asking user for permission to access location
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        // TODO: 16/04/2017 more needed here ??
        Log.d("checkLocationPermission", "entered");
        if (ContextCompat.checkSelfPermission(super.getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //permission denied
            return false;
        } else {
            //permission granted
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d("onReqPermissionsResult", "entered");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {

                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission granted
                    if (ContextCompat.checkSelfPermission(super.getActivity(),
                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                        if (googleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        googleMap.setMyLocationEnabled(true) ;
                    }

                } else {
                    // Permission denied
                    //Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////
    //                                FINDING PLACES                                 //
    ///////////////////////////////////////////////////////////////////////////////////

    // Find places
    public void findPlaces(Location mLocation){
        Log.d("findNearbyPlaces", "entered");
        googleMap.clear();

        // user's location
        location = mLocation;

        // if a location has not been specified in search, use current location
        if (sLocation.equals("")){
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
        // if a location has been specified, get latitude & longitude of location
        }else{
            if(Geocoder.isPresent()){
                try {
                    String location = sLocation;
                    Geocoder gc = new Geocoder(getActivity());
                    List<Address> addresses = gc.getFromLocationName(location, 3);
                    for (Address a : addresses) {
                        latitude = a.getLatitude();
                        longitude = a.getLongitude();
                    }
                } catch (IOException e) {
                    //todo: change to toast message
                    Log.d("findPlaces", "An error occurred retrieving latitude & longitude of " +
                            "location specified in search.");
                }
            }
        }

        String url = getUrl(latitude, longitude);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = googleMap;
        DataTransfer[1] = url;
        GetPlacesData getPlacesData = new GetPlacesData();
        getPlacesData.execute(DataTransfer);

        // TODO: 16/04/2017 place current location marker ??

        if (sLocation.equals("")){
            //Toast.makeText(MainActivity.this, "Pharmacies nearby", Toast.LENGTH_LONG).show();
        }else{
            //Toast.makeText(MainActivity.this, "Pharmacies near " + sLocation, Toast.LENGTH_LONG).show();
        }
    }

    // gets the url for the point of interest
    private String getUrl(double latitude, double longitude) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + "amusement_park|art_gallery|mosque|church|cemetery|city_hall|stadium|hindu_temple|library|museum|park|synagogue|university|zoo");

        // TODO: 16/04/2017 fix search by name
        Log.d("getUrl", "sName: " + sName);
        // if a pharmacy name has been specified in search, add to url
        /*
        if (!sName.equals("") || !sName.equals(null) ){
            googlePlacesUrl.append("&name=" + sName);
        }
        */

        googlePlacesUrl.append("&key=" + "AIzaSyAJ0uoGlxBCkOpTR1ASpy2V7e6m5_ywB3E");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
}