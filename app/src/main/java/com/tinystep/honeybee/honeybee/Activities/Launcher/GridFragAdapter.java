package com.tinystep.honeybee.honeybee.Activities.Launcher;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.tinystep.honeybee.honeybee.Utils.Logg;
import com.tinystep.honeybee.honeybee.Views.LauncherIconViewBuilder;

import java.util.ArrayList;

/**
 * Created by maurice on 10/06/15.
 */
public class GridFragAdapter extends BaseAdapter {
    String TAG = "OFFERSADAPTER";
    Activity mContext;
    private final ArrayList<GridFragment.GridObj> offers;

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

    public GridFragAdapter(Activity context, ArrayList<GridFragment.GridObj> offers){
//        super(context, R.layout.smslist_list_item, offers);
        this.mContext = context;
        this.offers = offers;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Logg.d(TAG,"getView : "+position);
        View view = null;
        if (convertView == null) {
            view = LauncherIconViewBuilder.getJobCardView(mContext);
        } else {
            view = convertView;
        }

        LauncherIconViewBuilder.ViewHolder holder = (LauncherIconViewBuilder.ViewHolder) view.getTag();

        GridFragment.GridObj offer = offers.get(position);
        holder.inflateData(offer);
        return view;
    }

    public void remove(int position) {
        offers.remove(position);
        //TODO : add in ignored lists
    }
}
