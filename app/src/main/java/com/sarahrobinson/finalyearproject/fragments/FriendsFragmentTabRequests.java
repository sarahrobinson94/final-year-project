package com.sarahrobinson.finalyearproject.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.classes.CircleTransform;
import com.sarahrobinson.finalyearproject.classes.Event;
import com.sarahrobinson.finalyearproject.classes.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.databaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.eventInviteeList;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragmentTabRequests extends Fragment {

    private static final String TAG = "FriendRequests ******* ";

    private FragmentActivity tabFriendRequestContext;

    private LinearLayout layoutFriendRequestList;

    private ArrayList<String> listFriendRequests = new ArrayList<>();

    private String userId, userName, userImg, userEmail;


    public FriendsFragmentTabRequests() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friends_tab_requests, container, false);

        // getting fragment context
        tabFriendRequestContext = getActivity();

        // getting layout to be inflated
        layoutFriendRequestList = (LinearLayout)rootView.findViewById(R.id.layoutFriendRequestsList);

        // clearing lists when fragment is first loaded
        layoutFriendRequestList.removeAllViews();
        listFriendRequests.clear();

        retrieveFriendRequests();

        return rootView;
    }

    // method to get user's pending friend requests from database
    public void retrieveFriendRequests() {
        Log.d(TAG, "retrieveFriendRequests entered");

        // creating search query
        Query query = databaseRef.child("friendships").child(currentUserId).orderByChild("requestStatus").equalTo("pending");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                        userId = dsp.getKey().toString();
                        retreiveUserDetails();

                    }
                } else {
                    // user has no pending requests in database
                    Toast.makeText(getActivity(), "No pending requests to show" ,Toast.LENGTH_SHORT).show();

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void retreiveUserDetails() {

        DatabaseReference pendingFriendsRef = databaseRef.child("users").child(userId);

        pendingFriendsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Getting pending friend");

                // getting pending friend details
                User user = dataSnapshot.getValue(User.class);

                userName = (user.getName());
                userEmail = (user.getEmail());
                userImg = (user.getImage());

                // TODO: 02/05/2017 inflate pending friend list item
                inflateFriendRequestListItem(userName, userEmail,
                        userImg);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void inflateFriendRequestListItem(String name, String email, String img) {
        // inflating layout to be used as a list item
        LayoutInflater inflator = (LayoutInflater)tabFriendRequestContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflator.inflate(R.layout.list_item_friend, layoutFriendRequestList, false);

        // adding inflated item layout to friend request list layout
        layoutFriendRequestList.addView(listItem, layoutFriendRequestList.getChildCount() - 1);

        TextView tvUserId = (TextView)listItem.findViewById(R.id.friendsListItemFriendId);
        TextView tvUserName = (TextView)listItem.findViewById(R.id.friendsListItemFriendName);
        //TextView tvUserEmail = (TextView)listItem.findViewById(R.id.friendsListItemFriendEmail);
        ImageView ivUserImg = (ImageView)listItem.findViewById(R.id.friendsListItemFriendImg);

        // invisible textView for storing id
        TextView tvEventId = (TextView) listItem.findViewById(R.id.eventsListItemId);

        // populating views with place details
        tvUserName.setText(name);
        //tvUserEmail.setText(email);
        Picasso.with(getContext())
                .load(img)
                .transform(new CircleTransform())
                .into(ivUserImg);
        tvUserId.setText(userId);
    }
}
