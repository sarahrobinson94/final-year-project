package com.sarahrobinson.finalyearproject.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sarahrobinson.finalyearproject.activities.LoginActivity;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.activities.MainActivity;
import com.sarahrobinson.finalyearproject.classes.Event;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.databaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.eventInviteeList;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.firebaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.fromFragmentString;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.selectedEventId;

public class EventFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CreateEventF ******* ";

    private FragmentManager fragmentManager;
    private Fragment fromFragment;

    public Event event;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // views
    private EditText txtEventName, txtEventDsc, txtEventLocation;
    private TextView tvEventDate, tvEventTime, tvNoAttendees;
    private ImageView btnDatePicker, btnTimePicker;
    private Spinner spinnerLocation;
    private Button btnInvite, btnEventSaveEdit, btnEventCancel;

    // for datetime pickers
    private int mYear, mMonth, mDay, mHour, mMinute;

    // for getting/storing favourite places
    private List<String> favPlacesIdList = new ArrayList<>();
    private List<String> favPlacesIdList2 = new ArrayList<>();
    private List<String> favPlacesInfoList = new ArrayList<>();
    private HashMap<Integer,String> spinnerMap = new HashMap<Integer, String>();

    // event details
    private String strEventId;
    private String strEventName;
    private String strEventDsc;
    private String strEventDate;
    private String strEventTime;
    private String strLocation;
    private String strLocationId;
    private String strEventImage;

    private Thread checkIfDoneThread;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentManager = getFragmentManager();
        fromFragment = ((MainActivity)getActivity()).eventFragment;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // starting login activity if user is not logged in
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        } else {
            // stay in fragment
        }

        // thread for concurrently checking if all fav place data has been retrieved
        checkIfDoneThread = new Thread(new Runnable() {
            public void run() {
                checkIfDone();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        // getting views
        //
        // editTexts
        txtEventName = (EditText) rootView.findViewById(R.id.txtEventName);
        //txtEventDsc = (EditText)rootView.findViewById(R.id.txtEventDsc);
        //txtEventLocation = (EditText) rootView.findViewById(R.id.txtEventLocation);
        // textViews
        tvEventDate = (TextView) rootView.findViewById(R.id.tvEventDate);
        tvEventTime = (TextView) rootView.findViewById(R.id.tvEventTime);
        tvNoAttendees = (TextView) rootView.findViewById(R.id.tvNoEventAttendees);
        // buttons
        btnDatePicker = (ImageView) rootView.findViewById(R.id.btnEventDatePicker);
        btnTimePicker = (ImageView) rootView.findViewById(R.id.btnEventTimePicker);
        btnInvite = (Button) rootView.findViewById(R.id.btnInvite);
        btnEventSaveEdit = (Button) rootView.findViewById(R.id.eventButtonSaveEdit);
        btnEventCancel = (Button) rootView.findViewById(R.id.eventButtonCancel);
        // spinner
        spinnerLocation = (Spinner) rootView.findViewById(R.id.spinnerEventLocation);

        // setting onclick listeners
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnEventSaveEdit.setOnClickListener(this);
        btnEventCancel.setOnClickListener(this);

        // if user is creating an event
        if (fromFragmentString == "Create event") {
            Log.d(TAG, "Create event");
            // change actionBar title
            getActivity().setTitle("Create Event");
            // set UI state
            editState(rootView);
            // get data for spinner
            getFavPlacesFromDb();
        // if user is viewing an event
        } else if (fromFragmentString == "EventsFragment") {
            Log.d(TAG, "View event");
            // change actionBar title
            getActivity().setTitle("Event Details");
            // retrieve event details
            strEventId = selectedEventId;
            retrieveSelectedEventDetails();
            // set UI state
            viewState(rootView);
        }

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)

        return rootView;
    }

    @Override
    public void onClick(View view) {
        //
        // if onclick datepicker
        if (view == btnDatePicker)
        {
            // get current date
            final Calendar c = Calendar.getInstance();
            mYear = c.get(Calendar.YEAR);
            mMonth = c.get(Calendar.MONTH);
            mDay = c.get(Calendar.DAY_OF_MONTH);
            // launch datepicker dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year,
                                              int monthOfYear, int dayOfMonth) {
                            tvEventDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                        }
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();
        //
        // if onclick timepicker
        }
        else if (view == btnTimePicker)
        {
            // get current time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay,
                                              int minute) {
                            tvEventTime.setText(hourOfDay + ":" + minute);
                        }
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        }
        //
        // if onclick spinner
        else if (view == spinnerLocation)
        {

        }
        //
        // if onclick save/edit
        else if (view == btnEventSaveEdit)
        {
            Log.d(TAG, "SAVING");
            // if onclick save
            if (btnEventSaveEdit.getText() == "SAVE") {
                getEventDetails(view);
            // if onclick edit
            } else if (btnEventSaveEdit.getText() == "EDIT") {
                editState(view);
            }
        }
        //
        // if onclick cancel
        else if (view == btnEventCancel)
        {
            Log.d(TAG, "CANCELLING");
            Log.d(TAG, "fromFragmentString: " + fromFragmentString);
            if (fromFragmentString == "Create event") {
                Toast.makeText(getActivity(), "Event creation cancelled", Toast.LENGTH_SHORT).show();
                // go back to events list
                EventsFragment eventsFragment = new EventsFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_main, eventsFragment)
                        .addToBackStack(null)
                        .commit();
            } else if (fromFragmentString == "EventsFragment") {
                viewState(view);
            }
        }
    }

    // method to retrieve favourite place id's from db
    public void getFavPlacesFromDb(){

        DatabaseReference favPlacesRef = databaseRef.child("users").child(currentUserId).child("favouritePlaces");

        favPlacesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        Log.d(TAG, "Getting favPlaceId");
                        // adding fav place id into array list
                        favPlacesIdList.add(String.valueOf(dsp.getKey()));
                        Log.d(TAG, "Favourite Places: " + favPlacesIdList);
                    }
                    requestFavPlaceDetails();
                    // start the thread
                    checkIfDoneThread.start();
                } else {
                    // user has no favourite places in database
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }

    // method to request favourite place details using place ids
    public void requestFavPlaceDetails(){
        for(int i=0; i<favPlacesIdList.size(); i++){
            String id = favPlacesIdList.get(i);
            ((MainActivity)getActivity()).getDetails(id, fromFragment);
        }
    }

    // method to get back the favourite place details
    public void retrieveFavPlaceDetails(String id, String name, String address){
        // concatenate name & address
        String placeInfo = name + " (" + address + ")";
        // storing details in arrayLists
        favPlacesIdList2.add(id);
        favPlacesInfoList.add(placeInfo);
    }

    public void checkIfDone(){
        if ((favPlacesIdList.size() > 0) && (favPlacesIdList2.size() == favPlacesIdList.size())) {
            try {
                populateSpinner();
                checkIfDoneThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            // sleep for 1 second then re-check
            try {
                TimeUnit.SECONDS.sleep(1);
                checkIfDone();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // method to add favourite place details to spinner
    public void populateSpinner(){
        String[] spinnerArray = new String[favPlacesIdList2.size()];
        // adding values
        for (int i = 0; i < favPlacesIdList2.size(); i++)
        {
            spinnerMap.put(i,favPlacesIdList2.get(i));
            spinnerArray[i] = favPlacesInfoList.get(i);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(R.layout.spinner_item_custom);

        // accessing view on original ui thread
        getActivity().runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                spinnerLocation.setAdapter(adapter);
                spinnerLocation.setDropDownWidth(920);
            }
        }));
    }


    //////////// SAVING EVENT TO FIREBASE REALTIME DATABASE ////////////


    private void getEventDetails(View view){
        // name
        if (!txtEventName.getText().toString().equals(null)) {
            strEventName = txtEventName.getText().toString();
        } else {
            strEventName = "";
        }
        // description
        /*
        if (!txtEventDsc.getText().toString().equals(null)) {
            strEventDsc = txtEventDsc.getText().toString();
        } else {
            strEventDsc = "";
        }
        */
        // date
        if (!tvEventDate.getText().toString().equals(null)) {
            strEventDate = tvEventDate.getText().toString();
        } else {
            strEventDate = "";
        }
        // time
        if (!tvEventTime.getText().toString().equals(null)) {
            strEventTime = tvEventTime.getText().toString();
        } else {
            strEventTime = "";
        }
        // location
        strLocation = spinnerLocation.getSelectedItem().toString();
        int selectedPos = spinnerLocation.getSelectedItemPosition();
        for ( int key : spinnerMap.keySet() ) {
            if (key == selectedPos) {
                strLocationId = spinnerMap.get(key);
            }
        }
        // image
        strEventImage = "";

        // ensuring user enters required fields
        if (strEventName.equals("") || strEventName.equals(" ")) {
            Toast.makeText(getActivity(), "Please enter an event name", Toast.LENGTH_SHORT).show();
        } else if (strEventDate.equals("")) {
            Toast.makeText(getActivity(), "Please select an event date", Toast.LENGTH_SHORT).show();
        } else if (strEventTime.equals("")) {
            Toast.makeText(getActivity(), "Please select an event time", Toast.LENGTH_SHORT).show();
        } else {
            // set event values
            setEvent(view);
        }
    }

    private void setEvent(View view){
        // setting event values
        event = new Event();
        event.setName(strEventName);
        event.setDescription(strEventDsc);
        event.setDate(strEventDate);
        event.setTime(strEventTime);
        event.setLocation(strLocation);
        event.setImage(strEventImage);

        // if creating a new event
        if (fromFragmentString == "Create event") {
            Log.d(TAG, "SAVING NEW EVENT");
            // save event to database
            writeNewEvent(view);
            // change fromFragmentString in order to view created event
            fromFragmentString = "EventsFragment";
        // if editing an existing event
        } else if (fromFragmentString == "EventsFragment") {
            Log.d(TAG, "UPDATING EXISTING EVENT");
            // update event on database
            writeUpdatedEvent(view);
        }
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

    private void writeUpdatedEvent(View view){
        // writing event to database
        firebaseRef.child("events").child(strEventId).setValue(event);
        // TODO: 30/04/2017  get updated eventinviteelist
        // update user nodes
        // show confirmation message
        Toast.makeText(getActivity(), "Event successfully updated", Toast.LENGTH_SHORT).show();
        // update UI state
        viewState(view);
    }

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


    ///////////////////////////////////////////////////////////////////


    private void editState(View view){
        btnEventSaveEdit.setText("SAVE");
        // make editTexts editable
        txtEventName.setEnabled(true);
        //txtEventDsc.setEnabled(true);
        spinnerLocation.setEnabled(true);
        btnDatePicker.setVisibility(view.VISIBLE);
        btnTimePicker.setVisibility(view.VISIBLE);
        btnInvite.setVisibility(view.VISIBLE);
        btnEventCancel.setVisibility(view.VISIBLE);
    }

    private void viewState(View view){
        btnEventSaveEdit.setText("EDIT");
        // make editTexts not editable
        txtEventName.setEnabled(false);
        //txtEventDsc.setEnabled(false);
        spinnerLocation.setEnabled(false);
        btnDatePicker.setVisibility(view.GONE);
        btnTimePicker.setVisibility(view.GONE);
        btnInvite.setVisibility(view.GONE);
        btnEventCancel.setVisibility(view.GONE);
    }

    private void retrieveSelectedEventDetails(){
        // getting reference to selected event
        DatabaseReference eventRef = databaseRef.child("events").child(strEventId);
        // getting event details
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Getting selected event details");
                // getting and displaying selected event details
                Event event = dataSnapshot.getValue(Event.class);
                txtEventName.setText(event.getName());
                //txtEventDsc.setText(event.getDescription());
                // TODO: 30/04/2017 set date & time pickers to selected date/time
                tvEventDate.setText(event.getDate());
                tvEventTime.setText(event.getTime());
                txtEventLocation.setText(event.getLocation());
                eventInviteeList = event.getInvited();
                displayInvitees();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    
    private void displayInvitees(){
        // TODO: 30/04/2017 show list of invitee names with choice to invite more friends
    }
}
