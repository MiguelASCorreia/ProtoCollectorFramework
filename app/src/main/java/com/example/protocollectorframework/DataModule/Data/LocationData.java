package com.example.protocollectorframework.DataModule.Data;


import java.io.Serializable;

/**
 * Information associated to a geographical point
 */
public class LocationData implements Serializable {
    private long timestamp;
    private double lat;
    private double lng;
    private double elevation;
    private float accuracy;
    private int sat_number;
    private String info;

    /**
     * Constructor
     *
     * @param lat: point's latitude
     * @param lng: point's longitude
     */
    public LocationData(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = 0.0;
        this.timestamp = 0;
        this.sat_number = 0;
    }

    /**
     * Constructor
     *
     * @param lat:       point's latitude
     * @param lng:       point's longitude
     * @param timestamp: point's capture timestamp
     */
    public LocationData(double lat, double lng, long timestamp) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = 0.0;
        this.timestamp = timestamp;
        this.sat_number = 0;

    }

    /**
     * Constructor
     *
     * @param lat:       point's latitude
     * @param lng:       point's longitude
     * @param elevation: point's elevation
     */
    public LocationData(double lat, double lng, double elevation) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
        this.timestamp = 0;
        this.accuracy = 0;
        this.sat_number = 0;

    }

    /**
     * Constructor
     *
     * @param lat:       point's latitude
     * @param lng:       point's longitude
     * @param timestamp: point's capture timestamp
     * @param elevation: point's elevation
     */
    public LocationData(double lat, double lng, long timestamp, double elevation) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
        this.timestamp = timestamp;
        this.accuracy = 0;
        this.sat_number = 0;

    }

    /**
     * Constructor
     *
     * @param lat:       point's latitude
     * @param lng:       point's longitude
     * @param timestamp: point's capture timestamp
     * @param elevation: point's elevation
     * @param accuracy:  point's horizontal accuracy
     */
    public LocationData(double lat, double lng, long timestamp, double elevation, float accuracy) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
        this.sat_number = 0;
    }

    /**
     * Constructor
     *
     * @param lat:        point's latitude
     * @param lng:        point's longitude
     * @param timestamp:  point's capture timestamp
     * @param elevation:  point's elevation
     * @param accuracy:   point's horizontal accuracy
     * @param sat_number: number of satellites used to get the point
     * @param info:       extra information associated with the point
     */
    public LocationData(double lat, double lng, long timestamp, double elevation, float accuracy, int sat_number, String info) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
        this.timestamp = timestamp;
        this.accuracy = accuracy;
        this.sat_number = sat_number;
        this.info = info;
    }

    /**
     * Returns the extra information associated with the point
     *
     * @return extra information associated with the point
     */
    public String getInfo() {
        return info;
    }

    /**
     * Sets the extra information associated with the point
     *
     * @param info extra information associated with the point
     */
    public void setInfo(String info) {
        this.info = info;
    }

    /**
     * Returns the number of satellites used to get the point
     *
     * @return number os satellites
     */
    public int getSatNumber() {
        return sat_number;
    }

    /**
     * Sets the number of satellites used to get the point
     *
     * @param sat_number number of satellites
     */
    public void setSatNumber(int sat_number) {
        this.sat_number = sat_number;
    }

    /**
     * Returns the horizontal accuracy in meters
     *
     * @return horizontal accuracy
     */
    public float getAccuracy() {
        return accuracy;
    }

    /**
     * Sets the horizontal accuracy
     *
     * @param accuracy: horizontal accuracy in meters
     */
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    /**
     * Returns the point's latitude
     *
     * @return point's latitude
     */
    public double getLat() {
        return lat;
    }

    /**
     * Sets the point latitude
     *
     * @param lat: point's latitude
     */
    public void setLat(double lat) {
        this.lat = lat;
    }

    /**
     * Returns the point's longitude
     *
     * @return point's longitude
     */
    public double getLng() {
        return lng;
    }

    /**
     * Sets the point longitude
     *
     * @param lng: point's longitude
     */
    public void setLng(double lng) {
        this.lng = lng;
    }

    /**
     * Returns the point capture timestamp
     *
     * @return
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the point capture timestamp
     *
     * @param time: capture timestamp
     */
    public void setTimestamp(long time) {
        this.timestamp = time;
    }

    /**
     * Returns the point elevation in meters
     *
     * @return point's elevation
     */
    public double getElevation() {
        return elevation;
    }

    /**
     * Sets the point elevation
     *
     * @param elevation: point's elevation
     */
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
