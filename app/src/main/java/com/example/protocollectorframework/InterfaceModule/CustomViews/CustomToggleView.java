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


    public boolean isChecked(){
        return isChecked;
    }

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

    public void deselected(){
        isSelected = false;
        positive.setBackground(getResources().getDrawable(R.drawable.custom_toggle_positive));
        positive.setTextColor(Color.BLACK);
        negative.setBackground(getResources().getDrawable(R.drawable.custom_toggle_negative));
        negative.setTextColor(Color.BLACK);
    }

    public boolean isSelected(){
        return isSelected;
    }

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

}
