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

/**
 * Custom view for the interval picker used on the interval component
 */
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

    /**
     * Initialize the layout that contains the interval picker
     */
    private void initView() {
        View v = inflate(getContext(), R.layout.custom_interval_picker, this);
        firstPicker = v.findViewById(R.id.first_value);
        secondPicker = v.findViewById(R.id.second_value);
    }

    /**
     * Sets the possible values for each interval limit
     * @param firstValues: domain of values for the left limit
     * @param lastValues: domain of values for the right limite
     */
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

    /**
     * Sets the possible values for each interval limit
     * @param firstValues: domain of values for both limits
     */
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

    /**
     * Sets the on value changed listener for each spinner
     */
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

    /**
     * Check if the value was changed by the user
     * @return true if changed, false otherwise
     */
    public boolean isValueChanged(){
        return valueChanged;
    }

    /**
     * Returns the domain of the left limit
     * @return domain of the left limit
     */
    public String[] getFirstValues(){
        return firstPicker.getDisplayedValues();
    }

    /**
     * Returns the domain of the right limit
     * @return domain of the right limit
     */
    public String[] getSecondValues(){
        return secondPicker.getDisplayedValues();
    }

    /**
     * Returns the current index of the left value
     * @return index of the left limit value
     */
    public int getFirstValue(){
        return firstPicker.getValue();
    }

    /**
     * Returns the current index of the right value
     * @return index of the right limit value
     */
    public int getSecondValue(){
        return secondPicker.getValue();
    }

    /**
     * Sets the left value
     * @param value left value
     */
    public void setFirstValue(int value){
        firstPicker.setValue(value);
    }

    /**
     * Sets the right value
     * @param value right value
     */
    public void setSecondValue(int value){
        secondPicker.setValue(value);
    }

    /**
     * Returns the interval value (left and right values divided by '/')
     * @return interval value
     */
    public String getRawValue(){
        return getStringValueFromFirst() + "/" + getStringValueFromSecond();
    }

    /**
     * Returns the interval value. If both limits are equal, only the left only is returned, otherwise the left and right values are joined, divided by '/'
     * @return interval value
     */
    public String getProcessedValue(){
        if(getFirstValues()[getFirstValue()-1].equals(getSecondValues()[getSecondValue()-1]))
            return getStringValueFromFirst();
        else
            return getRawValue();
    }

    /**
     * Returns the left value
     * @return left value
     */
    public String getStringValueFromFirst(){
        return getFirstValues()[getFirstValue()-1];
    }

    /**
     * Returns the right value
     * @return right value
     */
    public String getStringValueFromSecond(){
        return getSecondValues()[getSecondValue()-1];
    }

    /**
     * Sets each limit value
     * @param first: left value
     * @param second: right value
     */
    public void setValues(String first, String second){
        try{

            List<String> efs = Arrays.asList(getFirstValues());
            int index = efs.indexOf(first);
            if(index != -1)
                setFirstValue(index + 1);

        }catch (Exception e){
            e.printStackTrace();
        }

        try{
            List<String> efs = Arrays.asList(getSecondValues());
            int index = efs.indexOf(second);
            if(index != -1)
                setSecondValue(index + 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
