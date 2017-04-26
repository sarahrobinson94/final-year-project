package com.sarahrobinson.finalyearproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.sarahrobinson.finalyearproject.activities.LoginActivity;
import com.sarahrobinson.finalyearproject.activities.MainActivity;
import com.sarahrobinson.finalyearproject.R;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;

public class FavouritesFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "FavsFragment ******* ";

    private FragmentActivity favouritesFragmentContext;
    private FragmentManager fragmentManager;
    private Fragment fromFragment;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ArrayList<String> favPlacesList = new ArrayList<>();
    private LinearLayout layoutFavouritesList;

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getFragmentManager();
        fromFragment = ((MainActivity) getActivity()).favouritesFragment;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // starting login activity if user is not logged in
        if(firebaseAuth.getCurrentUser() == null){
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }else {
            // stay in fragment
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourites, container, false);

        // changing actionBar title
        getActivity().setTitle("Favourites");

        // getting fragment context
        favouritesFragmentContext = getActivity();

        // getting layout to be inflated
        layoutFavouritesList = (LinearLayout)rootView.findViewById(R.id.layoutFavouritesList);

        layoutFavouritesList.removeAllViews();
        favPlacesList.clear();

        getFavPlaceDetails();

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)

        return rootView;
    }

    public void getFavPlaceDetails(){

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favPlacesRef = databaseRef.child("users").child(currentUserId).child("favouritePlaces");

        favPlacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Getting favPlaceId");
                    // adding fav place id into array list
                    favPlacesList.add(String.valueOf(dsp.getKey()));
                    Log.d(TAG, "Favourite Places: " + favPlacesList);
                }
                getFavPlaceIds();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // method to get favourite place details from placeId
    public void getFavPlaceIds(){
        for(int i=0; i<favPlacesList.size(); i++){
            String id = favPlacesList.get(i);
            ((MainActivity)getActivity()).getDetails(id, fromFragment);
        }
    }

    // method to inflate a new layout for each place saved to user's favourites
    public void inflateNewListItem(String id, String image, String type, String name, String address){

        // inflating layout to be used as a list item
        LayoutInflater inflator = (LayoutInflater)favouritesFragmentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflator.inflate(R.layout.favourites_list_item, layoutFavouritesList, false);

        // adding inflated item layout to favourites list layout
        layoutFavouritesList.addView(listItem, layoutFavouritesList.getChildCount() - 1);

        ImageView imgIcon = (ImageView) listItem.findViewById(R.id.favsListItemPlaceIcon);
        TextView txtName = (TextView) listItem.findViewById(R.id.favsListItemPlaceName);
        TextView txtAddress = (TextView) listItem.findViewById(R.id.favsListItemPlaceAddress);
        // invisible textView for storing id
        TextView txtId = (TextView) listItem.findViewById(R.id.favsListItemPlaceId);

        // view more button
        Button btnView = (Button) listItem.findViewById(R.id.favsListItemBtnMore);


        // populating views with place details
        Picasso.with(getContext())
                .load(image)
                .into(imgIcon);
        txtName.setText(name);
        txtAddress.setText(address);
        txtId.setText(id);
    }

    @Override
    public void onClick(View view) {
    }
}
