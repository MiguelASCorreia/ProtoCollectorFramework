package com.example.protocollectorframework.RegistrationModule;

import android.content.Context;

import com.example.protocollectorframework.DataModule.ConfigTable;

import org.json.JSONArray;
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

public class ConfigurationManager {
    private ConfigTable mConfigTable;
    private Context context;

    public ConfigurationManager(Context context){
        this.context = context;
        mConfigTable = new ConfigTable(context);
    }

    public int getFileVersion(String name){
        if(mConfigTable != null)
            return mConfigTable.getConfigVersion(name);
        return 0;
    }

    public String getFilePath(String name){
        if(mConfigTable != null)
            return mConfigTable.getConfigPath(name);
        return null;
    }

    public String getFileEditTimestamp(String name){
        if(mConfigTable != null)
            return mConfigTable.getConfigEditTime(name);
        return null;
    }


    public String createFile(String name, int version, String path){
        if(mConfigTable != null)
            return mConfigTable.addConfigFile(name, version, path);
        return null;
    }

    public void editFile(String name, int version, String path){
        if(mConfigTable != null)
            mConfigTable.editConfig(name, version, path);
    }


    public JSONArray readProtocols(String file_name) {
        InputStream is = null;
        String protocol_json_path = mConfigTable.getConfigPath(file_name);
        if(protocol_json_path != null){
            File protocol_json_file = new File(protocol_json_path);
            if(protocol_json_file.exists()) {
                try {
                    is = new FileInputStream(protocol_json_file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }
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
            JSONArray array = jsonObject.getJSONArray("Protocols");
            return array;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
