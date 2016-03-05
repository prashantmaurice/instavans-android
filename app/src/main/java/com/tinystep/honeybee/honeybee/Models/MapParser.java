package com.tinystep.honeybee.honeybee.Models;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Utils.Logg;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by maurice on 06/03/16.
 */
public class MapParser {
    static String TAG = "MAPPARSER";

    public static void getBetween(double startLat, double startLong, double endLat, double endLong, final MapCallback cb ){
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                ""+startLat+","+startLong+"&destination="+endLat+","+endLong+"&sensor=false&units=metric";

        MainApplication.getInstance().addRequest(Request.Method.GET, url, new JSONObject(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                Logg.d(TAG, "USER ATA : " + jsonObject.toString());
                cb.success(parse(jsonObject));
            }


        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Logg.e(TAG, "ERROR : " + volleyError);
            }
        });
    }



    static ArrayList<Path> parse(JSONObject res){
        ArrayList<Path> points = new ArrayList<>();
        try {
            JSONArray routes = res.getJSONArray("routes");
            if(routes.length()==0) return points;

            JSONArray legs = routes.getJSONObject(0).getJSONArray("legs");
            if(legs.length()==0) return points;

            JSONArray steps = legs.getJSONObject(0).getJSONArray("steps");
            if(steps.length()==0) return points;

            for(int i=0;i<steps.length();i++){
                JSONObject step = steps.getJSONObject(i);
                JSONObject end_location = step.getJSONObject("end_location");
                JSONObject start_location = step.getJSONObject("start_location");

                Path path = new Path();
                path.endLat = end_location.getDouble("lat");
                path.endLng = end_location.getDouble("lng");
                path.startLat = start_location.getDouble("lat");
                path.startLng = start_location.getDouble("lng");
                path.text = step.getString("html_instructions");

                points.add(path);
            }


        } catch (JSONException e) {e.printStackTrace();}
        return points;
    }

    public static class Path{
        public double startLat,startLng, endLat, endLng;
        public String text;
    }

    public static abstract class MapCallback{
        ArrayList<Path> points = new ArrayList<>();

        public abstract void success(ArrayList<Path> parse);
    }

}
