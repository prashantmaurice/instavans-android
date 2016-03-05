package com.maurice.instavans.Controllers;

import android.content.Context;

import com.maurice.instavans.Utils.Settings;
import com.maurice.instavans.Utils.Utils;


/**
 *  Use this to show toasts to user, this shows small text if in production mode and shows detailed toast
 *  in debug mode for testers to identify problems.
 *
 */

public class ToastMain {

    public static void showSmartToast(Context context, String smallText, String detailedText){
        if(Settings.showDebugToasts) {
            if(detailedText!=null) Utils.showToast(context, detailedText);
        }else{
            if(smallText!=null) Utils.showToast(context, smallText);
        }
    }
    public static void showSmartToast(Context context, String generalText){
        if(generalText!=null) Utils.showToast(context, generalText);
    }

}
