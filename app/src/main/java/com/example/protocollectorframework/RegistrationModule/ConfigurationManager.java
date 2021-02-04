package com.example.protocollectorframework.RegistrationModule;

import android.content.Context;

import com.example.protocollectorframework.DataModule.DataBase.ConfigTable;

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
import java.nio.charset.StandardCharsets;

/**
 * Class accountable for all the logic associated with configuration files
 */
public class ConfigurationManager {
    private ConfigTable mConfigTable;

    /**
     * Constructor
     *
     * @param context: context of the activity
     */
    public ConfigurationManager(Context context) {
        mConfigTable = new ConfigTable(context);
    }

    /**
     * Returns the file version for the given file
     *
     * @param name: configuration file's name
     * @return file's version
     */
    public int getFileVersion(String name) {
        if (mConfigTable != null)
            return mConfigTable.getConfigVersion(name);
        return 0;
    }

    /**
     * Returns the path of the external storage for the given file
     *
     * @param name: configuration file's name
     * @return file's path
     */
    public String getFilePath(String name) {
        if (mConfigTable != null)
            return mConfigTable.getConfigPath(name);
        return null;
    }

    /**
     * Returns the file's edition timestamp
     *
     * @param name: configuration file's name
     * @return file's edition timestamp
     */
    public String getFileEditTimestamp(String name) {
        if (mConfigTable != null)
            return mConfigTable.getConfigEditTime(name);
        return null;
    }

    /**
     * Creates a configuration file given it's name, version and path to external storage
     *
     * @param name:    file's name
     * @param version: file's version
     * @param path:    file's path
     * @return file's identifier
     */
    public String createFile(String name, int version, String path) {
        if (mConfigTable != null)
            return mConfigTable.addConfigFile(name, version, path);
        return null;
    }

    /**
     * Edits a configuration file with the give name changing it's version and path to the external storage
     *
     * @param name:    file's name
     * @param version: file's version
     * @param path:    file's path
     */
    public void editFile(String name, int version, String path) {
        if (mConfigTable != null)
            mConfigTable.editConfig(name, version, path);
    }


    /**
     * Process one configuration file, given it's name, and extracts all the protocols that are associated to the field "Protocols"
     *
     * @param file_name: protocol file's name
     * @return JSONArray containing the information for each protocol
     */
    public JSONArray readProtocols(String file_name) {
        InputStream is = null;
        String protocol_json_path = mConfigTable.getConfigPath(file_name);
        if (protocol_json_path != null) {
            File protocol_json_file = new File(protocol_json_path);
            if (protocol_json_file.exists()) {
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
            Reader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();

        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject.getJSONArray("Protocols");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
