package com.ar.myfirstapp.view.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ar.myfirstapp.bt.DeviceManager;
import com.ar.myfirstapp.utils.Constants;
import com.ar.myfirstapp.view.MainActivity;


/**
 * Created by amal.george on 24-03-2017
 */

public abstract class BaseFragment extends Fragment {
    protected View rootView;

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(dataUpdateReceiver, new IntentFilter(Constants.TAG_NOTIFICATION_REFRESH));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(dataUpdateReceiver);
    }

    protected abstract void loadData();

    protected abstract void updateData(int[] commandCode);

    protected BroadcastReceiver dataUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.TAG_NOTIFICATION_REFRESH)) {
                int index = intent.getIntExtra(Constants.TAG_NOTIFICATION_COMMAND_INDEX, -1);
                int pId = intent.getIntExtra(Constants.TAG_NOTIFICATION_COMMAND_PID, -1);
                if (index != -1 && pId != -1)
                    updateData(new int[]{index, pId});
            }
        }
    };

}
