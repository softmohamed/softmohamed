package com.zakramlock.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.zakramlock.R;
import com.zakramlock.model.AppItem;

import java.util.List;

/**
 * Created by Devon 12/14/2016.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private List<AppItem> AppItems;
    private static final String TAG = RecycleViewAdapter.class.getName();
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public ViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.item_image);
        }
    }

    public RecycleViewAdapter(List<AppItem> AppItems) {
        this.AppItems = AppItems;
    }

    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carousel_view_adapter, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecycleViewAdapter.ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(this.AppItems.get(position).getBitmap());
    }

    @Override
    public int getItemCount() {
        return this.AppItems.size();
    }
}
