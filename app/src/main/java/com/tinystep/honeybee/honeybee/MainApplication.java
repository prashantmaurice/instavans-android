package com.tinystep.honeybee.honeybee;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.tinystep.honeybee.honeybee.Controllers.NetworkController;
import com.tinystep.honeybee.honeybee.Services.TrackingService;
import com.tinystep.honeybee.honeybee.storage.Data;

import org.json.JSONObject;

/**
 * Created by arunshankar on 19/05/15.
 */
public class MainApplication extends Application {

    private static MainApplication sInstance;
    private RequestQueue queue;
    private com.android.volley.toolbox.ImageLoader imageLoader;
    SharedPreferences sharedPreferences;
    public Data data;
    public NetworkController networkController;

    TrackingService mService;
    boolean mBound = false;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;
        networkController = NetworkController.getInstance(this);
        queue = Volley.newRequestQueue(this);
        data = Data.getInstance(this);

        startService(new Intent(this, TrackingService.class));
        sharedPreferences = getSharedPreferences("Tinystep", MODE_PRIVATE);//will be deprecated soon
    }

    public synchronized static MainApplication getInstance() {
        return sInstance;
    }

    public void addRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener){
        networkController.addNewRequest(method,url,jsonRequest, listener, errorListener);
    }

    public RequestQueue getRequestQueue() {
        return queue;
    }

    /**
     * Used to return the singleton imageloader
     * that utilizes the image lru cache.
     * @return ImageLoader
     */
    public com.android.volley.toolbox.ImageLoader getImageLoader(){
        return imageLoader;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }




}
