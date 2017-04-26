package com.sarahrobinson.finalyearproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.sarahrobinson.finalyearproject.activities.LoginActivity;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.classes.Event;
import com.sarahrobinson.finalyearproject.classes.User;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.firebaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.fromFragmentString;

public class EventFragment extends Fragment implements View.OnClickListener{

    public Event event;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private static final String TAG = "CreateEventF ******* ";

    // views
    private EditText txtEventName;
    private Button eventButton;

    // event details
    private String strEventName;
    private String strEventDsc;
    private String strEventDate;
    private String strEventTime;
    private String strLocation;
    private String strEventImage;
    private String strEventCreator;
    private ArrayList<String> listEventInvited;

    public EventFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        txtEventName = (EditText)rootView.findViewById(R.id.txtEventName);
        eventButton = (Button) rootView.findViewById(R.id.eventButton);
        eventButton.setOnClickListener(this);

        // checking where the user is coming from
        if (fromFragmentString == "Create event") {

            // changing actionBar title
            getActivity().setTitle("Create Event");

            txtEventName.setHint("Event Name");
            eventButton.setText("SAVE");

        } else {

            Log.d(TAG, "not create event");

        }

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == eventButton) {
            Log.d(TAG, "CREATING EVENT");
            if (eventButton.getText() == "SAVE") {

                // get event details from user input
                getEventDetails();
                // set up a new instance of event
                setNewEvent();
                // add event to firebase database
                writeNewEvent();

            }
        }
    }

    //////////// SAVING EVENT TO FIREBASE REALTIME DATABASE ////////////

    private void getEventDetails(){
        // getting event details
        strEventName = txtEventName.getText().toString();
        strEventDsc = null;
        strEventDate = null;
        strEventTime = null;
        strLocation = null;
        strEventImage = null;
        strEventCreator = currentUserId;
        listEventInvited = null;
    }

    private void setNewEvent(){
        // setting up new event
        event = new Event();
        event.setName(strEventName);
        event.setDescription(strEventDsc);
        event.setDate(strEventDate);
        event.setTime(strEventTime);
        event.setLocation(strLocation);
        event.setImage(strEventImage);
        event.setCreator(strEventCreator);
        event.setInvited(listEventInvited);
    }

    private void writeNewEvent(){
        // saving new event to firebase database
        firebaseRef.child("events").push().setValue(event);
        Toast.makeText(getActivity(), "Event successfully created", Toast.LENGTH_SHORT);
        eventButton.setText("EDIT");
        // make editTexts not editable
        txtEventName.setEnabled(false);

        /*
        firebaseRef.child("events").push().setValue((event), new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null){
                    Toast.makeText(getActivity(), "There was an error creating the event", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(getActivity(), "Event successfully created", Toast.LENGTH_SHORT);
                    eventButton.setText("EDIT");
                    // make editTexts not editable
                    txtEventName.setEnabled(false);
                }
            }
        });
        */
    }

    ///////////////////////////////////////////////////////////////////
}
