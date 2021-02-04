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


/**
 * Custom view for the custom datetime picker used on the temporal component
 */
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

    /**
     * Initialize the layout that contains the datetime picker
     */
    private void initView() {
        View v = inflate(getContext(), R.layout.custom_time_picker, this);
        hoursPicker = v.findViewById(R.id.hour_picker);
        minutesPicker = v.findViewById(R.id.minutes_picker);
        secondsPicker = v.findViewById(R.id.seconds_picker);

        hoursPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (callOnClick)
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


    /**
     * Returns the hours spinner value
     *
     * @return hours
     */
    public int getHours() {
        return hoursPicker.getValue();
    }

    /**
     * Returns the minutes spinner value
     *
     * @return minutes
     */
    public int getMinutes() {
        return minutesPicker.getValue();
    }

    /**
     * Returns the seconds spinner value
     *
     * @return seconds
     */
    public int getSeconds() {
        return secondsPicker.getValue();
    }

    /**
     * Sets each spinner value given the hour, minutes and seconds
     *
     * @param hours:   hours
     * @param minutes: minutes
     * @param seconds: seconds
     */
    public void setTime(int hours, int minutes, int seconds) {
        this.hoursPicker.setValue(hours);
        this.minutesPicker.setValue(minutes);
        this.secondsPicker.setValue(seconds);
    }

    /**
     * Returns the datetime in the format hh:mm:ss
     *
     * @return datetime in the format hh:mm:ss
     */
    public String getTime() {
        return hoursPicker.getValue() + ":" + minutesPicker.getValue() + ":" + secondsPicker.getValue();
    }

}
