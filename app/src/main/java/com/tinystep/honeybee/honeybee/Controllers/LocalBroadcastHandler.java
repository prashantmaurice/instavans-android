package com.tinystep.honeybee.honeybee.Controllers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by maurice on 11/08/15.
 */
public class LocalBroadcastHandler {

    //Custom events that happens within the app
    public static String OFFERS_UPDATED = "OFEFRS_UPDATEDD";
//    public static String LOCAL_UPDATE = "LOCAL_UPDATED";

    public static void sendBroadcast(Context context, String intentCode){
        Intent intent = new Intent(intentCode);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }


}
