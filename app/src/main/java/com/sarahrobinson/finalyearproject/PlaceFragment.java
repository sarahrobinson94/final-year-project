package com.sarahrobinson.finalyearproject;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import static com.sarahrobinson.finalyearproject.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.MainActivity.firebaseRef;
import static com.sarahrobinson.finalyearproject.MainActivity.fromFragmentString;
import static com.sarahrobinson.finalyearproject.MainActivity.selectedFavPlaceId;
import static com.sarahrobinson.finalyearproject.MapFragment.selectedPlaceId;

public class PlaceFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "PlaceFragment ******* ";

    private FragmentActivity placeFragmentContext;
    private Fragment fromFragment;
    private String placeId;

    private TextView tvPlaceType, tvPlaceName, tvPlaceAddress, tvPlacePhoneNo, tvPlaceWebsite;
    private ImageView ivPlaceIcon;
    private Button btnAddToFavourites;

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

        // add to favourites button
        btnAddToFavourites = (Button)rootView.findViewById(R.id.buttonAddToFavourites);
        btnAddToFavourites.setOnClickListener(this);

        // retrieving place details based on place id
        if (fromFragmentString == "MapFragment") {
            placeId = selectedPlaceId;
        } else if (fromFragmentString == "FavouritesFragment") {
            placeId = selectedFavPlaceId;
        }
        ((MainActivity)getActivity()).getDetails(placeId, fromFragment);

        return rootView;
    }

    public void ShowPlaceDetails(String id, String image, String type, String name, String address,
                                  String phoneNo, String website) {
        Log.d(TAG, "ShowPlaceDetails entered");
        // load image from url
        Picasso.with(getContext())
                .load(image)
                .into(ivPlaceIcon);
        // set string details
        tvPlaceType.setText(type);
        tvPlaceName.setText(name);
        tvPlaceAddress.setText(address);
        tvPlacePhoneNo.setText(phoneNo);
        tvPlaceWebsite.setText(website);
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
