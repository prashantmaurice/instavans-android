package com.tinystep.honeybee.honeybee.Views;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tinystep.honeybee.honeybee.Activities.Launcher.GridFragment;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.storage.Data;

/**
 * This is basic builder for getting a question view which can be used to inflate Question in Forum, ProfileScreen etc
 */
public class LauncherIconViewBuilder {
    static final String TAG = "BANKCARDVIEW";

    public static View getJobCardView(Activity activity){
        LayoutInflater inflator = LayoutInflater.from(activity);
        View mainView = inflator.inflate(R.layout.grid_list_item, null);
        ViewHolder holder = new ViewHolder(mainView, activity);
        mainView.setTag(holder);
        return mainView;
                
    }

    public static class ViewHolder{
        public View mainView;
        public TextView textView7;
        ImageView imageView3;
        public Context mContext;
        Data data;

        public ViewHolder(View view, Activity activity) {
            mContext = activity;
            mainView = view;
            textView7 = (TextView) view.findViewById(R.id.textView7);
            imageView3 = (ImageView) view.findViewById(R.id.imageView3);
        }


        // TODO - WTF! UserActivityObject and ActivityObject classes?! use ONE dude!

        public void inflateData(final GridFragment.GridObj msg){
            Logg.d(TAG, "Inflating data in Job view");
            String name = msg.name;
            if(name.length()>12) name = name.substring(0,9)+"...";
            textView7.setText(name);
            imageView3.setImageDrawable(msg.icon);

//            mainView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent launchIntent = mContext.getPackageManager().getLaunchIntentForPackage(msg.packageName);
//                    mContext.startActivity(launchIntent);
//                }
//            });

        }


    }
}
