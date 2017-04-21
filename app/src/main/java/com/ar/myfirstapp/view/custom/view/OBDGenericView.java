package com.ar.myfirstapp.view.custom.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ar.myfirstapp.R;
import com.ar.myfirstapp.obd2.Command;

/**
 * Created by amal.george on 29-03-2017
 */

public class OBDGenericView extends RelativeLayout implements OBDView {
    View rootView;
    TextView textViewLabel, textViewValue;

    public OBDGenericView(Context context) {
        super(context);
        initialize(context);
    }

    public OBDGenericView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public OBDGenericView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context);
    }

    private void initialize(Context context) {
        rootView = inflate(context, R.layout.item_dash_view_generic, this);
        textViewLabel = (TextView) rootView.findViewById(R.id.textViewLabel);
        textViewValue = (TextView) rootView.findViewById(R.id.textViewValue);
    }

    @Override
    public void display(Command command) {
        textViewLabel.setText(command.getName());
        textViewValue.setText(command.toString());
    }
}
