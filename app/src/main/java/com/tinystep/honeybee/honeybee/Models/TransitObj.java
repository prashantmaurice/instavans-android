package com.tinystep.honeybee.honeybee.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class TransitObj {

    public String jobId = "";
    public Long started = 0L,ended = 0L;

    public TransitObj() {}

    //SERVER ENCODERS
    public static TransitObj decode(JSONObject obj){
        TransitObj re = new TransitObj();
        try {
            re.jobId = (obj.has("jobId")) ? obj.getString("jobId") : null;
            re.started = (obj.has("started")) ? obj.getLong("started") : 0;
            re.ended = (obj.has("ended")) ? obj.getLong("ended") : 0;
        } catch (JSONException e) {e.printStackTrace();}
        return re;
    }

    public static ArrayList<TransitObj> decode(JSONArray obj){

        ArrayList<TransitObj> list = new ArrayList<>();
        for(int i=0;i<obj.length();i++){
            try {
                list.add(decode(obj.getJSONObject(i)));
            } catch (JSONException e) {e.printStackTrace();}
        }
        return list;
    }

    public JSONObject encode(){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put( "jobId", jobId );
            jsonObject.put( "started", started );
            jsonObject.put( "ended", ended );

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
