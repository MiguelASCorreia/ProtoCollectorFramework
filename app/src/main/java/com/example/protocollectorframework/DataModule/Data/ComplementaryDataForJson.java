package com.example.protocollectorframework.DataModule.Data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;

/**
 * Object used to generate the json with all information associated to the complementary data associated to the visit
 */
public class ComplementaryDataForJson implements Serializable {
    private String _id;
    private String visit_id;
    private String time_begin;
    private String time_end;
    private JsonObject eois;
    private JsonObject info;

    /**
     * Constructor for the JSON corresponding to the visit information
     *
     * @param id:         complementary observations identifier
     * @param visit_id:   visit's identifier
     * @param time_begin: visit start time
     * @param time_end:   visit end time
     * @param json_eois:  resulting data from the EOIs
     * @param json_extra: extra information
     */
    public ComplementaryDataForJson(String id, String visit_id, String time_begin, String time_end, String json_eois, String json_extra) {
        this._id = id;
        this.visit_id = visit_id;
        this.time_begin = time_begin;
        this.time_end = time_end;
        try {
            if (json_eois != null && !json_eois.isEmpty())
                this.eois = new Gson().fromJson(json_eois, JsonObject.class);

            if (json_extra != null && !json_extra.isEmpty())
                this.info = new Gson().fromJson(json_extra, JsonObject.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
