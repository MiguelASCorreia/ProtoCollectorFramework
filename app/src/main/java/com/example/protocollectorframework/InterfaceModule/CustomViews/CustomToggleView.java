package com.example.protocollectorframework.InterfaceModule.CustomViews;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.protocollectorframework.R;

/**
 * Custom view for the custom toggle used on the boolean component
 */
public class CustomToggleView extends FrameLayout {

    private boolean isChecked;
    private boolean isSelected;
    private Button positive;
    private Button negative;

    public CustomToggleView(Context context) {
        super(context);
        initView();
    }

    public CustomToggleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public CustomToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CustomToggleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView();
    }

    /**
     * Initialize the layout that contains the buttons for the boolean component
     */
    private void initView() {
        View v = inflate(getContext(), R.layout.custom_toggle, this);
        positive = v.findViewById(R.id.positive_button);
        negative = v.findViewById(R.id.negative_button);
        isChecked = false;
        isSelected = true;
        positive.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isChecked || !isSelected){
                    positive.setBackground(getResources().getDrawable(R.drawable.custom_toggle_positive_selected));
                    positive.setTextColor(Color.WHITE);
                    negative.setBackground(getResources().getDrawable(R.drawable.custom_toggle_negative));
                    negative.setTextColor(Color.BLACK);
                    isChecked = true;
                }
                isSelected = true;

                CustomToggleView.this.callOnClick();

            }
        });

        negative.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isChecked || !isSelected){
                    negative.setBackground(getResources().getDrawable(R.drawable.custom_toggle_negative_selected));
                    negative.setTextColor(Color.WHITE);
                    positive.setBackground(getResources().getDrawable(R.drawable.custom_toggle_positive));
                    positive.setTextColor(Color.BLACK);
                    isChecked = false;
                }
                isSelected = true;

                CustomToggleView.this.callOnClick();

            }
        });
    }


    /**
     * Check the current value of the component
     * @return true if positive option selected, false otherwise
     */
    public boolean isChecked(){
        return isChecked;
    }

    /**
     * Change the selected button based on the argument
     * @param checked: selection
     */
    public void setChecked(boolean checked){
        if(checked){
            positive.setBackground(getResources().getDrawable(R.drawable.custom_toggle_positive_selected));
            positive.setTextColor(Color.WHITE);
            negative.setBackground(getResources().getDrawable(R.drawable.custom_toggle_negative));
            negative.setTextColor(Color.BLACK);
        }else{
            negative.setBackground(getResources().getDrawable(R.drawable.custom_toggle_negative_selected));
            negative.setTextColor(Color.WHITE);
            positive.setBackground(getResources().getDrawable(R.drawable.custom_toggle_positive));
            positive.setTextColor(Color.BLACK);
        }
        isChecked = checked;
        isSelected = true;
    }

    /**
     * Deselected the view, disabling both buttons
     */
    public void deselected(){
        isSelected = false;
        positive.setBackground(getResources().getDrawable(R.drawable.custom_toggle_positive));
        positive.setTextColor(Color.BLACK);
        negative.setBackground(getResources().getDrawable(R.drawable.custom_toggle_negative));
        negative.setTextColor(Color.BLACK);
    }

    /**
     * Checks if the user selected any of the buttons
     * @return true if the user selected any one of the buttons, false otherwise
     */
    public boolean isSelected(){
        return isSelected;
    }


}
