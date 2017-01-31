package com.nfx.android.rangebarpreference;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import static com.nfx.android.rangebarpreference.RangeBarHelper.formatFloatToString;

/**
 * NFX Development
 * Created by Nick on 21.01.16
 */
class CustomValueDialog {

    private final String TAG = getClass().getSimpleName();

    private Dialog dialog;
    private EditText customValueView;

    private float minValue;
    private float maxValue;
    private float currentValue;
    private PersistValueListener persistValueListener;

    CustomValueDialog(Context context, int theme, float minValue, float maxValue, float currentValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.currentValue = currentValue;

        init(new AlertDialog.Builder(context, theme));
    }

    private void init(AlertDialog.Builder dialogBuilder) {
        View dialogView = LayoutInflater.from(dialogBuilder.getContext()).inflate(R.layout.value_selector_dialog, null);
        dialog = dialogBuilder.setView(dialogView).create();

        TextView minValueView = (TextView) dialogView.findViewById(R.id.minValue);
        TextView maxValueView = (TextView) dialogView.findViewById(R.id.maxValue);
        customValueView = (EditText) dialogView.findViewById(R.id.customValue);

        minValueView.setText(formatFloatToString(minValue));
        maxValueView.setText(formatFloatToString(maxValue));
        customValueView.setHint(formatFloatToString(currentValue));

        LinearLayout colorView = (LinearLayout) dialogView.findViewById(R.id.dialog_color_area);
        colorView.setBackgroundColor(fetchAccentColor(dialogBuilder.getContext()));

        Button applyButton = (Button) dialogView.findViewById(R.id.btn_apply);
        Button cancelButton = (Button) dialogView.findViewById(R.id.btn_cancel);

        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryApply();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private int fetchAccentColor(Context context) {
        TypedValue typedValue = new TypedValue();

        TypedArray a = context.obtainStyledAttributes(typedValue.data, new int[] { R.attr.colorAccent });
        int color = a.getColor(0, 0);
        a.recycle();

        return color;
    }

    CustomValueDialog setPersistValueListener(PersistValueListener listener) {
        persistValueListener = listener;
        return this;
    }

    void show() {
        dialog.show();
    }

    private void tryApply() {
        float value;

        try {
            value = Float.parseFloat(customValueView.getText().toString());
            if (value > maxValue) {
                Log.e(TAG, "wrong input( > than required): " + customValueView.getText().toString());
                notifyWrongInput();
                return;
            }
            else if (value < minValue) {
                Log.e(TAG, "wrong input( < then required): " + customValueView.getText().toString());
                notifyWrongInput();
                return;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "worng input(non-integer): " + customValueView.getText().toString());
            notifyWrongInput();
            return;
        }

        if(persistValueListener != null) {
            persistValueListener.persistFloat(value);
            dialog.dismiss();
        }
    }

    private void notifyWrongInput() {
        customValueView.setText("");
        customValueView.setHint("Wrong Input!");
    }
}
