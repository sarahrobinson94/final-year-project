package com.sarahrobinson.finalyearproject;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import static com.sarahrobinson.finalyearproject.HomeFragment.sLocation;
import static com.sarahrobinson.finalyearproject.HomeFragment.sRadius;
import static com.sarahrobinson.finalyearproject.MainActivity.location;
import static com.sarahrobinson.finalyearproject.MapFragment.thePlaceId;

public class PlaceFragment extends Fragment {

    private static final String TAG = "PlaceFragment ******* ";

    public static String thePlaceName, thePlacePhoneNo;
    public static TextView placeName, placeAddress, placePhoneNo, placeWebsite;

    public PlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place, container, false);

        placeName = (TextView)rootView.findViewById(R.id.textViewPlaceName);
        placeAddress = (TextView)rootView.findViewById(R.id.textViewPlaceAddress);
        placePhoneNo = (TextView)rootView.findViewById(R.id.textViewPlacePhoneNumber);
        placeWebsite = (TextView)rootView.findViewById(R.id.textViewPlaceWebsite);

        getPlaceDetails();

        return rootView;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                             GETTING PLACE DETAILS                             //
    ///////////////////////////////////////////////////////////////////////////////////


    public void getPlaceDetails(){
        Log.d(TAG, "getPlaceDetails");

        String url = getUrl();
        Object[] DataTransfer = new Object[1];
        //DataTransfer[0] = googleMap;
        DataTransfer[0] = url;
        GetPlaceDetails getPlaceDetails = new GetPlaceDetails();
        getPlaceDetails.execute(DataTransfer);

        // TODO: 16/04/2017 place current location marker ??
    }

    private String getUrl() {
        StringBuilder placeDetailsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        placeDetailsUrl.append("placeid=" + thePlaceId);
        placeDetailsUrl.append("&key=" + "AIzaSyDqO1XsZmh6XI1rqPbiaa2zEqqG7InpDCI");
        Log.d(TAG, "getUrl: " + placeDetailsUrl.toString());
        return (placeDetailsUrl.toString());
    }
}
