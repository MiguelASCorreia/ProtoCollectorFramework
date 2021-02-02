package com.example.protocollectorframework.RegistrationModule;

import android.content.Context;

import com.example.protocollectorframework.DataModule.Data.ComplementaryData;
import com.example.protocollectorframework.DataModule.Data.MethodData;
import com.example.protocollectorframework.DataModule.Data.MultimediaData;
import com.example.protocollectorframework.DataModule.Data.PlotData;
import com.example.protocollectorframework.DataModule.Data.AbstractConfig;
import com.example.protocollectorframework.DataModule.Data.AbstractData;
import com.example.protocollectorframework.DataModule.Data.VisitData;
import com.example.protocollectorframework.LocationModule.LocationModule;
import com.example.protocollectorframework.MultimediaModule.MultimediaManager;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

/**
 * Class that is responsible for processing the resume configuration file and create resumes
 * The configuration file must respect the template that is shown
 */

public class AbstractManager {
    private static String VISIT_DATA = "visit_data";
    private static String VISIT_INFO = "visit_info";
    private static String COMPLEMENTARY_DATA = "complementary_data";
    private static String COMPLEMENTARY_INFO = "complementary_info";
    private static String PLOT_DATA = "plot_data";
    private static String PLOT_INFO = "plot_info";
    private static String MULTIMEDIA_COUNT = "multimedia_count";
    private static String GPS_INFO = "gps_info";

    private ConfigurationManager manager;
    private AbstractConfig abstractConfig;
    private VisitManager visitManager;
    private ComplementaryManager complementaryManager;
    private LocationModule locationManager;
    private MultimediaManager multimediaManager;
    private AbstractData abstractData;

    private Context context;

    /**
     * Default constructor
     * @param context: current context
     */
    public AbstractManager(Context context){
        this.context = context;
        this.manager = new ConfigurationManager(context);
    }


    /**
     * Constructor that calls the process method for a given configuration file
     * @param context: current context
     * @param abstract_file_name: abstract configuration file name
     */
    public AbstractManager(Context context, String abstract_file_name){
        this.context = context;
        this.manager = new ConfigurationManager(context);
        processAbstractConfigFile(abstract_file_name);
    }

    /**
     * Returns the extracted abstract configuration setup
     * @return abstract configuration setup data
     */
    public AbstractConfig getAbstractConfig(){
        return abstractConfig;
    }

    /**
     * Processes the abstract configuration file
     * @param file_name: abstract configuration file name
     * @return configuration setup data
     */
    public AbstractConfig processAbstractConfigFile(String file_name){
        InputStream is = null;
        String protocol_json_path = manager.getFilePath(file_name);
        if(protocol_json_path != null){
            File protocol_json_file = new File(protocol_json_path);
            if(protocol_json_file.exists()) {
                try {
                    is = new FileInputStream(protocol_json_file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    return null;
                }
            }else
                return null;
        }else
            return null;
        Writer writer = new StringWriter();
        char[] buffer = new char[1024 * 1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String flags_json = jsonObject.getString("Flags");
            String methods = jsonObject.getString("Methods");

            Gson gson = new Gson();
            Type type = new TypeToken<AbstractConfig>() {
            }.getType();

            AbstractConfig auxConfig =  gson.fromJson(flags_json, type);

            type = new TypeToken<MethodData[]>() {
            }.getType();

            MethodData[] methodData = gson.fromJson(methods, type);

            auxConfig.setMethods(methodData);

            abstractConfig = auxConfig;

            return abstractConfig;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }

    /**
     * Invokes a method given an array of arguments
     * @param methodData: the desired method
     * @param args: array of arguments
     * @return the returned value of the given method
     */
    public static Object invokeUnknownMethod(MethodData methodData, Object[] args){
        try {
            Class<?> act = Class.forName(methodData.getPackage_class_name());
            Method methodToFind = act.getMethod(methodData.getMethod_name(), methodData.getProcessedClasses());
            return methodToFind.invoke(act, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     *
     * Processes the abstract configuration data at generates and abstract object data with the desired information
     * @param visit_id: visit's identifier
     * @param argsByMethod: structure that maps for each method and array of arguments
     * @return abstract object data with the desired information
     */
    public AbstractData getAbstractForVisit(String visit_id, HashMap<String,HashMap<String,Object[]>> argsByMethod){

        visitManager = new VisitManager(context);
        complementaryManager = new ComplementaryManager(context);
        locationManager = new LocationModule(context);

        long visit_start = -1;
        long visit_end = -1;
        String visit_info = null;

        String complementary_id = null;
        long complementary_start = -1;
        long complementary_end = -1;
        String complementary_info = null;

        String plot_id = null;
        String plot_acronym = null;
        String plot_name = null;
        String plot_info = null;

        HashMap<String,HashMap<String,Object>> resultsForMethods = null;

        HashMap<String, Integer> multimediaCountByType = null;

        VisitData visitData = visitManager.getVisitByID(visit_id);

        if(abstractConfig.isVisitDataAccountable()) {
            visit_start = visitData.getStart_time();
            visit_end = visitData.getEnd_time();
            if(abstractConfig.isVisitInfoAccountable())
                visit_info = visitData.getInfo_json();
        }

        if(abstractConfig.isComplementaryDataAccountable()) {
            ComplementaryData complementaryData = complementaryManager.getComplementaryByVisitId(visit_id);
            if(complementaryData != null) {
                complementary_id = complementaryData.getId();
                complementary_start = complementaryData.getStart_time();
                complementary_end = complementaryData.getEnd_time();
                if (abstractConfig.isComplementaryInfoAccountable())
                    complementary_info = visitData.getInfo_json();
            }
        }

        if(abstractConfig.isPlotDataAccountable()){
            PlotData plotData = locationManager.getPlotById(visitData.getPlot_id());
            plot_id = plotData.getID();
            plot_acronym = plotData.getAcronym();
            plot_name = plotData.getName();
            if(abstractConfig.isPlotInfoAccountable()){
                plot_info = plotData.getInfo();
            }
        }

        if(abstractConfig.isMultimediaCountAccountable()) {
            multimediaManager = new MultimediaManager(context, visit_id);
            List<MultimediaData> multimediaDataList = multimediaManager.getVisitMultimedia();
            multimediaCountByType = new HashMap<>(multimediaDataList.size());
            for (MultimediaData multimediaData : multimediaDataList) {
                String type = multimediaData.getType();
                int value = 1;
                if (multimediaCountByType.containsKey(type))
                    value += multimediaCountByType.get(type);
                multimediaCountByType.put(type, value);
            }
        }

        if(abstractConfig.getMethods() != null){
            resultsForMethods = new HashMap<>(abstractConfig.getMethods().length);
            for(MethodData methodData : abstractConfig.getMethods()){
                Object[] args = null;
                String package_name = methodData.getPackage_class_name();
                String method_name = methodData.getMethod_name();
                if(argsByMethod.containsKey(package_name) && argsByMethod.get(package_name) != null && argsByMethod.get(package_name).containsKey(method_name))
                    args = argsByMethod.get(package_name).get(method_name);
                if(resultsForMethods.get(package_name) == null)
                    resultsForMethods.put(package_name,new HashMap<>());
                resultsForMethods.get(package_name).put(method_name,invokeUnknownMethod(methodData,args));
            }

        }

        return new AbstractData(visit_id,visit_start,visit_end,visit_info,complementary_id,complementary_start,complementary_end,complementary_info,plot_id,plot_acronym,plot_name,plot_info,multimediaCountByType,resultsForMethods);
    }

}
