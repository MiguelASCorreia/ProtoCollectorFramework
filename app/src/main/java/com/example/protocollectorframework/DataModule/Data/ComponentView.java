package com.example.protocollectorframework.DataModule.Data;

import android.view.View;

import java.io.Serializable;

/**
 * Object used to save the views associated to the interface components
 */
public class ComponentView implements Serializable {

    private int type;
    private String units;
    private View view;


    /**
     * Constructor
     *
     * @param type: component type
     * @param view: component view
     */
    public ComponentView(int type, View view) {
        this.type = type;
        this.view = view;
    }

    /**
     * Constructor
     *
     * @param type:  component type
     * @param view:  component view
     * @param units: units of the value that the field allows
     */
    public ComponentView(int type, View view, String units) {
        this.type = type;
        this.view = view;
        this.units = units;
    }

    /**
     * Returns the units of the data associated to the field view
     *
     * @return field's units
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the units of the data associated to the field view
     *
     * @param units: field's units
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * Return the component type associated to the view
     *
     * @return component type identifier
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the component type
     *
     * @param type: component type identifier
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Returns the view associated to the component
     *
     * @return component view
     */
    public View getView() {
        return view;
    }

    /**
     * Sets the view associated to the component
     *
     * @param view: component view
     */
    public void setView(View view) {
        this.view = view;
    }

}
