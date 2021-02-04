package com.example.protocollectorframework.InterfaceModule;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.protocollectorframework.DataModule.Data.ComponentBuildInfo;
import com.example.protocollectorframework.DataModule.Data.ComponentData;
import com.example.protocollectorframework.DataModule.Data.ComponentView;
import com.example.protocollectorframework.DataModule.Data.HelperData;
import com.example.protocollectorframework.DataModule.Data.PlotData;
import com.example.protocollectorframework.RegistrationModule.ConfigurationManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Class that process the protocol specification language a generates the associated views and data
 */
public class ProtocolViewGenerator {

    private Context context;
    private HashMap<String, JSONObject> protocolsByTag;
    private List<String> mainProtocols;
    private HashMap<String, Integer> numberOfEOIsPerProtocol;

    private SortedSet<String> hiddenProtocols;
    private HashMap<String, HashMap<String, List<ComponentView>>> viewsPerProtocol;
    private HashMap<String, List<ComponentBuildInfo>> generalObservations;
    private HashMap<String, HashMap<String, List<Integer>>> limitedObservations;
    private HashMap<String, HashMap<String, List<HelperData>>> helpersPerProtocol;

    private ComponentGenerator mComponentAPI;

    /**
     * Constructor with activity's handler
     *
     * @param context:         current activity context
     * @param incomingHandler: activity handler to handle the modification of the data fields
     */
    public ProtocolViewGenerator(Context context, Handler incomingHandler) {
        this.context = context;
        this.mComponentAPI = new ComponentGenerator(context, incomingHandler);
    }

    /**
     * Changes the flag that indicates if the values must be sent to the activity handler after the view change
     *
     * @param flag: true if value must be sent
     */
    public void setOnChangeFlag(boolean flag) {
        this.mComponentAPI.setSaveValues(flag);
    }

    /**
     * Returns the component data associated to the previously generated view
     *
     * @param cv: desired component view
     * @return component data associated to the interface view
     */
    public ComponentData getComponent(ComponentView cv) {
        return this.mComponentAPI.getComponent(cv);
    }

    /**
     * Sets the component view value based on the corresponding component data
     *
     * @param cd: corresponding component data
     * @param cv: desired component view
     */
    public void setComponentValue(ComponentData cd, ComponentView cv) {
        this.mComponentAPI.setComponentValue(cd, cv);
    }

    /**
     * Returns the JSONObject data associated with each protocol
     *
     * @return structure that maps each protocol name to the corresponding JSONObject
     */
    public HashMap<String, JSONObject> getProtocolsByTag() {
        return protocolsByTag;
    }

    /**
     * Returns the main protocols for the given plot
     *
     * @return main protocols for the given plot
     */
    public List<String> getMainProtocols() {
        return mainProtocols;
    }

    /**
     * Returns the number of EOIs associated with each protocol
     *
     * @return structure that maps each protocol name to the corresponding EOI count
     */
    public HashMap<String, Integer> getNumberOfEOIsPerProtocol() {
        return numberOfEOIsPerProtocol;
    }


    /**
     * Returns the protocols that are not enabled on the current type of the year
     *
     * @return structure that contains the protocols not enabled
     */
    public SortedSet<String> getHiddenProtocols() {
        return hiddenProtocols;
    }

    /**
     * Returns each view associated with each observation with each protocol
     *
     * @return structure that maps each protocol name to the observations and associated views
     */
    public HashMap<String, HashMap<String, List<ComponentView>>> getViewsPerProtocol() {
        return viewsPerProtocol;
    }

    /**
     * Returns the component build info that is necessary to generate the general observation data fields
     *
     * @return structure that maps the protocol name to the observations component build info
     */
    public HashMap<String, List<ComponentBuildInfo>> getGeneralObservations() {
        return generalObservations;
    }

    /**
     * Returns the observations that are limited to certain EOIs in each protocol
     *
     * @return structure that maps each protocol name to the observations and associated limited EOIs
     */
    public HashMap<String, HashMap<String, List<Integer>>> getLimitedObservations() {
        return limitedObservations;
    }

