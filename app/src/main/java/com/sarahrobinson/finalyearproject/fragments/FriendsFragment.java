package com.sarahrobinson.finalyearproject.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarahrobinson.finalyearproject.activities.LoginActivity;
import com.sarahrobinson.finalyearproject.R;

import static android.R.id.tabhost;

public class FriendsFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "FriendsFragment ******* ";

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private FragmentTabHost mTabHost;

    public FriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // adding 'find friends' action item
        setHasOptionsMenu(true);

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
        View rootView = inflater.inflate(R.layout.fragment_friends_list, container, false);

        // changing actionBar title
        getActivity().setTitle("Friends");

        // setting up tabs
        mTabHost = (FragmentTabHost)rootView.findViewById(tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator("FRIENDS", null),
                FriendsFragmentTabFriends.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator("REQUESTS", null),
                FriendsFragmentTabRequests.class, null);

        // setting tab text colours
        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                // unselected tabs
                for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
                    TextView tv = (TextView) mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
                    tv.setTextColor(Color.parseColor("#ADADAD"));
                }
                // selected tab
                TextView tv = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title);
                tv.setTextColor(Color.parseColor("#666666"));
            }
        });

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)
        return rootView;
    }

    // changing action bar button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // hide setting action item
        menu.findItem(R.id.action_settings).setVisible(false);
        // inflate find friends action item
        inflater.inflate(R.menu.action_find_friends, menu);
    }

    @Override
    public void onResume() {
        // setting initial tab text colour
        for(int i=0;i<mTabHost.getTabWidget().getChildCount();i++)
        {
            // unselected tabs
            TextView tvUnselected = (TextView)mTabHost.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tvUnselected.setTextColor(Color.parseColor("#ADADAD"));
            // selected tab
            TextView tvSelected = (TextView) mTabHost.getCurrentTabView().findViewById(android.R.id.title); // selected tab
            tvSelected.setTextColor(Color.parseColor("#666666"));
        }
        super.onResume();
    }

    @Override
    public void onClick(View view) {

    }
}
