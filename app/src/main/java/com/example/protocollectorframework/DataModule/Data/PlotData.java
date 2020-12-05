package com.example.protocollectorframework.DataModule.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class PlotData implements Serializable {
    private String ID;
    private String name;
    private String acronym;
    private LocationData center;
    private List<LocationData> limits;
    private String info;


    public PlotData(){
    }

    public PlotData(String ID, String acronym, String name, List<LocationData> limits){
        this.ID = ID;
        this.acronym = acronym;
        this.name = name;
        this.center = null;
        this.limits = limits;
        this.info = null;
    }

    public PlotData(String ID, String acronym, String name, LocationData center, List<LocationData> limits){
        this.ID = ID;
        this.name = name;
        this.acronym = acronym;
        this.center = center;
        this.limits = limits;
        this.info = null;
    }



    public PlotData(String ID, String acronym, String name, LocationData center, List<LocationData> limits, String info){
        this.ID = ID;
        this.name = name;
        this.acronym = acronym;
        this.center = center;
        this.limits = limits;
        this.info = info;
    }


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

    public String getCulture() {
        try {
            Object object = getField("fruto");
            if(object != null)
            return object.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    public String[] getProtocols() {
        JSONArray arr = getArrayField("inimigo");
        String[] enemies = null;
        if(arr != null) {
            enemies = new String[arr.length()];
            for (int i = 0; i < arr.length(); i++) {
                try {
                    enemies[i] = arr.getString(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return enemies;
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public LocationData getCenter() {
        return center;
    }

    public void setCenter(LocationData center) {
        this.center = center;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<LocationData> getLimits() {
        return limits;
    }

    public void setLimits(List<LocationData> limits) {
        this.limits = limits;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}




