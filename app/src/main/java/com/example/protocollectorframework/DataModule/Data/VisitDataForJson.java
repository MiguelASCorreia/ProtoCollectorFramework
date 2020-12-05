package com.example.protocollectorframework.DataModule.Data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;
import java.util.HashMap;

public class VisitDataForJson implements Serializable {
    private String _id;
    private String plot_id;
    private String crops;
    private String time_begin;
    private String time_end;
    private HashMap<String,String> track_file;
    private int number_photos;
    private int number_voice_records;
    private JsonObject traps;
    private JsonObject eois;
    private JsonObject info;


    public VisitDataForJson(String _id, String plot_id, String crops, String time_begin, String time_end, HashMap<String,String> track_file, int photos_taken, int records_taken, String json_eois, String json_traps, String json_extra) {
        this._id = _id;
        this.plot_id = plot_id;
        this.crops = crops;
        this.time_begin = time_begin;
        this.time_end = time_end;
        this.track_file = track_file;
        this.number_photos = photos_taken;
        this.number_voice_records = records_taken;
        try {
            if(json_eois!= null && !json_eois.isEmpty())
                this.eois =  new Gson().fromJson(json_eois, JsonObject.class);

            if(json_traps!= null && !json_traps.isEmpty())
                this.traps = new Gson().fromJson(json_traps, JsonObject.class);

            if(json_extra!= null && !json_extra.isEmpty())
                this.info =  new Gson().fromJson(json_extra, JsonObject.class);
            
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
