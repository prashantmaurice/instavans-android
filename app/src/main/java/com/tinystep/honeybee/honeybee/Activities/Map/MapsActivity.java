package com.tinystep.honeybee.honeybee.Activities.Map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tinystep.honeybee.honeybee.Models.JobObj;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.storage.Data;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    static final String TAG = "MAPSACTIVITY";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    MapDataHandler mapDataHandler;
    ViewPager mViewPager;
    SectionsPagerAdapter mSectionsPagerAdapter;
    Data data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        setUpMapIfNeeded();
        data = Data.getInstance(this);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(),this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                JobObj offer = data.offers.get(position);
                scrollToJob(offer);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        recreateMarkers();
    }

    private void recreateMarkers(){
        mMap.clear();
        for(JobObj offer : data.offers){
            mMap.addMarker(new MarkerOptions().position(new LatLng(offer.lat, offer.longg)).title("Marker").snippet("Snippet"));
        }
    }

    private void scrollToJob(JobObj offer){
        LatLng latLng = new LatLng(offer.lat, offer.longg);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 10);
        mMap.animateCamera(cameraUpdate);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpHandlerAndUI();
            }
        }
    }

    private void setUpHandlerAndUI(){
        mapDataHandler = new MapDataHandler(mMap,this);
        mapDataHandler.setUpMap();
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mapDataHandler != null){
            mapDataHandler.mLastLocation = mLastLocation;
            mapDataHandler.addCurrentLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        MapsActivity mActivity;
        public SectionsPagerAdapter(FragmentManager fm, MapsActivity activity) {
            super(fm);
            mActivity = activity;
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            DataFragment frag = new DataFragment();
            frag.setActivity(mActivity);
            frag.setData(data.offers.get(position));
            return frag;
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return data.offers.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }

    }

    public static class DataFragment extends Fragment {

        public View mainView;
        public TextView tv_header, tv_subheader, tv_subheader2;
        public Context mContext;
        private JobObj data;

        public DataFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View dataView = inflater.inflate(R.layout.mycards_frag_list_item, container, false);
            mainView = dataView;
            tv_header = (TextView) dataView.findViewById(R.id.tv_header);
            tv_subheader = (TextView) dataView.findViewById(R.id.tv_subheader);
            tv_subheader2 = (TextView) dataView.findViewById(R.id.tv_subheader2);
            inflateData(data);
            return dataView;
        }

        public void setActivity(Context activity){
            mContext = activity;
        }

        public void inflateData(final JobObj msg){
            Logg.d(TAG, "Inflating data in Job view");
            tv_header.setText(""+msg.address);
            tv_subheader.setText(""+msg.startTime);
        }

        public void setData(JobObj data) {
            this.data = data;
        }
    }

}
