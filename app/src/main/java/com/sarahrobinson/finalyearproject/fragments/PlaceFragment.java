package com.sarahrobinson.finalyearproject.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
//import com.sarahrobinson.finalyearproject.Manifest;
import com.sarahrobinson.finalyearproject.activities.MainActivity;
import com.sarahrobinson.finalyearproject.R;
import com.squareup.picasso.Picasso;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.firebaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.fromFragmentString;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.location;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.selectedFavPlaceId;
import static com.sarahrobinson.finalyearproject.fragments.MapFragment.selectedPlaceId;

public class PlaceFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "PlaceFragment ******* ";

    private FragmentActivity placeFragmentContext;
    private Fragment fromFragment;

    private String placeId;
    private Double placeLat, placeLng;
    private boolean isFavourite;

    private TextView tvPlaceType, tvPlaceName, tvPlaceAddress, tvPlacePhoneNo, tvPlaceWebsite;
    private ImageView ivPlaceIcon;
    private LinearLayout btnCall, btnSuggest, btnFavourite, btnDirections;

    public PlaceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fromFragment = ((MainActivity) getActivity()).placeFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_place, container, false);

        // changing actionBar title
        getActivity().setTitle("Place Details");

        // getting fragment context
        placeFragmentContext = getActivity();

        // textviews to be populated from json response
        ivPlaceIcon = (ImageView)rootView.findViewById(R.id.imageViewPlaceIcon);
        tvPlaceType = (TextView)rootView.findViewById(R.id.textViewPlaceType);
        tvPlaceName = (TextView)rootView.findViewById(R.id.textViewPlaceName);
        tvPlaceAddress = (TextView)rootView.findViewById(R.id.textViewPlaceAddress);
        tvPlacePhoneNo = (TextView)rootView.findViewById(R.id.textViewPlacePhoneNumber);
        tvPlaceWebsite = (TextView)rootView.findViewById(R.id.textViewPlaceWebsite);

        // call button
        btnCall = (LinearLayout)rootView.findViewById(R.id.btnCallPlace);
        btnCall.setOnClickListener(this);

        // suggest button
        btnSuggest = (LinearLayout)rootView.findViewById(R.id.btnSuggestPlace);
        btnSuggest.setOnClickListener(this);

        // favourite button
        btnFavourite = (LinearLayout)rootView.findViewById(R.id.btnFavouritePlace);
        btnFavourite.setOnClickListener(this);

        // directions button
        btnDirections = (LinearLayout)rootView.findViewById(R.id.btnGetDirections);
        btnDirections.setOnClickListener(this);

        // retrieving place details based on place id
        if (fromFragmentString == "MapFragment") {
            placeId = selectedPlaceId;
        } else if (fromFragmentString == "FavouritesFragment") {
            placeId = selectedFavPlaceId;
        }
        ((MainActivity)getActivity()).getDetails(placeId, fromFragment);

        // initially setting isFavourite to false
        isFavourite = false;

        // check if place is in favourites
        checkIfFavourite();

        return rootView;
    }

    public void checkIfFavourite(){

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favPlaceRef = databaseRef.child("users").child(currentUserId)
                .child("favouritePlaces").child(placeId);

        favPlaceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    isFavourite = true;
                }
                else {
                    isFavourite = false;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });

    }

    public void ShowPlaceDetails(String id, String image, String type, String name, String address,
                                 Double lat, Double lng, String phoneNo, String website) {
        Log.d(TAG, "ShowPlaceDetails entered");
        // load image from url
        if (!image.isEmpty()) {
            Picasso.with(getContext())
                    .load(image)
                    .into(ivPlaceIcon);
        }
        // set string details
        tvPlaceType.setText(type);
        tvPlaceName.setText(name);
        tvPlaceAddress.setText(address);
        if (phoneNo == null) {
            tvPlacePhoneNo.setText("Telephone number not available");
            tvPlacePhoneNo.setTextColor(Color.parseColor("#ADADAD"));
            // disable call button if no phone number
            btnCall.setEnabled(false);
        } else {
            tvPlacePhoneNo.setText(phoneNo);
        }
        if (website == null) {
            tvPlaceWebsite.setText("Website not available");
            tvPlaceWebsite.setTextColor(Color.parseColor("#ADADAD"));
        } else {
            tvPlaceWebsite.setText(website);
        }

        // set variables for place lat & lng
        placeLat = lat;
        placeLng = lng;
    }

    @Override
    public void onClick(View view) {
        // TODO: 18/04/2017 change to switch statements (in all fragments/activities)
        if (view == btnFavourite) {
            if (isFavourite == false) {
                // add to favourites list
                firebaseRef.child("users").child(currentUserId).child("favouritePlaces").
                        child(placeId).setValue(true);
                // set isFavourite to true
                isFavourite = true;
                // show success toast
                Toast.makeText(getActivity(), "Added to favourites", Toast.LENGTH_SHORT).show();
            } else if (isFavourite == true) {
                // remove from favourites list
                firebaseRef.child("users").child(currentUserId).child("favouritePlaces").
                        child(placeId).removeValue();
                // set isFavourite to false
                isFavourite = false;
                // show success toast
                Toast.makeText(getActivity(), "Removed from favourites", Toast.LENGTH_SHORT).show();
            }
        } else if (view == btnCall) {
            // open dialer with pre loaded place tel number
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tvPlacePhoneNo.getText().toString()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (view == btnDirections) {
            String uri = "http://maps.google.com/maps?f=d&hl=en&saddr="+location.getLatitude()+","+
                    location.getLongitude()+"&daddr="+placeLat+","+placeLng;
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(Intent.createChooser(intent, "Select an application"));
        }
    }
}
