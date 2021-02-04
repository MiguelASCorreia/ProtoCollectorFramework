package com.example.protocollectorframework.DataModule.Data;

import java.io.Serializable;

/**
 * Class used to store the information that is extracted from each field associated to an observation in the specification of a protocol
 */
public class ComponentBuildInfo implements Serializable {

    private int type;
    private int min;
    private int max;
    private boolean unique;
    private String label;
    private String value_type;
    private String temporalType;
    private String units;
    private String observation_name;
    private String protocol;
    private String[] countValues;
    private String[] first_values;
    private String[] last_values;

    /**
     * Constructor
     *
     * @param type:         component data type
     * @param units:        data units
     * @param label:        label of the component
     * @param value_type:   defines the type of numeric value (integer or real)
     * @param min:          minimum numeric acceptable value
     * @param max:          maximum numeric acceptable value
     * @param countValues:  acceptable values in a count
     * @param temporalType: temporal data type (date or datetime)
     * @param first_values: left domain of the interval component
     * @param last_values:  right domain of the interval component
     * @param unique:       uniqueness of the selection in the categorical component type
     * @param observation:  associated observation
     * @param protocol:     associated protocol
     */
    public ComponentBuildInfo(int type, String units, String label, String value_type, int min, int max, String[] countValues, String temporalType, String[] first_values, String[] last_values, boolean unique, String observation, String protocol) {
        this.type = type;
        this.label = label;
        this.value_type = value_type;
        this.min = min;
        this.max = max;
        this.countValues = countValues;
        this.temporalType = temporalType;
        this.units = units;
        this.observation_name = observation;
        this.protocol = protocol;
        this.first_values = first_values;
        this.last_values = last_values;
        this.unique = unique;
    }

    /**
     * Check if the selection is unique
     *
     * @return true if only one value can be selected at a given time, false otherwise
     */
    public boolean getUnique() {
        return unique;
    }

    /**
     * Sets if the selection is unique
     *
     * @param unique: true if only one value can be selected at a given time, false otherwise
     */
    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    /**
     * Returns the numeric value type
     *
     * @return numeric value type
     */
    public String getValue_type() {
        return value_type;
    }

    /**
     * Sets the numeric value type
     *
     * @param value_type: numeric value type
     */
    public void setValue_type(String value_type) {
        this.value_type = value_type;
    }

    /**
     * Returns the left domain of values of the interval
     *
     * @return left domain of the interval
     */
    public String[] getFirst_values() {
        return first_values;
    }

    /**
     * Sets the left domain of values of the interval
     *
     * @param first_values: left domain of the interval
     */
    public void setFirst_values(String[] first_values) {
        this.first_values = first_values;
    }

    /**
     * Returns the right domain of values of the interval
     *
     * @return left right of the interval
     */
    public String[] getLast_values() {
        return last_values;
    }

    /**
     * Sets the right domain of values of the interval
     *
     * @param last_values: right domain of the interval
     */
    public void setLast_values(String[] last_values) {
        this.last_values = last_values;
    }

    /**
     * Returns the observation name associated to the component
     *
     * @return observation's name
     */
    public String getObservation_name() {
        return observation_name;
    }

    /**
     * Sets the observation name of the component
     *
     * @param observation_name: observation's name
     */
    public void setObservation_name(String observation_name) {
        this.observation_name = observation_name;
    }

    /**
     * Returns the protocol associated to the component
     *
     * @return protocol's name
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Sets the protocol associated to the component
     *
     * @param protocol: protocol's name
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Returns the units of the value
     *
     * @return units
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the units of the value
     *
     * @param units: units
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * Returns the component's data type
     *
     * @return component data type
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the component's data type
     *
     * @param type: data type identifier
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Returns the component's label
     *
     * @return component's label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the component's label
     *
     * @param label: component's label
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the minimum acceptable value
     *
     * @return minimum acceptable value
     */
    public int getMin() {
        return min;
    }

    /**
     * Sets the minimum acceptable value
     *
     * @param min: minimum acceptable value
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * Returns the maximum acceptable value
     *
     * @return maximum acceptable value
     */
    public int getMax() {
        return max;
    }

    /**
     * Sets the maximum acceptable value
     *
     * @param max: maximum acceptable value
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * Returns the acceptable values in a count
     *
     * @return domain of acceptable values in a count
     */
    public String[] getCountValues() {
        return countValues;
    }

    /**
     * Sets the domain of acceptable values in a count
     *
     * @param countValues: domain of acceptable values in a count
     */
    public void setCountValues(String[] countValues) {
        this.countValues = countValues;
    }

    /**
     * Returns the temporal type of the temporal component
     *
     * @return temporal type (date or datetime)
     */
    public String getTemporalType() {
        return temporalType;
    }

    /**
     * Sets the temporal type of the temporal component
     *
     * @param temporalType: temporal type (date or datetime)
     */
    public void setTemporalType(String temporalType) {
        this.temporalType = temporalType;
    }


}
