package com.sarahrobinson.finalyearproject.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.activities.LoginActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFriendsFragment extends Fragment {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FragmentTabHost tabHost;

    public FindFriendsFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_find_friends, container, false);

        // changing actionBar title
        getActivity().setTitle("Find Friends");

        // setting up tabs
        tabHost = (FragmentTabHost)rootView.findViewById(android.R.id.tabhost);
        tabHost.setup(getActivity(), getChildFragmentManager(), R.layout.fragment_find_friends);

        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator("FACEBOOK"),
                FindFriendsFragmentTabFacebook.class, null);
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator("GOOGLE"),
                FindFriendsFragmentTabGoogle.class, null);
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator("EMAIL"),
                FindFriendsFragmentTabEmail.class, null);

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)
        return rootView;
    }

}
