package com.sarahrobinson.finalyearproject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.sarahrobinson.finalyearproject.PlaceFragment.ivPlaceIcon;
import static com.sarahrobinson.finalyearproject.PlaceFragment.tvPlaceAddress;
import static com.sarahrobinson.finalyearproject.PlaceFragment.tvPlaceName;
import static com.sarahrobinson.finalyearproject.PlaceFragment.tvPlacePhoneNo;
import static com.sarahrobinson.finalyearproject.PlaceFragment.tvPlaceType;
import static com.sarahrobinson.finalyearproject.PlaceFragment.tvPlaceWebsite;

/**
 * Created by sarahrobinson on 18/04/2017.
 */

public class GetSinglePlaceData extends AsyncTask<Object, String, String> {

    private static final String TAG = "GetPlaceData ******* ";

    String googlePlaceData;
    String url;

    public static String selectedPlaceImage, selectedPlaceType, selectedPlaceName,
            selectedPlaceAddress, selectedPlacePhoneNo, selectedPlaceWebsite;

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
            if (!placeDetailsJsonArray.isNull("icon")) {
                selectedPlaceImage = placeDetailsJsonArray.getString("icon");
            }
            if (!placeDetailsJsonArray.isNull("type")) {
                Log.d(TAG, "PLACE TYPE: " + placeDetailsJsonArray.getJSONArray("type"));
                selectedPlaceType = placeDetailsJsonArray.getString("type");
            }
            if (!placeDetailsJsonArray.isNull("name")) {
                selectedPlaceName = placeDetailsJsonArray.getString("name");
            }
            if (!placeDetailsJsonArray.isNull("formatted_address")) {
                selectedPlaceAddress = placeDetailsJsonArray.getString("formatted_address");
            }
            if (!placeDetailsJsonArray.isNull("formatted_phone_number")) {
                selectedPlacePhoneNo = placeDetailsJsonArray.getString("formatted_phone_number");
            }
            if (!placeDetailsJsonArray.isNull("website")) {
                selectedPlaceWebsite = placeDetailsJsonArray.getString("website");
            }

        } catch (JSONException e) {
            Log.d(TAG, "Error parsing json results");
            e.printStackTrace();
        }
        ShowPlaceDetails(selectedPlaceImage, selectedPlaceType, selectedPlaceName,
                selectedPlaceAddress, selectedPlacePhoneNo, selectedPlaceWebsite);
    }

    private void ShowPlaceDetails(String image, String type, String name, String address,
                                  String phoneNo, String website) {
        Log.d(TAG, "ShowPlaceDetails entered");
        // load image from url
        Picasso.with(getContext())
                .load(image)
                .into(ivPlaceIcon);
        // set string details
        tvPlaceType.setText(type);
        tvPlaceName.setText(name);
        tvPlaceAddress.setText(address);
        tvPlacePhoneNo.setText(phoneNo);
        tvPlaceWebsite.setText(website);
    }
}
