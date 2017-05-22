package com.sarahrobinson.finalyearproject.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class EventsFragmentTabPending extends Fragment {

    private static final String TAG = "PendingEvents ******* ";

    private FragmentActivity tabPendingEventsContext;

    private LinearLayout layoutPendingEventsList;
    private TextView txtNoPendingEvents;

    private ArrayList<String> eventsListPending = new ArrayList<>();

    String pendingId, pendingDate, pendingTime, pendingName, pendingLocation;

    public EventsFragmentTabPending() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_events_tab_pending, container, false);

        // getting fragment context
        tabPendingEventsContext = getActivity();

        // showing no pending events textView on initial load
        txtNoPendingEvents = (TextView)rootView.findViewById(R.id.txtNoPendingEvents);
        txtNoPendingEvents.setVisibility(View.VISIBLE);

        // getting layout to be inflated
        layoutPendingEventsList = (LinearLayout)rootView.findViewById(R.id.layoutPendingEventsList);

        // clearing lists when fragment is first loaded
        layoutPendingEventsList.removeAllViews();
        eventsListPending.clear();

        retrieveEvents(rootView);

        return rootView;
    }

    // method to get user's events from database
    public void retrieveEvents(final View view){
        Log.d(TAG, "retrieveEvents() entered");

        DatabaseReference eventsRef = databaseRef.child("users").child(currentUserId).child("events");

        eventsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        Log.d(TAG, "Getting events");
                        // adding events to array list
                        if (dsp.getValue().equals("pending")) {
                            // add upcoming events
                            eventsListPending.add(String.valueOf(dsp.getKey()));
                            // hide 'no events' message
                            txtNoPendingEvents.setVisibility(view.GONE);
                        }
                    }
                    retrieveEventDetails();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    // method to get event details from event ID
    public void retrieveEventDetails(){
        Log.d(TAG, "retrieveEventDetails() entered");

        // getting pending event details
        for(int i=0; i<eventsListPending.size(); i++){

            pendingId = eventsListPending.get(i);
            DatabaseReference eventRef = databaseRef.child("events").child(pendingId);

            eventRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        Log.d(TAG, "Getting pending event details");
                        // getting and storing pending event details
                        Event event = dataSnapshot.getValue(Event.class);
                        pendingDate = event.getDate();
                        pendingTime = event.getTime();
                        pendingName = event.getName();
                        pendingLocation = event.getLocation();
                        pendingId = (String.valueOf(dataSnapshot.getKey()));
                        inflateNewPendingListItem(pendingId, pendingDate,
                                pendingTime, pendingName, pendingLocation);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }

    // method to inflate a new layout for each pending event
    public void inflateNewPendingListItem(String id, String date, String time, String name, String location){

        // inflating layout to be used as a list item
        LayoutInflater inflator = (LayoutInflater)tabPendingEventsContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflator.inflate(R.layout.list_item_event, layoutPendingEventsList, false);

        // adding inflated item layout to upcoming events list layout
        layoutPendingEventsList.addView(listItem, layoutPendingEventsList.getChildCount() - 1);

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
