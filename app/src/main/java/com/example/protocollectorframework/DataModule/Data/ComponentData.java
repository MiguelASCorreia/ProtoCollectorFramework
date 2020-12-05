package com.example.protocollectorframework.DataModule.Data;

import com.example.protocollectorframework.InterfaceModule.ComponentsAPI;

import java.io.Serializable;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class ComponentData implements Serializable {
    private int type;
    private String description;
    private String value;
    private String conflicted_value;
    private String units;
    private SortedSet<String> owners;


    public ComponentData(String value){
        this.description = null;
        this.value = value;
        this.owners = new TreeSet<>();
        units = null;
        type = ComponentsAPI.COMPONENT_NUMBER;
    }

    public ComponentData(String description, String value, int type, String owner){
        this.description = description;
        this.value = value;
        this.owners = new TreeSet<>();
        this.owners.add(owner);
        units = null;
        this.type = type;
    }

    public ComponentData(String value, int type, String owner){
        this.description = null;
        this.value = value;
        this.owners = new TreeSet<>();
        this.owners.add(owner);
        units = null;
        this.type = type;
    }

    public ComponentData(int type, String description, String value) {
        this.type = type;
        this.description = description;
        this.value = value;
        this.owners = new TreeSet<>();
    }

    public ComponentData(int type, String description, String value, String units) {
        this.type = type;
        this.description = description;
        this.value = value;
        this.units = units;
        this.owners = new TreeSet<>();
    }

    public String getConflicted_value() {
        return conflicted_value;
    }

    public void setConflicted_value(String conflicted_value) {
        this.conflicted_value = conflicted_value;
    }

    public void setOwners(SortedSet<String> owners) {
        this.owners = owners;
    }

    public void setOnlyOwner(String owner){
        owners = new TreeSet<>();
        owners.add(owner);
    }
    public Set<String> getOwners() {
        return owners;
    }

    public void setOwners(TreeSet<String> owners) {
        this.owners = owners;
    }

    public void addOwner(String owner){
        if(owners != null && owner != null)
            owners.add(owner);
    }

    public void addOwner(Set<String> newOwners){
        if(owners != null && newOwners != null)
            owners.addAll(newOwners);
    }

    public String getUnits() {
        return units;
    }

    public void setUnits(String units) {
        this.units = units;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
