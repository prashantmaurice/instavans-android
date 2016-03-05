package com.tinystep.honeybee.honeybee.Activities.Map;

import android.location.Location;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by maurice on 29/07/15.
 */
public class MapDataHandler {
    String TAG = "MapDataHandler";
    GoogleMap mMap;
    public Location mLastLocation;
    FragmentActivity activity;

    public MapDataHandler(GoogleMap mMap, FragmentActivity activity){
        this.mMap = mMap;
        this.activity = activity;
    }

    /** Main Functions */
//    public void showMyHistory(){
//        String url = Router.Data.getMyHistory(MainApplication.getInstance().data.userMain.userId);
//        Logg.d(TAG, "showMyHistory : " + url);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,new JSONObject(),new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                Logg.d(TAG, "HISTORY DATA : " + jsonObject.toString());
//                try {
//                    JSONObject result = jsonObject.getJSONObject("result");
//                    ArrayList<TrackPoint> tracks = TrackPoint.decodeFromServer(result.getJSONArray("tracks"));
//                    mMap.clear();
//                    drawPath(tracks);
//                } catch (JSONException e) {e.printStackTrace();}
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                Logg.e(TAG,"ERROR : "+volleyError);
//                Utils.showDebugToast(activity,volleyError.networkResponse.statusCode+" : Some error has occurred");
//            }
//        });
//        MainApplication.getInstance().getRequestQueue().add(jsonObjectRequest);
//    }
//
//    public void showAllUsers(){
//        String url = Router.Data.getOnlineUsersLoc(MainApplication.getInstance().data.userMain.userId);
//        Logg.d(TAG, "showAllUsers : " + url);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,new JSONObject(),new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                Logg.d(TAG, "HISTORY DATA : " + jsonObject.toString());
//                try {
//                    JSONObject result = jsonObject.getJSONObject("result");
//                    ArrayList<User> users = User.decodeFromServer(result.getJSONArray("users"));
//                    mMap.clear();
//                    drawUsers(users);
//                } catch (JSONException e) {e.printStackTrace();}
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                Logg.e(TAG,"ERROR : "+volleyError);
//                Utils.showDebugToast(activity,volleyError.networkResponse.statusCode+" : Some error has occurred");
//            }
//        });
//        MainApplication.getInstance().getRequestQueue().add(jsonObjectRequest);
//    }

//    private void drawPath(ArrayList<TrackPoint> tracks){
//        PolylineOptions shapeOptions = new PolylineOptions().width(5).color(Color.RED);
//        for(int i=0;i<tracks.size()-1;i++){
//            Logg.d("POINT",tracks.get(i).latitude+" : "+tracks.get(i).longitude);
//            shapeOptions.add(new LatLng(tracks.get(i).latitude, tracks.get(i).longitude), new LatLng(tracks.get(i+1).latitude, tracks.get(i+1).longitude));
//        }
//        Polyline polyline = mMap.addPolyline(shapeOptions);
////        mMap.addMa
//    }

//    private void drawUsers(ArrayList<User> users){
//        for(User user : users){
//            Marker marker = mMap.addMarker(new MarkerOptions()
//                    .position(new LatLng(user.trackPoint.latitude, user.trackPoint.longitude))
//                    .title("" + user.mobileNumber)
//                    .snippet("" + user.username));
//            marker.showInfoWindow();
//        }
//    }


    /** Helper Functions */

    public void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker").snippet("Snippet"));

        // Enable MyLocation Layer of Google Map
        mMap.setMyLocationEnabled(true);

        // set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        if(mLastLocation!=null) addCurrentLocation();
    }

    public void addCurrentLocation(){
//        Logg.d(TAG,"addCurrentLocation"+mLastLocation.getLatitude());

        // Get Current Location
        if(mLastLocation!=null){

            // Get latitude of the current location
            double latitude = mLastLocation.getLatitude();

            // Get longitude of the current location
            double longitude = mLastLocation.getLongitude();

            // Create a LatLng object for the current location
            LatLng latLng = new LatLng(latitude, longitude);

            // Show the current location in Google Map
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

            // Zoom in the Google Map
//            mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),16f));
        }

    }


}
