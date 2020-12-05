package com.example.protocollectorframework.InterfaceModule;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.protocollectorframework.DataModule.Data.ComponentBuildInfo;
import com.example.protocollectorframework.DataModule.Data.ComponentView;
import com.example.protocollectorframework.DataModule.Data.PlotData;
import com.example.protocollectorframework.RegistrationModule.ConfigurationManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

public class ProtocolViewGenerator {

    private Context context;
    private HashMap<String, JSONObject> protocolsByTag;
    private List<String> mainProtocols;
    private HashMap<String, List<String>> phasesProtocols;
    private HashMap<String, Integer> numberOfEOIsPerProtocol;
    private HashMap<String, String> selectedPhasePerProtocols;

    private SortedSet<String> hiddenProtocols;
    private HashMap<String, HashMap<String, List<ComponentView>>> viewsPerProtocol;
    private HashMap<String, List<ComponentBuildInfo>> generalObservations;
    private HashMap<String, HashMap<String, List<Integer>>> limitedObservations;

    private ComponentsAPI mComponentAPI;

    public ProtocolViewGenerator(Context context, Handler incomingHandler){
        this.context = context;
        this.mComponentAPI = new ComponentsAPI(context,incomingHandler);
    }

    public HashMap<String, JSONObject> getProtocolsByTag() {
        return protocolsByTag;
    }

    public List<String> getMainProtocols() {
        return mainProtocols;
    }

    public HashMap<String, List<String>> getPhasesProtocols() {
        return phasesProtocols;
    }

    public HashMap<String, Integer> getNumberOfEOIsPerProtocol() {
        return numberOfEOIsPerProtocol;
    }

    public HashMap<String, String> getSelectedPhasePerProtocols() {
        return selectedPhasePerProtocols;
    }

    public SortedSet<String> getHiddenProtocols() {
        return hiddenProtocols;
    }

    public HashMap<String, HashMap<String, List<ComponentView>>> getViewsPerProtocol() {
        return viewsPerProtocol;
    }

    public HashMap<String, List<ComponentBuildInfo>> getGeneralObservations() {
        return generalObservations;
    }

    public HashMap<String, HashMap<String, List<Integer>>> getLimitedObservations() {
        return limitedObservations;
    }

