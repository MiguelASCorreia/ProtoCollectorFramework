package com.example.protocollectorframework.DataModule.Data;

import java.io.Serializable;

/**
 * Objected used to save the information used to generate the resumes
 */
public class ResumeConfig implements Serializable {

    private boolean visit_data;
    private boolean visit_info;
    private boolean complementary_data;
    private boolean complementary_info;
    private boolean plot_data;
    private boolean plot_info;
    private boolean multimedia_count;
    private MethodData[] methods;

    /**
     * Empty constructor
     */
    public ResumeConfig(){

    }

    /**
     * Default constructor
     * @param visit_data: boolean that indicates if the visit data (id, start time and ending time) is to be accountable in the resume
     * @param visit_info: boolean that indicates if the visit information (stored in the info column of the corresponding table) is to be accountable in the resume
     * @param complementary_data: boolean that indicates if the complementary observations data (id, start time and ending time) is to be accountable in the resume
     * @param complementary_info: boolean that indicates if the  complementary observations information (stored in the info column of the corresponding table) is to be accountable in the resume
     * @param plot_data: boolean that indicates if the plot data (id, acronym and name) is to be accountable in the resume
     * @param plot_info: boolean that indicates if the plot information (stored in the info column of the corresponding table) is to be accountable in the resume
     * @param multimedia_count: boolean that indicates if the multimedia file count is to be accountable in the resume
     * @param methods: array of methods which the returned value is to be accountable in the resume
     */
    public ResumeConfig(boolean visit_data, boolean visit_info, boolean complementary_data, boolean complementary_info, boolean plot_data, boolean plot_info, boolean multimedia_count, MethodData[] methods) {
        this.visit_data = visit_data;
        this.visit_info = visit_info;
        this.complementary_data = complementary_data;
        this.complementary_info = complementary_info;
        this.plot_data = plot_data;
        this.plot_info = plot_info;
        this.multimedia_count = multimedia_count;
        this.methods = methods;
    }

    /**
     * Checks if the visit data is to be accountable in the resume
     * @return true if the visit data is to be accountable in the resume, false otherwise
     */
    public boolean isVisitDataAccountable() {
        return visit_data;
    }

    /**
     * Sets if the visit data is to be accountable in the resume
     * @param accountable: true if is to be accountable, false otherwise
     */
    public void setVisitDataFlag(boolean accountable) {
        this.visit_data = accountable;
    }

    /**
     * Checks if the visit information is to be accountable in the resume
     * @return true if the visit information is to be accountable in the resume, false otherwise
     */
    public boolean isVisitInfoAccountable () {
        return visit_info;
    }

    /**
     * Sets if the visit information is to be accountable in the resume
     * @param accountable: true if is to be accountable, false otherwise
     */
    public void setVisitInfoFlag(boolean accountable) {
        this.visit_info = accountable;
    }

    /**
     * Checks if the complementary data is to be accountable in the resume
     * @return true if the complementary data is to be accountable in the resume, false otherwise
     */
    public boolean isComplementaryDataAccountable() {
        return complementary_data;
    }

    /**
     * Sets if the complementary data is to be accountable in the resume
     * @param accountable: true if is to be accountable, false otherwise
     */
    public void setComplementaryDataFlag(boolean accountable) {
        this.complementary_data = accountable;
    }

    /**
     * Checks if the complementary info is to be accountable in the resume
     * @return true if the complementary info is to be accountable in the resume, false otherwise
     */
    public boolean isComplementaryInfoAccountable() {
        return complementary_info;
    }

    /**
     * Sets if the complementary info is to be accountable in the resume
     * @param accountable: true if is to be accountable, false otherwise
     */
    public void setComplementaryInfoFlag(boolean accountable) {
        this.complementary_info = accountable;
    }

    /**
     * Checks if the plot data is to be accountable in the resume
     * @return true if the plot data is to be accountable in the resume, false otherwise
     */
    public boolean isPlotDataAccountable() {
        return plot_data;
    }

    /**
     * Sets if the plot data is to be accountable in the resume
     * @param accountable: true if is to be accountable, false otherwise
     */
    public void setPlotDataFlag(boolean accountable) {
        this.plot_data = accountable;
    }

    /**
     * Checks if the plot info is to be accountable in the resume
     * @return true if the plot info is to be accountable in the resume, false otherwise
     */
    public boolean isPlotInfoAccountable() {
        return plot_info;
    }

    /**
     * Sets if the plot info is to be accountable in the resume
     * @param accountable: true if is to be accountable, false otherwise
     */
    public void setPlotInfoFlag(boolean accountable) {
        this.plot_info = accountable;
    }

    /**
     * Checks if the multimedia count is to be accountable in the resume
     * @return true if the multimedia count is to be accountable in the resume, false otherwise
     */
    public boolean isMultimediaCountAccountable() {
        return multimedia_count;
    }

    /**
     * Sets if the multimedia count is to be accountable in the resume
     * @param accountable: true if is to be accountable, false otherwise
     */
    public void setMultimediaCountFlag(boolean accountable) {
        this.multimedia_count = accountable;
    }

    /**
     * Returns the array of methods that the returned value is to be accountable in the resume
     * @return methods to be accountable
     */
    public MethodData[] getMethods() {
        return methods;
    }

    /**
     * Sets the array of methods that the returned value is to be accountable in the resume
     * @param methods: methods to be accountable
     */
    public void setMethods(MethodData[] methods) {
        this.methods = methods;
    }
}
