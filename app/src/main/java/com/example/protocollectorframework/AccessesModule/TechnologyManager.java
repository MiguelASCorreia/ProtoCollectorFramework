package com.example.protocollectorframework.AccessesModule;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.HashSet;
import java.util.Set;

/**
 * Class accountable for all the logic associated with the management of the required technologies
 * Technologies are one type of access identified by the constant TECHNOLOGIES
 * Each one is identified by a string (Example: "WIFI") that explicits the need to use that technology
 * Four different identifiers are implemented, however, more can be created easily according to the needs of the application
 * This information is stored via the SharedPreferences
 */
public class TechnologyManager {

    // Identifier for internet connection
    public final static String INTERNET = "INTERNET";

    // Identifier for internet connection exclusively via Wi-Fi
    public final static String WIFI = "WIFI";

    // Identifier for bluetooth connection
    public final static String BLUETOOTH = "BLUETOOTH";

    // Identifier for GPS signal
    public final static String GPS = "GPS";

    public static String TECHNOLOGIES = "TECHNOLOGIES";

    private Context context;
    private SharedPreferences preferences;

    /**
     * Default constructor
     * @param context: current context
     */
    protected TechnologyManager(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(AccessesManager.ACCESSES_PREFS,Context.MODE_PRIVATE);
    }

    /**
     * Sets the required technologies for the application
     * @param identifiers: Set of technologies identifiers (Example: WIFI)
     */
    protected void setRequiredTechnologies(Set<String> identifiers){
        preferences.edit().putStringSet(TECHNOLOGIES,identifiers).apply();
    }

    /**
     * Returns the set of required technologies
     * @return Set of technologies identifiers
     */
    protected Set<String> getRequiredTechnologies(){
        return preferences.getStringSet(TECHNOLOGIES,new HashSet<>(0));
    }

    /**
     * Adds a required technologies
     * @param identifier: Technology identifier
     */
    protected void addRequiredTechnology(String identifier){
        Set<String> set = getRequiredTechnologies();
        set.add(identifier);
        setRequiredTechnologies(set);
    }

    /**
     * Deletes a required technologies
     * @param identifier: Technology identifier
     */
    protected void deleteRequiredTechnology(String identifier){
        Set<String> set = getRequiredTechnologies();
        set.remove(identifier);
        setRequiredTechnologies(set);
    }

    /**
     * Checks if the given technology is available
     * @param identifier: Technology identifier
     * @return true if the given technology is recognized and available, false otherwise
     */
    private boolean checkTechnology(String identifier){
        try {
            switch (identifier) {
                case INTERNET:
                    ConnectivityManager connManager1 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    return connManager1.getActiveNetworkInfo() != null && connManager1.getActiveNetworkInfo().isConnected();
                case WIFI:
                    ConnectivityManager connManager2 = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager2.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    return mWifi.isConnected();
                case BLUETOOTH:
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
                case GPS:
                    final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                    return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                default:
                    return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the technologies that are not available at the given moment
     * @return Set of technologies that are not available
     */
    protected  Set<String> getMissingRequiredTechnologies(){
        Set<String> required = preferences.getStringSet(TECHNOLOGIES, new HashSet<>(0));
        Set<String> missing = new HashSet<>();
        assert required != null;
        for(String identifier : required){
            if(!checkTechnology(identifier))
                missing.add(identifier);
        }
        return missing;
    }

    /**
     * Checks if all the required technologies are available
     * @return if all technologies are available, false otherwise
     */
    protected  boolean checkRequiredTechnologies(){
        return getMissingRequiredTechnologies().size() == 0;
    }
}
