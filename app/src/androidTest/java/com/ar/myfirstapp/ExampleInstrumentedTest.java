package com.ar.myfirstapp;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.ar.myfirstapp.obd2.Command;
import com.ar.myfirstapp.obd2.parser.Parser;
import com.ar.myfirstapp.utils.DashUtils;
import com.ar.myfirstapp.utils.DataStorage;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static String TAG = "AndroidTEST";

    public Command getACommand(int modeID, int pId, String name) {
        return new Command(String.valueOf(modeID), String.valueOf(pId), name, new Parser() {
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
        });

    }

    @Test
    public void addTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DashUtils.resetDash(new DataStorage(appContext));

        Command a = getACommand(1, 0, "A");
        DashUtils.addToDash(a, appContext);
        Log.d(TAG, "ADD: " + new DataStorage(appContext).getData(DataStorage.TAG_DASH_CONFIG));
        assertEquals(true, DashUtils.isInDash(a, appContext));
    }

    @Test
    public void removeTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DashUtils.resetDash(new DataStorage(appContext));

        Command b = getACommand(2, 1, "B");
        DashUtils.addToDash(b, appContext);
        DashUtils.removeFromDash(b, appContext);
        Log.d(TAG, "REMOVE: " + new DataStorage(appContext).getData(DataStorage.TAG_DASH_CONFIG));
        assertEquals(false, DashUtils.isInDash(b, appContext));
    }

    @Test
    public void getAllTest() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DashUtils.resetDash(new DataStorage(appContext));

        DashUtils.addToDash(getACommand(5, 4, "A"), appContext);
        DashUtils.addToDash(getACommand(6, 5, "B"), appContext);
        DashUtils.addToDash(getACommand(7, 6, "C"), appContext);
        List<int[]> as = DashUtils.getAll(new DataStorage(appContext));

    }
}
