package com.example.protocollectorframework.RegistrationModule;

import android.content.Context;
import android.database.Cursor;

import com.example.protocollectorframework.DataModule.Data.MultimediaData;
import com.example.protocollectorframework.DataModule.Data.PlotData;
import com.example.protocollectorframework.DataModule.Data.VisitData;
import com.example.protocollectorframework.DataModule.Data.VisitDataForJson;
import com.example.protocollectorframework.DataModule.DataBase.VisitTable;
import com.example.protocollectorframework.LocationModule.LocationModule;
import com.example.protocollectorframework.MultimediaModule.MultimediaManager;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Class accountable for all the logic associated with visit data
 */

public class VisitManager {

    private VisitTable mVisitTable;
    private LocationModule mLocationModule;
    private Context context;

    /**
     * Constructor
     *
     * @param context: context of the activity
     */

    public VisitManager(Context context) {
        this.context = context;
        mVisitTable = new VisitTable(context);
        mLocationModule = new LocationModule(context);
    }

    /**
     * Creates a visit given the plot where it takes place
     *
     * @param plotId: plot's identifier where the visit takes place
     * @return visit's identifier
     */
    public String addVisit(String plotId) {
        if (mVisitTable != null)
            return mVisitTable.addVisit(plotId);
        return null;
    }

    /**
     * Edits the information of a visit
     *
     * @param visit_id:   visit's identifier
     * @param eoi_json:   json that contains the registered information from the eois
     * @param extra_info: json that contains extra registered information
     * @return true if success, false otherwise
     */
    public boolean editVisit(String visit_id, String eoi_json, String extra_info) {
        if (mVisitTable != null)
            return mVisitTable.editVisit(visit_id, eoi_json, extra_info);
        return false;
    }

    /**
     * Terminates a visit
     *
     * @param visit_id:   visit's identifier
     * @param eoi_json:   json that contains the registered information from the eois
     * @param extra_info: json that contains extra registered information
     * @return true if success, false otherwise
     */
    public boolean finishVisit(String visit_id, String eoi_json, String extra_info) {
        if (mVisitTable != null)
            return mVisitTable.finishVisit(visit_id, eoi_json, extra_info);
        return false;
    }

    /**
     * Fetch concluded visits
     *
     * @return list of concluded visit objects
     */
    public List<VisitData> getConcludedVisits() {
        List<VisitData> list = new ArrayList<>();
        if (mVisitTable != null)
            list = mVisitTable.getVisits();
        return list;
    }

    /**
     * Fetch visit by its identifier
     *
     * @param visit_id: visit's identifier
     * @return visit object
     */
    public VisitData getVisitByID(String visit_id) {
        if (mVisitTable != null)
            return mVisitTable.getVisitByID(visit_id);
        return null;
    }

    /**
     * Fetch finished visit by its identifier
     *
     * @param visit_id: visit's identifier
     * @return visit object
     */
    public VisitData getFinishedVisitByID(String visit_id) {
        if (mVisitTable != null)
            return mVisitTable.getFinishedVisitByID(visit_id);
        return null;
    }


    /**
     * Cancels a visit
     *
     * @param visit_id: visit's identifier
     * @return true if success, false otherwise
     */
    public boolean cancelVisit(String visit_id) {
        if (mVisitTable != null)
            return mVisitTable.cancelVisit(visit_id);
        return false;
    }

    /**
     * Fetch the plot's identifier where the visit takes place
     *
     * @param visit_id: visit's identifier
     * @return plot's identifier
     */
    public String getPlotIdFromVisit(String visit_id) {
        if (mVisitTable != null)
            return mVisitTable.getPlotIdForVisit(visit_id);
        return null;
    }

    /**
     * Fetch sync status from visit
     *
     * @param visit_id: visit's identifier
     * @return integer that identifies the status.
     * 0 == not uploaded
     * 1 == uploaded
     * 2 == edited
     * -1  == error on upload
     */
    public int getSyncStatus(String visit_id) {
        if (mVisitTable != null)
            return mVisitTable.getSyncStatus(visit_id);
        return Integer.MIN_VALUE;
    }


    /**
     * Fetch the plot object where the visit takes place
     *
     * @param visit_id: visit's identifier
     * @return plot object
     */
    public PlotData getVisitPlot(String visit_id) {
        if (mLocationModule != null)
            return mLocationModule.getPlotById(getVisitByID(visit_id).getPlot_id());
        return null;
    }

    /**
     * Creates a visit based on downloaded data from google drive
     *
     * @param paths:      list of paths to the gpx files
     * @param start:      visit start timestamp
     * @param end:        visit end timestamp
     * @param eoi_json:   json that contains the registered information from the eois
     * @param extra_info: json that contains extra registered information
     * @param plot_id:    plot's identifier where visit takes place
     * @param version:    visit version
     * @return visit's identifier
     */
    public String createDownloadedVisit(List<String> paths, long start, long end, String eoi_json, String extra_info, long plot_id, int version) {
        if (mVisitTable != null)
            return mVisitTable.downloadVisit(paths, start, end, eoi_json, extra_info, plot_id, version);
        return null;
    }

