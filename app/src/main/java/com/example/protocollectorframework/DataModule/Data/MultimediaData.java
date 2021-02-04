package com.example.protocollectorframework.DataModule.Data;

import org.jetbrains.annotations.NotNull;

/**
 * Object that stores the information associated to the multimedia files
 */
public class MultimediaData {
    private String id;
    private String type;
    private String path;
    private long timestamp;
    private LocationData location;
    private String description;
    private String owner;
    private String info;

    /**
     * Constructor
     *
     * @param ID:          multimedia file identifier
     * @param type:        multimedia file type identifier
     * @param path:        multimedia file external path
     * @param timestamp:   multimedia file creation timestamp in milliseconds
     * @param location:    associated location
     * @param description: multimedia file description
     * @param owner:       multimedia file owner's identifier
     * @param info:        multimedia file auxiliary information
     */

    public MultimediaData(String ID, String type, String path, long timestamp, LocationData location, String description, String owner, String info) {
        this.id = ID;
        this.type = type;
        this.path = path;
        this.location = location;
        this.timestamp = timestamp;
        this.description = description;
        this.owner = owner;
        this.info = info;
    }

    /**
     * Constructor
     *
     * @param owner:    multimedia file owner's identifier
     * @param type:     multimedia file type identifier
     * @param path:     multimedia file external path
     * @param location: associated location
     */
    public MultimediaData(String owner, String type, String path, LocationData location) {
        this.type = type;
        this.path = path;
        this.location = location;
        this.timestamp = 0;
        this.description = null;
        this.owner = owner;
    }

    /**
     * Constructor
     *
     * @param type:        multimedia file type identifier
     * @param path:        multimedia file external path
     * @param location:    associated location
     * @param description: multimedia file description
     */
    public MultimediaData(String type, String path, LocationData location, String description) {
        this.type = type;
        this.path = path;
        this.location = location;
        this.timestamp = 0;
        this.description = description;
    }


    /**
     * Returns the file's owner identifier
     *
     * @return owner identifier
     */
    public String getOwner() {
        return owner;
    }

    /**
     * Sets the file owner
     *
     * @param owner: owner's identifier
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Returns the file description
     *
     * @return file's description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the file description
     *
     * @param description: file's description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the file auxiliary information
     *
     * @return file's auxiliary information
     */
    public String getAuxiliaryInformation() {
        return info;
    }

    /**
     * Sets the file auxiliary information
     *
     * @param description: file's auxiliary information
     */
    public void setAuxiliaryInformation(String description) {
        this.info = info;
    }

    /**
     * Returns the file identifier
     *
     * @return file's identifier
     */
    public String getID() {
        return id;
    }

    /**
     * Sets the file identifier
     *
     * @param ID: file's identifier
     */
    public void setID(String ID) {
        this.id = ID;
    }

    /**
     * Returns the file's creation timestamp in milliseconds
     *
     * @return creation timestamp in milliseconds
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the file's creation timestamp in milliseconds
     *
     * @param timestamp: creation timestamp in milliseconds
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Returns the file's geographical location
     *
     * @return file's geographical location
     */
    public LocationData getLocation() {
        return location;
    }

    /**
     * Sets the file's geographical location
     *
     * @param location: file's geographical location
     */
    public void setLocation(LocationData location) {
        this.location = location;
    }

    /**
     * Returns the file's type identifier
     *
     * @return file's type identifier
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the file's type identifier
     *
     * @param type: file's type identifier
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Returns the file's external storage path
     *
     * @return file's external storage path
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the file's external storage path
     *
     * @param path: file's external storage path
     */
    public void setPath(String path) {
        this.path = path;
    }

    @NotNull
    public String toString() {
        if (location != null)
            return type + " " + location.getLat() + " " + location.getLng() + " " + location.getElevation() + " " + location.getAccuracy() + " " + location.getSat_number();
        else return type + " " + path;
    }
}
