package com.ar.myfirstapp.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ar.myfirstapp.obd2.saej1979.ModeFactory;
import com.ar.myfirstapp.view.MainActivity;
import com.ar.myfirstapp.R;
import com.ar.myfirstapp.obd2.Command;
import com.ar.myfirstapp.obd2.parser.Parser;
import com.ar.myfirstapp.obd2.saej1979.Mode1;
import com.ar.myfirstapp.bt.DeviceManager;

import java.util.List;
import java.util.Map;

/**
 * Created by amal.george on 24-03-2017
 */

public class LogFragment extends BaseFragment {
    private Button buttonSend;
    private EditText editTextInput;
    private TextView textViewLog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_log, container, false);
        initUI();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    @Override
    protected void loadData() {
        textViewLog.setText("");
        List<Command> commands = ((MainActivity) getActivity()).getCommandLog();
        for (Command command : commands) {
            textViewLog.append(command.toString());
        }
    }

    @Override
    protected void updateData(int[] commandCode) {
        List<Command> commands = ((MainActivity) getActivity()).getCommandLog();
        textViewLog.append(commands.get(commands.size() - 1).toString());
    }

    private void initUI() {
        editTextInput = (EditText) rootView.findViewById(R.id.editTextInput);
        textViewLog = (TextView) rootView.findViewById(R.id.textViewLog);
        buttonSend = (Button) rootView.findViewById(R.id.buttonSend);

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = editTextInput.getText().toString();
                if (!TextUtils.isEmpty(message)) {
                    if (message.startsWith("m")) {
                        String args[] = message.split(" ");
                        Command c = ModeFactory.getCommand(
                                args[0].replace("m", ""),
                                args[1]);
                        if (c != null)
                            DeviceManager.getInstance().send(c);
                        return;
                    }
                    DeviceManager.getInstance().send(new Command("", message + "\r", "", new Parser() {
                        @Override
                        public void parse(Command command) {
                            byte[] rawResp = command.getRawResp();
                            StringBuilder sb = new StringBuilder();
                            if (rawResp != null) {
                                for (byte aByte : rawResp) {
                                    sb.append(aByte).append(' ');
                                }
                            }
                            command.setResult(sb.toString());
                        }
                    }));
                    editTextInput.setText(message.split(" ")[0]);
                }
            }
        });
    }
}
