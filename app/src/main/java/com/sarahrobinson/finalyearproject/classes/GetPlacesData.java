package com.sarahrobinson.finalyearproject.classes;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sarahrobinson.finalyearproject.activities.MainActivity;

import static com.sarahrobinson.finalyearproject.fragments.MapFragment.thePlaceAddress;
import static com.sarahrobinson.finalyearproject.fragments.MapFragment.thePlaceId;
import static com.sarahrobinson.finalyearproject.fragments.MapFragment.thePlaceName;

/**
 * Created by sarahrobinson on 16/04/2017.
 */

public class GetPlacesData extends AsyncTask<Object, String, String> {

    private static final String TAG = "GetPlacesData ******* ";

    String googlePlacesData;
    GoogleMap googleMap;
    String url;

    public static List<HashMap<String, String>> placesList;

    private Context getContext() {
        return MainActivity.getContext();
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d(TAG, "doInBackground entered");
            googleMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d(TAG, "doInBackground Exit");
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute entered");
        placesList = null;
        PlacesDataParser dataParser = new PlacesDataParser();
        placesList = dataParser.parse(result);
        showPlaces(placesList);
        Log.d(TAG, "onPostExecute Exit");
    }

    private void showPlaces(List<HashMap<String, String>> placesList) {
        for (int i = 0; i < placesList.size(); i++) {
            Log.d(TAG, "ShowPlaces for loop entered");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = placesList.get(i);
            Log.d(TAG, "googlePlace: " + googlePlace);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            thePlaceId = googlePlace.get("place_id");
            thePlaceName = googlePlace.get("place_name");
            thePlaceAddress = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(thePlaceName);
            markerOptions.snippet(thePlaceAddress);
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            googleMap.addMarker(markerOptions).setTag(thePlaceId);

            //move map camera
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }
}