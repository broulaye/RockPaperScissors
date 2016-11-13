package com.example.broulaye.rockpaperscissor;

import android.content.Context;
import android.support.wearable.view.WearableListView;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Shuo on 11/1/2016.
 */

public class WearableListItemLayout extends LinearLayout implements WearableListView.OnCenterProximityListener {
    private TextView mName;

    public WearableListItemLayout(Context context) {
        super(context);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WearableListItemLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    // Get references to the icon and text in the item layout definition
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // These are defined in the layout file for list items
        mName = (TextView) findViewById(R.id.choice);
    }

    @Override
    public void onCenterPosition(boolean animate) {
        //Set the alpha channel to 1 when the item is in the center
        mName.setAlpha(1f);
    }

    @Override
    public void onNonCenterPosition(boolean animate) {
        //Set the alpha channel to 0.5 when the item is not in the center
        mName.setAlpha(0.5f);
    }
}
