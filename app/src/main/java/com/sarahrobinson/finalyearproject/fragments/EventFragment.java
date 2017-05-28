package com.sarahrobinson.finalyearproject.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sarahrobinson.finalyearproject.activities.LoginActivity;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.activities.MainActivity;
import com.sarahrobinson.finalyearproject.classes.CircleTransform;
import com.sarahrobinson.finalyearproject.classes.Event;
import com.sarahrobinson.finalyearproject.classes.Server;
import com.sarahrobinson.finalyearproject.classes.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.databaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.eventFragment;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.eventInviteeList;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.firebaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.dialogFriendIdList;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.fromFragmentString;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.selectedEventId;

public class EventFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CreateEventF ******* ";

    private FragmentManager fragmentManager;
    private Fragment fromFragment;
    // for inflating invitee scrollview
    private FragmentActivity eventFragmentContext;

    public Event event;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    // views
    private EditText txtEventName, txtEventDsc;
    private TextView tvEventDate, tvEventTime, tvEventLocation, tvEventLocationId, tvNoAttendees;
    private ImageView btnDatePicker, btnTimePicker;
    private Spinner spinnerLocation;
    private Button btnInvite, btnEventSaveEdit, btnEventCancel, btnAccept, btnDecline;
    // horizontal scrollview of event invitees
    private LinearLayout layoutHorizontalScrollViewInvitees, inflatedLayoutEventInvitees;

    // for datetime pickers
    private int mYear, mMonth, mDay, mHour, mMinute;

    // for getting/storing favourite places
    private List<String> favPlacesIdList = new ArrayList<>();
    private List<String> favPlacesIdList2 = new ArrayList<>();
    private List<String> favPlacesInfoList = new ArrayList<>();
    private HashMap<Integer,String> spinnerMap = new HashMap<Integer, String>();
    // if a place was suggested
    private String suggestedPlaceId;
    private int suggestedPlacePosition;

    // event details
    private String strEventId;
    private String strEventName;
    private String strEventDsc;
    private String strEventDate;
    private String strEventTime;
    private String strLocation;
    private String strLocationId;
    private String strEventImage;

    // invitee details
    private String strInviteeId;
    private String strInviteeName;
    private String strInviteePhoto;
    private List<String> fullInviteeListFromDb = new ArrayList<>();
    private boolean hasInvitees = false;

    private Thread checkFavPlacesRetrievedThread = null;
    private volatile boolean exitCheckFavPlaces = false;

    private Thread checkInviteesRetrievedThread = null;
    private volatile boolean exitCheckInvitees = false;

    //private Server runnable = null;

    public EventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // receiving bundle arguments
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey("suggestedPlaceId")) {
            suggestedPlaceId = arguments.getString("suggestedPlaceId");
        }

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
        checkFavPlacesRetrievedThread = new Thread(new Runnable() {
            public void run() {
                while(!exitCheckFavPlaces) {
                    checkIfFavPlacesDone();
                }
            }
        });

        // thread for concurrently checking if all invitee data has been retrieved
        checkInviteesRetrievedThread = new Thread(new Runnable() {
            public void run() {
                while(!exitCheckInvitees) {
                    checkIfInviteesDone();
                }
            }
        });

        /*
        runnable = new Server();
        checkFavPlacesRetrievedThread = new Thread(runnable);
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_event, container, false);

        // getting fragment context
        eventFragmentContext = getActivity();

        // getting views
        //
        // editTexts
        txtEventName = (EditText) rootView.findViewById(R.id.txtEventName);
        //txtEventDsc = (EditText)rootView.findViewById(R.id.txtEventDsc);
        //txtEventLocation = (EditText) rootView.findViewById(R.id.txtEventLocation);
        // textViews
        tvEventDate = (TextView) rootView.findViewById(R.id.tvEventDate);
        tvEventTime = (TextView) rootView.findViewById(R.id.tvEventTime);
        tvEventLocation = (TextView) rootView.findViewById(R.id.tvEventLocation);
        tvEventLocationId = (TextView) rootView.findViewById(R.id.tvEventLocationId);
        tvNoAttendees = (TextView) rootView.findViewById(R.id.tvNoEventAttendees);
        // buttons
        btnDatePicker = (ImageView) rootView.findViewById(R.id.btnEventDatePicker);
        btnTimePicker = (ImageView) rootView.findViewById(R.id.btnEventTimePicker);
        btnInvite = (Button) rootView.findViewById(R.id.btnInvite);
        btnEventSaveEdit = (Button) rootView.findViewById(R.id.eventButtonSaveEdit);
        btnEventCancel = (Button) rootView.findViewById(R.id.eventButtonCancel);
        btnAccept = (Button) rootView.findViewById(R.id.eventButtonAccept);
        btnDecline = (Button) rootView.findViewById(R.id.eventButtonDecline);
        // spinner
        spinnerLocation = (Spinner) rootView.findViewById(R.id.spinnerEventLocation);
        // layouts
        layoutHorizontalScrollViewInvitees = (LinearLayout)
                rootView.findViewById(R.id.layoutHorizontalScrollViewInvitees);
        inflatedLayoutEventInvitees = (LinearLayout)
                rootView.findViewById(R.id.inflatedLayoutEventInvitees);

        // setting onclick listeners
        btnDatePicker.setOnClickListener(this);
        btnTimePicker.setOnClickListener(this);
        btnEventSaveEdit.setOnClickListener(this);
        btnEventCancel.setOnClickListener(this);
        btnAccept.setOnClickListener(this);
        btnDecline.setOnClickListener(this);

        // if user is creating an event
        if (fromFragmentString == "Create event") {
            Log.d(TAG, "Create event");
            // change actionBar title
            getActivity().setTitle("Create Event");
            // set UI state
            editState(rootView);
            // clear event invitee list
            if (eventInviteeList == null) {
                // do nothing
            } else {
                eventInviteeList.clear();
            }
            // get data for places spinner
            getFavPlacesFromDb();

        // if user is viewing an event
        } else if (fromFragmentString == "EventsFragment") {
            Log.d(TAG, "View event");
            // change actionBar title
            getActivity().setTitle("Event Details");
            // retrieve event details
            strEventId = selectedEventId;
            viewState(rootView);
        }

        // initially hiding accept/decline buttons & invitee scrollview
        btnAccept.setVisibility(View.GONE);
        btnDecline.setVisibility(View.GONE);
        layoutHorizontalScrollViewInvitees.setVisibility(View.GONE);

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
                // clearing lists
                inflatedLayoutEventInvitees.removeAllViews();
                dialogFriendIdList.clear();
                // getting event details to save to db
                getEventDetails(view);
            // if onclick edit
            } else if (btnEventSaveEdit.getText() == "EDIT") {
                // check fromFragment is set to EventFragment
                if (fromFragment == null) {
                    fromFragment = eventFragment;
                }
                // get data for places spinner
                getFavPlacesFromDb();
                // change to edit state
                editState(view);
            }
        }
        //
        // if onclick cancel
        else if (view == btnEventCancel)
        {
            Log.d(TAG, "CANCELLING");
            Log.d(TAG, "fromFragmentString: " + fromFragmentString);
            dialogFriendIdList.clear();
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
        //
        // if onclick accept
        else if (view == btnAccept)
        {
            Log.d(TAG, "Event invite accepted");
            String response = "accepted";
            // change event status to 'accepted'
            respondToInvite(response);
            // hide buttons
            btnAccept.setVisibility(view.GONE);
            btnDecline.setVisibility(view.GONE);
            // show message
            Toast.makeText(getActivity(), "Event invite accepted", Toast.LENGTH_SHORT).show();
        }
        //
        // if onclick decline
        else if (view == btnDecline)
        {
            Log.d(TAG, "Event invite declined");
            String response = "declined";
            // change event status to 'declined'
            respondToInvite(response);
            // hide buttons
            btnAccept.setVisibility(view.GONE);
            btnDecline.setVisibility(view.GONE);
            // show message
            Toast.makeText(getActivity(), "Event invite declined", Toast.LENGTH_SHORT).show();
        }
    }


    //////////////// RETRIEVING USER'S FAVOURITE PLACES ////////////////


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
                    checkFavPlacesRetrievedThread.start();
                    // TODO: 27/05/2017 fix error ^^^^ "thread already started"
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
            ((MainActivity)getActivity()).getDetails(id, eventFragment);
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

    public void checkIfFavPlacesDone(){
        if ((favPlacesIdList.size() > 0) && (favPlacesIdList2.size() == favPlacesIdList.size())) {
            populateSpinner();
            exitCheckFavPlaces = true;
            try {
                checkFavPlacesRetrievedThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            // sleep for 1 second then re-check
            try {
                TimeUnit.SECONDS.sleep(1);
                checkIfFavPlacesDone();
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
            String currentVal = favPlacesIdList2.get(i).toString();

            spinnerMap.put(i,favPlacesIdList2.get(i));
            spinnerArray[i] = favPlacesInfoList.get(i);

            // if a place suggestion was made, get the position of suggested place
            if (suggestedPlaceId != null) {
                if (currentVal.equals(suggestedPlaceId)) {
                    suggestedPlacePosition = i;
                }
            }
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(R.layout.spinner_item_custom);

        // accessing view on original ui thread
        getActivity().runOnUiThread(new Thread(new Runnable() {
            @Override
            public void run() {
                spinnerLocation.setAdapter(adapter);
                // if a place suggestion was made, set the dropdown selection to that place
                spinnerLocation.setSelection(suggestedPlacePosition);
                spinnerLocation.setDropDownWidth(920);
            }
        }));
    }


    /////////////////// DISPLAYING SELECTED INVITEES ///////////////////


    public void showTemporaryInviteeList(View view) {
        // get details and inflate layout for each selected friend to invite
        for (int i=0; i<eventInviteeList.size(); i++) {
            String userId = eventInviteeList.get(i).toString();
            retrieveUserDetails(userId);
        }
        // show selected friends
        layoutHorizontalScrollViewInvitees.setVisibility(view.VISIBLE);
        tvNoAttendees.setVisibility(view.GONE);
    }

    public void retrieveInvitees(final View view) {

        // TODO: 28/05/2017 FIX QUERY
        Query queryInvitees = databaseRef.child("users").orderByChild("events").orderByKey().equalTo(selectedEventId);

        queryInvitees.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "Getting event invitees by id");
                        // adding users ids to list
                        fullInviteeListFromDb.add(String.valueOf(snapshot.getKey()));
                        Log.d(TAG, "Friend ids: " + fullInviteeListFromDb);
                    }
                    tvNoAttendees.setVisibility(view.GONE);
                    layoutHorizontalScrollViewInvitees.setVisibility(View.VISIBLE);
                    // start the thread
                    checkInviteesRetrievedThread.start();
                    // TODO: 27/05/2017 fix error ^^^^ "thread already started"
                } else {
                    tvNoAttendees.setVisibility(view.VISIBLE);
                    layoutHorizontalScrollViewInvitees.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void checkIfInviteesDone(){
        if ((fullInviteeListFromDb.size() > 0)) {
            showInviteeList();
            exitCheckInvitees = true;
            try {
                checkInviteesRetrievedThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            // sleep for 1 second then re-check
            try {
                TimeUnit.SECONDS.sleep(1);
                checkIfInviteesDone();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void showInviteeList() {
        // get details and inflate layout for each invited friend
        for (int i=0; i<fullInviteeListFromDb.size(); i++) {
            String userId = fullInviteeListFromDb.get(i).toString();
            retrieveUserDetails(userId);
        }
    }

    public void retrieveUserDetails(String userId) {
        // get user details from db
        databaseRef.child("users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Getting user details");
                // getting user details
                User user = dataSnapshot.getValue(User.class);
                strInviteeId = dataSnapshot.getKey();
                strInviteeName = user.getName();
                strInviteePhoto = user.getImage();
                // inflate scrollView with invitee
                inflateInviteeScrollView(strInviteeId, strInviteeName, strInviteePhoto);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void inflateInviteeScrollView(String id, String name, String photo) {
        Log.d(TAG, "Inflating invitee scrollview");

        // inflating layout to be used as a scrollview item
        LayoutInflater inflator = (LayoutInflater)eventFragmentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View scrollViewItem = inflator.inflate(R.layout.h_scrollview_invitee_item, inflatedLayoutEventInvitees, false);

        // adding inflated item layout to scrollview layout
        inflatedLayoutEventInvitees.addView(scrollViewItem, inflatedLayoutEventInvitees.getChildCount() - 1);

        // getting inflated item views
        ImageView ivInviteePhoto = (ImageView)scrollViewItem.findViewById(R.id.ivInviteeScrollViewPhoto);
        TextView tvInviteeName = (TextView)scrollViewItem.findViewById(R.id.tvInviteeScrollViewName);
        TextView tvInviteeId = (TextView)scrollViewItem.findViewById(R.id.tvInviteeScrollViewId);

        // populating views with user details

        if (photo != null && !photo.isEmpty()) {
            Picasso.with(getContext())
                    .load(photo)
                    .transform(new CircleTransform())
                    .into(ivInviteePhoto);
        }

        tvInviteeName.setText(name);
        tvInviteeId.setText(id);
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
        event.setLocationId(strLocationId);
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
        // setting event to view
        selectedEventId = strEventId;
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
        if (eventInviteeList.size() > 0) {
            for (int i=0; i<eventInviteeList.size(); i++) {
                // get user id
                String friendId = eventInviteeList.get(i);
                // updating user node
                firebaseRef.child("users").child(friendId).child("events").child(strEventId).setValue("pending");
            }
        }
        // show confirmation message
        Toast.makeText(getActivity(), "Event successfully created", Toast.LENGTH_SHORT).show();
        // update UI state
        viewState(view);
    }


    ///////////////////////////////////////////////////////////////////


    private void editState(View view){
        btnEventSaveEdit.setText("SAVE");
        // make views editable
        txtEventName.setEnabled(true);
        //txtEventDsc.setEnabled(true);
        spinnerLocation.setVisibility(view.VISIBLE);
        btnDatePicker.setVisibility(view.VISIBLE);
        btnTimePicker.setVisibility(view.VISIBLE);
        btnInvite.setVisibility(view.VISIBLE);
        btnEventCancel.setVisibility(view.VISIBLE);
        // hide views
        tvEventLocation.setVisibility(view.GONE);
    }

    private void viewState(View view){
        // make views not editable
        txtEventName.setEnabled(false);
        //txtEventDsc.setEnabled(false);
        // hide views
        btnDatePicker.setVisibility(view.GONE);
        btnTimePicker.setVisibility(view.GONE);
        spinnerLocation.setVisibility(view.GONE);
        btnInvite.setVisibility(view.GONE);
        btnEventCancel.setVisibility(view.GONE);
        // show views
        tvEventLocation.setVisibility(view.VISIBLE);

        checkUserStatusForEvent();
        retrieveSelectedEventDetails();
        retrieveInvitees(view);
    }

    public void checkUserStatusForEvent() {

        // show edit button if user is the event creator
        databaseRef.child("users").child(currentUserId).child("events").child(selectedEventId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            if (dataSnapshot.getValue().equals("creator")) {
                                btnEventSaveEdit.setText("EDIT");
                            } else if (dataSnapshot.getValue().equals("pending")) {
                                // hide save/edit button
                                btnEventSaveEdit.setVisibility(View.GONE);
                                // show accept invite & decline buttons
                                btnAccept.setVisibility(View.VISIBLE);
                                btnDecline.setVisibility(View.VISIBLE);
                            } else if (dataSnapshot.getValue().equals("accepted")) {
                                // hide all buttons
                                btnEventSaveEdit.setVisibility(View.GONE);
                                btnAccept.setVisibility(View.GONE);
                                btnDecline.setVisibility(View.GONE);
                            }
                        } else {
                            checkUserStatusForEvent();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

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
                tvEventLocation.setText(event.getLocation());
                tvEventLocationId.setText(event.getLocationId());
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void respondToInvite(String response) {
        firebaseRef.child("users").child(currentUserId).child("events").
                child(selectedEventId).setValue(response);
    }

}
