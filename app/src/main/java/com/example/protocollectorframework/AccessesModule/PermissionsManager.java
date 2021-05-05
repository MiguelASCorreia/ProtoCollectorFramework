package com.example.protocollectorframework.AccessesModule;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.HashSet;
import java.util.Set;
/**
 * Class accountable for all the logic associated with the management of permissions (checking and requiring)
 * Permissions are one type of access identified by the constant PERMISSIONS
 * This information is stored via the SharedPreferences
 */
public class PermissionsManager {

    private static final int REQUEST_PERMISSIONS_CODE = 0;
    public static String PERMISSIONS = "PERMISSIONS";

    private Context context;
    private SharedPreferences preferences;

    /**
     * Default constructor
     * @param context: current context
     */
    protected PermissionsManager(Context context){
        this.context = context;
        preferences = context.getSharedPreferences(AccessesManager.ACCESSES_PREFS,Context.MODE_PRIVATE);
    }

    /**
     * Sets the required permissions for the application
     * @param identifiers: Set of permissions identifiers (Example: Manifest.permission.ACCESS_WIFI_STATE)
     */
    protected void setRequiredPermissions(Set<String> identifiers){
        preferences.edit().putStringSet(PERMISSIONS,identifiers).apply();
    }

    /**
     * Returns the set of required permissions
     * @return Set of permissions identifiers
     */
    protected Set<String> getRequiredPermissions(){
        return preferences.getStringSet(PERMISSIONS,new HashSet<>(0));
    }

    /**
     * Adds a required permission
     * @param identifier: Permission identifier
     */
    protected void addRequiredPermission(String identifier){
        Set<String> set = getRequiredPermissions();
        set.add(identifier);
        setRequiredPermissions(set);
    }

    /**
     * Deletes a required permission
     * @param identifier: Permission identifier
     */
    protected void deleteRequiredPermission(String identifier){
        Set<String> set = getRequiredPermissions();
        set.remove(identifier);
        setRequiredPermissions(set);
    }

    /**
     * Checks if the given permission is granted
     * @param identifier: Permission identifier
     * @return true if the given permission is granted, false otherwise
     */
    private boolean checkPermission(String identifier){
        return ContextCompat.checkSelfPermission(context, identifier) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Returns the permissions that are not granted at the given moment
     * @return Set of permissions that are not granted
     */
    protected  Set<String> getMissingRequiredPermissions(){
        Set<String> required = preferences.getStringSet(PERMISSIONS, new HashSet<>(0));
        Set<String> missing = new HashSet<>();
        assert required != null;
        for(String identifier : required){
            if(!checkPermission(identifier))
                missing.add(identifier);
        }
        return missing;
    }

    /**
     * Requests the missing required permissions
     * For this method to be successful, the given context must be and instance of Activity
     */
    protected void requestMissingPermissions(){
        Set<String> set = getMissingRequiredPermissions();
        if(set.size() > 0){
            try{
                Activity activity = (Activity) context;
                String[] permissions = new String[set.size()];
                int i = 0;
                for(String permission : set){
                    permissions[i++] = permission;
                }
                ActivityCompat.requestPermissions(activity,permissions,REQUEST_PERMISSIONS_CODE);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Checks if all the required permissions are granted
     * @return if all permissions are granted, false otherwise
     */
    protected  boolean checkRequiredPermissions(){
        return getMissingRequiredPermissions().size() == 0;
    }
}
