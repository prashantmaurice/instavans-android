package com.tinystep.honeybee.honeybee.Activities.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.tinystep.honeybee.honeybee.R;

/**
 * This is the main fragment user for listing user Notifications
 */
public class UserFragment extends android.support.v4.app.Fragment {

    public MainActivity mActivity;

    public UserFragment() {
    }

    public static UserFragment newInstance(MainActivity activityContext) {
        UserFragment myFragment = new UserFragment();
        myFragment.mActivity = activityContext;
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView;
        rootView = inflater.inflate(R.layout.fragment_notifications, null);


        return rootView;
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        for(int i=0;i<menu.size();i++){
            menu.getItem(i).setVisible(false);
        }
    }

    @Override
    public void onDestroy() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver( notificationReceiver );
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.mActivity = (MainActivity) activity;
    }

}

