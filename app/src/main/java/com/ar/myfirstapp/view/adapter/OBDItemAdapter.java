package com.ar.myfirstapp.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.ar.myfirstapp.R;
import com.ar.myfirstapp.obd2.Command;
import com.ar.myfirstapp.utils.DataStorage;
import com.ar.myfirstapp.utils.DashUtils;

import java.util.ArrayList;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

/**
 * Created by amal.george on 24-03-2017
 */

public class OBDItemAdapter extends RecyclerView.Adapter<OBDItemAdapter.ViewHolder> {
    private Map<Integer, Command> commands;
    private List<Integer> keyList;
    private Context context;

    public OBDItemAdapter(Map<Integer, Command> commands, Context context) {
        if (commands != null) {
            this.commands = commands;
            this.keyList = new ArrayList<>(commands.keySet());
        } else {
            this.commands = new TreeMap<>();
            this.keyList = new ArrayList<>();
        }
        this.context = context;
    }


    public void add(Command command) {
        int key = Integer.parseInt(command.getPid(), 16);
        commands.put(key, command);
        if (keyList.contains(key)) {
        } else {
            keyList = new ArrayList<>(commands.keySet());
            notifyItemInserted(keyList.indexOf(key));
        }
    }

    @Override
    public OBDItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_obd_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final OBDItemAdapter.ViewHolder holder, int position) {
        final Command command = commands.get(keyList.get(position));
        holder.textViewOBDKey.setText(command.getName());
        holder.textViewOBDValue.setText(command.toString());
        holder.checkboxIsInDash.setChecked(DashUtils.isInDash(command, context));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkboxIsInDash.setChecked(!holder.checkboxIsInDash.isChecked());
            }
        });
        /*
        holder.itemView.setOnTouchListener(new RepeatListener(0, Constants.ELMTimeDelay, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeviceManager.getInstance().send(command, new RequestResponseListener() {
                    @Override
                    public void onResponseReceived(Command command) {
                        holder.textViewOBDKey.setText(command.getName());
                        holder.textViewOBDValue.setText(command.toString());
                    }
                });
            }
        }));
        */
        holder.checkboxIsInDash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DashUtils.addToDash(command, context);
                } else {
                    DashUtils.removeFromDash(command, context);
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return commands.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewOBDKey;
        TextView textViewOBDValue;
        CheckBox checkboxIsInDash;

        ViewHolder(View itemView) {
            super(itemView);
            textViewOBDKey = (TextView) itemView.findViewById(R.id.textViewOBDKey);
            textViewOBDValue = (TextView) itemView.findViewById(R.id.textViewOBDValue);
            checkboxIsInDash = (CheckBox) itemView.findViewById(R.id.checkboxIsInDash);
        }
    }


}
