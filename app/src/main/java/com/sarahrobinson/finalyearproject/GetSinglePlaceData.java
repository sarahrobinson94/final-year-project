package com.sarahrobinson.finalyearproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import static com.sarahrobinson.finalyearproject.MapFragment.thePlaceAddress;
import static com.sarahrobinson.finalyearproject.MapFragment.thePlaceId;
import static com.sarahrobinson.finalyearproject.MapFragment.thePlaceName;
import static com.sarahrobinson.finalyearproject.PlaceFragment.tvPlaceName;

/**
 * Created by sarahrobinson on 18/04/2017.
 */

public class GetSinglePlaceData extends AsyncTask<Object, String, String> {

    private static final String TAG = "GetPlaceData ******* ";

    String googlePlaceData;
    String url;

    public static String selectedPlaceName;

    //public static List<HashMap<String, String>> placeDetailsList;
    //public static String placeDetailsString;

    private Context getContext() {
        return MainActivity.getContext();
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d(TAG, "doInBackground entered");
            url = (String) params[0];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlaceData = downloadUrl.readUrl(url);
            Log.d(TAG, "doInBackground Exit");
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return googlePlaceData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d(TAG, "onPostExecute entered");
        try {
            Log.d("Place", "parse");

            // Create a JSON object hierarchy from the results
            JSONObject jsonObject = new JSONObject((String)result);
            JSONObject placeDetailsJsonArray = jsonObject.getJSONObject("result");

            // Extract the place details from the results
            selectedPlaceName = placeDetailsJsonArray.getString("name");

        } catch (JSONException e) {
            Log.d(TAG, "Error parsing json results");
            e.printStackTrace();
        }
        ShowPlaceDetails(selectedPlaceName);
    }

    private void ShowPlaceDetails(String name) {
        Log.d(TAG, "ShowPlaceDetails entered");
        Log.d(TAG, "PlaceDetails: " + name);
        tvPlaceName.setText(name);
    }
}
