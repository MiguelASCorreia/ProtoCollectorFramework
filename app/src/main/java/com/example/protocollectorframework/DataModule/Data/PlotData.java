package com.example.protocollectorframework.DataModule.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

/**
 * Information associated with plots
 */
public class PlotData implements Serializable {
    private String ID;
    private String name;
    private String acronym;
    private LocationData center;
    private List<LocationData> limits;
    private String info;

    /**
     * Constructor
     * @param ID: plot's identifier
     * @param acronym: plot's acronym
     * @param name: plot's name
     * @param limits: plot's limits
     */
    public PlotData(String ID, String acronym, String name, List<LocationData> limits){
        this.ID = ID;
        this.acronym = acronym;
        this.name = name;
        this.center = null;
        this.limits = limits;
        this.info = null;
    }

    /**
     * Constructor
     * @param ID: plot's identifier
     * @param acronym: plot's acronym
     * @param name: plot's name
     * @param center: plot's center
     * @param limits: plot's limits
     */
    public PlotData(String ID, String acronym, String name, LocationData center, List<LocationData> limits){
        this.ID = ID;
        this.name = name;
        this.acronym = acronym;
        this.center = center;
        this.limits = limits;
        this.info = null;
    }


    /**
     * Constructor
     * @param ID: plot's identifier
     * @param acronym: plot's acronym
     * @param name: plot's name
     * @param center: plot's center
     * @param limits: plot's limits
     * @param info: extra information
     */
    public PlotData(String ID, String acronym, String name, LocationData center, List<LocationData> limits, String info){
        this.ID = ID;
        this.name = name;
        this.acronym = acronym;
        this.center = center;
        this.limits = limits;
        this.info = info;
    }

    /**
     * Fetch the desired field from the extra information
     * @param tag: field's tag
     * @return object associated to the tag
     */
    public Object getField(String tag){
        if(info != null){
            try{
                JSONObject jsonObject = new JSONObject(info);
                if(jsonObject.has(tag))
                    return jsonObject.get(tag);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Fetch the desired field from the extra information in the JSONArray format
     * @param tag: field's tag
     * @return JSONArray associated to the tag
     */
    public JSONArray getArrayField(String tag){
        if(info != null){
            try{
                JSONObject jsonObject = new JSONObject(info);
                if(jsonObject.has(tag))
                    return jsonObject.getJSONArray(tag);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Returns plot's acronym
     * @return plot's acronym
     */
    public String getAcronym() {
        return acronym;
    }

    /**
     * Sets the plot's acronym
     * @param acronym: plot's acronym
     */
    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    /**
     * Returns plot's center
     * @return plot's center
     */
    public LocationData getCenter() {
        return center;
    }

    /**
     * Sets plot's center
     * @param center: plot's center
     */
    public void setCenter(LocationData center) {
        this.center = center;
    }

    /**
     * Returns plot's identifier
     * @return plot's identifier
     */
    public String getID() {
        return ID;
    }

    /**
     * Sets plot's identifier
     * @param ID: plot's identifier
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Returns plot's name
     * @return plot's name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets plot's name
     * @param name: plot's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns plot's limits
     * @return plot's limits
     */
    public List<LocationData> getLimits() {
        return limits;
    }

    /**
     * Sets plot's limits
     * @param limits: plot's limits
     */
    public void setLimits(List<LocationData> limits) {
        this.limits = limits;
    }

    /**
     * Returns plot's extra information
     * @return plot's extra information
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets plot's extra information
     * @param info: plot's extra information
     */
    public void setInfo(String info) {
        this.info = info;
    }
}




