package com.example.protocollectorframework.DataModule.Data;

import java.util.HashMap;

/**
 * Objected used to save the information generated for the resume
 */
public class ResumeData {
    private String visit_id;
    private long visit_start;
    private long visit_end;
    private String visit_info;

    private String complementary_id;
    private long complementary_start;
    private long complementary_end;
    private String complementary_info;

    private String plot_id;
    private String plot_acronym;
    private String plot_name;
    private String plot_info;

    private HashMap<String,Integer> multimediaCountByType;

    private HashMap<String,HashMap<String,Object>> resultsForMethods;

    /**
     * Empty constructor
     */
    public ResumeData(){

    }
    /**
     * Constructor
     * @param visit_id: visit identifier
     * @param visit_start: visit start time in milliseconds
     * @param visit_end: visit ending time in milliseconds
     * @param visit_info: information associated to the visit (stored in the info column of the corresponding table)
     * @param complementary_id: complementary observations identifier
     * @param complementary_start: complementary observations start time in milliseconds
     * @param complementary_end: complementary observations ending time in milliseconds
     * @param complementary_info: information associated to the complementary observations (stored in the info column of the corresponding table)
     * @param plot_id: plot identifier
     * @param plot_acronym: plot acronym
     * @param plot_name: plot name
     * @param plot_info: information associated to the plot (stored in the info column of the corresponding table)
     * @param multimediaCountByType: structure that maps for each multimedia type the corresponding counter
     * @param resultsForMethods: structure that maps for each class the returned value for each assigned method
     */
    public ResumeData(String visit_id, long visit_start, long visit_end, String visit_info, String complementary_id, long complementary_start, long complementary_end, String complementary_info, String plot_id, String plot_acronym, String plot_name, String plot_info, HashMap<String, Integer> multimediaCountByType, HashMap<String,HashMap<String, Object>> resultsForMethods) {
        this.visit_id = visit_id;
        this.visit_start = visit_start;
        this.visit_end = visit_end;
        this.visit_info = visit_info;
        this.complementary_id = complementary_id;
        this.complementary_start = complementary_start;
        this.complementary_end = complementary_end;
        this.complementary_info = complementary_info;
        this.plot_id = plot_id;
        this.plot_acronym = plot_acronym;
        this.plot_name = plot_name;
        this.plot_info = plot_info;
        this.multimediaCountByType = multimediaCountByType;
        this.resultsForMethods = resultsForMethods;
    }

    /**
     * Returns the visit identifier
     * @return visit's identifier
     */
    public String getVisit_id() {
        return visit_id;
    }

    /**
     * Sets the visit identifier
     * @param visit_id: visit's identifier
     */
    public void setVisit_id(String visit_id) {
        this.visit_id = visit_id;
    }

    /**
     * Returns the visit start time
     * @return visit start time in milliseconds
     */
    public long getVisit_start() {
        return visit_start;
    }

    /**
     * Sets the visit start time
     * @param visit_start: visit start time in milliseconds
     */
    public void setVisit_start(long visit_start) {
        this.visit_start = visit_start;
    }

    /**
     * Returns the visit ending time
     * @return visit ending time in milliseconds
     */
    public long getVisit_end() {
        return visit_end;
    }

    /**
     * Sets the visit ending time
     * @param visit_end: visit ending time in milliseconds
     */
    public void setVisit_end(long visit_end) {
        this.visit_end = visit_end;
    }

    /**
     * Returns JSON string corresponding to the visit info data
     * @return extra data
     */
    public String getVisit_info() {
        return visit_info;
    }

    /**
     * Sets the visit info data
     * @param visit_info: JSON string corresponding to the extra data
     */
    public void setVisit_info(String visit_info) {
        this.visit_info = visit_info;
    }

    /**
     * Returns the complementary observations identifier
     * @return complementary observations identifier
     */
    public String getComplementary_id() {
        return complementary_id;
    }

    /**
     * Sets the complementary observations identifier
     * @param complementary_id: complementary observations identifier
     */
    public void setComplementary_id(String complementary_id) {
        this.complementary_id = complementary_id;
    }

    /**
     * Returns the complementary observations start time
     * @return complementary observations start time in milliseconds
     */
    public long getComplementary_start() {
        return complementary_start;
    }

    /**
     * Sets the complementary observations start time
     * @param complementary_start: complementary observations start time in milliseconds
     */
    public void setComplementary_start(long complementary_start) {
        this.complementary_start = complementary_start;
    }

    /**
     * Returns the complementary observations ending time
     * @return complementary observations ending time in milliseconds
     */
    public long getComplementary_end() {
        return complementary_end;
    }

    /**
     * Sets the complementary observations ending time
     * @param complementary_end: complementary observations ending time in milliseconds
     */
    public void setComplementary_end(long complementary_end) {
        this.complementary_end = complementary_end;
    }

    /**
     * Returns JSON string corresponding to the complementary observations info data
     * @return extra data
     */
    public String getComplementary_info() {
        return complementary_info;
    }
    /**
     * Sets the complementary observations info data
     * @param complementary_info: JSON string corresponding to the extra data
     */
    public void setComplementary_info(String complementary_info) {
        this.complementary_info = complementary_info;
    }

    /**
     * Returns the plot identifier
     * @return plot identifier
     */
    public String getPlot_id() {
        return plot_id;
    }

    /**
     * Sets the plot's identifier
     * @param plot_id: plot's identifier
     */
    public void setPlot_id(String plot_id) {
        this.plot_id = plot_id;
    }

    /**
     * Returns the plot's acronym
     * @return plot's acronym
     */
    public String getPlot_acronym() {
        return plot_acronym;
    }

    /**
     * Sets the plot's acronym
     * @param plot_acronym: plot's acronym
     */
    public void setPlot_acronym(String plot_acronym) {
        this.plot_acronym = plot_acronym;
    }

    /**
     * Returns plot's name
     * @return plot's name
     */
    public String getPlot_name() {
        return plot_name;
    }

    /**
     * Sets plot's name
     * @param plot_name: plot's name
     */
    public void setPlot_name(String plot_name) {
        this.plot_name = plot_name;
    }

    /**
     * Returns plot's info data
     * @return plot's info data
     */
    public String getPlot_info() {
        return plot_info;
    }

    /**
     * Sets plot's info data
     * @param plot_info: plot's info data
     */
    public void setPlot_info(String plot_info) {
        this.plot_info = plot_info;
    }

    /**
     * Returns the number of multimedia files created during the visit for each type
     * @return structure that maps for each type the corresponding counter
     */
    public HashMap<String, Integer> getMultimediaCountByType() {
        return multimediaCountByType;
    }

    /**
     * Sets the number of multimedia files created during the visit for each type
     * @param multimediaCountByType: structure that maps for each type the corresponding counter
     */
    public void setMultimediaCountByType(HashMap<String, Integer> multimediaCountByType) {
        this.multimediaCountByType = multimediaCountByType;
    }

    /**
     * Returns the returned value for each of the assigned methods, mapped the corresponding method and class
     * @return structure that maps for each package class name, the method and the associated returned value
     */
    public HashMap<String,HashMap<String, Object>> getResultsForMethods() {
        return resultsForMethods;
    }

    /**
     * Sets the returned value for each of the assigned methods, mapped the corresponding method and class
     * @param resultsForMethods that maps for each package class name, the method and the associated returned value
     */
    public void setResultsForMethods(HashMap<String,HashMap<String, Object>> resultsForMethods) {
        this.resultsForMethods = resultsForMethods;
    }
}
