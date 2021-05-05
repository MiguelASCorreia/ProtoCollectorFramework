package com.example.protocollectorframework.AccessesModule;

import android.content.Context;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
/**
 * Class accountable for all the logic associated with the management of resources for the normal operation of the desired system
 * The accesses resources are divided in permissions and available technology. Each of this has their unique manager which are used by this class.
 * Each type is defined by a string constant
 * Permissions: PERMISSIONS
 * Required technology: TECHNOLOGIES
 */
public class AccessesManager {

    // Number of distinct accesses types (permissions and available technology)
    private final static int ACCESSES_TYPES = 2;

    protected final static String ACCESSES_PREFS = "ACCESSES_PREFS";

    private Context context;

    private PermissionsManager permissionsManager;
    private TechnologyManager technologyManager;

    /**
     * Default constructor
     * @param context: current context
     */
    public AccessesManager(Context context){
        this.context = context;
        permissionsManager = new PermissionsManager(context);
        technologyManager = new TechnologyManager(context);
    }

    /**
     * Return the required accesses mapped by type
     * @return Structure that maps for each type of access, the set of string identifiers
     */
    public HashMap<String, Set<String>> getRequiredAccesses(){
        HashMap<String,Set<String>> accesses = new HashMap<>(ACCESSES_TYPES);
        accesses.put(PermissionsManager.PERMISSIONS, permissionsManager.getRequiredPermissions());
        accesses.put(TechnologyManager.TECHNOLOGIES, technologyManager.getRequiredTechnologies());
        return accesses;
    }

    /**
     * Sets the required accesses
     * @param requiredAccesses: structure that maps for each type of access, the set of string identifiers
     */
    public void setRequiredAccesses(HashMap<String, Set<String>> requiredAccesses){
        if(requiredAccesses.containsKey(PermissionsManager.PERMISSIONS))
            permissionsManager.setRequiredPermissions(requiredAccesses.get(PermissionsManager.PERMISSIONS));
        if(requiredAccesses.containsKey(TechnologyManager.TECHNOLOGIES))
            technologyManager.setRequiredTechnologies(requiredAccesses.get(TechnologyManager.TECHNOLOGIES));
    }

    /**
     * Adds a required accesses for a given type
     * @param type: type of access
     * @param requiredPermission: identifier of the given access
     */
    public void addRequiredAccess(String type, String requiredPermission){
        if(type.equals(PermissionsManager.PERMISSIONS))
            permissionsManager.addRequiredPermission(requiredPermission);
        else if(type.equals(TechnologyManager.TECHNOLOGIES))
            technologyManager.addRequiredTechnology(requiredPermission);
    }

    /**
     * Deletes one required accesses, given it's type and identifier
     * @param type: type of access
     * @param requiredPermission: identifier of the given access
     */
    public void deleteRequiredAccess(String type, String requiredPermission){
        if(type.equals(PermissionsManager.PERMISSIONS))
            permissionsManager.deleteRequiredPermission(requiredPermission);
        else if(type.equals(TechnologyManager.TECHNOLOGIES))
            technologyManager.deleteRequiredTechnology(requiredPermission);
    }

    /**
     * Returns the accesses that are not met at the given moment
     * @return structure that maps for each type of access, the set of string identifiers containing the missing required accesses
     */
    public HashMap<String, Set<String>> getMissingRequiredAccesses(){
        HashMap<String,Set<String>> accesses = new HashMap<>(ACCESSES_TYPES);
        accesses.put(PermissionsManager.PERMISSIONS, permissionsManager.getMissingRequiredPermissions());
        accesses.put(TechnologyManager.TECHNOLOGIES, technologyManager.getMissingRequiredTechnologies());
        return accesses;
    }

    /**
     * Request the missing accesses of the type PERMISSIONS
     */
    public void requestMissingPermissions(){
        permissionsManager.requestMissingPermissions();
    }

    /**
     * Checks if every accesses are granted
     * @return true if every required accesses is granted, false otherwise
     */
    public boolean checkRequiredAccesses(){
        return permissionsManager.checkRequiredPermissions() && technologyManager.checkRequiredTechnologies();
    }



}