    /**
     * Returns the list of helpers associated to an observation for each protocol. Helpers are used to teach the user how to proceed for a given observation
     *
     * @return list of helpers associated to an observation for each protocol
     */
    public HashMap<String, HashMap<String, List<HelperData>>> getHelpersPerProtocol() {
        return helpersPerProtocol;
    }

    /**
     * Process the protocol configuration file and extracts the data depending on the plot
     *
     * @param plotData:            plot object data
     * @param protocols_tag:       tag associated with the protocols in the extra information of the plot
     * @param protocols_file_name: protocol's configuration file name
     * @param eoi_type:            EOIs name type, used to filter the protocols for those EOIs
     * @return count of total EOIs
     */
    public int processProtocolsForPlot(PlotData plotData, String protocols_tag, String protocols_file_name, String eoi_type) {

        int eois = 0;

        ConfigurationManager cf = new ConfigurationManager(context);
        JSONArray jsonArray = cf.readProtocols(protocols_file_name);

        if (jsonArray != null) {


            protocolsByTag = new HashMap<>(jsonArray.length());
            numberOfEOIsPerProtocol = new HashMap<>(jsonArray.length());
            helpersPerProtocol = new HashMap<>(jsonArray.length());

            try {

                JSONArray protocols = plotData.getArrayField(protocols_tag);
                if (protocols != null) {
                    mainProtocols = new ArrayList<>(protocols.length());
                    for (int i = 0; i < protocols.length(); i++) {
                        mainProtocols.add(protocols.getString(i));
                    }
                }

                for (int y = 0; y < jsonArray.length(); y++) {
                    JSONObject protocol = jsonArray.getJSONObject(y);
                    String type = null;
                    if (protocol.getJSONObject("eoi").has("name"))
                        type = protocol.getJSONObject("eoi").getString("name");

                    if (type != null && type.equals(eoi_type)) {

                        protocolsByTag.put(protocol.getString("name"), protocol);
                        int numberOfEOIs = 0;

                        if (protocol.getJSONObject("eoi").has("number"))
                            numberOfEOIs = protocol.getJSONObject("eoi").getInt("number");


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


    /**
     * Returns the views associated to the protocol observations
     *
     * @param protocol:     protocol's name
     * @param observations: observations structure
     * @return views associated to the protocol observations
     */
    private HashMap<String, List<ComponentView>> getViews(String protocol, JSONArray observations) {
        HashMap<String, List<ComponentView>> observations_map = new HashMap<>(observations.length());


        for (int w = 0; w < observations.length(); w++) {
            try {
                JSONObject ob = observations.getJSONObject(w);

                String name = ob.getString("name");
                JSONArray iterations = ob.getJSONArray("iterations");

                if (ob.has("limited_to")) {
                    JSONArray array = ob.optJSONArray("limited_to");
                    if (array == null)
                        continue;

                    List<Integer> limited_to = new ArrayList<>(array.length());
                    for (int l = 0; l < array.length(); l++) {
                        limited_to.add(l, array.getInt(l));
                    }

                    if (limitedObservations.get(protocol) == null)
                        limitedObservations.put(protocol, new HashMap<String, List<Integer>>());

                    Objects.requireNonNull(limitedObservations.get(protocol)).put(name, limited_to);


                }

                if (ob.has("helper")) {
                    JSONArray array = ob.optJSONArray("helper");
                    for (int l = 0; l < array.length(); l++) {
                        JSONObject helper = array.getJSONObject(l);
                        HelperData helperData;
                        if (helper.has("extra"))
                            helperData = new HelperData(helper.getInt("position"), helper.getString("title"), helper.getString("message"), helper.getString("extra"));
                        else
                            helperData = new HelperData(helper.getInt("position"), helper.getString("title"), helper.getString("message"));

                        if (!helpersPerProtocol.containsKey(protocol))
                            helpersPerProtocol.put(protocol, new HashMap<>());
                        if (!Objects.requireNonNull(helpersPerProtocol.get(protocol)).containsKey(name))
                            Objects.requireNonNull(helpersPerProtocol.get(protocol)).put(name, new ArrayList<>());
                        Objects.requireNonNull(Objects.requireNonNull(helpersPerProtocol.get(protocol)).get(name)).add(helperData);
                    }
                }

                for (int z = 0; z < iterations.length(); z++) {
                    JSONObject iteration_obj = iterations.getJSONObject(z);

                    ComponentBuildInfo buildInfo = setComponent(iteration_obj, name, protocol);
                    if (buildInfo == null)
                        continue;

                    ComponentView cv = mComponentAPI.setComponent(buildInfo);
                    if (cv == null)
                        continue;

                    if (!observations_map.containsKey(name) || observations_map.get(name) == null)
                        observations_map.put(name, new ArrayList<>());

                    if (observations_map.get(name) != null)
                        Objects.requireNonNull(observations_map.get(name)).add(cv);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return observations_map;
    }

    /**
     * Returns the component build info based on the observation's iteration associated to the protocol
     *
     * @param iteration_obj: observation's iteration structure
     * @param ob_name:       observation's name
     * @param protocol:      protocol's name
     * @return component build info extracted from the protocol data field
     */
    private ComponentBuildInfo setComponent(JSONObject iteration_obj, String ob_name, String protocol) {

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
            if (iteration_obj.has("units"))
                units = iteration_obj.getString("units");

            if (iteration_obj.has("value_type"))
                value_type = iteration_obj.getString("value_type");

            if (type == ComponentGenerator.COMPONENT_COUNT) {
                //["(1,20,1)","20+","100"];
                JSONArray offset = iteration_obj.getJSONArray("offset");
                temp_values = new ArrayList<>(offset.length());
                for (int x = 0; x < offset.length(); x++) {
                    String offset_value = offset.getString(x);
                    if (offset_value.charAt(0) == '(' && offset_value.charAt(offset_value.length() - 1) == ')') {
                        String aux = offset_value.replace("(", "").replace(")", "");
                        String[] tuple = aux.split(",");
                        try {
                            if (value_type == null || value_type.equals("integer")) {
                                int first_value = Integer.parseInt(tuple[0]);
                                int last_value = Integer.parseInt(tuple[1]);
                                int step = Integer.parseInt(tuple[2]);
                                for (int m = first_value; m <= last_value; m += step)
                                    temp_values.add(Integer.toString(m));
                            } else {
                                double first_value = Double.parseDouble(tuple[0]);
                                double last_value = Double.parseDouble(tuple[1]);
                                double step = Double.parseDouble(tuple[2]);
                                for (double m = first_value; m <= last_value; m += step)
                                    temp_values.add(Double.toString(m));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    } else {
                        temp_values.add(offset_value);
                    }
                }

            } else if (type == ComponentGenerator.COMPONENT_TIME) {
                temporal_type = iteration_obj.getString("subtype");
            } else if (type == ComponentGenerator.COMPONENT_CATEGORY) {
                JSONArray arr = iteration_obj.getJSONArray("values");
                temp_values = new ArrayList<>(arr.length());
                for (int i = 0; i < arr.length(); i++) {
                    temp_values.add(arr.getString(i));
                }
                if (iteration_obj.has("unique"))
                    unique = iteration_obj.getBoolean("unique");
            } else if (type == ComponentGenerator.COMPONENT_INTERVAL) {
                JSONArray first = iteration_obj.getJSONArray("first");
                firstValues = new String[first.length()];
                for (int x = 0; x < first.length(); x++) {
                    firstValues[x] = first.getString(x);
                }


                if (iteration_obj.has("last")) {
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

            return new ComponentBuildInfo(type, units, label, value_type, min, max, finalValues, temporal_type, firstValues, lastValues, unique, ob_name, protocol);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Create all views for the current protocols
     *
     * @return true if success, false otherwise
     */
    public boolean fillViews() {
        hiddenProtocols = new TreeSet<>();
        viewsPerProtocol = new HashMap<>();
        generalObservations = new HashMap<>();
        limitedObservations = new HashMap<>();

        for (String key : protocolsByTag.keySet()) {
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

                viewsPerProtocol.put(key, getViews(key, protocol.getJSONArray("observations")));


                if (protocol.has("general_data")) {
                    JSONArray observations_for_visit_for_protocol = protocol.getJSONArray("general_data");
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
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }


}
