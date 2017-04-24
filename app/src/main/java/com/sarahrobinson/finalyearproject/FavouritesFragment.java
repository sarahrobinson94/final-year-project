package com.sarahrobinson.finalyearproject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sarahrobinson.finalyearproject.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.MainActivity.firebaseRef;
import static com.sarahrobinson.finalyearproject.MapFragment.selectedPlaceId;

public class FavouritesFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "FavsFragment ******* ";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private ListView listView;
    private ArrayList<String> favPlacesList = new ArrayList<>();

    public FavouritesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        listView = (ListView)rootView.findViewById(R.id.listViewFavs);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference favPlacesRef = databaseRef.child("users").child(currentUserId).child("favouritePlaces");

        favPlacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Log.d(TAG, "GETTING CHILD");
                    favPlacesList.add(String.valueOf(dsp.getKey())); //add result into array list
                    Log.d(TAG, "Favourite Places: " + favPlacesList);
                    populateList();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)

        return rootView;
    }

    private void populateList(){
        // This is the array adapter, it takes the context of the activity as a
        // first parameter, the type of list view as a second parameter and your
        // array as a third parameter.
        ArrayAdapter arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.favourites_list_item, R.id.favsListItemPlaceName,
                favPlacesList);

        listView.setAdapter(arrayAdapter);
    }

    @Override
    public void onClick(View view) {
        
    }
}
