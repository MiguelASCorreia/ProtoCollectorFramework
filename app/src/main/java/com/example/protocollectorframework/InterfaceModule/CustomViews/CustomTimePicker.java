package com.example.protocollectorframework.InterfaceModule.CustomViews;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.protocollectorframework.R;
import com.shawnlin.numberpicker.NumberPicker;

public class CustomTimePicker extends FrameLayout {

    private NumberPicker hoursPicker;
    private NumberPicker minutesPicker;
    private NumberPicker secondsPicker;

    private boolean callOnClick = true;

    public CustomTimePicker(Context context) {
        super(context);
        initView();
    }

    public CustomTimePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomTimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomTimePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View v = inflate(getContext(), R.layout.custom_time_picker, this);
        hoursPicker = v.findViewById(R.id.hour_picker);
        minutesPicker = v.findViewById(R.id.minutes_picker);
        secondsPicker = v.findViewById(R.id.seconds_picker);

        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(callOnClick)
                    callOnClick();
            }
        });

        minutesPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                callOnClick();

            }
        });
        secondsPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                callOnClick();
            }
        });


        hoursPicker.setMaxValue(23);
        hoursPicker.setMinValue(0);
        hoursPicker.setValue(0);

        minutesPicker.setMaxValue(59);
        minutesPicker.setMinValue(0);
        minutesPicker.setValue(0);

        secondsPicker.setMaxValue(59);
        secondsPicker.setMinValue(0);
        secondsPicker.setValue(0);

    }


    public int getHours(){
        return hoursPicker.getValue();
    }
    public int getMinutes(){
        return minutesPicker.getValue();
    }
    public int getSeconds(){
        return secondsPicker.getValue();
    }

    public void setTime(int hours, int minutes, int seconds){
        this.hoursPicker.setValue(hours);
        this.minutesPicker.setValue(minutes);
        this.secondsPicker.setValue(seconds);
    }

    public String getTime(){
        return hoursPicker.getValue() + ":" + minutesPicker.getValue() + ":" + secondsPicker.getValue();
    }

}
