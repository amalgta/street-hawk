package com.ar.myfirstapp.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.ar.myfirstapp.obd2.Command;
import com.ar.myfirstapp.utils.DashUtils;
import com.ar.myfirstapp.view.custom.view.OBDGenericView;

import java.util.List;

/**
 * Created by amal.george on 12-04-2017
 */

public class DashItemAdapter extends RecyclerView.Adapter<DashItemAdapter.DashViewHolder> {
    private List<Command> commandList;
    private Context context;

    public DashItemAdapter(List<Command> commandList, Context context) {
        this.commandList = commandList;
        this.context = context;
    }

    public void add(Command commandCode) {
        if (commandList.contains(commandCode)) {
            commandList.set(commandList.indexOf(commandCode), commandCode);
            int position = commandList.indexOf(commandCode);
            notifyItemChanged(position);
        } else {
            commandList.add(commandCode);
            int position = commandList.indexOf(commandCode);
            notifyItemInserted(position);
        }
    }

    public void remove(Command commandCode) {
        if (!commandList.contains(commandCode)) return;
        int position = commandList.indexOf(commandCode);
        commandList.remove(commandCode);
        notifyItemRemoved(position);
    }

    @Override
    public DashViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        DashViewHolder viewHolder;
        FrameLayout frameLayout = new FrameLayout(parent.getContext());
        frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        switch (viewType) {
            default:
                viewHolder = new DashViewHolder(frameLayout);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(DashViewHolder holder, int position) {
        holder.obdGenericDial.display(commandList.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        return DashUtils.getDashType(commandList.get(position));
    }

    @Override
    public int getItemCount() {
        return commandList.size();
    }

    class DashViewHolder extends RecyclerView.ViewHolder {
        OBDGenericView obdGenericDial;

        DashViewHolder(FrameLayout itemView) {
            super(itemView);
            obdGenericDial = new OBDGenericView(itemView.getContext());
            obdGenericDial.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            itemView.addView(obdGenericDial);
        }
    }
}
