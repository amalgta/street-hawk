package com.ar.myfirstapp.view.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.ar.myfirstapp.R;
import com.ar.myfirstapp.bt.DeviceManager;
import com.ar.myfirstapp.bt.DeviceResponseHandler;
import com.ar.myfirstapp.obd2.Command;
import com.ar.myfirstapp.utils.Constants;
import com.ar.myfirstapp.utils.DashUtils;
import com.ar.myfirstapp.utils.DataStorage;
import com.ar.myfirstapp.utils.Logger;
import com.ar.myfirstapp.view.MainActivity;
import com.ar.myfirstapp.view.adapter.DashItemAdapter;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by amal.george on 12-04-2017
 */

public class DashFragment extends BaseFragment {
    RecyclerView recyclerView;
    DashItemAdapter dashItemAdapter;
    Switch switchListen;
    WorkerThread workerThread;
    public DeviceResponseHandler deviceResponseHandler;

    private BroadcastReceiver dashUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constants.TAG_DASH_ADD)) {
                int index = intent.getIntExtra(Constants.TAG_NOTIFICATION_COMMAND_INDEX, -1);
                int pId = intent.getIntExtra(Constants.TAG_NOTIFICATION_COMMAND_PID, -1);
                if (index != -1 && pId != -1)
                    addToDash(new int[]{index, pId});
            } else if (intent.getAction().equals(Constants.TAG_DASH_REMOVE)) {
                int index = intent.getIntExtra(Constants.TAG_NOTIFICATION_COMMAND_INDEX, -1);
                int pId = intent.getIntExtra(Constants.TAG_NOTIFICATION_COMMAND_PID, -1);
                if (index != -1 && pId != -1)
                    removeFromDash(new int[]{index, pId});
            }
        }
    };

    private void removeFromDash(int[] commandCode) {
        dashItemAdapter.add(((MainActivity) getActivity()).getCommands()[commandCode[0]].get(commandCode[1]));
    }

    private void addToDash(int[] commandCode) {
        dashItemAdapter.remove((((MainActivity) getActivity()).getCommands()[commandCode[0]].get(commandCode[1])));
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(dashUpdateReceiver, new IntentFilter(Constants.TAG_DASH_ADD));
        getActivity().registerReceiver(dashUpdateReceiver, new IntentFilter(Constants.TAG_DASH_REMOVE));
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(dashUpdateReceiver);
    }

    @Override
    protected void loadData() {
        List<Command> commands = new LinkedList<>();
        for (int[] commandCode : DashUtils.getAll(new DataStorage(getContext()))) {
            commands.add((((MainActivity) getActivity()).getCommands()[commandCode[0]].get(commandCode[1])));
        }
        dashItemAdapter = new DashItemAdapter(commands, getContext());
        recyclerView.setAdapter(dashItemAdapter);
    }

    @Override
    protected void updateData(int[] commandCode) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceResponseHandler = new DeviceResponseHandler(new DeviceResponseHandler.DeviceResponseListener() {
            @Override
            public void onStateChanged(int state) {
                switchListen.setVisibility((state == DeviceManager.BLUETOOTH_STATE.CONNECTED) ? View.VISIBLE : View.GONE);
                /*TODO Additionally stop the repeated sending process here*/
            }

            @Override
            public void onConnected(String connectedDeviceName) {

            }

            @Override
            public void onWriteCommand(Command command) {

            }

            @Override
            public void onReadCommand(Command command) {

            }

            @Override
            public void onNotification(String notificationText) {

            }
        });
        DeviceManager.getInstance().addResponseHandler(deviceResponseHandler);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        initUI();
        return rootView;
    }

    private void initUI() {
        switchListen = (Switch) rootView.findViewById(R.id.switchListen);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        switchListen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    workerThread = new WorkerThread(((MainActivity) getActivity()).getCommands(), getContext());
                    DeviceManager.getInstance().addResponseHandler(new DeviceResponseHandler(workerThread));
                    workerThread.start();
                } else {
                    DeviceManager.getInstance().removeResponseHandler(new DeviceResponseHandler(workerThread));
                    DeviceManager.getInstance().clearQueue();
                    workerThread.cancel();
                }
            }
        });
        switchListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private class WorkerThread extends Thread implements DeviceResponseHandler.DeviceResponseListener {
        private static final String TAG = "WorkerThread";
        TreeMap<Integer, Command>[] commands;
        Context context;
        boolean isRunning = true;

        WorkerThread(Map<Integer, Command>[] commands, Context context) {
            this.commands = (TreeMap<Integer, Command>[]) commands;
            this.context = context;
        }

        @Override
        public void run() {
            while (isRunning) {
                if ((DeviceManager.getInstance().getCurrentState() == DeviceManager.BLUETOOTH_STATE.CONNECTED)) {
                    List<int[]> dashBoardItems = DashUtils.getAll(new DataStorage(context));
                    for (int[] thisCommandCode : dashBoardItems) {
                        Map<Integer, Command> commandHashMap = commands[thisCommandCode[0]];
                        if (commandHashMap == null) continue;
                        if (commandHashMap.containsKey(thisCommandCode[1]))
                            DeviceManager.getInstance().send(commandHashMap.get(thisCommandCode[1]));
                    }
                }
            }
        }

        @Override
        public void onStateChanged(int state) {

        }

        @Override
        public void onConnected(String connectedDeviceName) {

        }

        @Override
        public void onWriteCommand(Command command) {

        }

        @Override
        public void onReadCommand(Command command) {
            try {
                int index = (Integer.parseInt(command.getCommandId(), 16)) - 1;
                if (commands[index] == null) commands[index] = new TreeMap<>();
                try {
                    int pId = Integer.parseInt(command.getPid(), 16);
                    commands[index].put(pId, command);
                } catch (Exception e) {
                    Logger.e(TAG, e.toString());
                }
            } catch (NumberFormatException ignored) {
            }
        }

        @Override
        public void onNotification(String notificationText) {

        }

        void cancel() {
            isRunning = false;
        }
    }


}
