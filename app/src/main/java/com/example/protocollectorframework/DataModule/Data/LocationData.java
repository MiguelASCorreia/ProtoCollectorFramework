package com.example.protocollectorframework.DataModule.Data;


import java.io.Serializable;

public class LocationData implements Serializable {
    private long timestamp;
    private double lat;
    private double lng;
    private double elevation;
    private float accuracy;
    private int sat_number;

    public LocationData(){

    }

    public LocationData(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = 0.0;
        this.timestamp = 0;
        this.sat_number = 0;
    }

    public LocationData(double lat, double lng, long timestamp) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = 0.0;
        this.timestamp = timestamp;
        this.sat_number = 0;

    }


    public LocationData(double lat, double lng, double elevation) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
        this.timestamp = 0;
        this.accuracy = 0;
        this.sat_number = 0;

    }

    public LocationData(double lat, double lng, long timestamp, double elevation) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
        this.timestamp = timestamp;
        this.accuracy = 0;
        this.sat_number = 0;

    }

    public LocationData(double lat, double lng, long timestamp, double elevation, float accuracy) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
        this.sat_number = 0;
    }

    public LocationData(double lat, double lng, long timestamp, double elevation, float accuracy, int sat_number) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
        this.sat_number = sat_number;
    }

    public int getSat_number() {
        return sat_number;
    }

    public void setSat_number(int sat_number) {
        this.sat_number = sat_number;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long time) {
        this.timestamp = time;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    @Override
    public String toString() {
        String longitude = String.format("%.7f", lng);
        String latitude = String.format("%.7f", lat);
        return "[" + longitude + "," + latitude + "]";
    }

}
