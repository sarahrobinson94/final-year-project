package com.sarahrobinson.finalyearproject.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import static com.sarahrobinson.finalyearproject.activities.MainActivity.eventInviteeList;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.firebaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.fromFragmentString;

public class EventFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = "CreateEventF ******* ";

    private FragmentManager fragmentManager;

    public Event event;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // views
    private EditText txtEventName, txtEventDsc, txtEventDateTime, txtEventLocation;
    private Button btnEventSaveEdit, btnEventCancel, btnInvite;

    // event details
    private String strEventId;
    private String strEventName;
    private String strEventDsc;
    private String strEventDate;
    private String strEventTime;
    private String strLocation;
    private String strEventImage;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getFragmentManager();

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

        // getting views
        txtEventName = (EditText)rootView.findViewById(R.id.txtEventName);
        txtEventDsc = (EditText)rootView.findViewById(R.id.txtEventDsc);
        txtEventDateTime = (EditText)rootView.findViewById(R.id.txtEventDate);
        txtEventLocation = (EditText)rootView.findViewById(R.id.txtEventLocation);
        btnEventSaveEdit = (Button) rootView.findViewById(R.id.eventButtonSaveEdit);
        btnEventCancel = (Button) rootView.findViewById(R.id.eventButtonCancel);
        btnInvite = (Button) rootView.findViewById(R.id.btnInvite);
        btnEventSaveEdit.setOnClickListener(this);
        btnEventCancel.setOnClickListener(this);

        // user creating an event
        if (fromFragmentString == "Create event") {

            // changing actionBar title
            getActivity().setTitle("Create Event");

            btnEventSaveEdit.setText("SAVE");

        // user viewing an event
        } else {

            Log.d(TAG, "Not create event");

            // changing actionBar title
            getActivity().setTitle("Event Details");

            // hiding invite button
            btnInvite.setVisibility(rootView.GONE);
        }

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == btnEventSaveEdit)
        {
            if (btnEventSaveEdit.getText() == "SAVE")
            {
                Log.d(TAG, "CREATING EVENT");
                // get event details from user input
                getEventDetails(view);
            }
            else if (btnEventSaveEdit.getText() == "EDIT")
            {
                editState(view);
            }
        }
        else if (view == btnEventCancel)
        {
            Log.d(TAG, "CANCELLING EVENT CREATION");
            // go back to events list
            EventsFragment eventsFragment = new EventsFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_main, eventsFragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    //////////// SAVING EVENT TO FIREBASE REALTIME DATABASE ////////////

    private void getEventDetails(View view){
        // name
        if (!txtEventName.getText().toString().equals(null)) {
            strEventName = txtEventName.getText().toString();
        } else {
            strEventName = null;
        }
        // description
        if (!txtEventDsc.getText().toString().equals(null)) {
            strEventDsc = txtEventDsc.getText().toString();
        } else {
            strEventDsc = null;
        }
        // date & time
        if (!txtEventDateTime.getText().toString().equals(null)) {
            // TODO: 30/04/2017 split string into date and time
            strEventDate = txtEventDateTime.getText().toString();
            strEventTime = txtEventDateTime.getText().toString();
        } else {
            strEventDate = null;
            strEventTime = null;
        }
        // location
        if (txtEventLocation.getText().toString() != null) {
            strLocation = txtEventLocation.getText().toString();
        } else {
            strLocation = null;
        }
        // image
        strEventImage = null;

        // ensuring user enters a name
        if (strEventName.equals(null) || strEventName.equals("")|| strEventName.equals(" ")) {
            Toast.makeText(getActivity(), "Please enter an event name", Toast.LENGTH_SHORT).show();
        } else {
            // set up a new instance of event
            setNewEvent(view);
        }
    }

    private void setNewEvent(View view){
        // setting up new event
        event = new Event();
        event.setName(strEventName);
        event.setDescription(strEventDsc);
        event.setDate(strEventDate);
        event.setTime(strEventTime);
        event.setLocation(strLocation);
        event.setImage(strEventImage);
        // add event to firebase database
        writeNewEvent(view);
    }

    private void writeNewEvent(View view){
        // fetching unique key in advance
        strEventId = firebaseRef.child("events").push().getKey();
        // writing event to database
        firebaseRef.child("events").child(strEventId).setValue(event);
        // updating users
        updateUsers(view);
        /*
        firebaseRef.child("events").push().setValue((event), new DatabaseReference.CompletionListener() {
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error != null){
                    Toast.makeText(getActivity(), "There was an error creating the event", Toast.LENGTH_SHORT);
                } else {
                    Toast.makeText(getActivity(), "Event successfully created", Toast.LENGTH_SHORT);
                    btnEvent.setText("EDIT");
                    // make editTexts not editable
                    txtEventName.setEnabled(false);
                }
            }
        });
        */
    }

    // updating event creator & invitees on database
    private void updateUsers(View view){
        // updating event creator
        firebaseRef.child("users").child(currentUserId).child("events").child(strEventId).setValue("creator");
        // updating event invitees
        for (int i=0; i<eventInviteeList.size(); i++) {
            // get user id
            String friendId = eventInviteeList.get(i);
            // updating user node
            firebaseRef.child("users").child(friendId).child("events").child(strEventId).setValue("pending");
        }
        // show confirmation message
        Toast.makeText(getActivity(), "Event successfully created", Toast.LENGTH_SHORT).show();
        // update UI state
        viewState(view);
    }

    private void editState(View view){
        btnEventSaveEdit.setText("SAVE");
        // make editTexts editable
        txtEventName.setEnabled(true);
        txtEventDsc.setEnabled(true);
        txtEventDateTime.setEnabled(true);
        txtEventLocation.setEnabled(true);
        btnInvite.setVisibility(view.VISIBLE);
    }

    private void viewState(View view){
        btnEventSaveEdit.setText("EDIT");
        // make editTexts not editable
        txtEventName.setEnabled(false);
        txtEventDsc.setEnabled(false);
        txtEventDateTime.setEnabled(false);
        txtEventLocation.setEnabled(false);
        btnInvite.setVisibility(view.GONE);
    }

    ///////////////////////////////////////////////////////////////////
}
