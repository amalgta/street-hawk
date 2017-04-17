package com.ar.myfirstapp.utils;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ar.myfirstapp.obd2.Command;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by amal.george on 11-04-2017
 */

public class DashUtils {

    public static final class WIDGET_TYPE {
        public static final int TEXT = 1;
    }

    public static int getDashType(Command command) {
        //TODO Analyse the command to identify the type of widget
        return WIDGET_TYPE.TEXT;
    }

    //12,213:12,24:
    public static List<int[]> getAll(DataStorage dataStorage) {
        List<int[]> list = new LinkedList<>();
        String data = dataStorage.getData(DataStorage.TAG_DASH_CONFIG);
        if (!TextUtils.isEmpty(data)) {
            String[] dataArray = data.split(DataStorage.DELIMITER_COLON);
            for (String thisString : dataArray) {
                String[] content = thisString.split(DataStorage.DELIMITER_COMMA);
                list.add(new int[]{Integer.parseInt(content[0]), Integer.parseInt(content[1])});
            }
        }
        return list;
    }

    public static void resetDash(DataStorage dataStorage) {
        dataStorage.setData(DataStorage.TAG_DASH_CONFIG, "");
    }

    public static boolean isInDash(Command command, Context context) {
        String data = new DataStorage(context).getData(DataStorage.TAG_DASH_CONFIG);
        int[] commandReference = command.getUniqueReference();
        return data.contains(commandReference[0] + DataStorage.DELIMITER_COMMA + commandReference[1]);
    }

    public static void removeFromDash(Command command, Context context) {
        if (!isInDash(command, context)) return;
        String data = new DataStorage(context).getData(DataStorage.TAG_DASH_CONFIG);
        int[] commandReference = command.getUniqueReference();
        String currentCommand = (commandReference[0] + DataStorage.DELIMITER_COMMA + commandReference[1] + DataStorage.DELIMITER_COLON);
        data = data.replace(currentCommand, "");
        new DataStorage(context).setData(DataStorage.TAG_DASH_CONFIG, data);
        sendNotification(context, Constants.TAG_DASH_REMOVE, commandReference[0], commandReference[1]);
    }

    public static void addToDash(Command command, Context context) {
        if (isInDash(command, context)) return;
        String data = new DataStorage(context).getData(DataStorage.TAG_DASH_CONFIG);
        int[] commandReference = command.getUniqueReference();
        data = data.concat(commandReference[0] + DataStorage.DELIMITER_COMMA + commandReference[1] + DataStorage.DELIMITER_COLON);
        new DataStorage(context).setData(DataStorage.TAG_DASH_CONFIG, data);
        sendNotification(context, Constants.TAG_DASH_ADD, commandReference[0], commandReference[1]);
    }

    private static void sendNotification(Context context, String mode, int index, int pId) {
        Intent intent = new Intent(mode);
        intent.putExtra(Constants.TAG_NOTIFICATION_COMMAND_INDEX, index);
        intent.putExtra(Constants.TAG_NOTIFICATION_COMMAND_PID, pId);
        context.sendBroadcast(intent);
    }
}
