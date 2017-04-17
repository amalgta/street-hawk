package com.ar.myfirstapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by amal.george on 11-04-2017
 */

public class DataStorage {
    public static final String TAG_DASH_CONFIG = "DASH_CONFIG";
    public static final String DELIMITER_COLON = ":";
    public static final String DELIMITER_COMMA = ",";

    private Context context;

    public DataStorage(Context context) {
        this.context = context;
    }

    public String getData(String key) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(key, "");
    }

    public void setData(String key, String data) {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key, data);
        editor.apply();
    }
}
