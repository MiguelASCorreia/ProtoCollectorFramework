package com.example.protocollectorframework.DataModule.Data;

/**
 * Objected used to save the information associated to the realization of complementary observations associated to a field visit
 */
public class ComplementaryData {

    private String id;
    private String visit_id;
    private long start_time;
    private long end_time;
    private String eoi_json;
    private String info_json;

    /**
     * Constructor
     *
     * @param id:         complementary observations identifier
     * @param visit_id:   corresponding visit identifier
     * @param start_time: complementary observations start time in milliseconds
     * @param end_time:   complementary observations ending time in milliseconds
     * @param eoi_json:   JSON string corresponding to the data obtained over the EOIs
     * @param info_json:  JSON string corresponding to the extra data
     */
    public ComplementaryData(String id, String visit_id, long start_time, long end_time, String eoi_json, String info_json) {
        this.id = id;
        this.visit_id = visit_id;
        this.start_time = start_time;
        this.end_time = end_time;
        this.eoi_json = eoi_json;
        this.info_json = info_json;
    }


    /**
     * Returns the complementary observations identifier
     *
     * @return complementary observations identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the complementary observations identifier
     *
     * @param id: complementary observations identifier
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Returns the visit identifier
     *
     * @return visit's identifier
     */
    public String getVisit_id() {
        return visit_id;
    }

    /**
     * Sets the visit identifier
     *
     * @param visit_id: visit's identifier
     */
    public void setVisit_id(String visit_id) {
        this.visit_id = visit_id;
    }

    /**
     * Returns the JSON string corresponding to the data obtained over the EOIs
     *
     * @return data obtained over the EOIs
     */
    public String getEoi_json() {
        return eoi_json;
    }

    /**
     * Sets the data obtained over the EOIs
     *
     * @param eoi_json: JSON string corresponding to the data obtained over the EOIs
     */
    public void setEoi_json(String eoi_json) {
        this.eoi_json = eoi_json;
    }

    /**
     * Returns JSON string corresponding to the extra data
     *
     * @return extra data
     */
    public String getInfo_json() {
        return info_json;
    }

    /**
     * Sets the extra data
     *
     * @param info_json: JSON string corresponding to the extra data
     */
    public void setInfo_json(String info_json) {
        this.info_json = info_json;
    }

    /**
     * Returns the complementary observations start time
     *
     * @return complementary observations start time in milliseconds
     */
    public long getStart_time() {
        return start_time;
    }

    /**
     * Sets the complementary observations start time
     *
     * @param start_time: complementary observations start time in milliseconds
     */
    public void setStart_time(long start_time) {
        this.start_time = start_time;
    }

    /**
     * Returns the complementary observations ending time
     *
     * @return complementary observations ending time in milliseconds
     */
    public long getEnd_time() {
        return end_time;
    }

    /**
     * Sets the complementary observations ending time
     *
     * @param end_time: complementary observations ending time in milliseconds
     */
    public void setEnd_time(long end_time) {
        this.end_time = end_time;
    }

}
