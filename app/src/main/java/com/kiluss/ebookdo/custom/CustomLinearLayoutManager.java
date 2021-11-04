package com.kiluss.ebookdo.custom;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class CustomLinearLayoutManager extends LinearLayoutManager {
    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    public CustomLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public CustomLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        final LinearSmoothScroller linearSmoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    private static final float MILLISECONDS_PER_INCH = 35f;

                    @Override
                    public PointF computeScrollVectorForPosition(int targetPosition) {
                        return CustomLinearLayoutManager.this
                                .computeScrollVectorForPosition(targetPosition);
                    }

                    @Override
                    protected float calculateSpeedPerPixel
                            (DisplayMetrics displayMetrics) {
                        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
                    }
                    //This method calculates the sliding time. Indirectly control the speed here.
                    //Calculates the time it should take to scroll the given distance (in pixels)
                    @Override
                    protected int calculateTimeForScrolling(int dx) {
               /*
                   Control the distance and then calculate the time according to the speed provided by the calculateSpeedPerPixel()).

                   By default, a scroll TARGET_SEEK_SCROLL_DISTANCE_PX = 10000 pixels.

                   This value can be reduced here to reduce the rolling time.
                */

                        //Increase speed in indirect computing or directly in calculateSpeedPerPixel
                        if (dx > 2000) {
                            dx = 2000;
                        }

                        int time = super.calculateTimeForScrolling(dx);

                        return time;
                    }


                };
        linearSmoothScroller.setTargetPosition(position);
        startSmoothScroll(linearSmoothScroller);
    }
}
