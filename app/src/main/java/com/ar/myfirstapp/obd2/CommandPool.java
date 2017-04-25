package com.ar.myfirstapp.obd2;

import com.ar.myfirstapp.utils.Constants;
import com.ar.myfirstapp.utils.Logger;
import com.ar.myfirstapp.view.fragments.FragmentFactory;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

/**
 * Created by amal.george on 25-04-2017
 */

public class CommandPool {
    private static final int LOG_HISTORY_SIZE = 10;

    private static final String TAG = "CommandPool";

    private Map<Integer, Command>[] commandPool = new TreeMap[FragmentFactory.getLastIndex()];
    private Queue<Command> poolLog = new CircularFifoQueue<>(LOG_HISTORY_SIZE);

    public void push(Command command) {
        try {
            int index = (Integer.parseInt(command.getCommandId(), 16)) - 1;
            if (commandPool[index] == null) commandPool[index] = new TreeMap<>();
            int pId = Integer.parseInt(command.getPid(), 16);
            commandPool[index].put(pId, command);
        } catch (Exception e) {
            Logger.e(TAG, e.toString());
        } finally {
            poolLog.add(command);
        }
    }


    /**
     * Instance loader
     */
    private static class InstanceLoader {
        static CommandPool INSTANCE = new CommandPool();
    }

    public static CommandPool getInstance() {
        return CommandPool.InstanceLoader.INSTANCE;
    }

    private CommandPool() {

    }

    public Map<Integer, Command> getCommands(int index) {
        if (commandPool[index] == null) commandPool[index] = new TreeMap<>();
        return commandPool[index];
    }

    public Map<Integer, Command>[] getCommands() {
        return commandPool;
    }

    public Queue<Command> getLog() {
        return poolLog;
    }

}
