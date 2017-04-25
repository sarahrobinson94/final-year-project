package com.sarahrobinson.finalyearproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import static com.sarahrobinson.finalyearproject.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.MainActivity.firebaseRef;
import static com.sarahrobinson.finalyearproject.MapFragment.selectedPlaceId;

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

        ((MainActivity)getActivity()).getPlaceDetails(selectedPlaceId);

        return rootView;
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
