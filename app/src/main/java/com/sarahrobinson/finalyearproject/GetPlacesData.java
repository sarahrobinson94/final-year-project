package com.sarahrobinson.finalyearproject;

import android.os.AsyncTask;
import android.util.Log;

import java.util.HashMap;
import java.util.List;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by sarahrobinson on 16/04/2017.
 */

public class GetPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap googleMap;
    String url;

    private static final String TAG = "GetPlacesData ******* ";

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
        List<HashMap<String, String>> placesList = null;
        PlacesDataParser dataParser = new PlacesDataParser();
        placesList = dataParser.parse(result);
        ShowPlaces(placesList);
        Log.d(TAG, "onPostExecute Exit");
    }

    private void ShowPlaces(List<HashMap<String, String>> placesList) {
        for (int i = 0; i < placesList.size(); i++) {
            Log.d(TAG, "ShowPlaces for loop entered");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = placesList.get(i);
            Log.d(TAG, "googlePlace: " + googlePlace);
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeId = googlePlace.get("place_id");
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName);
            markerOptions.snippet(placeId);
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            googleMap.addMarker(markerOptions);

            //move map camera
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(10));
        }
    }
}