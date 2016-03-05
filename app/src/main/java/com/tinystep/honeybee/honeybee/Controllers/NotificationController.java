package com.tinystep.honeybee.honeybee.Controllers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.tinystep.honeybee.honeybee.Activities.Login.LoginActivity;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Models.JobObj;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.storage.Data;

import java.util.ArrayList;

/**
 *  This Controller basically handles all Notification related logics, currently this has notifications clubbed
 *  into three categories {@link #CHAT_NOTIFICATION}, {@link #FORUM_NOTIFICATION}, {@link #VACCINE_NOTIFICATION}
 *
 */
public class NotificationController {
    private static final long NOTIFICATION_TIME_DIFF = 1000;//1 sec gap between each notification ping sound
    Context mContext;
    static NotificationController instance;
    private static final String TAG = "NOTIFCONTROLLER";

    public static final int CHAT_NOTIFICATION = 121;
    public static final int FORUM_NOTIFICATION = 122;
    public static final int QUESTION_FORUM_NOTIFICATION = 1220;
    public static final int ANSWER_FORUM_NOTIFICATION = 1221;
    public static final int COMMENT_FORUM_NOTIFICATION = 1222;
    public static final int PROFILE_FORUM_NOTIFICATION = 1223;
    public static final int REFERRAL_NOTIFICATION = 1224;
    public static final int FRAGMENT_APP_NOTIFICATION = 123;
    public static final int ANNOUNCMENT_NOTIFICATION = 124;
    public static final int REVIEW_NOTIFICATION = 125;


    public static final int VACCINE_NOTIFICATION = 123;
    private long lastNotifTime = 0l;
    public static final int MSGS_LIMIT = 8;
    private String lastChatNotifSmallTxt = "";
    Data data;


    private NotificationController(Context context){
        mContext = context;
        data = MainApplication.getInstance().data;
    }
    public static NotificationController getInstance(Context context){
        if(instance==null) instance = new NotificationController(context);
        return instance;
    }


    public void updateOfferNotification(){
        ArrayList<JobObj> newjobs = data.getnewJobs();
        if(newjobs.size()==0) return;
        String title = newjobs.size()+" new jobs found...!";
        String smallText = "Tap to see them";
        showNotification(title,smallText,smallText,FORUM_NOTIFICATION);
        data.setAllSeen();
    }


    //NOTIFICATION BASE HANDLERS
    public void showNotification( String title, String smallText, String bigText, int notificationId){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext)
                .setSmallIcon(R.drawable.ic_cast_dark)
                .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), R.drawable.ic_cast_dark))
                .setContentTitle(title)
                .setContentText(smallText)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setTicker("Tinystep");
//                        .setColor(Color.argb(255, 255, 255, 255));

        mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        lastNotifTime = System.currentTimeMillis();


        if(bigText!=null) mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(bigText)
                        .setSummaryText(smallText));



        if( notificationId == QUESTION_FORUM_NOTIFICATION ||
                notificationId == ANSWER_FORUM_NOTIFICATION ||
                notificationId == COMMENT_FORUM_NOTIFICATION ||
                notificationId == PROFILE_FORUM_NOTIFICATION )
            notificationId = FORUM_NOTIFICATION;

        Logg.d(TAG, "Showing notification with Id: " + notificationId);

        Intent resultIntent = new Intent(mContext, LoginActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent resultPendingIntent =PendingIntent.getActivity(mContext,notificationId,resultIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder.setOnlyAlertOnce(true);
        Notification notification = mBuilder.build();
        notification.flags =  Notification.FLAG_AUTO_CANCEL;
        mNotifyMgr.notify(notificationId, notification);

    }

}
