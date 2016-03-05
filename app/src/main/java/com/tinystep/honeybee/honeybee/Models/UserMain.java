package com.tinystep.honeybee.honeybee.Models;

import android.content.Context;

import com.tinystep.honeybee.honeybee.Utils.Utils;
import com.tinystep.honeybee.honeybee.storage.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * This contains all the User data excluding kids,
 */
public class UserMain {
    private Context mContext;
    private static UserMain instance;
    private SharedPrefs sPrefs;
    public String name;
    public String userId;
    public double lat, longg;
    ArrayList<String> ignoredJobs = new ArrayList<>();
    public ArrayList<String> shownJobs = new ArrayList<>();
    public String xmppPass;
    public String email;
    public String imageUrl;
    public String gcmId;
    public String facebookId;
    public String phone;
    public String address;
    public String coverPic;
    public Boolean isDoctor = false;
    public String userType; //can takes values "parent","doctor","none"
    public Boolean isMale = false;
    public String token;
    public String authProvider;


    private UserMain(Context context) {
        mContext = context;
        pullUserDataFromLocal();
    }
    public static UserMain getInstance(Context context) {
        if(instance == null) {
            instance = new UserMain(context);
        }
        return instance;
    }


    //LOCAL STORAGE ENCODERS
    public void pullUserDataFromLocal() {
        sPrefs = SharedPrefs.getInstance(mContext);
        try {
            name = (sPrefs.userData.has("name"))?sPrefs.userData.getString("name"):"";
            phone = (sPrefs.userData.has("phone"))?sPrefs.userData.getString("phone"):"";
            userId = (sPrefs.userData.has("userId"))?sPrefs.userData.getString("userId"):"";
            lat = (sPrefs.userData.has("lat"))?sPrefs.userData.getDouble("lat"):0;
            longg = (sPrefs.userData.has("longg"))?sPrefs.userData.getDouble("longg"):0;
            ArrayList<String> ignoredJobs2 = (sPrefs.userData.has("ignoredJobs"))? Utils.decodeStringArray(sPrefs.userData.getJSONArray("ignoredJobs")) : new ArrayList<String>();
            ignoredJobs.addAll(ignoredJobs2);
            ArrayList<String> shownJobs2 = (sPrefs.userData.has("shownJobs"))? Utils.decodeStringArray(sPrefs.userData.getJSONArray("shownJobs")) : new ArrayList<String>();
            shownJobs.addAll(shownJobs2);
        } catch (JSONException e) {e.printStackTrace();}
    }
    public void saveUserDataLocally() {
        try {
            if(ignoredJobs != null || ignoredJobs.size() == 0)
                sPrefs.userData.put("ignoredJobs", Utils.encodeStringArray(ignoredJobs));
            if(shownJobs != null || shownJobs.size() == 0)
                sPrefs.userData.put("shownJobs", Utils.encodeStringArray(shownJobs));

            sPrefs.userData.put("userId", userId);
            sPrefs.userData.put("phone", phone);
            sPrefs.userData.put("name", name);
            sPrefs.userData.put("lat", lat);
            sPrefs.userData.put("longg", longg);
        } catch (JSONException e) {e.printStackTrace();}
        sPrefs.saveUserData();
    }

    public void setLocation(double lat, double longg){
        this.lat = lat;
        this.longg = longg;
        saveUserDataLocally();
    }


    //SERVER ENCODERS
    public void decodeFromServer(JSONObject obj2){
        try {
            if(obj2.has("user")){
                JSONObject obj = obj2.getJSONObject("user");
                name = (obj.has("username"))?obj.getString("username"):"";
                userId = (obj.has("userId"))?obj.getString("userId"):"";
                phone = (obj.has("mobileNumber"))?obj.getString("mobileNumber"):"";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean loggedIn() {
        if(userId!=null&&!userId.isEmpty()) return true;
        return false;
    }
}
