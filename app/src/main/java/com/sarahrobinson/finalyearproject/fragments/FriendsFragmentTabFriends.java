package com.sarahrobinson.finalyearproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sarahrobinson.finalyearproject.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragmentTabFriends extends Fragment {


    public FriendsFragmentTabFriends() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends_tab_friends, container, false);
    }

}
