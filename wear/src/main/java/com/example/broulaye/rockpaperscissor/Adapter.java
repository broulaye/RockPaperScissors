package com.example.broulaye.rockpaperscissor;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Adapter extends WearableListView.Adapter {
    private final LayoutInflater mInflater;
    private String[] mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public Adapter(Context context, String[] dataset) {
        Context mContext = context;
        mInflater = LayoutInflater.from(context);
        mDataset = dataset;
    }

    //This private class points to the list_item.xml. We use ItemViewHolder to assign the string in mDataset to the textView
    public static class ItemViewHolder extends WearableListView.ViewHolder {
        private TextView textView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.choice);
        }
    }

    @Override
    public WearableListView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemViewHolder(mInflater.inflate(R.layout.list_item, null));
    }

    @Override
    public void onBindViewHolder(WearableListView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        TextView view = itemViewHolder.textView;
        view.setText(mDataset[position]);
        ((ItemViewHolder) holder).itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDataset.length;
    }
}
