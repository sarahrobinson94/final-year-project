package com.sarahrobinson.finalyearproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarahrobinson.finalyearproject.activities.LoginActivity;
import com.sarahrobinson.finalyearproject.R;

public class SettingsFragment extends Fragment implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private static final String TAG = "FavouritesFragment ******* ";

    public SettingsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_favourites_list, container, false);

        // changing actionBar title
        getActivity().setTitle("Settings");

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)

        return rootView;
    }

    @Override
    public void onClick(View view) {

    }
}
