package com.tinystep.honeybee.honeybee.Views;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tinystep.honeybee.honeybee.Activities.Drive.DriveActivity;
import com.tinystep.honeybee.honeybee.Controllers.LocalBroadcastHandler;
import com.tinystep.honeybee.honeybee.Controllers.ToastMain;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Models.JobObj;
import com.tinystep.honeybee.honeybee.Models.UserMain;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.Utils.NetworkCallback;
import com.tinystep.honeybee.honeybee.storage.Data;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * This is basic builder for getting a question view which can be used to inflate Question in Forum, ProfileScreen etc
 */
public class JobViewBuilder {
    static final String TAG = "BANKCARDVIEW";

    public static View getJobCardView(Activity activity){
        LayoutInflater inflator = LayoutInflater.from(activity);
        View mainView = inflator.inflate(R.layout.mycards_list_item, null);
        ViewHolder holder = new ViewHolder(mainView, activity);
        mainView.setTag(holder);
        return mainView;
                
    }

    public static class ViewHolder{
        public View mainView;
        public TextView tv_header, tv_subheader, tv_subheader2,tv_body,tv_cost;
        public ImageView iv_left;
        public TextView btn_one,btn_two,tv_right,tv_subheader3;
        public Activity mContext;
        public LinearLayout left_cont;
        Data data;

        public ViewHolder(View view, Activity activity) {
            mContext = activity;
            mainView = view;
            tv_header = (TextView) view.findViewById(R.id.tv_header);
            tv_subheader = (TextView) view.findViewById(R.id.tv_subheader);
            tv_subheader2 = (TextView) view.findViewById(R.id.tv_subheader2);
            tv_subheader3 = (TextView) view.findViewById(R.id.tv_subheader3);
            tv_right= (TextView) view.findViewById(R.id.tv_right);
            btn_one = (TextView) view.findViewById(R.id.btn_one);
            btn_two = (TextView) view.findViewById(R.id.btn_two);
            left_cont = (LinearLayout) view.findViewById(R.id.left_cont);
            data = MainApplication.getInstance().data;
        }


        // TODO - WTF! UserActivityObject and ActivityObject classes?! use ONE dude!

        public static double round (double value, int precision) {
            int scale = (int) Math.pow(10, precision);
            return (double) Math.round(value * scale) / scale;
        }

        public void inflateData(final JobObj msg){
            Logg.d(TAG, "Inflating data in Job view");
            tv_header.setText("["+msg.getCreator()+":"+msg.jobId+"]"+msg.address);
            final UserMain userMain = MainApplication.getInstance().data.userMain;
            tv_right.setText("("+round(Data.distFrom((float) userMain.lat, (float) userMain.longg, (float) msg.lat, (float) msg.longg)/1000,1)+"km)");
            tv_subheader3.setText("Rs."+msg.cost);
            if(userMain.isInTransit(msg.jobId)){

                //GOING ON
                long millis = System.currentTimeMillis()-userMain.getTransit(msg.jobId).started;
                long minutes = millis/(1000*60);
                tv_subheader.setText(getTime(minutes)+" in Transit");
                left_cont.setBackgroundColor(mContext.getResources().getColor(R.color.present));

                btn_two.setText("Finish");
                btn_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        data.ignoreOffer(msg);
                        userMain.setEndtrack(msg.jobId);
                        LocalBroadcastHandler.sendBroadcast(mContext, LocalBroadcastHandler.OFFERS_UPDATED);
                    }
                });

            }else if(userMain.isTrackFinished(msg.jobId)){

                //FINISHED
                long millis = userMain.getTransit(msg.jobId).ended-userMain.getTransit(msg.jobId).started;
                long minutes = millis/(1000*60);
                tv_subheader.setText("finished transit in "+getTime(minutes));
                left_cont.setBackgroundColor(mContext.getResources().getColor(R.color.past));

                btn_two.setText("View");
                btn_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        data.ignoreOffer(msg);
                        Intent intent = new Intent(mContext,DriveActivity.class);
                        intent.putExtra("data",msg.encode().toString());
                        mContext.startActivity(intent);
//                        LocalBroadcastHandler.sendBroadcast(mContext, LocalBroadcastHandler.OFFERS_UPDATED);
                    }
                });

            }else{
                long millis = msg.arrivalTime-System.currentTimeMillis();
                long minutes = millis/(1000*60);
                if(millis<0){
                    //MISSED

                    tv_subheader.setText("you missed this");
                    left_cont.setBackgroundColor(mContext.getResources().getColor(R.color.missed));

                    btn_two.setText("View");
                    btn_two.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                        data.ignoreOffer(msg);
                            Intent intent = new Intent(mContext,DriveActivity.class);
                            intent.putExtra("data",msg.encode().toString());
                            mContext.startActivity(intent);
//                        LocalBroadcastHandler.sendBroadcast(mContext, LocalBroadcastHandler.OFFERS_UPDATED);
                        }
                    });

                }else{

                    //UPCMING
                    tv_subheader.setText("Will start in "+getTime(minutes));
                    left_cont.setBackgroundColor(mContext.getResources().getColor(R.color.future));


                    if(data.isAccepted(msg.jobId)){
                        btn_two.setText("Navigate");
                        btn_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    Intent intent = new Intent(mContext,DriveActivity.class);
                                    intent.putExtra("data",msg.encode().toString());
                                    mContext.startActivity(intent);
//                                data.ignoreOffer(msg);
//                                LocalBroadcastHandler.sendBroadcast(mContext, LocalBroadcastHandler.OFFERS_UPDATED);
                            }
                        });
                    }else{
                        btn_two.setText("Ignore");
                        btn_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
    //                            Intent intent = new Intent(mContext,DriveActivity.class);
    //                            intent.putExtra("data",msg.encode().toString());
    //                            mContext.startActivity(intent);
                            data.ignoreOffer(msg);
                            LocalBroadcastHandler.sendBroadcast(mContext, LocalBroadcastHandler.OFFERS_UPDATED);
                            }
                        });
                    }
                }
            }


            String dateString = new SimpleDateFormat("dd LLL HH:mm aa").format(new Date(msg.arrivalTime));
            String dateString2 = new SimpleDateFormat("dd LLL HH:mm aa").format(new Date(msg.endTime));
            tv_subheader2.setText(dateString+" - "+dateString2);

            btn_one.setVisibility((data.isAccepted(msg.jobId))?View.GONE:View.VISIBLE);
            btn_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.acceptOffer(msg,new NetworkCallback() {
                        @Override
                        public void onSuccess() {
                            ToastMain.showSmartToast(mContext,null,"Successfully accepted");
                        }

                        @Override
                        public void onError() {
                            ToastMain.showSmartToast(mContext,null,"Some error has occurred");
                        }
                    });
                }
            });



        }


    }

    static String getTime(long minutes){
        if(minutes<60) return minutes+" minutes";
        if(minutes<60*24) return ViewHolder.round(minutes/60,1)+" hrs";
        return ViewHolder.round(minutes/(60*24),0)+" days";
    }
}
