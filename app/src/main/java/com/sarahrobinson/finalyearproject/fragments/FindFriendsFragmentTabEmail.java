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
import com.google.firebase.database.ValueEventListener;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.classes.Event;
import com.sarahrobinson.finalyearproject.classes.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.databaseRef;

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

    private String searchedEmail;

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
        searchedEmail = txtEmailSearch.getText().toString();

        /*
        databaseRef.child("users").orderByChild("email").equalTo(searchedEmail).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String name = dataSnapshot.child("name").getValue().toString();
                    Log.d(TAG, name);

                    Log.d(TAG, "email exists");

                    // retrieve user details
                    //tvEmailUserId.setText(user.getId());
                    //tvEmailUserName.setText(user.getName());
                    //Picasso.with(getContext())
                    //        .load(user.getImage())
                    //        .into(imgEmailUserImg);
                    // show user
                    //layoutEmailUser.setVisibility(View.VISIBLE);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        */

        /*
        // creating database refereence
        DatabaseReference usersRef = databaseRef.child("users");
        // searching for email
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<String> list = new ArrayList<String>();
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                for (DataSnapshot child : children) {

                    User user = new User();

                    Iterable<DataSnapshot> children2 = child.getChildren();

                    for (DataSnapshot child2 : children2) {

                        if (child2.getKey().equals("email")) {

                            if (child2.getValue().equals(searchedEmail)) {

                                Log.d(TAG, "email exists");

                                // retrieve user details
                                //tvEmailUserId.setText(user.getId());
                                //tvEmailUserName.setText(user.getName());
                                //Picasso.with(getContext())
                                //        .load(user.getImage())
                                //        .into(imgEmailUserImg);
                                // show user
                                //layoutEmailUser.setVisibility(View.VISIBLE);
                            } else {
                                //Toast.makeText(getActivity(),"User does not exist",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        */
    }

    private void showEmailUser() {

    }

    @Override
    public void onClick(View view) {
        if (view == btnFindEmailUser) {
            Log.d(TAG, "btnFindEmailUser clicked");
            findEmailUser();
        }
    }
}
