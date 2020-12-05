package com.example.protocollectorframework.RegistrationModule;

import android.content.Context;

import com.example.protocollectorframework.DataModule.ComplementaryTable;
import com.example.protocollectorframework.DataModule.Data.ComplementaryData;
import com.example.protocollectorframework.DataModule.Data.ComplementaryDataForJson;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Class accountable for all the logic associated with complementary data
 */

public class ComplementaryManager {


    private ComplementaryTable mComplementaryTable;
    private Context context;

    /**
     * Constructor
     * @param context: context of the activity
     */
    public ComplementaryManager(Context context){
        this.context = context;
        mComplementaryTable = new ComplementaryTable(context);
    }

    /**
     * Creates complementary observations associated to a visit
     * @param start: complementary start timestamp
     * @param end: complementary end timestamp
     * @param eoi_json: json that contains the registered information from the eois
     * @param extra_info: json that contains extra registered information
     * @param visit_id: visit id
     * @param version: visit version
     * @return
     */
    public String createComplementary(String start, String end, String eoi_json, String extra_info, String visit_id, int version){
        if(mComplementaryTable != null)
            return mComplementaryTable.downloadComplementary(start,end,eoi_json,extra_info,visit_id,version);
        return null;

    }

    /**
     * Fetch complementary observations associated to a visit by id
     * @param complementary_id: complementary id
     * @return complementary observations object
     */
    public ComplementaryData getComplementaryById(String complementary_id){
        if(mComplementaryTable != null)
            return mComplementaryTable.getComplementaryByID(complementary_id);
        return null;
    }

    /**
     * Fetch complementary observations associated to a visit by visit id
     * @param visit_id: visit id
     * @return complementary observations object
     */
    public ComplementaryData getComplementaryByVisitId(String visit_id){
        if(mComplementaryTable != null)
            return mComplementaryTable.getComplementaryByVisitId(visit_id);
        return null;
    }

    /**
     * Creates complementary observations associated to a visit
     * @param visit_id: visit id
     * @return complementary observations id
     */
    public String initializeComplementary(String visit_id){
        if(mComplementaryTable != null)
            return mComplementaryTable.initializeComplementary(visit_id);
        return null;
    }

    /**
     * Edits the information of complementary observations
     * @param complementary_id: complementary id
     * @param eoi_json: json that contains the registered information from the eois
     * @param extra_info: json that contains extra registered information
     * @return true if success, false otherwise
     */
    public boolean editComplementary(String complementary_id, String eoi_json, String extra_info){
        if(mComplementaryTable != null)
            return mComplementaryTable.editComplementary(complementary_id,eoi_json,extra_info) > 0;
        return false;
    }

    /**
     * Terminates complementary observations
     * @param complementary_id: complementary id
     * @param eoi_json: json that contains the registered information from the eois
     * @param extra_info: json that contains extra registered information
     * @return true if success, false otherwise
     */
    public boolean finishComplementary(String complementary_id, String eoi_json, String extra_info){
        if(mComplementaryTable != null)
            return mComplementaryTable.finishComplementary(complementary_id,eoi_json,extra_info) > 0;
        return false;
    }

    /**
     * Cancels complementary observations
     * @param complementary_id: complementary id
     * @return true if success, false otherwise
     */
    public boolean cancelComplementary(String complementary_id){
        if(mComplementaryTable != null)
            return mComplementaryTable.cancelComplementary(complementary_id);
        return false;
    }

    /**
     * Fetch sync status from complementary observations
     * @param complementary_id: complementary id
     * @return integer that identifies the status.
     *         0 == not uploaded
     *         1 == uploaded
     *         2 == edited
     *         -1  == error on upload
     */
    public int getSyncStatus(String complementary_id){
        if(mComplementaryTable != null)
            return mComplementaryTable.getSyncStatus(complementary_id);
        return Integer.MIN_VALUE;
    }

    /**
     * Fetch not uploaded complementary observations (status != 1)
     * @return list of complementary observations objects with status != 1
     */
    public List<ComplementaryData> getNotUploadedComplementary(){
        List<ComplementaryData> list = new ArrayList<>();
        if(mComplementaryTable != null)
            list =  mComplementaryTable.getNotUploadedComplementary();
        return list;
    }

    /**
     * Fetch complementary observations version
     * @param complementary_id: complementary id
     * @return complementary version
     */
    public int getComplementaryVersion(String complementary_id){
        if(mComplementaryTable != null)
            return mComplementaryTable.getComplementaryVersion(complementary_id);
        return 0;
    }

    /**
     * Changes the sync status to uploaded (1)
     * @param complementary_id: complementary id
     * @return true if success, false otherwise
     */
    public long uploadComplementary(String complementary_id){
        if(mComplementaryTable != null)
            return mComplementaryTable.uploadComplementary(complementary_id);
        return -1;

    }


    /**
     * Cast complementary observations to JSON format
     * @param complementaryData: complementary object
     * @return json of the complementary observations
     */
    public String complementaryToJson(ComplementaryData complementaryData){
        Gson gson = new Gson();

        long begin = Long.parseLong(complementaryData.getStart_time());
        String time_begin = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date(begin));

        long end = Long.parseLong(complementaryData.getEnd_time());
        String time_end = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date(end));


        ComplementaryDataForJson v = new ComplementaryDataForJson(complementaryData.getId(),complementaryData.getVisit_id(),time_begin,time_end,complementaryData.getEoi_json(),complementaryData.getInfo_json());

        return gson.toJson(v);
    }




}
