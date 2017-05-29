package com.sarahrobinson.finalyearproject.fragments;

import android.content.pm.PackageManager;
import android.Manifest;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.sarahrobinson.finalyearproject.classes.GetPlacesData;
import com.sarahrobinson.finalyearproject.R;

import java.io.IOException;
import java.util.List;

import static android.R.id.tabhost;
import static com.sarahrobinson.finalyearproject.fragments.HomeFragment.barSelected;
import static com.sarahrobinson.finalyearproject.fragments.HomeFragment.cafeSelected;
import static com.sarahrobinson.finalyearproject.fragments.HomeFragment.restaurantSelected;
import static com.sarahrobinson.finalyearproject.fragments.HomeFragment.sLocation;
import static com.sarahrobinson.finalyearproject.fragments.HomeFragment.sRadius;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.fromFragmentString;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.googleApiClient;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.location;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.placeFragment;
import static com.sarahrobinson.finalyearproject.fragments.HomeFragment.takeawaySelected;

public class MapFragment extends Fragment implements
        OnMapReadyCallback,
        LocationListener {

    private static final String TAG = "MapFragment ******* ";

    private FragmentManager fragmentManager;

    private FragmentTabHost mTabHost;

    private GoogleMap googleMap;
    private LatLng latLng;
    private double latitude, longitude;
    private Marker currLocationMarker; // TODO: 17/04/2017 needed ??
    private Marker selectedMarker;

    public static String thePlaceId, thePlaceName, thePlaceAddress, thePlacePhoneNo;
    public static String selectedPlaceId;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflating layout
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        // changing actionBar title
        getActivity().setTitle("Results");

        fragmentManager = getFragmentManager();

        // setting up tabs
        mTabHost = (FragmentTabHost)rootView.findViewById(tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator("MAP", null),
                MapFragmentTabMap.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator("LIST", null),
                MapFragmentTabList.class, null);

        // setting tab text colours
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                // unselected tabs
                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                    TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                    tv.setTextColor(Color.parseColor("#ADADAD"));
                }
                // selected tab
                TextView tv = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title);
                tv.setTextColor(Color.parseColor("#666666"));
            }
        });

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // loading the map fragment on startup
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);

        /*
        setUpMapIfNeeded();
        */
    }

    @Override
    public void onResume() {
        // setting initial tab text colour
        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
        {
            // unselected tabs
            TextView tvUnselected = (TextView)mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tvUnselected.setTextColor(Color.parseColor("#ADADAD"));
            // selected tab
            TextView tvSelected = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title); // selected tab
            tvSelected.setTextColor(Color.parseColor("#666666"));
        }
        super.onResume();
    }

    /*
    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (googleMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
            mapFragment.getMapAsync(this);
        }
    }
    */


    ///////////////////////////////////////////////////////////////////////////////////
    //                               MAP INITIALIZATION                              //
    ///////////////////////////////////////////////////////////////////////////////////


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
        Log.d(TAG, "onMapReady");
        googleMap = mMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // necessary permissions check
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // permission not granted
            Toast.makeText(getActivity(), "Permission to access the device's location has not been" +
                    "granted", Toast.LENGTH_LONG).show();
        }else{
            googleMap.setMyLocationEnabled(true);
            setMarker(location);
            findPlaces(location);
        }
        // marker info window onclick listener
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d(TAG, "info window clicked");
                selectedMarker = marker;
                selectedPlaceId = selectedMarker.getTag().toString();
                fromFragmentString = "MapFragment";
                placeFragment = new PlaceFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_main, placeFragment)
                        // TODO: 17/04/2017 fix crash when navigating back to this fragment
                        .addToBackStack(null)
                        .commit();
            }
        });
    }

    // add map marker and zoom to location
    public void setMarker(Location mLocation){
        Log.d(TAG, "setMarker");
        location = mLocation;
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17));
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                             HANDLING LOCATION CHANGE                          //
    ///////////////////////////////////////////////////////////////////////////////////


    // TODO: 17/04/2017 check this works

    // Method called when user's location changes
    @Override
    public void onLocationChanged(Location mLocation) {
        Log.d(TAG, "onLocationChanged");
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
    //                                  NEARBY SEARCH                                //
    ///////////////////////////////////////////////////////////////////////////////////

    public void findPlaces(Location mLocation){
        Log.d("findNearbyPlaces", "entered");
        googleMap.clear();

        // user's location
        location = mLocation;

        // if a location has not been specified in search, use current location
        if (sLocation == null || sLocation == ""){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
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
                    // TODO: 17/04/2017 change to toast message
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
    }

    private String getUrl(double latitude, double longitude)
    {
        // checking which place filters have been selected and creating string to append to url
        String types = "";
        if (cafeSelected){
            types = types + "cafe|";
        }
        if (restaurantSelected){
            types = types + "restaurant|";
        }
        if (takeawaySelected){
            types = types + "meal_takeaway|";
        }
        if (barSelected){
            types = types + "bar|";
        }

        if (types != "" && types.length() > 0 && types.charAt(types.length()-1)=='|') {
            // remove "|" from end of types string
            types = types.substring(0, types.length()-1);
        }

        // building url
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude + "&radius=" + sRadius + "&type=" + types);
        googlePlacesUrl.append("&key=" + "AIzaSyDqO1XsZmh6XI1rqPbiaa2zEqqG7InpDCI");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
}