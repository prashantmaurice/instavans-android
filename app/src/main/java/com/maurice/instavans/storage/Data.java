package com.maurice.instavans.storage;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.maurice.instavans.Controllers.LocalBroadcastHandler;
import com.maurice.instavans.MainApplication;
import com.maurice.instavans.Models.JobObj;
import com.maurice.instavans.Models.UserMain;
import com.maurice.instavans.Utils.Logg;
import com.maurice.instavans.Utils.NetworkCallback;
import com.maurice.instavans.Utils.Router;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *  Instance of Data object contains all access to the complete date model underneath.
 *  Just pull the Data instance in your screen and make modifications to exposed objects
 *  underneath as you wish. Make sure you commit to changes after making changes in order
 *  to take effect in Server and LocalDb
 *
 *  @author maurice
 */


public class Data {
    String TAG = "DATA";
    private static Data instance;
    public UserMain userMain;
    Context mContext;


    public ArrayList<JobObj> offers = new ArrayList<>();
    ArrayList<JobObj> done = new ArrayList<>();

    private Data(Context context) {
        mContext = context;
        userMain = UserMain.getInstance(context);

        completePullFromServer();
    }

    public void completePullFromServer() {
        pullOffersFromServer(null);
        pullCompletedFromServer(null);
    }

    //use this to retreive an instance of Data
    public static Data getInstance(Context context) {
        if(instance == null) instance = new Data(context);
        return instance;
    }

    //Refill function to generate all previous data
    public void refillCompleteData(JSONObject response){
        userMain.decodeFromServer(response);
        userMain.saveUserDataLocally();
    };

    public void pullOffersFromServer(final NetworkCallback callback){
        String url = Router.Jobs.offers();
        JSONObject jsonObject = new JSONObject();
        Logg.m("MAIN", "Pulling offers data from server : ");

        MainApplication.getInstance().addRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Logg.d(TAG, "USER DATA : " + jsonObject.toString());
                try {
                    JSONObject result = jsonObject.getJSONObject("result");
                    JSONArray offersJSON = result.getJSONArray("offers");
                    offers.clear();
                    offers.addAll(JobObj.decode(offersJSON));
                    LocalBroadcastHandler.sendBroadcast(mContext, LocalBroadcastHandler.OFFERS_UPDATED);

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

    public void pullCompletedFromServer(final NetworkCallback callback){
        String url = Router.Jobs.completed();
        JSONObject jsonObject = new JSONObject();
        Logg.m("MAIN", "Pulling offers data from server : ");

        MainApplication.getInstance().addRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Logg.d(TAG, "USER DATA : " + jsonObject.toString());
                try {
                    JSONObject result = jsonObject.getJSONObject("result");
                    JSONArray offersJSON = result.getJSONArray("done");
                    done.clear();
                    done.addAll(JobObj.decode(offersJSON));

                    LocalBroadcastHandler.sendBroadcast(mContext, LocalBroadcastHandler.DONE_UPDATED);


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


    public void saveCompleteDataLocally(){
        userMain.saveUserDataLocally();
    }

}
