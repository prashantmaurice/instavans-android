package com.tinystep.honeybee.honeybee.Activities.Launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;

import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.tinystep.honeybee.honeybee.Activities.Login.LoginActivity;
import com.tinystep.honeybee.honeybee.Activities.Main.OffersFragAdapter;
import com.tinystep.honeybee.honeybee.Controllers.ToastMain;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Models.JobObj;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.Utils.NetworkCallback;
import com.tinystep.honeybee.honeybee.storage.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * This is the main fragment user for listing user Notifications
 */
public class GridFragment extends android.support.v4.app.Fragment {

    public LauncherActivity mActivity;
    ArrayList<JobObj> allSMSDirectories = new ArrayList<>();
    ListView notificationsLV;
    SwipeRefreshLayout refresh_cont;
    OffersFragAdapter adapter;
    View cont_noevents;
    Data data;

    public GridFragment() {
    }

    public static GridFragment newInstance(LauncherActivity activityContext) {
        GridFragment myFragment = new GridFragment();
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
        rootView = inflater.inflate(R.layout.fragment_grid, null);

        //Data
        final ArrayList<GridObj> res = new ArrayList<>();


        List<PackageInfo> packs = mActivity.getPackageManager().getInstalledPackages(0);
        for(int i=0;i<packs.size();i++) {
            PackageInfo p = packs.get(i);
            if(isSystemPackage(p)) continue;
            GridObj newInfo = new GridObj();
            newInfo.name = p.applicationInfo.loadLabel(mActivity.getPackageManager()).toString();
            newInfo.packageName = p.packageName;
            newInfo.icon = p.applicationInfo.loadIcon(mActivity.getPackageManager());
            res.add(newInfo);
        }



        GridView gridView = (GridView) rootView.findViewById(R.id.gridView1);

        // Set custom adapter (GridAdapter) to gridview

        gridView.setAdapter(  new GridFragAdapter(mActivity,res) );

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                if(res.get(position).packageName.equals("com.tinystep.honeybee.honeybee")){
                    Intent launchIntent = new Intent(mActivity, LoginActivity.class);
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mActivity.startActivity(launchIntent);
                }else{
                    Intent launchIntent = mActivity.getPackageManager().getLaunchIntentForPackage(res.get(position).packageName);
                    mActivity.startActivity(launchIntent);
                }

            }
        });


        return rootView;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return ((pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) ? true
                : false;
    }

    private void setSwipeDismiss() {
        final SwipeToDismissTouchListener<ListViewAdapter> touchListener =
                new SwipeToDismissTouchListener<>(
                        new ListViewAdapter(notificationsLV),
                        new SwipeToDismissTouchListener.DismissCallbacks<ListViewAdapter>() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListViewAdapter view, int position) {
                                adapter.remove(position);
                            }
                        });
        notificationsLV.setOnTouchListener(touchListener);
        notificationsLV.setOnScrollListener((AbsListView.OnScrollListener) touchListener.makeScrollListener());
        notificationsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (touchListener.existPendingDismisses()) {
                    touchListener.undoPendingDismiss();
                } else {
                    ToastMain.showSmartToast(mActivity,"Position " + position,"");
                }
            }
        });
    }

    public void completeRefresh(){
        data.pullOffersFromServer(new NetworkCallback() {
            @Override
            public void onSuccess() {
                notifyDataSetChanged();
                refresh_cont.setRefreshing(false);
            }

            @Override
            public void onError() {
                notifyDataSetChanged();
                refresh_cont.setRefreshing(false);
            }
        });
    }

    public void notifyDataSetChanged() {
        allSMSDirectories.clear();
//        allSMSDirectories.addAll(MainApplication.getInstance().data.offers);
        for(JobObj job: MainApplication.getInstance().data.offers){
            if(!data.userMain.isJobIgnored(job.jobId)){
                allSMSDirectories.add(job);
            }
        }
        if(cont_noevents!=null)cont_noevents.setVisibility(allSMSDirectories.size()==0?View.VISIBLE:View.GONE);
//        sort();
        if(adapter!=null) adapter.notifyDataSetChanged();
    }

    public void sort(){
        Collections.sort(allSMSDirectories, new Comparator<JobObj>() {
            @Override
            public int compare(JobObj s1, JobObj s2) {
                return s1.arrivalTime.compareTo(s2.arrivalTime);
            }
        });
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
        this.mActivity = (LauncherActivity) activity;
    }

    public class GridObj{
        public String name;
        public String packageName;
        public Drawable icon;
    }

}

