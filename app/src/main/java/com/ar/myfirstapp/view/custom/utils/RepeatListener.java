package com.ar.myfirstapp.view.custom.utils;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by amal.george on 04-04-2017
 */

public class RepeatListener implements View.OnTouchListener {
    private Handler handler = new Handler();
    private int initialInterval;
    private final int normalInterval;
    private final View.OnClickListener clickListener;
    private Runnable handlerRunnable = new Runnable() {
        @Override
        public void run() {
            clickListener.onClick(itemView);
            handler.postDelayed(handlerRunnable, normalInterval);
        }
    };
    private View itemView;

    /**
     * @param initialInterval The interval after first click event
     * @param normalInterval  The interval after second and subsequent click
     *                        events
     * @param clickListener   The OnClickListener, that will be called
     *                        periodically
     */
    public RepeatListener(int initialInterval, int normalInterval,
                          View.OnClickListener clickListener) {
        if (clickListener == null)
            throw new IllegalArgumentException("clickListener is null");
        if (initialInterval < 0 || normalInterval < 0)
            throw new IllegalArgumentException("negative interval");

        this.initialInterval = initialInterval;
        this.normalInterval = normalInterval;
        this.clickListener = clickListener;
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                handler.removeCallbacks(handlerRunnable);
                handler.removeCallbacksAndMessages(null);
                itemView = view;
                handler.postDelayed(handlerRunnable, initialInterval);
                clickListener.onClick(view);
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                handler.removeCallbacks(handlerRunnable);
                handler.removeCallbacksAndMessages(null);
                itemView = null;
                return true;
        }
        return false;
    }

}