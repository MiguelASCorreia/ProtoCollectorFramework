package com.example.protocollectorframework.DataModule.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Visit information with corresponding plot
 */
public class VisitWithPlotData {

    private String visit_id;
    private long start_time;
    private long end_time;
    private String plot_id;
    private String name;

    /**
     * Constructor
     * @param visit_id: visit identifier
     * @param start_time: visit start time
     * @param end_time: visit end time
     * @param plot_id: plot identifier
     * @param name: plot name
     */
    public VisitWithPlotData(String visit_id, long start_time, long end_time, String plot_id, String name) {
        this.visit_id = visit_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.plot_id = plot_id;
        this.name = name;
    }

    /**
     * Returns the visit identifier
     * @return visit's identifier
     */
    public String getVisit_id() {
        return visit_id;
    }


    /**
     * Returns the visit start time
     * @return visit's start time
     */
    public long getStart_time() {
        return start_time;
    }

    /**
     * Returns the visit ending time
     * @return visit's ending time
     */
    public long getEnd_time() {
        return end_time;
    }

    /**
     * Returns the plot identifier
     * @return plot's identifier
     */
    public String getPlot_id() {
        return plot_id;
    }

    /**
     * Returns the plot name
     * @return plot's name
     */
    public String getName() {
        return name;
    }


}
