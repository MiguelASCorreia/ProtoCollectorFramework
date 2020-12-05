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

import java.util.Calendar;

public class CustomDatePicker extends FrameLayout {

    private NumberPicker dayOfMonthPicker;
    private NumberPicker monthPicker;
    private NumberPicker yearPicker;

    private boolean callOnClick = true;

    public CustomDatePicker(Context context) {
        super(context);
        initView();
    }

    public CustomDatePicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomDatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomDatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View v = inflate(getContext(), R.layout.custom_date_picker, this);
        dayOfMonthPicker = v.findViewById(R.id.day_picker);
        monthPicker = v.findViewById(R.id.month_picker);
        yearPicker = v.findViewById(R.id.year_picker);

        dayOfMonthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(callOnClick)
                    callOnClick();
            }
        });

        monthPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDay(dayOfMonthPicker.getValue(),newVal,yearPicker.getValue());

                callOnClick();

            }
        });
        yearPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setDay(dayOfMonthPicker.getValue(),monthPicker.getValue(),newVal);

                callOnClick();
            }
        });

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(1);
        monthPicker.setValue(month);

        yearPicker.setMaxValue(2030);
        yearPicker.setMinValue(1970);
        yearPicker.setValue(year);

        setDay(day,month,year);


    }



    private void setDay( int day, int month, int year){
        callOnClick = false;

        dayOfMonthPicker.setMinValue(1);
        if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12)
            dayOfMonthPicker.setMaxValue(31);
        else if(month == 2){
            if((year % 4 == 0) && (year % 100 != 0))
                dayOfMonthPicker.setMaxValue(29);
            else
                dayOfMonthPicker.setMaxValue(28);
        } else
            dayOfMonthPicker.setMaxValue(30);


        if(day <= dayOfMonthPicker.getMaxValue())
            dayOfMonthPicker.setValue(day);
        else
            dayOfMonthPicker.setValue(1);

        callOnClick = true;

    }

    public int getDayOfMonth(){
        return dayOfMonthPicker.getValue();
    }
    public int getMonth(){
        return monthPicker.getValue();
    }
    public int getYear(){
        return yearPicker.getValue();
    }

    public void setDate(int year, int month, int dayOfMonth){
        yearPicker.setValue(year);
        monthPicker.setValue(month);
        dayOfMonthPicker.setValue(dayOfMonth);
    }

    public String getDate(){
        return dayOfMonthPicker.getValue() + "/" + monthPicker.getValue() + "/" + yearPicker.getValue();
    }

}
