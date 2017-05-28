package com.sarahrobinson.finalyearproject.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.sarahrobinson.finalyearproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sarahrobinson.finalyearproject.activities.LoginActivity.facebookAccessToken;

/**
 * A simple {@link Fragment} subclass.
 */
public class FindFriendsFragmentTabFacebook extends Fragment {


    public FindFriendsFragmentTabFacebook() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_find_friends_tab_facebook, container, false);

        // TODO: 28/05/2017
        // if user is logged in with facebook:
        retrieveFbFriends();
        // else
        // show textview saying "must be logged in with facebook to use this feature"

        return rootView;
    }

    public void retrieveFbFriends() {

        AccessToken token = facebookAccessToken;
        GraphRequest graphRequest = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                try {
                    JSONArray jsonArrayFriends = jsonObject.getJSONObject("friendlist").getJSONArray("data");
                    JSONObject friendlistObject = jsonArrayFriends.getJSONObject(0);
                    String friendListID = friendlistObject.getString("id");
                    myNewGraphReq(friendListID);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        /*
        Bundle param = new Bundle();
        param.putString("fields", "friendlist", "members");
        graphRequest.setParameters(param);
        graphRequest.executeAsync();
        */

    }

    private void myNewGraphReq(String friendlistId) {

        final String graphPath = "/"+friendlistId+"/members/";
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = new GraphRequest(token, graphPath, null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                JSONObject object = graphResponse.getJSONObject();
                try {
                    JSONArray arrayOfUsersInFriendList= object.getJSONArray("data");
                /* Do something with the user list */
                /* ex: get first user in list, "name" */
                    JSONObject user = arrayOfUsersInFriendList.getJSONObject(0);
                    String usersName = user.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle param = new Bundle();
        param.putString("fields", "name");
        request.setParameters(param);
        request.executeAsync();

    }

}
