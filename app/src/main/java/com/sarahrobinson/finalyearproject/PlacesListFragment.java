package com.sarahrobinson.finalyearproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import static com.sarahrobinson.finalyearproject.GetPlacesData.placesList;
import static com.sarahrobinson.finalyearproject.MapFragment.thePlaceAddress;
import static com.sarahrobinson.finalyearproject.MapFragment.thePlaceId;
import static com.sarahrobinson.finalyearproject.MapFragment.thePlaceName;

public class PlacesListFragment extends Fragment {

    private static final String TAG = "PlacesListFrag ******* ";

    private FragmentManager fragmentManager;
    private OnFragmentInteractionListener mListener;

    private ListView placesListView;

    public PlacesListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflating layout
        View rootView = inflater.inflate(R.layout.fragment_places_list, container, false);

        // changing actionBar title
        getActivity().setTitle("Results List");

        fragmentManager = getFragmentManager();
        placesListView = (ListView)rootView.findViewById(R.id.listViewPlaces);

        // populating list with places
        populateList();

        // listItem click event
        placesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // getting values from selected ListItem // TODO: 18/04/2017 pass to placeFragment
                thePlaceId = ((TextView)view.findViewById(R.id.placesListItemPlaceId)).getText().toString();
                // replacing fragment
                PlaceFragment placeFragment = new PlaceFragment();
                fragmentManager.beginTransaction()
                        .replace(R.id.content_main, placeFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        return rootView;
    }

    public void populateList(){
        // list adapter
        SimpleAdapter adapter = new SimpleAdapter(getContext(), placesList,
                R.layout.places_list_item,
                new String[] { thePlaceId, thePlaceName, thePlaceAddress},
                new int[] { R.id.placesListItemPlaceId, R.id.placesListItemPlaceName, R.id.placesListItemPlaceAddress });

        // Adding data into listview
        placesListView.setAdapter(adapter);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
