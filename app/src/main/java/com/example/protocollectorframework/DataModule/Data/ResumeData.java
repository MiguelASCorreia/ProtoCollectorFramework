package com.example.protocollectorframework.DataModule.Data;

import java.util.HashMap;

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

    public String getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(String visit_id) {
        this.visit_id = visit_id;
    }

    public long getVisit_start() {
        return visit_start;
    }

    public void setVisit_start(long visit_start) {
        this.visit_start = visit_start;
    }

    public long getVisit_end() {
        return visit_end;
    }

    public void setVisit_end(long visit_end) {
        this.visit_end = visit_end;
    }

    public String getVisit_info() {
        return visit_info;
    }

    public void setVisit_info(String visit_info) {
        this.visit_info = visit_info;
    }

    public String getComplementary_id() {
        return complementary_id;
    }

    public void setComplementary_id(String complementary_id) {
        this.complementary_id = complementary_id;
    }

    public long getComplementary_start() {
        return complementary_start;
    }

    public void setComplementary_start(long complementary_start) {
        this.complementary_start = complementary_start;
    }

    public long getComplementary_end() {
        return complementary_end;
    }

    public void setComplementary_end(long complementary_end) {
        this.complementary_end = complementary_end;
    }

    public String getComplementary_info() {
        return complementary_info;
    }

    public void setComplementary_info(String complementary_info) {
        this.complementary_info = complementary_info;
    }

    public String getPlot_id() {
        return plot_id;
    }

    public void setPlot_id(String plot_id) {
        this.plot_id = plot_id;
    }

    public String getPlot_acronym() {
        return plot_acronym;
    }

    public void setPlot_acronym(String plot_acronym) {
        this.plot_acronym = plot_acronym;
    }

    public String getPlot_name() {
        return plot_name;
    }

    public void setPlot_name(String plot_name) {
        this.plot_name = plot_name;
    }

    public String getPlot_info() {
        return plot_info;
    }

    public void setPlot_info(String plot_info) {
        this.plot_info = plot_info;
    }

    public HashMap<String, Integer> getMultimediaCountByType() {
        return multimediaCountByType;
    }

    public void setMultimediaCountByType(HashMap<String, Integer> multimediaCountByType) {
        this.multimediaCountByType = multimediaCountByType;
    }

    public HashMap<String,HashMap<String, Object>> getResultsForMethods() {
        return resultsForMethods;
    }

    public void setResultsForMethods(HashMap<String,HashMap<String, Object>> resultsForMethods) {
        this.resultsForMethods = resultsForMethods;
    }
}
