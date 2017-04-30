package com.sarahrobinson.finalyearproject.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.classes.Event;

import java.util.ArrayList;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.currentUserId;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.databaseRef;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.eventsFragmentTabPending;
import static com.sarahrobinson.finalyearproject.activities.MainActivity.eventsFragmentTabUpcoming;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragmentTabUpcoming extends Fragment {

    private static final String TAG = "UpcomingEvents ******* ";

    private FragmentActivity tabUpcomingEventsContext;

    private LinearLayout layoutUpcomingEventsList;

    private ArrayList<String> eventsListUpcoming = new ArrayList<>();

    String upcomingId, upcomingDate, upcomingTime, upcomingName, upcomingLocation;

    public EventsFragmentTabUpcoming() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_events_tab_upcoming, container, false);

        // getting fragment context
        tabUpcomingEventsContext = getActivity();

        // getting layout to be inflated
        layoutUpcomingEventsList = (LinearLayout)rootView.findViewById(R.id.layoutUpcomingEventsList);

        // clearing lists when fragment is first loaded
        layoutUpcomingEventsList.removeAllViews();
        eventsListUpcoming.clear();

        retrieveEvents();

        return rootView;
    }

    // method to get user's events from database
    public void retrieveEvents() {
        Log.d(TAG, "retrieveEvents() entered");

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
        Log.d(TAG, "retrieveEventDetails() entered");

        // getting upcoming event details
        for(int i=0; i<eventsListUpcoming.size(); i++){

            upcomingId = eventsListUpcoming.get(i);
            DatabaseReference eventRef = databaseRef.child("events").child(upcomingId);

            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "Getting upcoming event details");
                        // getting and storing upcoming event details
                        Event event = dataSnapshot.getValue(Event.class);
                        upcomingDate = event.getDate();
                        upcomingTime = event.getTime();
                        upcomingName = event.getName();
                        upcomingLocation = event.getLocation();
                        upcomingId = (String.valueOf(dataSnapshot.getKey()));
                        inflateNewUpcomingListItem(upcomingId, upcomingDate,
                                upcomingTime, upcomingName, upcomingLocation);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    // method to inflate a new layout for each upcoming event
    public void inflateNewUpcomingListItem(String id, String date, String time, String name, String location){

        // inflating layout to be used as a list item
        LayoutInflater inflator = (LayoutInflater)tabUpcomingEventsContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflator.inflate(R.layout.list_item_event, layoutUpcomingEventsList, false);

        // adding inflated item layout to upcoming events list layout
        layoutUpcomingEventsList.addView(listItem, layoutUpcomingEventsList.getChildCount() - 1);

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
}
