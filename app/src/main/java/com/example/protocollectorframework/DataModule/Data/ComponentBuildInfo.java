package com.example.protocollectorframework.DataModule.Data;

import java.io.Serializable;

public class ComponentBuildInfo implements Serializable {

    private int type;
    private String label;
    private int min;
    private int max;
    private String value_type;
    private String[] finalValues;
    private String temporalType;
    private String units;
    private String observation_name;
    private String protocol;
    private boolean unique;

    private String[] first_values;
    private String[] last_values;

    public ComponentBuildInfo(int type, String units, String label, String value_type, int min, int max, String[] finalValues, String temporalType, String observation, String protocol, String[] first_values, String[] last_values, boolean unique) {
        this.type = type;
        this.label = label;
        this.value_type = value_type;
        this.min = min;
        this.max = max;
        this.finalValues = finalValues;
        this.temporalType = temporalType;
        this.units = units;
        this.observation_name = observation;
        this.protocol = protocol;
        this.first_values = first_values;
        this.last_values = last_values;
        this.unique = unique;
    }

    public boolean getUnique(){
        return unique;
    }

    public String getValue_type() {
        return value_type;
    }

    public void setValue_type(String value_type) {
        this.value_type = value_type;
    }

    public String[] getFirst_values() {
        return first_values;
    }

    public void setFirst_values(String[] first_values) {
        this.first_values = first_values;
    }

    public String[] getLast_values() {
        return last_values;
    }

    public void setLast_values(String[] last_values) {
        this.last_values = last_values;
    }

    public String getObservation_name() {
        return observation_name;
    }

    public void setObservation_name(String observation_name) {
        this.observation_name = observation_name;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setFinalValues(String[] finalValues) {
        this.finalValues = finalValues;
    }

    public void setTemporalType(String temporalType) {
        this.temporalType = temporalType;
    }

    public int getType() {
        return type;
    }

    public String getLabel() {
        return label;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public String[] getFinalValues() {
        return finalValues;
    }

    public String getTemporalType() {
        return temporalType;
    }
}
