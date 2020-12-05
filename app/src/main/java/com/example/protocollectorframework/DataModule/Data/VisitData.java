package com.example.protocollectorframework.DataModule.Data;

import java.io.Serializable;
import java.util.HashMap;

public class VisitData implements Serializable {
    private String id;
    private String plot_id;
    private HashMap<String, String> route_path;
    private long start_time;
    private long end_time;
    private String eoi_json;
    private String trap_json;
    private String info_json;

    public VisitData(String id, String plot_id, HashMap<String, String> route_path, long start_time, long end_time) {
        this.id = id;
        this.plot_id = plot_id;
        this.route_path = route_path;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    public VisitData(String id, String plot_id, HashMap<String, String> route_path, long start_time, long end_time, String eoi_json, String trap_json, String info_json) {
        this.id = id;
        this.plot_id = plot_id;
        this.route_path = route_path;
        this.start_time = start_time;
        this.end_time = end_time;
        this.eoi_json = eoi_json;
        this.trap_json = trap_json;
        this.info_json = info_json;
    }

    public String getEoi_json() {
        return eoi_json;
    }

    public void setEoi_json(String eoi_json) {
        this.eoi_json = eoi_json;
    }

    public String getInfo_json() {
        return info_json;
    }

    public void setInfo_json(String info_json) {
        this.info_json = info_json;
    }

    public String getTrap_json() {
        return trap_json;
    }

    public void setTrap_json(String trap_json) {
        this.trap_json = trap_json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlot_id() {
        return plot_id;
    }

    public void setPlot_id(String plot_id) {
        this.plot_id = plot_id;
    }

    public HashMap<String, String> getRoute_path() {
        return route_path;
    }

    public void setRoute_path(HashMap<String, String> route_path) {
        this.route_path = route_path;
    }

    public long getStart_time() {
        return start_time;
    }

    public long getEnd_time() {
        return end_time;
    }
}
