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

import java.util.Arrays;
import java.util.List;

public class CustomIntervalPicker extends FrameLayout {

    private NumberPicker firstPicker;
    private NumberPicker secondPicker;
    private boolean valueChanged = false;

    public CustomIntervalPicker(Context context) {
        super(context);
        initView();
    }

    public CustomIntervalPicker(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public CustomIntervalPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomIntervalPicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    private void initView() {
        View v = inflate(getContext(), R.layout.custom_interval_picker, this);
        firstPicker = v.findViewById(R.id.first_value);
        secondPicker = v.findViewById(R.id.second_value);
    }

    public void setValues(String[] firstValues, String[] lastValues){
        firstPicker.setMinValue(1);
        firstPicker.setMaxValue(firstValues.length);
        firstPicker.setDisplayedValues(firstValues);
        firstPicker.setWrapSelectorWheel(false);

        if(lastValues != null) {
            secondPicker.setMinValue(1);
            secondPicker.setMaxValue(lastValues.length);
            secondPicker.setDisplayedValues(lastValues);
            secondPicker.setWrapSelectorWheel(false);
        }else{
            secondPicker.setMinValue(1);
            secondPicker.setMaxValue(firstValues.length);
            secondPicker.setDisplayedValues(firstValues);
            secondPicker.setWrapSelectorWheel(false);
        }

        setOnChange();
    }

    public void setValues(String[] firstValues){
        firstPicker.setMinValue(1);
        firstPicker.setMaxValue(firstValues.length);
        firstPicker.setDisplayedValues(firstValues);
        firstPicker.setWrapSelectorWheel(false);

        secondPicker.setMinValue(1);
        secondPicker.setMaxValue(firstValues.length);
        secondPicker.setDisplayedValues(firstValues);
        secondPicker.setWrapSelectorWheel(false);

        setOnChange();
    }

    private void setOnChange(){
        firstPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(!valueChanged)
                    valueChanged = true;
                secondPicker.setValue(newVal);

                CustomIntervalPicker.this.callOnClick();

            }
        });

        secondPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(!valueChanged)
                    valueChanged = true;
                if(firstPicker.getValue() == 1 || firstPicker.getValue() > newVal)
                    firstPicker.setValue(newVal);


                CustomIntervalPicker.this.callOnClick();

            }
        });

    }

    public boolean isValueChanged(){
        return valueChanged;
    }

    public String[] getFirstValues(){
        return firstPicker.getDisplayedValues();
    }

    public String[] getSecondValues(){
        return secondPicker.getDisplayedValues();
    }

    public int getFirstValue(){
        return firstPicker.getValue();
    }

    public int getSecondValue(){
        return secondPicker.getValue();
    }

    public void setFirstValue(int value){
        firstPicker.setValue(value);
    }

    public void setSecondValue(int value){
        secondPicker.setValue(value);
    }

    public String getRawValue(){
        return getStringValueFromFirst() + "/" + getStringValueFromSecond();
    }
    public String getProcessedValue(){
        if(getFirstValues()[getFirstValue()-1].equals(getSecondValues()[getSecondValue()-1]))
            return getStringValueFromFirst();
        else
            return getRawValue();
    }

    public String getStringValueFromFirst(){
        return getFirstValues()[getFirstValue()-1];
    }

    public String getStringValueFromSecond(){
        return getSecondValues()[getSecondValue()-1];
    }

    public void setValues(String first, String second){
        try{

            List<String> efs = Arrays.asList(getFirstValues());
            int index = efs.indexOf(first);
            if(index != -1)
                setFirstValue(index + 1);

        }catch (Exception e){
            //first value error
            e.printStackTrace();
        }

        try{
            List<String> efs = Arrays.asList(getSecondValues());
            int index = efs.indexOf(second);
            if(index != -1)
                setSecondValue(index + 1);
        }catch (Exception e){
            //second value error
            e.printStackTrace();
        }
    }


}
