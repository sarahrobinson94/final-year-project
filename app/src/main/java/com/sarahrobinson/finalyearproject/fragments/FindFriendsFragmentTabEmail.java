package com.sarahrobinson.finalyearproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ser.std.IterableSerializer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.classes.CircleTransform;
import com.sarahrobinson.finalyearproject.classes.Event;
import com.sarahrobinson.finalyearproject.classes.Friendship;
import com.sarahrobinson.finalyearproject.classes.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentLocation;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.databaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.firebaseRef;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFriendsFragmentTabEmail extends Fragment implements View.OnClickListener {

    private static final String TAG = "TabEmail ******* ";

    private EditText txtEmailSearch;
    private LinearLayout layoutEmailUser;
    private ImageView imgEmailUserImg;
    private TextView tvEmailUserId;
    private TextView tvEmailUserName;
    private Button btnFindEmailUser;

    private String strSearchedEmail;
    private String searchedEmailStatusInDb;
    private String strSearchedUserId;
    private String strSearchedUserName;
    private String strSearchedUserImg;

    public FindFriendsFragmentTabEmail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_friends_tab_email, container, false);

        // getting views
        txtEmailSearch = (EditText)rootView.findViewById(R.id.editTextFindEmailUser);
        layoutEmailUser = (LinearLayout) rootView.findViewById(R.id.tabLayoutEmailUser);
        imgEmailUserImg = (ImageView)rootView.findViewById(R.id.tabEmailUserImg);
        tvEmailUserId = (TextView) rootView.findViewById(R.id.tabEmailUserId);
        tvEmailUserName = (TextView) rootView.findViewById(R.id.tabEmailUserName);
        btnFindEmailUser = (Button) rootView.findViewById(R.id.btnFindEmailUser);

        // hiding search result layout until user is found
        layoutEmailUser.setVisibility(View.GONE);

        // find user button onclick listener
        btnFindEmailUser.setOnClickListener(this);

        return rootView;
    }

    private void findEmailUser() {

        // getting searched email
        strSearchedEmail = txtEmailSearch.getText().toString().trim();
        Log.d(TAG, "email " + strSearchedEmail);

        // checking if email has been entered
        if (strSearchedEmail.isEmpty()) {
            Toast.makeText(getActivity(), "Please enter an email address" ,Toast.LENGTH_SHORT).show();
        } else {

            // creating search query
            Query query = databaseRef.child("users").orderByChild("email").equalTo(strSearchedEmail);

            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "email exists");

                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            strSearchedUserId = dsp.getKey().toString();

                            checkSearchedEmail();

                            if (strSearchedUserId.equals(currentUserId)) {
                                // searched email is the current user's email address
                                Toast.makeText(getActivity(), "Searched email matches the email address" +
                                        "of this account", Toast.LENGTH_SHORT).show();
                            } else if (searchedEmailStatusInDb.equals("pending")) {
                                // searched email is the current user's pending friend
                                Toast.makeText(getActivity(), "Searched user is already a pending" +
                                        "friend", Toast.LENGTH_SHORT).show();
                            } else if (searchedEmailStatusInDb.equals("accepted")) {
                                // searched email is the current user's friend
                                Toast.makeText(getActivity(), "Searched user is already on your" +
                                        "friends list", Toast.LENGTH_SHORT).show();
                            } else {

                                if (dsp.child("image").exists()) {
                                    strSearchedUserImg = dsp.child("image").getValue().toString();
                                    Picasso.with(getContext())
                                            .load(strSearchedUserImg)
                                            .transform(new CircleTransform())
                                            .into(imgEmailUserImg);
                                }

                                strSearchedUserName = dsp.child("name").getValue().toString();

                                Log.d(TAG, "id " + strSearchedUserId);
                                Log.d(TAG, "name " + strSearchedUserName);
                                Log.d(TAG, "imgUrl " + strSearchedUserImg);

                                // populate views
                                tvEmailUserId.setText(strSearchedUserId);
                                tvEmailUserName.setText(strSearchedUserName);
                                // show user
                                layoutEmailUser.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        // searched email does not exist in database
                        Toast.makeText(getActivity(), "Email not found" ,Toast.LENGTH_SHORT).show();

                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    private String checkSearchedEmail() {

        // initially setting variable to non-existent
        searchedEmailStatusInDb = "non-existent";

        // creating search query to check if searched user is pending friend
        Query queryPending = databaseRef.child("friendships").child(currentUserId)
                .orderByChild("requestStatus").equalTo("pending");

        queryPending.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        String userId = dsp.getKey().toString();
                        if (userId.equals(strSearchedUserId)) {
                            searchedEmailStatusInDb = "pending";
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        // creating search query to check if searched user is accepted friend
        Query queryAccepted = databaseRef.child("friendships").child(currentUserId)
                .orderByChild("requestStatus").equalTo("accepted");

        queryAccepted.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        String userId = dsp.getKey().toString();
                        if (userId == strSearchedUserId) {
                            searchedEmailStatusInDb = "accepted";
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return searchedEmailStatusInDb;
    }

    @Override
    public void onClick(View view) {
        if (view == btnFindEmailUser) {
            Log.d(TAG, "btnFindEmailUser clicked");
            findEmailUser();
        }
    }
}