    /**
     * Fetch visits carried out on plot
     *
     * @param plot_id:  plot's identifier
     * @param visit_id: visit's identifier
     * @return list of visits that took place on the given plot
     */
    public List<VisitData> getLastVisitsOnPlot(String plot_id, String visit_id) {
        List<VisitData> list = new ArrayList<>();
        if (mVisitTable != null)
            list = mVisitTable.getVisitsOnPlot(plot_id, visit_id);
        return list;
    }

    /**
     * Fetch cursor for visit list
     *
     * @param plot_id:    plot's identifier
     * @param start_time: visit start time
     * @param end_time:   visit ent time
     * @param sync_state: visit sync status
     * @return cursor
     */
    public Cursor getVisitWithPlot(String plot_id, Calendar start_time, Calendar end_time, int sync_state) {
        if (mVisitTable != null)
            return mVisitTable.getVisitsWithPlotCursor(plot_id, start_time, end_time, sync_state);
        return null;
    }

    /**
     * Fetch not uploaded visits (status != 1)
     *
     * @return list of visit object with status != 1
     */
    public List<VisitData> getNotUploadedVisits() {
        List<VisitData> list = new ArrayList<>();
        if (mVisitTable != null)
            list = mVisitTable.getNotUploadedVisits();
        return list;
    }

    /**
     * Check if there is a visit on data and plot
     *
     * @param date:    date on format yyyy.MM.dd
     * @param plot_id: plot's identifier
     * @return true if there is a visit on that date and plot, false otherwise
     */
    public boolean hasVisitOnDateAndPlot(String date, String plot_id) {
        if (mVisitTable != null)
            return mVisitTable.hasVisitOnDateAndPlot(date, plot_id);
        return false;
    }


    /**
     * Fetch visit version
     *
     * @param visit_id: visit's identifier
     * @return visit version
     */
    public int getVisitVersion(String visit_id) {
        if (mVisitTable != null)
            return mVisitTable.getVisitVersion(visit_id);
        return 0;
    }

    /**
     * Changes the sync status to uploaded (1)
     *
     * @param visit_id: visit's identifier
     * @return true if success, false otherwise
     */
    public boolean uploadVisit(String visit_id) {
        if (mVisitTable != null)
            return mVisitTable.uploadVisit(visit_id) > 0;
        return false;

    }

    /**
     * Changes the sync status to error (-1)
     *
     * @param visit_id: visit's identifier
     * @return true if success, false otherwise
     */
    public boolean setStatusError(String visit_id) {
        if (mVisitTable != null)
            return mVisitTable.errorOnUpload(visit_id);
        return false;
    }

    /**
     * Cast visit to JSON format
     *
     * @param visit_id: visit's identifier
     * @return visit's json
     */
    public String visitToJson(String visit_id) {
        if (mVisitTable != null)
            return visitToJson(mVisitTable.getVisitByID(visit_id));
        return null;
    }

    /**
     * Cast visit to JSON format
     *
     * @param visitData: visit object
     * @return json of the visit
     */
    public String visitToJson(VisitData visitData) {
        if (visitData != null) {
            Gson gson = new Gson();

            long begin = visitData.getStart_time();
            String time_begin = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date(begin));

            long end = visitData.getEnd_time();
            String time_end = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date(end));

            MultimediaManager mMultimediaManager = new MultimediaManager(context, visitData.getId());

            List<MultimediaData> photos = mMultimediaManager.getMultimediaPhotos();
            List<MultimediaData> voices = mMultimediaManager.getMultimediaVoice();

            int number_photos = photos.size();
            int number_voice = voices.size();

            HashMap<String, String> track = visitData.getRoute_path();
            HashMap<String, String> aux = new HashMap<>(track.size());

            for (String key : track.keySet()) {
                if (track.get(key) != null) {
                    String[] aux2 = Objects.requireNonNull(track.get(key)).split("/");
                    String name = aux2[aux2.length - 1];
                    aux.put(key, name);
                }
            }

            String info = null;
            LocationModule mLocationModule = new LocationModule(context);
            try {
                info = mLocationModule.getPlotById(visitData.getPlot_id()).getInfo();
            } catch (Exception e) {
                e.printStackTrace();
            }

            VisitDataForJson v = new VisitDataForJson(visitData.getId(), visitData.getPlot_id(), info, time_begin, time_end, aux, number_photos, number_voice, visitData.getEoi_json(), visitData.getInfo_json());

            return gson.toJson(v);
        }
        return null;
    }


}
