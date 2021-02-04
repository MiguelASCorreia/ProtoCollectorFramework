package com.example.protocollectorframework.DataModule.Data;

import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Object used to save the data associated to the interface components
 */
public class ComponentData implements Serializable {
    private int type;
    private String description;
    private String value;
    private String units;
    private SortedSet<String> owners;

    /**
     * Constructor
     *
     * @param type:        component type identifier
     * @param description: component description
     * @param value:       component value
     */
    public ComponentData(int type, String description, String value) {
        this.type = type;
        this.description = description;
        this.value = value;
        this.owners = new TreeSet<>();
    }

    /**
     * Constructor
     *
     * @param type:        component type identifier
     * @param description: component description
     * @param value:       component value
     * @param units:       value's units
     */
    public ComponentData(int type, String description, String value, String units) {
        this.type = type;
        this.description = description;
        this.value = value;
        this.units = units;
        this.owners = new TreeSet<>();
    }

    /**
     * Sets component owners
     *
     * @param owners: component owners
     */
    public void setOwners(SortedSet<String> owners) {
        this.owners = owners;
    }

    /**
     * Sets the only owner of the component
     *
     * @param owner: component owner
     */
    public void setOnlyOwner(String owner) {
        owners = new TreeSet<>();
        owners.add(owner);
    }

    /**
     * Returns the component owners
     *
     * @return component owners
     */
    public Set<String> getOwners() {
        return owners;
    }

    /**
     * Adds a new owner to the component
     *
     * @param owner: new component owner
     */
    public void addOwner(String owner) {
        if (owners != null && owner != null)
            owners.add(owner);
    }

    /**
     * Adds multiple owners to the component
     *
     * @param newOwners: set of owners
     */

    public void addOwners(Set<String> newOwners) {
        if (owners != null && newOwners != null)
            owners.addAll(newOwners);
    }

    /**
     * Returns the units associated to the component data
     *
     * @return units associated to the component data
     */
    public String getUnits() {
        return units;
    }

    /**
     * Sets the units of the component data
     *
     * @param units: units of the component data
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * Returns the component type
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
     * Returns the component description
     *
     * @return component description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the component description
     *
     * @param description: component description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the current value of the component
     *
     * @return value of the component
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the current value of the component
     *
     * @param value: value of the component
     */
    public void setValue(String value) {
        this.value = value;
    }
}
