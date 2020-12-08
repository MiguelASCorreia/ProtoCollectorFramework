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

/**
 * Custom view for the custom date picker used on the temporal component
 */
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

    /**
     * Initialize the layout that contains the date picker
     */
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


    /**
     * Sets each spinner value
     * @param day: day of the month
     * @param month: month
     * @param year: year
     */
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

    /**
     * Returns the current value on the day of the month spinner
     * @return day of the month
     */
    public int getDayOfMonth(){
        return dayOfMonthPicker.getValue();
    }

    /**
     * Returns the current value on the month spinner
     * @return month
     */
    public int getMonth(){
        return monthPicker.getValue();
    }

    /**
     * Returns the current value on the year spinner
     * @return year
     */
    public int getYear(){
        return yearPicker.getValue();
    }

    /**
     * Sets the date given the year, month and day of the month
     * @param year: year
     * @param month: month
     * @param dayOfMonth: day of the month
     */
    public void setDate(int year, int month, int dayOfMonth){
        yearPicker.setValue(year);
        monthPicker.setValue(month);
        dayOfMonthPicker.setValue(dayOfMonth);
    }

    /**
     * Returns the date shown on the view on the format dd/MM/yyyy
     * @return date on the format dd/mm/yyyy
     */
    public String getDate(){
        return dayOfMonthPicker.getValue() + "/" + monthPicker.getValue() + "/" + yearPicker.getValue();
    }

}
