package com.tinystep.honeybee.honeybee.Activities.Drive;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tinystep.honeybee.honeybee.Activities.Map.MapDataHandler;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Models.JobObj;
import com.tinystep.honeybee.honeybee.Models.MapParser;
import com.tinystep.honeybee.honeybee.Models.UserMain;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.storage.Data;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.tinystep.honeybee.honeybee.Models.MapParser.MapCallback;
import static com.tinystep.honeybee.honeybee.Models.MapParser.Path;

public class DriveActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final String TAG = "MAPSACTIVITY";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    MapDataHandler mapDataHandler;
    Data data;
    TextView tv_title,btn_start,btn_end,btn_instructions;
    JobObj jobObj;
    UserMain userMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive);
        try {
            JSONObject obj = new JSONObject(getIntent().getStringExtra("data"));
            jobObj = JobObj.decode(obj);
        } catch (JSONException e) {e.printStackTrace();finish();}


        tv_title = (TextView) findViewById(R.id.tv_title);
        btn_start = (TextView) findViewById(R.id.btn_start);
        btn_end = (TextView) findViewById(R.id.btn_end);
        btn_instructions = (TextView) findViewById(R.id.btn_instructions);

        tv_title.setText(jobObj.address);
        userMain = MainApplication.getInstance().data.userMain;

        findViewById(R.id.btn_google).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri gmmIntentUri = Uri.parse("google.navigation:q="+jobObj.lat+","+jobObj.longg+"&avoid=tf");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            }
        });
        data = Data.getInstance(this);

        refreshButtons();

        buildGoogleApiClient();
        setUpMapIfNeeded();



        recreateMarkers();
        scrollToJob(jobObj);
    }

    private void refreshButtons() {
        if(userMain.isTrackStarted(jobObj.jobId)&&!userMain.isTrackFinished(jobObj.jobId)){
            //Present
            btn_start.setVisibility(View.GONE);
            btn_end.setVisibility(View.VISIBLE);
        }else if(userMain.isTrackFinished(jobObj.jobId)){
            //Past
            btn_start.setVisibility(View.GONE);
            btn_end.setVisibility(View.GONE);
        }else{
            long millis = jobObj.arrivalTime-System.currentTimeMillis();
            if(millis<0){
                //Future
                btn_start.setVisibility(View.GONE);
                btn_end.setVisibility(View.GONE);
            }else {
                //Future
                btn_start.setVisibility(View.VISIBLE);
                btn_end.setVisibility(View.GONE);
            }
        }
        if(!data.isAccepted(jobObj.jobId)){
            btn_start.setVisibility(View.GONE);
            btn_end.setVisibility(View.GONE);
        }

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMain.setStartTrack(jobObj.jobId);
                refreshButtons();
            }
        });
        btn_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMain.setEndtrack(jobObj.jobId);
                refreshButtons();
            }
        });
    }

    private void drawPolygons(Location mLastLocation) {
        MapParser.getBetween(mLastLocation.getLatitude(),mLastLocation.getLongitude(),jobObj.lat,jobObj.longg, new MapCallback(){
            @Override
            public void success(ArrayList<Path> parse) {
                String completeText = "";
                for(Path path : parse){
                    mMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(path.startLat, path.startLng), new LatLng(path.endLat,path.endLng))
                            .width(10)
                            .color(getResources().getColor(R.color.ColorPrimary)));
                    completeText+= path.text+"<br>";
                }


                final String finalCompleteText = completeText;
                btn_instructions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog dialog = new AlertDialog.Builder(DriveActivity.this)
                                .setTitle("Instructions")
                                .setMessage(Html.fromHtml(finalCompleteText))
//                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        // continue with delete
//                                        dialog.dismiss();
//                                    }
//                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                        dialog.dismiss();
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                });
            }
        });
    }

    private void recreateMarkers(){
        mMap.addMarker(new MarkerOptions().position(new LatLng(jobObj.lat, jobObj.longg)).title("Marker").snippet("Snippet"));

    }

    private void scrollToJob(JobObj offer){
        LatLng latLng = new LatLng(offer.lat, offer.longg);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
        mMap.animateCamera(cameraUpdate);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpHandlerAndUI();
            }
        }
    }

    private void setUpHandlerAndUI(){
        mapDataHandler = new MapDataHandler(mMap,this);
        mapDataHandler.setUpMap();
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mapDataHandler != null){
            mapDataHandler.mLastLocation = mLastLocation;
            mapDataHandler.addCurrentLocation();
            drawPolygons(mLastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


}
