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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private String strFriendship;
    private String strSearchedUserId;
    private String strSearchedUserName;
    private String strSearchedUserImg;

    private Pattern validEmailRegex =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

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

    // method for validating entered email,
    // checking if it exists in db,
    // and displaying the user if appropriate
    private void findEmailUser() {

        // getting searched email
        strSearchedEmail = txtEmailSearch.getText().toString().trim();
        Log.d(TAG, "email " + strSearchedEmail);

        // checking searched email
        if (strSearchedEmail.isEmpty()) {
            // no text entered
            Toast.makeText(getActivity(), "Please enter an email address" ,Toast.LENGTH_LONG).show();
        } else if (!isValidEmailAddress(strSearchedEmail)) {
            // invalid email address entered
            Toast.makeText(getActivity(), "Please enter a valid email address" ,Toast.LENGTH_LONG).show();
        } else {
            // valid email address entered, creating db search query
            Query query1 = databaseRef.child("users").orderByChild("email").equalTo(strSearchedEmail);

            query1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // checking if searched email exists in the database
                    if (dataSnapshot.exists()) {
                        // searched email exists in the database
                        Log.d(TAG, "email exists");
                        // getting search user's id
                        for (final DataSnapshot dsp1 : dataSnapshot.getChildren()) {
                            strSearchedUserId = dsp1.getKey().toString();
                            // if searched email is the current user's email address, show message
                            if (strSearchedUserId.equals(currentUserId)) {
                                Toast.makeText(getActivity(), "Searched email matches the email address" +
                                        " of this account", Toast.LENGTH_LONG).show();
                            // if searched email is not the current user's email address, continue
                            // and check if a friendship already exists
                            } else {
                                // checking if friendship exists in db
                                Query query2 = databaseRef.child("friendships").child(currentUserId)
                                        .child(strSearchedUserId);
                                query2.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            // friendship already exists with searched email user
                                            Friendship friendship = dataSnapshot.getValue(Friendship.class);
                                            // checking friendship status
                                            if (friendship.getRequestStatus().equals("pending")) {
                                                Toast.makeText(getActivity(), "User is already" +
                                                        " a pending friend", Toast.LENGTH_LONG).show();
                                            } else if (friendship.getRequestStatus().equals("accepted")) {
                                                Toast.makeText(getActivity(), "User is already" +
                                                        " on your friends list", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            // friendship doesn't exist with searched email user
                                            // populate user id, name & image views
                                            if (dsp1.child("image").exists()) {
                                                strSearchedUserImg = dsp1.child("image").getValue().toString();
                                                if (!strSearchedUserImg.isEmpty())
                                                Picasso.with(getContext())
                                                        .load(strSearchedUserImg)
                                                        .transform(new CircleTransform())
                                                        .into(imgEmailUserImg);
                                            }
                                            strSearchedUserName = dsp1.child("name").getValue().toString();
                                            tvEmailUserId.setText(strSearchedUserId);
                                            tvEmailUserName.setText(strSearchedUserName);
                                            // show user
                                            layoutEmailUser.setVisibility(View.VISIBLE);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    } else {
                        // searched email does not exist in database
                        Toast.makeText(getActivity(), "User not found" ,Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    // method for validating email address format
    private boolean isValidEmailAddress(String email) {
        Matcher matcher = validEmailRegex .matcher(email);
        return matcher.find();
    }

    @Override
    public void onClick(View view) {
        if (view == btnFindEmailUser) {
            Log.d(TAG, "btnFindEmailUser clicked");
            findEmailUser();
        }
    }
}
