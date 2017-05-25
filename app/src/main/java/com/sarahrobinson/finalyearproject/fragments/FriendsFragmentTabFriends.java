package com.sarahrobinson.finalyearproject.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.classes.CircleTransform;
import com.sarahrobinson.finalyearproject.classes.Friendship;
import com.sarahrobinson.finalyearproject.classes.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.databaseRef;

/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragmentTabFriends extends Fragment {

    private static final String TAG = "Friends ******* ";

    private FragmentActivity tabFriendContext;

    private LinearLayout layoutFriendList;
    private TextView txtNoFriends;

    private ArrayList<String> listFriends = new ArrayList<>();
    private String userId, userName, userImg, userEmail;

    public FriendsFragmentTabFriends() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_friends_tab_friends, container, false);

        // getting fragment context
        tabFriendContext = getActivity();

        // hiding no friends textView on initial load
        txtNoFriends = (TextView)rootView.findViewById(R.id.txtNoFriends);
        txtNoFriends.setVisibility(rootView.GONE);

        // getting layout to be inflated
        layoutFriendList = (LinearLayout)rootView.findViewById(R.id.layoutFriendsList);

        // clearing lists when fragment is first loaded
        listFriends.clear();

        retrieveFriends(rootView);

        return rootView;
    }

    // method to get user's friend list from database
    public void retrieveFriends(final View view) {
        Log.d(TAG, "retrieveFriends entered");

        // creating search query
        Query query = databaseRef.child("friendships").child(currentUserId)
                .orderByChild("requestStatus").equalTo("accepted");

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        // getting friend request details
                        userId = dsp.getKey().toString();
                        retreiveUserDetails();
                    }
                } else {
                    // user has no friends in database, show message
                    txtNoFriends.setVisibility(view.VISIBLE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void retreiveUserDetails() {

        DatabaseReference friendRef = databaseRef.child("users").child(userId);

        friendRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Getting friend details");
                // getting friend details
                User user = dataSnapshot.getValue(User.class);
                userName = (user.getName());
                userEmail = (user.getEmail());
                userImg = (user.getImage());
                // creating list item
                inflateFriendListItem(userName, userEmail,
                        userImg);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void inflateFriendListItem(String name, String email, String img) {
        // inflating layout to be used as a list item
        LayoutInflater inflator = (LayoutInflater)tabFriendContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflator.inflate(R.layout.list_item_friend, layoutFriendList, false);

        // adding inflated item layout to friend list layout
        layoutFriendList.addView(listItem, layoutFriendList.getChildCount() - 1);

        TextView tvUserId = (TextView)listItem.findViewById(R.id.friendsListItemFriendId);
        TextView tvUserName = (TextView)listItem.findViewById(R.id.friendsListItemFriendName);
        //TextView tvUserEmail = (TextView)listItem.findViewById(R.id.friendsListItemFriendEmail);
        ImageView ivUserImg = (ImageView)listItem.findViewById(R.id.friendsListItemFriendImg);
        CheckBox chkBoxAcceptRequest = (CheckBox)listItem.findViewById(R.id.friendsListItemChkBox);

        // populating views with user details
        tvUserName.setText(name);
        //tvUserEmail.setText(email);
        if (!img.isEmpty()) {
            Picasso.with(getContext())
                    .load(img)
                    .transform(new CircleTransform())
                    .into(ivUserImg);
        }
        tvUserId.setText(userId);

        // hiding checkbox
        chkBoxAcceptRequest.setVisibility(listItem.GONE);
    }
}
