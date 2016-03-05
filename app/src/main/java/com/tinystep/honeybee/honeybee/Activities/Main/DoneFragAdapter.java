package com.tinystep.honeybee.honeybee.Activities.Main;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tinystep.honeybee.honeybee.Models.JobObj;
import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.Views.JobViewBuilder;

import java.util.ArrayList;

/**
 * Created by maurice on 10/06/15.
 */
public class DoneFragAdapter extends BaseAdapter {
    String TAG = "OFFERSADAPTER";
    Activity mContext;
    private final ArrayList<JobObj> offers;

    @Override
    public int getCount() {
        return offers.size();
    }

    @Override
    public Object getItem(int position) {
        return offers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public DoneFragAdapter(Activity context, ArrayList<JobObj> offers){
//        super(context, R.layout.smslist_list_item, offers);
        this.mContext = context;
        this.offers = offers;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Logg.d(TAG,"getView : "+position);
        View view = null;
        if (convertView == null) {
            view = JobViewBuilder.getJobCardView(mContext);
        } else {
            view = convertView;
        }

        JobViewBuilder.ViewHolder holder = (JobViewBuilder.ViewHolder) view.getTag();

        JobObj offer = offers.get(position);
        holder.inflateData(offer);
        return view;
    }

    public void remove(int position) {
        offers.remove(position);
        //TODO : add in ignored lists
    }
}
