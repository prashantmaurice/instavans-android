package com.tinystep.honeybee.honeybee.Activities.Launcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.tinystep.honeybee.honeybee.Controllers.LocalBroadcastHandler;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.storage.Data;

import java.util.Locale;

public class LauncherActivity extends AppCompatActivity {
    String TAG = "MAINACTIVITY";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    Data data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        if(getSupportActionBar()!=null) getSupportActionBar().hide();

        data = MainApplication.getInstance().data;
        data.completePullFromServer();

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mSectionsPagerAdapter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter(LocalBroadcastHandler.OFFERS_UPDATED));
        mSectionsPagerAdapter.getLauncherFrag().notifyDataSetChanged();
    }

    // handler for received Intents from xmppservice
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Logg.d(TAG, "Refreshing each chats page ");
            if(mSectionsPagerAdapter!=null){
                mSectionsPagerAdapter.getLauncherFrag().notifyDataSetChanged();
            }
        }
    };


    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    public Data getData() {
        return data;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        LauncherActivity mActivity;
        GridFragment gridFrag;
        LauncherFragment launcherFrag;

        public SectionsPagerAdapter(FragmentManager fm, LauncherActivity activity) {
            super(fm);
            mActivity = activity;
            launcherFrag = LauncherFragment.newInstance(mActivity);
            gridFrag = GridFragment.newInstance(mActivity);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position) {
                case 0: return launcherFrag;
                case 1: return gridFrag;
            }
            return gridFrag;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0: return "Offers";
                case 1: return "Done";
                case 2: return "PROFILE";
            }
            return "";
        }

        public LauncherFragment getLauncherFrag(){
            return launcherFrag;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
    }


}
