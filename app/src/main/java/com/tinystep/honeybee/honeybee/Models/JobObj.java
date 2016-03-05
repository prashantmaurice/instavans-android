package com.tinystep.honeybee.honeybee.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JobObj implements Comparable<JobObj> {

    public String jobId = "";
    public Long arrivalTime = 1457286107000L,endTime = 1457386107000L;
    public double lat,longg;
    public String address = "No address";
    public int cost = 1000;
    public int distance = 1000;
    public int portersRequired = 1;
    public JSONObject raw;

    public JobObj() {}

    //SERVER ENCODERS
    public static JobObj decode(JSONObject obj){
        JobObj re = new JobObj();
        try {
            re.raw = obj;
            re.jobId = (obj.has("jobId")) ? obj.getString("jobId") : null;
            re.endTime = (obj.has("endTime")) ? obj.getLong("endTime") : 0;
            re.arrivalTime = (obj.has("arrivalTimestamp")) ? obj.getLong("arrivalTimestamp") : 0;
            re.endTime = (obj.has("unloadCompleteTimestamp")) ? obj.getLong("unloadCompleteTimestamp") : 0;

            if(obj.has("location")){
                JSONArray locarion = obj.getJSONArray("location");
                if(locarion!=null){
                    if(locarion.get(0) instanceof Integer){
                        re.lat = (double) locarion.getInt(1);
                        re.longg = (double) locarion.getInt(0);
                    }else{
                        re.lat = locarion.getDouble(1);
                        re.longg = locarion.getDouble(0);
                    }
                }
            }

            re.address = (obj.has("locationText")) ? obj.getString("locationText") : "No address";
            re.cost = (obj.has("cost")) ? obj.getInt("cost") : 0;
            re.distance = (obj.has("distance")) ? obj.getInt("distance") : 0;
            re.portersRequired = (obj.has("portersRequired")) ? obj.getInt("portersRequired") : 0;
        } catch (JSONException e) {e.printStackTrace();}
        return re;
    }

    public static ArrayList<JobObj> decode(JSONArray obj){

        ArrayList<JobObj> list = new ArrayList<>();
        for(int i=0;i<obj.length();i++){
            try {
                list.add(decode(obj.getJSONObject(i)));
            } catch (JSONException e) {e.printStackTrace();}
        }
        return list;
    }

    public JSONObject encode(){
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put( "jobId", jobId );
//            jsonObject.put( "arrivalTime", arrivalTime );
//            jsonObject.put( "endTime", endTime );
//            jsonObject.put( "lat", lat );
//            jsonObject.put( "longg", longg );
//            jsonObject.put( "cost", cost );
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        return raw;
//        return jsonObject;
    }

    @Override
    public int compareTo(JobObj another) {
        return 0;//TODO : implement this
    }

}
