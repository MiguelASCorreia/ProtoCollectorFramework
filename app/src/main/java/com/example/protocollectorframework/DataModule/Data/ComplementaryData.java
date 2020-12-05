package com.example.protocollectorframework.DataModule.Data;

public class ComplementaryData {

    private String id;
    private String visit_id;
    private String start_time;
    private String end_time;
    private String eoi_json;
    private String info_json;

    public ComplementaryData(String id, String visit_id, String start_time, String end_time, String eoi_json, String info_json) {
        this.id = id;
        this.visit_id = visit_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.eoi_json = eoi_json;
        this.info_json = info_json;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getVisit_id() {
        return visit_id;
    }

    public void setVisit_id(String visit_id) {
        this.visit_id = visit_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
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
}
