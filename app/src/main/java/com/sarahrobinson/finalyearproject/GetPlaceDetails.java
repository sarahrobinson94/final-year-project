package com.sarahrobinson.finalyearproject;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sarahrobinson.finalyearproject.MainActivity.favouritesFragment;
import static com.sarahrobinson.finalyearproject.MainActivity.placeFragment;

/**
 * Created by sarahrobinson on 18/04/2017.
 */

public class GetPlaceDetails extends AsyncTask<Object, String, String> {

    private static final String TAG = "GetPlaceData ******* ";

    Fragment fromFragment;

    String googlePlaceData;
    String url;

    private String placeId, placeImage, placeType, placeName,
            placeAddress, placePhoneNo, placeWebsite;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d(TAG, "doInBackground entered");
            url = (String) params[0];
            fromFragment = (Fragment) params[1];
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
            // Create a JSON object hierarchy from the results
            JSONObject jsonObject = new JSONObject((String)result);
            JSONObject placeDetailsJsonArray = jsonObject.getJSONObject("result");

            // Extract the place details from the results
            if (!placeDetailsJsonArray.isNull("icon")) {
                placeImage = placeDetailsJsonArray.getString("icon");
            }
            if (!placeDetailsJsonArray.isNull("types")) {
                JSONArray types = new JSONArray();
                types = placeDetailsJsonArray.getJSONArray("types");
                String strTypes = "";
                for (int i = 0; i < types.length(); ++i) {
                    String type = types.getString(i);
                    strTypes = strTypes + (type + ", ");
                }
                // removing underscores
                strTypes = strTypes.replaceAll("_", " ");
                // removing comma at end of string // TODO: 18/04/2017 fix
                strTypes = strTypes.substring(0, strTypes.length()- 1);
                placeType = strTypes;
            }
            if (!placeDetailsJsonArray.isNull("name")) {
                placeName = placeDetailsJsonArray.getString("name");
            }
            if (!placeDetailsJsonArray.isNull("formatted_address")) {
                placeAddress = placeDetailsJsonArray.getString("formatted_address");
            }
            if (!placeDetailsJsonArray.isNull("formatted_phone_number")) {
                placePhoneNo = placeDetailsJsonArray.getString("formatted_phone_number");
            }
            if (!placeDetailsJsonArray.isNull("website")) {
                placeWebsite = placeDetailsJsonArray.getString("website");
            }
            placeId = placeDetailsJsonArray.getString("place_id");

        } catch (JSONException e) {
            Log.d(TAG, "Error parsing json results");
            e.printStackTrace();
        }

        // checking which fragment is requesting place details
        if (fromFragment instanceof PlaceFragment)
        {
            Log.d(TAG, "fromFragment = PlaceFragment");
            // calling the ShowPlaceDetails method, passing in the place details
            placeFragment.ShowPlaceDetails(placeId, placeImage, placeType, placeName,
                    placeAddress, placePhoneNo, placeWebsite);
        }
        else if (fromFragment instanceof FavouritesFragment)
        {
            Log.d(TAG, "fromFragment = FavouritesFragment");
            // calling the inflateNewListItem method, passing in the place details
            favouritesFragment.inflateNewListItem(placeId, placeImage, placeType, placeName,
                    placeAddress);
        }
    }
}
