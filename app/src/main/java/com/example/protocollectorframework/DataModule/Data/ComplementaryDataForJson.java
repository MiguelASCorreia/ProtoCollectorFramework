package com.example.protocollectorframework.DataModule.Data;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.Serializable;

public class ComplementaryDataForJson implements Serializable {
    private String _id;
    private String visit_id;
    private String time_begin;
    private String time_end;
    private JsonObject eois;
    private JsonObject info;


    public ComplementaryDataForJson(String id, String visit_id, String time_begin, String time_end, String json_eois, String json_extra) {
        this._id = _id;
        this.time_begin = time_begin;
        this.time_end = time_end;
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
