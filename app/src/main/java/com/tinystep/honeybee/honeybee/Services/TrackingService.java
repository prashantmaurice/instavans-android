package com.tinystep.honeybee.honeybee.Services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.tinystep.honeybee.honeybee.Controllers.SocketController;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Models.UserMain;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.Utils.Router;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;

public class TrackingService extends Service implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    Location mCurrentLocation;
    String mLastUpdateTime;
    boolean mRequestingLocationUpdates = true;
    GoogleApiClient mGoogleApiClient;
    static TrackingService instance;
    NotificationManager mNM;
    int NOTIFICATION_ID = 112;
    SocketController socketController;
    static String TAG = "SERVICE";
    UserMain userMain;
    LocationRequest mLocationRequest = new LocationRequest()
            .setInterval(60*1000)
            .setFastestInterval(5000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public TrackingService() {
        instance = this;
        userMain = UserMain.getInstance(this);
        establishConnection();
    }
    public void establishConnection(){
        if(socketController==null) socketController = SocketController.getInstance(this);
        socketController.connectIfNotConnected();
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }



    @Override
    public void onConnected(Bundle bundle) {
        Log.d(TAG,"LocationServices onConnected");
//        if (mRequestingLocationUpdates) {
            startLocationUpdates();
//        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "LocationServices onConnectionSuspended");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG,"LocationServices onConnectionFailed");

    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        userMain.setLocation(location.getLatitude(),location.getLongitude());
        Log.d(TAG,"LOCATION UPDATE"+mCurrentLocation.toString());
        dumpDataInServer();
    }

    private void dumpDataInServer() {
        String url = Router.Login.locationUpdate();
        if(mCurrentLocation==null) return;
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat",mCurrentLocation.getLatitude());
            jsonObject.put("userId",MainApplication.getInstance().data.userMain.userId);
            jsonObject.put("lng",mCurrentLocation.getLongitude());


            MainApplication.getInstance().addRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    Logg.d(TAG, "USER ATA : " + jsonObject.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Logg.e(TAG, "ERROR : " + volleyError);
                }
            });
        } catch (JSONException e) {e.printStackTrace();}

    }


    @Override
    public void onCreate() {

        //Add notification
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }






    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        mNM.cancel(NOTIFICATION_ID);

        // Tell the user we stopped.
        Toast.makeText(this, "Tracker service stopped", Toast.LENGTH_SHORT).show();
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        TrackingService getService() {return TrackingService.this;}
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}