    public int processProtocolsForPlot(PlotData plotData, String protocols_file_name) {

        int eois = 0;

        ConfigurationManager cf = new ConfigurationManager(context);
        JSONArray jsonArray = cf.readProtocols(protocols_file_name);

        if (jsonArray != null) {


            protocolsByTag = new HashMap<>(jsonArray.length());
            numberOfEOIsPerProtocol = new HashMap<>(jsonArray.length());
            phasesProtocols = new HashMap<>(jsonArray.length());
            selectedPhasePerProtocols = new HashMap<>();

            try {

                String[] mp = plotData.getProtocols();
                if (mp != null)
                    mainProtocols = Arrays.asList(mp);


                for (int y = 0; y < jsonArray.length(); y++) {
                    JSONObject protocol = jsonArray.getJSONObject(y);
                    protocolsByTag.put(protocol.getString("name"), protocol);
                    int numberOfEOIs = 0;

                    String observation_type;
                    String observation_number;
                    if (mainProtocols != null && mainProtocols.contains(protocol.getString("name"))) {
                        observation_type = "observations_specific";
                        observation_number = "number_specific";
                    } else {
                        observation_type = "observations_general";
                        observation_number = "number_general";
                    }

                    if (protocol.has("phases")) {
                        JSONArray phases = protocol.getJSONArray("phases");
                        List<String> phases_names = new ArrayList<>(phases.length());
                        for (int i = 0; i < phases.length(); i++) {
                            JSONObject actualPhase = phases.getJSONObject(i);
                            if (actualPhase.has(observation_type))
                                phases_names.add(actualPhase.getString("name"));
                        }
                        if (phases_names.size() > 0)
                            phasesProtocols.put(protocol.getString("name"), phases_names);
                    } else {
                        if (protocol.getJSONObject("eoi").has(observation_number))
                            numberOfEOIs = protocol.getJSONObject("eoi").getInt(observation_number);

                        numberOfEOIsPerProtocol.put(protocol.getString("name"), numberOfEOIs);

                        if (eois < numberOfEOIs)
                            eois = numberOfEOIs;
                    }


                }
                return eois;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return -1;
    }


    public int selectPhase(int eois, String selectedPhase, List<String> protocolsWithPhases){
        selectedPhasePerProtocols.put(protocolsWithPhases.get(0),selectedPhase);
        try {
            JSONArray phases = protocolsByTag.get(protocolsWithPhases.get(0)).getJSONArray("phases");
            int phases_eois_count = 0;
            for(int i = 0; i < phases.length(); i++){
                JSONObject phase = phases.getJSONObject(i);
                if(phase.getString("name").equals(selectedPhase)) {
                    if (mainProtocols.contains(protocolsWithPhases.get(0))) {
                        phases_eois_count = phase.getJSONObject("eoi").getInt("number_specific");
                    } else if (phase.getJSONObject("eoi").has("number_general")) {
                        phases_eois_count = phase.getJSONObject("eoi").getInt("number_general");
                    }
                }
            }

            numberOfEOIsPerProtocol.put(protocolsWithPhases.get(0),phases_eois_count);
            if(eois < phases_eois_count)
                return phases_eois_count;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return eois;
    }



    private HashMap<String, List<ComponentView>> getViews(String key, JSONArray observations){
        HashMap<String, List<ComponentView>> observations_map = new HashMap<>(observations.length());


        for(int w = 0; w < observations.length(); w++){
            try {
                JSONObject ob = observations.getJSONObject(w);

                String name = ob.getString("name");
                JSONArray iterations = ob.getJSONArray("iterations");

                if (ob.has("limited_to")) {
                    JSONArray array = ob.optJSONArray("limited_to");
                    if (array == null)
                        continue;

                    List<Integer> limited_to = new ArrayList<>(array.length());
                    for(int l = 0; l < array.length(); l++){
                        limited_to.add(l,array.getInt(l));
                    }

                    if(limitedObservations.get(key) == null)
                        limitedObservations.put(key,new HashMap<String, List<Integer>>());

                    limitedObservations.get(key).put(name,limited_to);


                }
                for (int z = 0; z < iterations.length(); z++) {
                    JSONObject iteration_obj = iterations.getJSONObject(z);

                    ComponentBuildInfo buildInfo = setComponent(iteration_obj,name,key);
                    if (buildInfo == null)
                        continue;

                    ComponentView cv = mComponentAPI.setComponent(buildInfo);
                    if(cv == null)
                        continue;

                    if (!observations_map.containsKey(name) || observations_map.get(name) == null)
                        observations_map.put(name, new ArrayList<>());

                    if(observations_map.get(name) != null)
                        observations_map.get(name).add(cv);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return observations_map;
    }

    private ComponentBuildInfo setComponent(JSONObject iteration_obj, String ob_name, String protocol){

        List<String> temp_values = null;
        String temporal_type = null;
        String[] finalValues = null;
        String[] firstValues = null;
        String[] lastValues = null;
        String value_type = "integer";
        boolean unique = true;

        try {

            String label = iteration_obj.getString("name");

            int type = iteration_obj.getInt("data_type");
            String units = null;
            if(iteration_obj.has("units"))
                units = iteration_obj.getString("units");

            if(iteration_obj.has("value_type"))
                value_type = iteration_obj.getString("value_type");

            if (type == ComponentsAPI.COMPONENT_COUNT) {
                //["(1,20,1)","20+","100"];
                JSONArray offset = iteration_obj.getJSONArray("offset");
                temp_values = new ArrayList<>(offset.length());
                for (int x = 0; x < offset.length(); x++) {
                    String offset_value = offset.getString(x);
                    if (offset_value.charAt(0) == '(' && offset_value.charAt(offset_value.length() - 1) == ')') {
                        String aux = offset_value.replace("(", "").replace(")", "");
                        String[] tuple = aux.split(",");
                        try {
                            int first_value = Integer.parseInt(tuple[0]);
                            int last_value = Integer.parseInt(tuple[1]);
                            int step = Integer.parseInt(tuple[2]);
                            for (int m = first_value; m <= last_value; m += step) {
                                temp_values.add(Integer.toString(m));

                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        temp_values.add(offset_value);
                    }
                }

            } else if (type == ComponentsAPI.COMPONENT_TIME) {
                temporal_type = iteration_obj.getString("subtype");
            }else if(type == ComponentsAPI.COMPONENT_CATEGORY){
                JSONArray arr = iteration_obj.getJSONArray("values");
                temp_values = new ArrayList<>(arr.length());
                for(int i = 0; i < arr.length(); i++){
                    temp_values.add(arr.getString(i));
                }
                if(iteration_obj.has("unique"))
                    unique = iteration_obj.getBoolean("unique");
            }else if(type == ComponentsAPI.COMPONENT_INTERVAL){
                JSONArray first = iteration_obj.getJSONArray("first");
                firstValues = new String[first.length()];
                for (int x = 0; x < first.length(); x++) {
                    firstValues[x] = first.getString(x);
                }



                if( iteration_obj.has("last")) {
                    JSONArray last = iteration_obj.getJSONArray("last");
                    lastValues = new String[last.length()];
                    for (int x = 0; x < last.length(); x++) {
                        lastValues[x] = last.getString(x);
                    }
                }
            }

            int max = Integer.MAX_VALUE;
            int min = Integer.MIN_VALUE;
            if (iteration_obj.has("max"))
                max = iteration_obj.getInt("max");
            if (iteration_obj.has("min"))
                min = iteration_obj.getInt("min");

            if (temp_values != null) {
                finalValues = temp_values.toArray(new String[0]);
            }

            return new ComponentBuildInfo(type, units, label, value_type, min, max, finalValues, temporal_type, ob_name, protocol,firstValues,lastValues,unique);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }


    //mlimitedObservationsEOis
    //hidden
    //mViewsPErProtocols
    //mVisitObservations

    public boolean fillViews(){
        hiddenProtocols = new TreeSet<>();
        viewsPerProtocol = new HashMap<>();
        generalObservations = new HashMap<>();
        limitedObservations = new HashMap<>();

        for(String key : protocolsByTag.keySet()) {
            JSONObject protocol = protocolsByTag.get(key);
            if (protocol == null)
                continue;
            try {

                if (protocol.has("date_min") && protocol.has("date_max")) {
                    String minDate = protocol.getString("date_min");
                    String maxDate = protocol.getString("date_max");
                    String actual = new SimpleDateFormat("MM/dd", Locale.getDefault()).format(new Date());
                    try {
                        int minMonth = Integer.parseInt(minDate.split("/")[0]);
                        int minDay = Integer.parseInt(minDate.split("/")[1]);

                        int maxMonth = Integer.parseInt(maxDate.split("/")[0]);
                        int maxDay = Integer.parseInt(maxDate.split("/")[1]);


                        int actualMonth = Integer.parseInt(actual.split("/")[0]);
                        int actualDay = Integer.parseInt(actual.split("/")[1]);

                        if (actualMonth < minMonth || actualMonth > maxMonth || (actualMonth == minMonth && actualDay < minDay) || (actualMonth == maxMonth && actualDay > maxDay))
                            hiddenProtocols.add(key);

                    } catch (Exception e) {
                        Log.e("Date_format_error", e.toString());
                        e.printStackTrace();
                    }
                }

                JSONObject target;
                if (!protocol.has("phases")) {
                    target = protocol;
                    if (mainProtocols.contains(key))
                        viewsPerProtocol.put(key, getViews(key, protocol.getJSONArray("observations_specific")));
                    else if (protocol.getJSONObject("eoi").has("number_general"))
                        viewsPerProtocol.put(key, getViews(key, protocol.getJSONArray("observations_general")));

                } else {
                    JSONArray phases = protocol.getJSONArray("phases");
                    JSONObject phase = null;
                    for (int w = 0; w < phases.length(); w++) {
                        JSONObject aux = phases.getJSONObject(w);
                        if (aux.getString("name").equals(selectedPhasePerProtocols.get(key))) {
                            phase = aux;
                        }
                    }
                    if (phase != null) {
                        target = phase;
                        if (mainProtocols.contains(key))
                            viewsPerProtocol.put(key, getViews(key, phase.getJSONArray("observations_specific")));
                        else if (phase.getJSONObject("eoi").has("number_general"))
                            viewsPerProtocol.put(key, getViews(key, protocol.getJSONArray("observations_general")));
                    } else
                        target = null;

                }


                if (target != null && target.has("observations_for_visit")) {
                    JSONArray observations_for_visit_for_protocol = target.getJSONArray("observations_for_visit");
                    List<ComponentBuildInfo> observations_for_visit_data = new ArrayList<>(observations_for_visit_for_protocol.length());
                    for (int z = 0; z < observations_for_visit_for_protocol.length(); z++) {
                        JSONObject ob = observations_for_visit_for_protocol.getJSONObject(z);
                        ComponentBuildInfo cv = setComponent(ob, null, key);
                        if (cv == null)
                            continue;
                        observations_for_visit_data.add(cv);
                    }
                    generalObservations.put(key, observations_for_visit_data);
                }
            }catch (Exception e){
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }







}
