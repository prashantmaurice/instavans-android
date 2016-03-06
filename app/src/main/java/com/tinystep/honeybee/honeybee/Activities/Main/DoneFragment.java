package com.tinystep.honeybee.honeybee.Activities.Main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hudomju.swipe.SwipeToDismissTouchListener;
import com.hudomju.swipe.adapter.ListViewAdapter;
import com.tinystep.honeybee.honeybee.Controllers.ToastMain;
import com.tinystep.honeybee.honeybee.MainApplication;
import com.tinystep.honeybee.honeybee.Models.JobObj;
import com.tinystep.honeybee.honeybee.R;
import com.tinystep.honeybee.honeybee.Utils.NetworkCallback;
import com.tinystep.honeybee.honeybee.storage.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * This is the main fragment user for listing user Notifications
 */
public class DoneFragment extends android.support.v4.app.Fragment {

    public MainActivity mActivity;
    ArrayList<JobObj> allSMSDirectories = new ArrayList<>();
    ListView notificationsLV;
    SwipeRefreshLayout refresh_cont;
    DoneFragAdapter adapter;
    View cont_noevents;
    Data data;

    public DoneFragment() {
    }

    public static DoneFragment newInstance(MainActivity activityContext) {
        DoneFragment myFragment = new DoneFragment();
        myFragment.mActivity = activityContext;
        myFragment.data = Data.getInstance(activityContext);
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

        notificationsLV = (ListView) rootView.findViewById(R.id.notificationsLV);

        cont_noevents = rootView.findViewById(R.id.cont_noevents);
        adapter = new DoneFragAdapter(getActivity(), allSMSDirectories);
        notificationsLV.setAdapter(adapter);
        refresh_cont = (SwipeRefreshLayout) rootView.findViewById(R.id.refresh_cont);
        refresh_cont.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                completeRefresh();
            }
        });
        notifyDataSetChanged();


        //Set swipe dismiss
        setSwipeDismiss();

        return rootView;
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
        data.pullCompletedFromServer(new NetworkCallback() {
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
        allSMSDirectories.addAll(MainApplication.getInstance().data.done);
        if(cont_noevents!=null) cont_noevents.setVisibility(allSMSDirectories.size()==0?View.VISIBLE:View.GONE);
        sort();
        if(adapter!=null)adapter.notifyDataSetChanged();
    }

    public void sort(){
        Collections.sort(allSMSDirectories, new Comparator<JobObj>() {
            @Override
            public int compare(JobObj s1, JobObj s2) {
                return -s1.arrivalTime.compareTo(s2.arrivalTime);
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
        this.mActivity = (MainActivity) activity;
    }

}

