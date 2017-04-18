package com.sarahrobinson.finalyearproject;


import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import static com.sarahrobinson.finalyearproject.HomeFragment.sLocation;
import static com.sarahrobinson.finalyearproject.HomeFragment.sRadius;
import static com.sarahrobinson.finalyearproject.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.MainActivity.firebaseRef;
import static com.sarahrobinson.finalyearproject.MainActivity.location;
import static com.sarahrobinson.finalyearproject.MapFragment.selectedPlaceId;
import static com.sarahrobinson.finalyearproject.MapFragment.thePlaceId;

public class PlaceFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "PlaceFragment ******* ";

    public static TextView tvPlaceType, tvPlaceName, tvPlaceAddress, tvPlacePhoneNo, tvPlaceWebsite;
    public static ImageView ivPlaceIcon;
    private Button btnAddToFavourites;

    public PlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place, container, false);

        // changing actionBar title
        getActivity().setTitle("Place Details");

        // textviews to be populated from json response
        ivPlaceIcon = (ImageView)rootView.findViewById(R.id.imageViewPlaceIcon);
        tvPlaceType = (TextView)rootView.findViewById(R.id.textViewPlaceType);
        tvPlaceName = (TextView)rootView.findViewById(R.id.textViewPlaceName);
        tvPlaceAddress = (TextView)rootView.findViewById(R.id.textViewPlaceAddress);
        tvPlacePhoneNo = (TextView)rootView.findViewById(R.id.textViewPlacePhoneNumber);
        tvPlaceWebsite = (TextView)rootView.findViewById(R.id.textViewPlaceWebsite);

        // add to favourites button
        btnAddToFavourites = (Button)rootView.findViewById(R.id.buttonAddToFavourites);
        btnAddToFavourites.setOnClickListener(this);

        getPlaceDetails();

        return rootView;
    }


    ///////////////////////////////////////////////////////////////////////////////////
    //                             GETTING PLACE DETAILS                             //
    ///////////////////////////////////////////////////////////////////////////////////

    // TODO: 18/04/2017 move to seperate class and pass in the relevant placeId

    public void getPlaceDetails(){
        Log.d(TAG, "getPlaceDetails");

        String url = getUrl();
        Object[] DataTransfer = new Object[1];
        //DataTransfer[0] = googleMap;
        DataTransfer[0] = url;
        GetSinglePlaceData getSinglePlaceData = new GetSinglePlaceData();
        getSinglePlaceData.execute(DataTransfer);
    }

    private String getUrl() {
        StringBuilder placeDetailsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
        placeDetailsUrl.append("placeid=" + selectedPlaceId);
        placeDetailsUrl.append("&key=" + "AIzaSyDqO1XsZmh6XI1rqPbiaa2zEqqG7InpDCI");
        Log.d(TAG, "getUrl: " + placeDetailsUrl.toString());
        return (placeDetailsUrl.toString());
    }

    @Override
    public void onClick(View view) {
        // TODO: 18/04/2017 change to switch statements (in all fragments/activities)
        if (view == btnAddToFavourites){
            // add to favourites list
            firebaseRef.child("users").child(currentUserId).child("favouritePlaces").
                    child(selectedPlaceId).setValue(true);
            // show success toast
            Toast.makeText(getActivity(), "Added to favourites", Toast.LENGTH_SHORT).show();
        }
    }
}
