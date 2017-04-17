package com.ar.myfirstapp.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by amal.george on 12-04-2017
 */

public class DashItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<int[]> commandList;
    private Context context;

    public enum DASH_WIDGET_TYPE {SPEEDOMETER, TEXT}

    public DashItemAdapter(List<int[]> commandList, Context context) {
        this.commandList = commandList;
        this.context = context;
    }

    public void add(int[] commandCode) {
        if (commandList.contains(commandCode)) return;
        commandList.add(commandCode);
        int position = commandList.indexOf(commandCode);
        notifyItemInserted(position);
    }

    public void remove(int[] commandCode) {
        if (!commandList.contains(commandCode)) return;
        int position = commandList.indexOf(commandCode);
        commandList.remove(commandCode);
        notifyItemRemoved(position);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return commandList.size();
    }
}
