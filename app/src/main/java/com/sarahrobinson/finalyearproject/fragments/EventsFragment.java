package com.sarahrobinson.finalyearproject.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sarahrobinson.finalyearproject.activities.LoginActivity;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.activities.MainActivity;
import com.sarahrobinson.finalyearproject.classes.Event;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.databaseRef;

public class EventsFragment extends Fragment implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private static final String TAG = "EventsFragment ******* ";

    private FragmentActivity eventsFragmentContext;

    private ArrayList<String> eventsListUpcoming = new ArrayList<>();
    private ArrayList<String> eventsListPending = new ArrayList<>();

    String upcomingId, upcomingDate, upcomingTime, upcomingName, upcomingLocation,
            pendingId, pendingDate, pendingTime, pendingName, pendingLocation;

    private String eventId, eventDate, eventTime, eventName,
            eventLocation;

    private LinearLayout layoutEventsList;

    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // adding 'create event' action item
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
        View rootView = inflater.inflate(R.layout.fragment_events_list, container, false);

        // changing actionBar title
        getActivity().setTitle("Events");

        // getting fragment context
        eventsFragmentContext = getActivity();

        // getting layout to be inflated
        layoutEventsList = (LinearLayout)rootView.findViewById(R.id.layoutEventsList);

        // clearing lists when fragment is first loaded
        layoutEventsList.removeAllViews();
        eventsListUpcoming.clear();
        eventsListPending.clear();

        // retrieving user's events
        retrieveEvents();

        // TODO: 19/03/2017 get name from database and add ValueEventListener ?? (see android bash blog post)

        return rootView;
    }

    // changing action bar button
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // hide setting action item
        menu.findItem(R.id.action_settings).setVisible(false);
        // inflate event action item
        inflater.inflate(R.menu.action_create_event, menu);
    }

    // method to get user's events from database
    public void retrieveEvents(){

        DatabaseReference eventsRef = databaseRef.child("users").child(currentUserId).child("events");

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    Log.d(TAG, "Getting events");
                    // adding events to array list
                    if (dsp.getValue().equals("creator") || dsp.getValue().equals("accepted")) {
                        // add upcoming events
                        eventsListUpcoming.add(String.valueOf(dsp.getKey()));
                    } else if (dsp.getValue().equals("pending")) {
                        // add pending events
                        eventsListPending.add(String.valueOf(dsp.getKey()));
                    }
                }
                retrieveEventDetails();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // method to get event details from event ID
    public void retrieveEventDetails(){

        // getting upcoming event details
        for(int i=0; i<eventsListUpcoming.size(); i++){

            upcomingId = eventsListUpcoming.get(i);
            DatabaseReference eventRef = databaseRef.child("events").child(upcomingId);

            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Getting upcoming event details");
                    // getting and storing upcoming event details
                    Event event = dataSnapshot.getValue(Event.class);
                    upcomingDate = event.getDate();
                    upcomingTime = event.getTime();
                    upcomingName = event.getName();
                    upcomingLocation = event.getLocation();
                    upcomingId = (String.valueOf(dataSnapshot.getKey()));
                    inflateNewListItem(upcomingId, upcomingDate, upcomingTime, upcomingName, upcomingLocation);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

        // getting pending event details
        for(int i=0; i<eventsListPending.size(); i++){

            pendingId = eventsListPending.get(i);
            DatabaseReference eventRef = databaseRef.child("events").child(pendingId);

            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "Getting pending event details");
                    // getting and storing pending event details
                    Event event = dataSnapshot.getValue(Event.class);
                    pendingDate = event.getDate();
                    pendingTime = event.getTime();
                    pendingName = event.getName();
                    pendingLocation = event.getLocation();
                    pendingId = (String.valueOf(dataSnapshot.getKey()));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    // method to inflate a new layout for each event
    public void inflateNewListItem(String id, String date, String time, String name, String location){

        // inflating layout to be used as a list item
        LayoutInflater inflator = (LayoutInflater)eventsFragmentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflator.inflate(R.layout.list_item_event, layoutEventsList, false);

        // adding inflated item layout to favourites list layout
        layoutEventsList.addView(listItem, layoutEventsList.getChildCount() - 1);

        TextView tvEventDate = (TextView)listItem.findViewById(R.id.eventsListItemDate);
        TextView tvEventTime = (TextView) listItem.findViewById(R.id.eventsListItemTime);
        TextView tvEventName = (TextView) listItem.findViewById(R.id.eventsListItemName);
        TextView tvEventLocation = (TextView) listItem.findViewById(R.id.eventsListItemLocation);

        // invisible textView for storing id
        TextView tvEventId = (TextView) listItem.findViewById(R.id.eventsListItemId);

        // populating views with place details
        tvEventDate.setText(date);
        tvEventTime.setText(time);
        tvEventName.setText(name);
        tvEventLocation.setText(location);
        tvEventId.setText(id);
    }

    @Override
    public void onClick(View view) {

    }
}
