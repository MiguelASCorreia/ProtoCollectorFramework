package com.example.protocollectorframework.DataModule.Data;

import java.util.ArrayList;
import java.util.List;

public class VisitWithPlotData {

    private String id;
    private String route_path;
    private long start_time;
    private long end_time;

    private String plot_id;
    private String name;
    private List<LocationData> limits;

    public VisitWithPlotData(String id, long start_time, long end_time, String plot_id, String name) {
        this.id = id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.plot_id = plot_id;
        this.name = name;
        this.limits = new ArrayList<LocationData>(0);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoute_path() {
        return route_path;
    }

    public void setRoute_path(String route_path) {
        this.route_path = route_path;
    }

    public long getStart_time() {
        return start_time;
    }


    public long getEnd_time() {
        return end_time;
    }


    public String getPlot_id() {
        return plot_id;
    }

    public void setPlot_id(String plot_id) {
        this.plot_id = plot_id;
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
}
