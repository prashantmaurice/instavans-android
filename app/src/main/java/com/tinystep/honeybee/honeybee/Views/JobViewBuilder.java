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
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Models.JobObj;
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
        public TextView btn_one,btn_two,tv_acc_3,tv_acc_4;
        public Activity mContext;
        public LinearLayout left_cont;
        Data data;

        public ViewHolder(View view, Activity activity) {
            mContext = activity;
            mainView = view;
            tv_header = (TextView) view.findViewById(R.id.tv_header);
            tv_subheader = (TextView) view.findViewById(R.id.tv_subheader);
            tv_subheader2 = (TextView) view.findViewById(R.id.tv_subheader2);
            btn_one = (TextView) view.findViewById(R.id.btn_one);
            btn_two = (TextView) view.findViewById(R.id.btn_two);
            left_cont = (LinearLayout) view.findViewById(R.id.left_cont);
            data = MainApplication.getInstance().data;
        }


        // TODO - WTF! UserActivityObject and ActivityObject classes?! use ONE dude!

        public void inflateData(final JobObj msg){
            Logg.d(TAG, "Inflating data in Job view");
            tv_header.setText(msg.jobId+":"+msg.address);
            tv_subheader.setText(""+msg.arrivalTime);

            String dateString = new SimpleDateFormat("dd LLL yyyy").format(new Date(msg.arrivalTime));
            String dateString2 = new SimpleDateFormat("dd LLL yyyy").format(new Date(msg.endTime));
            tv_subheader2.setText(dateString+" - "+dateString2);

            long curr = System.currentTimeMillis();


            if(msg.arrivalTime>curr){
                //Future
                btn_two.setText("Ignore");
                btn_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        data.ignoreOffer(msg);
                        LocalBroadcastHandler.sendBroadcast(mContext, LocalBroadcastHandler.OFFERS_UPDATED);
                    }
                });


                left_cont.setBackgroundColor(mContext.getResources().getColor(R.color.future));
            }else if(msg.endTime<curr){
                //Past
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
                left_cont.setBackgroundColor(mContext.getResources().getColor(R.color.past));
            }else{
                //Current
                btn_two.setText("Navigate");
                btn_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext,DriveActivity.class);
                        intent.putExtra("data",msg.encode().toString());
                        mContext.startActivity(intent);
//                        data.ignoreOffer(msg);
//                        LocalBroadcastHandler.sendBroadcast(mContext, LocalBroadcastHandler.OFFERS_UPDATED);
                    }
                });
                left_cont.setBackgroundColor(mContext.getResources().getColor(R.color.present));
            }

            btn_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.acceptOffer(msg,new NetworkCallback() {
                        @Override
                        public void onSuccess() {


                        }

                        @Override
                        public void onError() {

                        }
                    });
                }
            });



        }


    }
}
