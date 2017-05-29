package com.sarahrobinson.finalyearproject.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sarahrobinson.finalyearproject.R;
import com.sarahrobinson.finalyearproject.activities.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import static com.sarahrobinson.finalyearproject.activities.MainActivity.mapFragmentTabList;
import static com.sarahrobinson.finalyearproject.classes.GetPlacesData.placesList;
import static com.sarahrobinson.finalyearproject.fragments.MapFragmentTabMap.thePlaceAddress;
import static com.sarahrobinson.finalyearproject.fragments.MapFragmentTabMap.thePlaceId;
import static com.sarahrobinson.finalyearproject.fragments.MapFragmentTabMap.thePlaceName;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragmentTabList extends Fragment implements View.OnClickListener{

    private static final String TAG = "MapListFragment ******* ";

    private FragmentActivity mapListFragmentContext;
    private FragmentManager fragmentManager;
    private Fragment fromFragment;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private LinearLayout layoutResultsList;
    private TextView txtNoResults;

    private String placeId;
    private String placeName;
    private String placeAddress;


    public MapFragmentTabList() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_map_tab_list, container, false);

        // getting fragment context
        mapListFragmentContext = getActivity();

        // changing actionBar title
        getActivity().setTitle("Results");

        fragmentManager = getFragmentManager();
        mapFragmentTabList = new MapFragmentTabList();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


        // hiding no results textView on initial load
        txtNoResults = (TextView)rootView.findViewById(R.id.txtNoResults);
        txtNoResults.setVisibility(rootView.GONE);

        // getting layout to be inflated
        layoutResultsList = (LinearLayout)rootView.findViewById(R.id.layoutMapList);

        // clearing list when fragment is first loaded
        layoutResultsList.removeAllViews();

        getPlaceDetails(rootView);

        return rootView;
    }

    public void getPlaceDetails(View view) {
        if (placesList.equals(null)){
            txtNoResults.setVisibility(view.GONE);
        } else {
            // get place details
            for (int i = 0; i < placesList.size(); i++) {
                HashMap<String, String> googlePlace = placesList.get(i);
                placeId = googlePlace.get("place_id");
                ((MainActivity)getActivity()).getDetails(placeId, mapFragmentTabList);
            }
        }
    }

    // method to inflate a new layout for each place result
    public void inflateNewListItem(String id, String image, String type, String name, String address){

        // inflating layout to be used as a list item
        LayoutInflater inflator = (LayoutInflater)mapListFragmentContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflator.inflate(R.layout.list_item_favourite, layoutResultsList, false);

        // adding inflated item layout to results list layout
        layoutResultsList.addView(listItem, layoutResultsList.getChildCount() - 1);

        ImageView imgIcon = (ImageView) listItem.findViewById(R.id.favsListItemPlaceIcon);
        TextView txtName = (TextView) listItem.findViewById(R.id.favsListItemPlaceName);
        TextView txtAddress = (TextView) listItem.findViewById(R.id.favsListItemPlaceAddress);
        // invisible textView for storing id
        TextView txtId = (TextView) listItem.findViewById(R.id.favsListItemPlaceId);

        // populating views with place details
        if (!image.isEmpty()) {
            Picasso.with(getContext())
                    .load(image)
                    .into(imgIcon);
        }
        txtName.setText(name);
        txtAddress.setText(address);
        txtId.setText(id);
    }

    @Override
    public void onClick(View view) {
    }

}
