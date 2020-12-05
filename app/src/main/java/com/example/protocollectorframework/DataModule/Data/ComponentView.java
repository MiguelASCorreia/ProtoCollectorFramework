package com.example.protocollectorframework.DataModule.Data;

import android.view.View;

import java.io.Serializable;


public class ComponentView implements Serializable {

    private int type;
    private String units;
    private View view;


    public ComponentView(int type, View view) {
        this.type = type;
        this.view = view;
    }

    public ComponentView(int type, View view, String units) {
        this.type = type;
        this.view = view;
        this.units = units;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getType(){
        return type;
    }

    public void setType(int type){
         this.type = type;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

}
