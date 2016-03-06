package com.tinystep.honeybee.honeybee.Activities.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Models.UserMain;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.Utils.NetworkCallback;
import com.tinystep.honeybee.honeybee.Utils.Router;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This is the main fragment user for listing user Notifications
 */
public class UserFragment extends android.support.v4.app.Fragment {
    String TAG = "USERFRAG";
    public MainActivity mActivity;

    TextView tv_name, tv_score;

    public UserFragment() {
    }

    public static UserFragment newInstance(MainActivity activityContext) {
        UserFragment myFragment = new UserFragment();
        myFragment.mActivity = activityContext;
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView;
        rootView = inflater.inflate(R.layout.fragment_user, null);
        tv_name = (TextView) rootView.findViewById(R.id.tv_name);
        tv_score = (TextView) rootView.findViewById(R.id.tv_score);
        refreshUI();
        pullFromServer(null);
        return rootView;
    }
    private void refreshUI() {
        UserMain userMain = MainApplication.getInstance().data.userMain;
        tv_name.setText(""+userMain.name);
        tv_score.setText("You have a total of "+userMain.score+" rs since you joined us");

    }


    public void pullFromServer(final NetworkCallback callback){
        String url = Router.Login.data();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userId",MainApplication.getInstance().data.userMain.userId);
        } catch (JSONException e) {e.printStackTrace();}
        Logg.m("MAIN", "Pulling offers data from server : ");

        MainApplication.getInstance().addRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Logg.d(TAG, "USER DATA : " + jsonObject.toString());
                try {
                    JSONObject offersJSON = jsonObject.getJSONObject("result");
                    UserMain userMain = MainApplication.getInstance().data.userMain;
                    userMain.score = offersJSON.getInt("score");
                    userMain.name = offersJSON.getString("name");
                    userMain.saveUserDataLocally();
                    refreshUI();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(callback!=null) callback.onSuccess();
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Logg.e(TAG, "ERROR : " + volleyError);
                if(callback!=null) callback.onError();
            }
        });
    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        for(int i=0;i<menu.size();i++){
            menu.getItem(i).setVisible(false);
        }
    }

    @Override
    public void onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver( notificationReceiver );
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (MainActivity) activity;
    }

}

