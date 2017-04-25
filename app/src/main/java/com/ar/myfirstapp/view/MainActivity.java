package com.ar.myfirstapp.view;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ar.myfirstapp.R;
import com.ar.myfirstapp.bt.DeviceManager;
import com.ar.myfirstapp.bt.DeviceResponseHandler;
import com.ar.myfirstapp.obd2.Command;
import com.ar.myfirstapp.obd2.at.AtCommands;
import com.ar.myfirstapp.obd2.saej1979.ModeFactory;
import com.ar.myfirstapp.utils.Constants;
import com.ar.myfirstapp.utils.Logger;
import com.ar.myfirstapp.utils.Utils;
import com.ar.myfirstapp.view.custom.infinteviewpager.CircleIndicator;
import com.ar.myfirstapp.view.custom.infinteviewpager.InfiniteViewPager;
import com.ar.myfirstapp.view.fragments.BaseFragment;
import com.ar.myfirstapp.view.fragments.DashFragment;
import com.ar.myfirstapp.view.fragments.FragmentFactory;
import com.ar.myfirstapp.view.fragments.LogFragment;
import com.ar.myfirstapp.view.fragments.OBDFragment;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.HashMap;
import java.util.Queue;
import java.util.TreeMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements DeviceResponseHandler.DeviceResponseListener {

    private static final String TAG = "MainActivity";

    private DeviceManager deviceManager;

    private InfiniteViewPager viewPager;
    private Button buttonConnect;
    private TextView textViewTitle;
    private CircleIndicator circleIndicator;

    private BroadcastReceiver bluetoothStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(intent.getAction())) {
                if (!Utils.isBluetoothEnabled(MainActivity.this)) {
                    //buttonScan.setActivated(false);
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, Utils.BT_INT_REQ);
                } else {
                    //buttonScan.setActivated(true);
                }
            }
        }
    };

    void startConnection() {
        String deviceAddress = deviceManager.getELM327Address("OBDII");
        if (!TextUtils.isEmpty(deviceAddress)) {
            BluetoothDevice device = deviceManager.getBluetoothAdapter().getRemoteDevice(deviceAddress);
            deviceManager.connect(device);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        deviceManager.terminateConnection();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        bluetoothStateReceiver.onReceive(this, new Intent(BluetoothAdapter.ACTION_STATE_CHANGED));

        if (deviceManager == null) {
            deviceManager = DeviceManager.getInstance();
            deviceManager.addResponseHandler(new DeviceResponseHandler(this));
            deviceManager.initialize();
            startConnection();
        }
    }

    private void initUI() {
        viewPager = (InfiniteViewPager) findViewById(R.id.viewPager);
        textViewTitle = (TextView) findViewById(R.id.textViewTitle);
        buttonConnect = (Button) findViewById(R.id.buttonConnect);
        circleIndicator = (CircleIndicator) findViewById(R.id.circleIndicator);

        ScreenSlidePagerAdapter pagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        circleIndicator.setViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                textViewTitle.setText(FragmentFactory.getTitle()[position]);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setCurrentItem(FragmentFactory.getLastIndex());

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startConnection();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(bluetoothStateReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(bluetoothStateReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Utils.BT_INT_REQ:
                if (resultCode == Activity.RESULT_OK)
                    bluetoothStateReceiver.onReceive(this, new Intent(BluetoothAdapter.ACTION_STATE_CHANGED));
                else {
                    Toast.makeText(MainActivity.this, R.string.error_bluetooth, Toast.LENGTH_SHORT).show();
                }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void fireTasks() {
        try {
            for (Command proto : AtCommands.protoIter) {
                for (Command initC : AtCommands.initCommands)
                    deviceManager.send(initC);
                deviceManager.send(proto);
                for (String str : ModeFactory.getSupportedModes()) {
                    for (Command c : ModeFactory.getDiscoveryCommand(str)) {
                        if (c == null) continue;
                        deviceManager.send(c);
                    }
                }
            }

        } catch (Exception e) {
            Log.e("MActivity", "NPE", e);
        }
    }

    @Override
    public void onStateChanged(int state) {
        buttonConnect.setVisibility(state == DeviceManager.BLUETOOTH_STATE.NONE ? View.VISIBLE : View.GONE);
        if (state == DeviceManager.BLUETOOTH_STATE.CONNECTING) {
            Toast.makeText(this, "Connecting", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(String connectedDeviceName) {
        Toast.makeText(this, "Connected to " + connectedDeviceName, Toast.LENGTH_SHORT).show();
        fireTasks();
    }

    @Override
    public void onWriteCommand(Command command) {

    }

    @Override
    public void onReadCommand(final Command command) {
        if (command.getCommandType() == Command.CommandType.MODEX_DIS) {
            Command[] commands = ModeFactory.getSupportedPidCommands(command);
            if (commands != null) {
                for (Command c : commands)
                    if (c != null)
                        DeviceManager.getInstance().send(c);
            }
        }
        try {
            int index = (Integer.parseInt(command.getCommandId(), 16)) - 1;
            int pId = Integer.parseInt(command.getPid(), 16);
            sendNotification(index, pId);
        } catch (NumberFormatException ignored) {
        }
    }

    /**
     * Sends notification to all fragments that a new command request has received
     *
     * @param index Command mode
     * @param pId   pid of command
     */
    private void sendNotification(int index, int pId) {
        Intent intent = new Intent(Constants.TAG_NOTIFICATION_REFRESH);
        intent.putExtra(Constants.TAG_NOTIFICATION_COMMAND_INDEX, index);
        intent.putExtra(Constants.TAG_NOTIFICATION_COMMAND_PID, pId);
        sendBroadcast(intent);
    }

    @Override
    public void onNotification(String notificationText) {
        Toast.makeText(this, notificationText, Toast.LENGTH_SHORT).show();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            position = InfiniteViewPager.toRealPosition(position, getCount());
            BaseFragment fragment;
            if (position == 0) {
                fragment = new DashFragment();
            } else if (position == FragmentFactory.getLastIndex()) {
                fragment = new LogFragment();
            } else {
                fragment = new OBDFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                fragment.setArguments(bundle);
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return FragmentFactory.getLength();
        }
    }
}