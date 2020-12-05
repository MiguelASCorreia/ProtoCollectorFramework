package com.example.protocollectorframework.DataModule.Data;

import org.json.JSONObject;

public class LoadPlotInfo {
    private String acronym;
    private JSONObject info;

    public LoadPlotInfo(String acronym, JSONObject info) {
        this.acronym = acronym;
        this.info = info;
    }

    public String getAcronym() {
        return acronym;
    }


    public JSONObject getInfo() {
        return info;
    }
}
