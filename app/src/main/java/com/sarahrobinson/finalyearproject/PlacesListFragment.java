package com.sarahrobinson.finalyearproject;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import static com.sarahrobinson.finalyearproject.MapFragment.thePlaceId;

public class PlacesListFragment extends Fragment {

    private static final String TAG = "PlacesListFragment ******* ";
    private FragmentManager fragmentManager;
    private OnFragmentInteractionListener mListener;
    public static ListView placesListView;

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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
