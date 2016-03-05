package com.maurice.instavans.Activities.Main;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.maurice.instavans.Models.JobObj;
import com.maurice.instavans.R;
import com.maurice.instavans.Views.JobViewBuilder;

import java.util.ArrayList;

/**
 * Created by maurice on 10/06/15.
 */
public class OffersFragAdapter extends ArrayAdapter<JobObj> {
    Activity mContext;
    private final ArrayList<JobObj> offers;

    @Override
    public int getCount() {
        return offers.size();
    }

    public OffersFragAdapter(Activity context, ArrayList<JobObj> offers){
        super(context, R.layout.smslist_list_item, offers);
        this.mContext = context;
        this.offers = offers;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = mContext.getLayoutInflater();
            view = JobViewBuilder.getJobCardView(mContext);
        } else {
            view = convertView;
        }

        JobViewBuilder.ViewHolder holder = (JobViewBuilder.ViewHolder) view.getTag();

        JobObj offer = offers.get(position);
        holder.inflateData(offer);
        return view;
    }

}
