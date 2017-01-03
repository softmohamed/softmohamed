package com.zakramlock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.zakramlock.R;
import com.zakramlock.model.AppItem;

import java.util.List;

/**
 * Created by Devon 12/11/2016.
 */

public class AppsViewAdapter extends BaseAdapter {
    private Context context;
    private List<AppItem> apps;

    public AppsViewAdapter(Context context, List<AppItem> apps) {
        this.context = context;
        this.apps = apps;
    }

    public void setData (List<AppItem> app){
       // this.apps.clear();
        this.apps.addAll(app);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int i) {
        return apps.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView = inflater.inflate(R.layout.icons_adapter, null);
        ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
        AppItem selectedApp = apps.get(i);
        //imageView.setImageDrawable(selectedApp.getIcon());
        imageView.setImageBitmap(selectedApp.getBitmap());

        return gridView;
    }
}
