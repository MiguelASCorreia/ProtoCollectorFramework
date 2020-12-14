package com.example.protocollectorframework.DataModule.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Objected used to save the information associated to the realization of a field visit
 */
public class VisitData implements Serializable {
    private String id;
    private String plot_id;
    private HashMap<String, String> route_path;
    private long start_time;
    private long end_time;
    private String eoi_json;
    private String info_json;

    /**
     * Constructor
     * @param id: visit's identifier
     * @param plot_id: plot's identifier
     * @param route_path: structure that maps the owner (his identifier) to the path of the GPX file
     * @param start_time: visit start time in milliseconds
     * @param end_time: visit ending time in milliseconds
     */
    public VisitData(String id, String plot_id, HashMap<String, String> route_path, long start_time, long end_time) {
        this.id = id;
        this.plot_id = plot_id;
        this.route_path = route_path;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    /**
     * Constructor
     * @param id: visit's identifier
     * @param plot_id: plot's identifier
     * @param route_path: structure that maps the owner (his identifier) to the path of the GPX file
     * @param start_time: visit start time in milliseconds
     * @param end_time: visit ending time in milliseconds
     * @param eoi_json: JSON string corresponding to the data obtained over the EOIs
     * @param info_json: JSON string corresponding to the extra data
     */
    public VisitData(String id, String plot_id, HashMap<String, String> route_path, long start_time, long end_time, String eoi_json, String info_json) {
        this.id = id;
        this.plot_id = plot_id;
        this.route_path = route_path;
        this.start_time = start_time;
        this.end_time = end_time;
        this.eoi_json = eoi_json;
        this.info_json = info_json;
    }

    /**
     * Returns the JSON string corresponding to the data obtained over the EOIs
     * @return data obtained over the EOIs
     */
    public String getEoi_json() {
        return eoi_json;
    }

    /**
     * Sets the data obtained over the EOIs
     * @param eoi_json: JSON string corresponding to the data obtained over the EOIs
     */
    public void setEoi_json(String eoi_json) {
        this.eoi_json = eoi_json;
    }

    /**
     * Returns JSON string corresponding to the extra data
     * @return extra data
     */
    public String getInfo_json() {
        return info_json;
    }

    /**
     * Sets the extra data
     * @param info_json: JSON string corresponding to the extra data
     */
    public void setInfo_json(String info_json) {
        this.info_json = info_json;
    }

    /**
     * Returns the visit identifier
     * @return visit's identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the visit identifier
     * @param id: visit's identifier
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the plot identifier where the visit took place
     * @return plot identifier
     */
    public String getPlot_id() {
        return plot_id;
    }

    /**
     * Sets the plot identifier where the visit took place
     * @param plot_id: plot identifier
     */
    public void setPlot_id(String plot_id) {
        this.plot_id = plot_id;
    }

    /**
     * Returns the structure that maps each owner to the path to his GPX file
     * @return structure that maps each owner to the path to his GPX file
     */
    public HashMap<String, String> getRoute_path() {
        return route_path;
    }

    /**
     * Sets the structure that maps each owner to the path to his GPX file
     * @param route_path: structure that maps each owner to the path to his GPX file
     */
    public void setRoute_path(HashMap<String, String> route_path) {
        this.route_path = route_path;
    }

    /**
     * Returns the visit start time
     * @return visit start time in milliseconds
     */
    public long getStart_time() {
        return start_time;
    }

    /**
     * Sets the visit start time
     * @param start_time: visit start time in milliseconds
     */
    public void setStart_time(long start_time){
        this.start_time = start_time;
    }

    /**
     * Returns the visit ending time
     * @return visit ending time in milliseconds
     */
    public long getEnd_time() {
        return end_time;
    }

    /**
     * Sets the visit ending time
     * @param end_time: visit ending time in milliseconds
     */
    public void setEnd_time(long end_time){
        this.end_time = end_time;
    }
}
