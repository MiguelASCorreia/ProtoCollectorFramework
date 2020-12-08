package com.example.protocollectorframework.DataModule.Data;

import com.google.api.client.json.Json;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Object used to generate the json with all information associated to the visit
 */
public class VisitDataForJson implements Serializable {
    private String _id;
    private String plot_id;
    private String plot_info;
    private String time_begin;
    private String time_end;
    private HashMap<String,String> track_file;
    private int number_photos;
    private int number_voice_records;
    private JsonObject eois;
    private JsonObject info;

    /**
     * Constructor for the JSON corresponding to the visit information
     * @param _id: visit identifier
     * @param plot_id: plot identifier
     * @param plot_info: plot extra information
     * @param time_begin: visit start time
     * @param time_end: visit end time
     * @param track_file: track file path
     * @param photos_taken: number of photos taken
     * @param records_taken: number of audios taken
     * @param json_eois: resulting data from the EOIs
     * @param json_extra: extra information
     */

    public VisitDataForJson(String _id, String plot_id, String plot_info, String time_begin, String time_end, HashMap<String,String> track_file, int photos_taken, int records_taken, String json_eois, String json_extra) {
        this._id = _id;
        this.plot_id = plot_id;
        this.plot_info = plot_info;
        this.time_begin = time_begin;
        this.time_end = time_end;
        this.track_file = track_file;
        this.number_photos = photos_taken;
        this.number_voice_records = records_taken;
        try {
            if(json_eois!= null && !json_eois.isEmpty())
                this.eois =  new Gson().fromJson(json_eois, JsonObject.class);

            if(json_extra!= null && !json_extra.isEmpty())
                this.info =  new Gson().fromJson(json_extra, JsonObject.class);
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
