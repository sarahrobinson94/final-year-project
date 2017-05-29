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

public class MapFragment extends Fragment {

    private static final String TAG = "MapFragment ******* ";

    private FragmentTabHost mTabHost;

    public MapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflating layout
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

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
}