package com.example.protocollectorframework.DataModule.Data;

public class MultimediaData {
    private String ID;
    private String type;
    private String path;
    private long timestamp;
    private LocationData location;
    private String description;
    private String owner;

    public MultimediaData(){}


    public MultimediaData(String ID, String type, String path, long timestamp, LocationData location, String description, String owner) {
        this.ID = ID;
        this.type = type;
        this.path = path;
        this.location = location;
        this.timestamp = timestamp;
        this.description = description;
        this.owner = owner;
    }


    public MultimediaData(String ID, String type, String path, long timestamp, LocationData location, String description) {
        this.ID = ID;
        this.type = type;
        this.path = path;
        this.location = location;
        this.timestamp = timestamp;
        this.description = description;
    }


    public MultimediaData(String owner, String type, String path, LocationData location) {
        this.type = type;
        this.path = path;
        this.location = location;
        this.timestamp = 0;
        this.description = null;
        this.owner = owner;
    }

    public MultimediaData(String type, String path, LocationData location, String description) {
        this.type = type;
        this.path = path;
        this.location = location;
        this.timestamp = 0;
        this.description = description;
    }


    public MultimediaData(String type, String path, String description) {
        this.type = type;
        this.path = path;
        this.description = description;
        this.location = null;
        this.timestamp = 0;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public LocationData getLocation() {
        return location;
    }

    public void setLocation(LocationData location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String toString() {
        if(location != null)
            return type + " " + location.getLat() + " " + location.getLng() + " " + location.getElevation() + " " + location.getAccuracy() + " " + location.getSat_number();
        else return type + " " + path;
    }
    }
