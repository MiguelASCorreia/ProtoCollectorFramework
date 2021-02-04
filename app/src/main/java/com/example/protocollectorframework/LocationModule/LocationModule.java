package com.example.protocollectorframework.LocationModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.example.protocollectorframework.Complements.SharedMethods;
import com.example.protocollectorframework.DataModule.Data.LocationData;
import com.example.protocollectorframework.DataModule.Data.MultimediaData;
import com.example.protocollectorframework.DataModule.Data.PlotData;
import com.example.protocollectorframework.DataModule.DataBase.PlotTable;
import com.example.protocollectorframework.DataModule.DataBase.TrajectoryPointTable;
import com.example.protocollectorframework.DataModule.DataBase.TrajectorySegmentTable;
import com.example.protocollectorframework.DataModule.DataBase.TrajectoryTable;
import com.example.protocollectorframework.InterfaceModule.AnimationLibrary;
import com.example.protocollectorframework.R;
import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.LineString;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.OnSymbolClickListener;
import com.mapbox.mapboxsdk.plugins.annotation.Symbol;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.FillLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.snatik.polygon.Point;
import com.snatik.polygon.Polygon;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import static com.mapbox.mapboxsdk.style.layers.PropertyFactory.fillColor;

/**
 * Class accountable for all the logic associated with location data.
 * Uses the Plot, Trajectory, Segment and Point database to store user's route.
 * Contains listener to manage location updates
 */
public class LocationModule {
    public static final String DIR_GPX = "GPX";

    public static final String MARKER_RED = "MARKER_RED";

    public static final int GPS_PERMISSIONS = 0;
    private static final int DEFAULT_SIZE = 50;

    private static final long DEFAULT_REFRESH_DISTANCE = 7;
    private static final long DEFAULT_REFRESH_TIME = 5000;

    private static final long MAX_INTERVAL = 60 * 1000;

    private Context context;
    private Activity mActivity;

    private List<PlotData> mPlots;
    private LocationData mLastKnownLocation;
    private PlotData mActualPlot;
    private List<LocationData> mLocations;

    private Polygon mActualPolygon;
    private MapboxMap mMapBox;
    private MapView mMapView;
    private Style mMapStyle;
    private SymbolManager mSymbolManager;
    private Symbol mActualMarker;

    private int acceptedErrors;
    private int zoom;
    private int tilt;
    private int animationTime;
    private int current_errors;
    private long refresh_time;
    private long refresh_distance;
    private String trajectoryId;
    private String segmentId;

    private LocationManager mLocationManager;
    private LocationListener mListener;

    private TrajectoryPointTable mPointTable;
    private TrajectorySegmentTable mSegmentsTable;
    private TrajectoryTable mTrajectoryTable;
    private PlotTable mPlotTable;

    private AnimationLibrary animationLibrary;

    public AlertDialog mOutsidePlotDialog;

    private View targetView;

    /**
     * Constructor
     *
     * @param context: the context of the activity
     */

    public LocationModule(Context context) {
        this.context = context;
        this.mPlotTable = new PlotTable(context);
        this.mTrajectoryTable = new TrajectoryTable(context);
        this.mPointTable = new TrajectoryPointTable(context);
        this.mSegmentsTable = new TrajectorySegmentTable(context);
        this.animationLibrary = new AnimationLibrary(context);

    }


    /**
     * Constructor
     *
     * @param activity:         current activity
     * @param refresh_time:     minimum time between gps points
     * @param refresh_distance: minimum distance between gps points
     */
    public LocationModule(Activity activity, long refresh_time, long refresh_distance) {
        this.mActivity = activity;
        this.context = activity.getApplicationContext();
        this.animationLibrary = new AnimationLibrary(context);
        this.mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.mMapBox = null;
        this.mMapView = null;
        this.mMapStyle = null;
        this.mSymbolManager = null;
        this.mPlotTable = new PlotTable(activity);
        this.mPlots = mPlotTable.getPlots();
        this.current_errors = 0;
        this.refresh_distance = refresh_distance;
        this.refresh_time = refresh_time;
        this.mLocations = new ArrayList<>(DEFAULT_SIZE);
        this.mLastKnownLocation = null;
        this.mActualPlot = null;
        this.mActualPolygon = null;
        this.mActualMarker = null;
        this.mListener = null;
        this.mTrajectoryTable = new TrajectoryTable(activity);
        this.mPointTable = new TrajectoryPointTable(activity);
        this.mSegmentsTable = new TrajectorySegmentTable(activity);
    }


    /**
     * Constructor
     *
     * @param activity:         current activity
     * @param refresh_time:     minimum time between gps points
     * @param refresh_distance: minimum distance between gps points
     * @param plotData:         plot associated
     */
    public LocationModule(Activity activity, long refresh_time, long refresh_distance, PlotData plotData) {
        this.mActivity = activity;
        this.context = activity.getApplicationContext();
        this.animationLibrary = new AnimationLibrary(context);
        this.mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.mMapBox = null;
        this.mMapView = null;
        this.mMapStyle = null;
        this.mSymbolManager = null;
        this.mPlotTable = new PlotTable(activity);
        this.mPlots = mPlotTable.getPlots();
        this.current_errors = 0;
        this.refresh_distance = refresh_distance;
        this.refresh_time = refresh_time;
        this.mLocations = new ArrayList<>(DEFAULT_SIZE);
        this.mLastKnownLocation = null;
        this.mActualPlot = plotData;
        this.mActualPolygon = getPolygonForPlot(plotData);
        this.mActualMarker = null;
        this.mListener = null;
        this.mTrajectoryTable = new TrajectoryTable(activity);
        this.mPointTable = new TrajectoryPointTable(activity);
        this.mSegmentsTable = new TrajectorySegmentTable(activity);
    }

    /**
     * Constructor
     *
     * @param activity:    current activity
     * @param plotData:    plot associated
     * @param actualVisit: current visit id
     * @param userId:      user identifier
     * @param locations:   list of previous locations to be considered
     */
    public LocationModule(Activity activity, PlotData plotData, String actualVisit, String userId, List<LocationData> locations) {
        this.mActivity = activity;
        this.context = activity.getApplicationContext();
        this.animationLibrary = new AnimationLibrary(context);
        this.mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.mPlotTable = new PlotTable(activity);
        this.mPlots = mPlotTable.getPlots();
        this.current_errors = 0;
        this.mActualPolygon = getPolygonForPlot(plotData);
        this.refresh_distance = DEFAULT_REFRESH_DISTANCE;
        this.refresh_time = DEFAULT_REFRESH_TIME;
        this.mLastKnownLocation = null;
        this.mActualPlot = plotData;
        this.mMapBox = null;
        this.mMapStyle = null;
        this.mMapView = null;
        this.mSymbolManager = null;
        this.mActualMarker = null;
        this.mListener = null;
        this.mTrajectoryTable = new TrajectoryTable(activity);
        this.mPointTable = new TrajectoryPointTable(activity);
        this.mSegmentsTable = new TrajectorySegmentTable(context);

        trajectoryId = mTrajectoryTable.getTrajectoryId(actualVisit, userId);
        if (trajectoryId != null)
            this.mLocations = mPointTable.getPointsFromTrajectory(trajectoryId);
        else
            trajectoryId = mTrajectoryTable.initializeTrajectory(actualVisit, userId);

        if (mLocations == null && locations != null)
            this.mLocations = locations;
        else if (mLocations == null)
            this.mLocations = new ArrayList<>();
    }

    /**
     * Create a plot
     *
     * @param p:           Plot information
     * @param json_limits: geojson with the plot limits, id and name
     * @return true if created with success, false otherwise
     */

    public boolean addPlot(PlotData p, String json_limits) {
        if (mPlotTable != null)
            return mPlotTable.addPlot(p, json_limits);
        return false;

    }

    /**
     * Edit the information associated with a plot
     *
     * @param plot_id: target plot id
     * @param acronym: string with the main protocols divided by comas
     * @param info:    JSON string with complementary information
     */

    public void editPlotInfo(String plot_id, String info, String acronym) {
        if (mPlotTable != null)
            mPlotTable.editInfo(plot_id, info, acronym);
    }


    /**
     * Edit the information associated with a plot
     *
     * @param plot_id: target plot id
     * @param acronym: string with the main protocols divided by comas
     * @param info:    JSON string with complementary information
     */

    public void editPlotInfo(String plot_id, String info, String acronym, String plot, double lat, double ln) {
        if (mPlotTable != null)
            mPlotTable.editInfo(plot_id, info, acronym, plot, lat, ln);
    }

    /**
     * Delete a plot
     *
     * @param plot_id: target plot id
     */
    public void deletePlot(String plot_id) {
        if (mPlotTable != null)
            mPlotTable.deletePlotViaFlag(plot_id);
    }


    /**
     * Fetch the information associated with the plot
     *
     * @param plot_id: target plot id
     * @return plot information
     */
    public PlotData getPlotById(String plot_id) {
        if (mPlotTable != null)
            return mPlotTable.getPlotById(plot_id);
        return null;

    }

    /**
     * Fetch all plots ids
     *
     * @return list containing all plot ids
     */
    public List<String> getPlotsIds() {
        List<String> list = new ArrayList<>();
        if (mPlotTable != null)
            list = mPlotTable.getPlotsIds();
        return list;

    }

    /**
     * Fetch all plots
     *
     * @return list containing all plot
     */
    public List<PlotData> getPlots() {
        List<PlotData> list = new ArrayList<>();
        if (mPlotTable != null)
            list = mPlotTable.getPlots();
        return list;

    }

    /**
     * Fetch all plots, even the ones marked as deleted
     *
     * @return list containing all plot
     */
    public List<PlotData> getPlotsForSelection() {
        List<PlotData> list = new ArrayList<>();
        if (mPlotTable != null)
            list = mPlotTable.getPlotsForSelection();
        return list;

    }

    /**
     * Fetch plot edit timestamp
     *
     * @param plot_id: target plot id
     * @return edit timestamp
     */
    public String getPlotEditTimestamp(String plot_id) {
        if (mPlotTable != null)
            return mPlotTable.getPlotEditTime(plot_id);
        return null;
    }

    /**
     * Sets the current plot
     *
     * @param plotData: current plot
     */
    public void setPlot(PlotData plotData) {
        this.mActualPlot = plotData;
    }

    /**
     * Sets the current locations
     *
     * @param locations list of locations
     */
    public void setLocations(List<LocationData> locations) {
        this.mLocations = locations;
    }

    /**
     * Creates a track associated to the visit
     *
     * @param visit_id: target visit id
     * @param path:     path to gpx file
     * @param ownerId:  owner identifier
     * @return track id
     */
    public String addTrack(String visit_id, String path, String ownerId) {
        if (mTrajectoryTable != null)
            return mTrajectoryTable.addTrajectory(visit_id, path, ownerId);
        return null;

    }

    /**
     * Delete all tracks associated to a visit
     *
     * @param visit_id: target visit id
     */
    public void deleteTrack(String visit_id) {
        if (mTrajectoryTable != null)
            mTrajectoryTable.deleteTrack(visit_id);
    }


    /**
     * Edit the information associated with a track
     *
     * @param visit_id: target visit id
     * @param gps_path: path to gpx file
     * @param owner:    owner identifier
     */

    public void editTrack(String visit_id, String gps_path, String owner) {
        if (mTrajectoryTable != null)
            mTrajectoryTable.editTrajectory(visit_id, gps_path, owner);
    }

    /**
     * Associate a gpx file path to the current track
     *
     * @param gps_path: path to gpx file
     */

    public void completeTrack(String gps_path) {
        if (mTrajectoryTable != null)
            mTrajectoryTable.completeTrajectory(trajectoryId, gps_path);
    }


    /**
     * Set map preferences
     *
     * @param mapBox:        Mapbox map
     * @param mapView:       Map view
     * @param mapStyle:      Map style
     * @param zoom:          default zoom, must be greater that zero
     * @param tilt:          default tilt, must be greater that zero
     * @param animationTime: time that a changing position animation takes
     */
    public void setMap(MapboxMap mapBox, MapView mapView, Style mapStyle, int zoom, int tilt, int animationTime) {
        mMapBox = mapBox;
        mMapView = mapView;
        mMapStyle = mapStyle;
        mSymbolManager = new SymbolManager(mMapView, mMapBox, mMapStyle);
        mSymbolManager.setIconAllowOverlap(true);
        mSymbolManager.setIconIgnorePlacement(true);
        this.zoom = zoom;
        this.tilt = tilt;
        this.animationTime = animationTime;
    }

    /**
     * Set a click listener on map markers
     *
     * @param listener: Click listener
     */
    public void setMarkerClickListener(OnSymbolClickListener listener) {
        if (mSymbolManager != null && listener != null)
            mSymbolManager.addClickListener(listener);
    }

    /**
     * Draws a polygon on the map if it was setup
     *
     * @param polygon:   polygon limits defined by a list of lists of points
     * @param color:     polygon filling color
     * @param source_id: polygon source identifier
     * @param layer_id:  polygon layer identifier
     */
    public void addPolygon(List<List<com.mapbox.geojson.Point>> polygon, int color, String source_id, String layer_id) {
        if (mMapBox != null && mMapBox.getStyle() != null) {
            mMapBox.getStyle().addSource(new GeoJsonSource(source_id, com.mapbox.geojson.Polygon.fromLngLats(polygon)));
            mMapBox.getStyle().addLayerBelow(new FillLayer(layer_id, source_id).withProperties(
                    fillColor(color)), "settlement-label");
        }

    }

    /**
     * Draws a line on the map if it was setup
     *
     * @param line:      line defined by a list of points
     * @param color:     line color
     * @param source_id: line source identifier
     * @param layer_id:  line layer identifier
     * @param above_id:  identifier of the layer that the line is to be drawn above
     */
    public void addPath(List<com.mapbox.geojson.Point> line, int color, String source_id, String layer_id, String above_id) {
        if (mMapBox != null && mMapBox.getStyle() != null) {
            mMapBox.getStyle().addSource(new GeoJsonSource(source_id,
                    FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
                            LineString.fromLngLats(line)
                    )})));

            try {
                mMapBox.getStyle().addLayerAbove(new LineLayer(layer_id, source_id).withProperties(
                        PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                        PropertyFactory.lineWidth(5f),
                        PropertyFactory.lineColor(color)
                ), above_id);
            } catch (Exception e) {
                mMapBox.getStyle().addLayerAbove(new LineLayer(layer_id, source_id).withProperties(
                        PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                        PropertyFactory.lineWidth(5f),
                        PropertyFactory.lineColor(color)
                ), "settlement-label");
            }
        }

    }

    /**
     * Fetch all current locations
     *
     * @return list of locations
     */
    public List<LocationData> getLocations() {
        return mLocations;
    }

    /**
     * Fetch the last known location
     *
     * @return last known location
     */
    public LocationData getLastKnownLocation() {
        return mLastKnownLocation;
    }

    /**
     * Fetch current plot
     *
     * @return current plot
     */
    public PlotData getActualPlot() {
        return mActualPlot;
    }

    /**
     * Check if GPS permissions are enabled
     *
     * @param context: context from the calling activity
     * @return true if it has permissions, false otherwise
     */
    private static boolean needGpsPermissions(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * As user for GPS permissions
     */
    private void askForGPSPermissions() {
        String[] permission = new String[1];
        permission[0] = Manifest.permission.ACCESS_FINE_LOCATION;
        ActivityCompat.requestPermissions(mActivity, permission, GPS_PERMISSIONS);
    }

    /**
     * Event after permissions request result. If permissions was accepted, start location updates
     *
     * @param requestCode  request code
     * @param grantResults granted results
     */
    public void onRequestPermissionsResult(int requestCode, int[] grantResults) {
        if (requestCode == GPS_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (mListener != null)
                    startLocationUpdates(mListener);
            }
        }
    }

    /**
     * Start location updates
     *
     * @param listener location listener
     */
    @SuppressLint("MissingPermission")
    public void startLocationUpdates(LocationListener listener) {
        mListener = listener;
        if (!needGpsPermissions(context)) {
            //startTime = (SystemClock.elapsedRealtimeNanos() / 1000000);
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    refresh_time,
                    refresh_distance, listener);
        } else if (needGpsPermissions(context)) {
            askForGPSPermissions();

        }
    }

    /**
     * Stop the current location listener
     */
    public void stopLocationUpdates() {
        if (mSymbolManager != null)
            mSymbolManager.deleteAll();
        if (mListener != null)
            mLocationManager.removeUpdates(mListener);
    }

    /**
     * Split a track into fragments for the final GPX file
     *
     * @param locations: list of locations
     * @return list composed by list of locations (segments)
     */
    private List<List<LocationData>> splitTrackIntoSegments(List<LocationData> locations) {
        List<List<LocationData>> segments = new ArrayList<>(5);
        int segmentIndex = 0;
        for (int i = 0; i < locations.size(); i++) {
            LocationData actualLocation = locations.get(i);

            if (segmentIndex == segments.size()) {
                segments.add(segmentIndex, new ArrayList<>());
            }

            segments.get(segmentIndex).add(actualLocation);

            if (i != locations.size() - 1) {
                LocationData nextLocation = locations.get(i + 1);
                long actualStamp = actualLocation.getTimestamp();
                long newStamp = nextLocation.getTimestamp();
                long duration = newStamp - actualStamp;
                if (duration > MAX_INTERVAL)
                    segmentIndex++;
            }

        }

        return segments;

    }

    /**
     * Export gpx file
     *
     * @param multimedia: list of multimedia files associated with the visit
     * @param id:         owner id
     * @param timestamp:  creation timestamp
     * @return file's external storage path if exported with success, null otherwise
     */
    public String exportGPXFile(List<MultimediaData> multimedia, String id, String timestamp) {
        String auxName;
        if (mActualPlot != null)
            auxName = mActualPlot.getID() + "_" + mActualPlot.getAcronym() + "_track_" + id + "_" + timestamp;
        else
            auxName = "track_" + id + "_" + timestamp;

        return exportGPX(multimedia, mLocations, auxName);
    }

    /**
     * Export gpx file
     *
     * @param multimedia: list of multimedia files associated with the visit
     * @param locations:  list of locations
     * @param fileName:   file name
     * @return file's external storage path if exported with success, null otherwise
     */
    private String exportGPX(List<MultimediaData> multimedia, List<LocationData> locations, String fileName) {
        if (isExternalStorageWritable() && locations.size() > 0) {
            List<List<LocationData>> segmentList = splitTrackIntoSegments(locations);

            String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><gpx xmlns=\"http://www.topografix.com/GPX/1/1\" creator=\"MapSource 6.15.5\" version=\"1.1\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"  xsi:schemaLocation=\"http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd\">\n<trk>\n";

            String name = "<name>" + fileName + "</name>\n";


            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

            StringBuilder segment_text = new StringBuilder();

            for (List<LocationData> segment : segmentList) {
                segment_text.append("<trkseg>\n");

                for (LocationData location : segment) {
                    if (location != null) {
                        segment_text.append("<trkpt lat=\"").append(location.getLat()).append("\" lon=\"").append(location.getLng()).append("\">").append("<time>").append(df.format(new Date(location.getTimestamp()))).append("</time>").append("<sat>").append(location.getSat_number()).append("</sat>");

                        segment_text.append("<ele>").append(location.getElevation()).append("</ele>");

                        segment_text.append("<accuracy>").append(location.getAccuracy()).append("</accuracy>");

                        segment_text.append("</trkpt>\n");
                    }
                }

                segment_text.append("</trkseg>\n");  //</gpx>
            }


            segment_text.append("</trk>\n");

            String points = "";

            if (multimedia != null && !multimedia.isEmpty()) {
                for (MultimediaData m : multimedia) {
                    String[] nameArray = m.getPath().split("/");
                    String wptName = nameArray[nameArray.length - 1];
                    LocationData location = m.getLocation();
                    if (location != null && (location.getLat() != 0.0 || location.getLng() != 0.0)) {
                        points += "<wpt lat=\"" + location.getLat() + "\" lon=\"" + location.getLng() + "\">" +
                                "<time>" + df.format(new Date(location.getTimestamp())) + "</time>" +
                                "<sat>" + location.getSat_number() + "</sat>";

                        points += "<ele>" + location.getElevation() + "</ele>";

                        points += "<accuracy>" + location.getAccuracy() + "</accuracy>";
                        points += "<name>" + wptName + "</name>";

                        points += "</wpt>\n";
                    }
                }
            }

            String foot = "</gpx>";

            File dir = SharedMethods.createDirectories(DIR_GPX);
            if (dir == null) {
                showError();
            }

            File gpxFile = new File(dir, fileName + ".gpx");
            try {
                FileWriter writer = new FileWriter(gpxFile, false);
                writer.append(header);
                writer.append(name);
                writer.append(segment_text.toString());
                if (!points.isEmpty())
                    writer.append(points);
                writer.append(foot);
                writer.flush();
                writer.close();
                return gpxFile.getPath();
            } catch (Exception e) {
                Log.e("Export_error", e.toString());
            }
        }
        return null;
    }


    /**
     * Show unknown error on current activity
     */
    private void showError() {
        AlertDialog.Builder builder;
        if (context != null)
            builder = new AlertDialog.Builder(context);
        else
            builder = new AlertDialog.Builder(mActivity);

        builder.setTitle("Ocorreu um erro!").setMessage("Não foi possível guardar o ficheiro. Tente novamente.")
                .setCancelable(false)
                .setPositiveButton(mActivity.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Check if external storage is writable
     *
     * @return true if external storage is writable, false otherwise
     */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }


    /**
     * Check if any of the known plots contains the given location
     *
     * @param lat:   point latitude
     * @param ln:    point longitude
     * @param plots: list of plots
     * @return plot that contains the given location
     */
    private PlotData getActualPlot(double lat, double ln, List<PlotData> plots) {
        Point actualPosition = new Point(lat, ln);
        Polygon actual_polygon = null;
        for (int i = 0; i < plots.size(); i++) {
            PlotData actualPlot = plots.get(i);
            actual_polygon = getPolygonForPlot(actualPlot);
            if (actual_polygon == null)
                continue;
            if (actual_polygon.contains(actualPosition)) {
                mActualPolygon = actual_polygon;
                return actualPlot;
            }
        }
        return null;
    }

    /**
     * Reads a gpx file
     *
     * @param file: Gpx file
     * @return List of mapbox points extracted from the file
     */

    public List<com.mapbox.geojson.Point> decodeGPX(File file) {
        List<com.mapbox.geojson.Point> list = new ArrayList<com.mapbox.geojson.Point>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");

            for (int i = 0; i < nodelist_trkpt.getLength(); i++) {

                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                double lat = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                double lon = Double.parseDouble(newLongitude);


                list.add(com.mapbox.geojson.Point.fromLngLat(lon, lat));

            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Reads a gpx file
     *
     * @param file: Gpx file
     * @return List of location points extracted from the file
     */

    public static List<LocationData> decodeGPXWithAllData(File file) {
        List<LocationData> list = new ArrayList<LocationData>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            FileInputStream fileInputStream = new FileInputStream(file);
            Document document = documentBuilder.parse(fileInputStream);
            Element elementRoot = document.getDocumentElement();

            NodeList nodelist_trkpt = elementRoot.getElementsByTagName("trkpt");

            for (int i = 0; i < nodelist_trkpt.getLength(); i++) {

                Node node = nodelist_trkpt.item(i);
                NamedNodeMap attributes = node.getAttributes();
                NodeList children = node.getChildNodes();

                String newLatitude = attributes.getNamedItem("lat").getTextContent();
                double lat = Double.parseDouble(newLatitude);

                String newLongitude = attributes.getNamedItem("lon").getTextContent();
                double lon = Double.parseDouble(newLongitude);


                String time = "";
                int sat = 0;
                double ele = 0;
                float accuracy = 0;

                for (int w = 0; w < children.getLength(); w++) {
                    Node sub_node = children.item(w);
                    String node_name = sub_node.getNodeName();
                    switch (node_name) {
                        case "time":
                            time = sub_node.getTextContent();
                            break;
                        case "sat":
                            sat = Integer.parseInt(sub_node.getTextContent());
                            break;
                        case "ele":
                            ele = Double.parseDouble(sub_node.getTextContent());
                            break;
                        case "accuracy":
                            accuracy = Float.parseFloat(sub_node.getTextContent());
                            break;
                        default:
                            break;
                    }
                }

                long timeStamp = -1;
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.getDefault());
                try {
                    Date mDate = df.parse(time);
                    timeStamp = mDate.getTime();
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                list.add(new LocationData(lat, lon, timeStamp, ele, accuracy, sat));

            }
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }


    /**
     * Gives the distances in meters between two points
     *
     * @param lat1: first point latitude
     * @param ln1:  first point longitude
     * @param lat2: second points latitude
     * @param ln2:  seconde points longitude
     * @return distance between the two points
     */
    public double getDistanceInMeters(double lat1, double ln1, double lat2, double ln2) {
        Location l1 = new Location("plot1");
        Location l2 = new Location("plot2");

        l1.setLatitude(lat1);
        l1.setLongitude(ln1);
        l2.setLatitude(lat2);
        l2.setLongitude(ln2);

        return l1.distanceTo(l2);
    }

    /**
     * Defines the symbol of the marker
     *
     * @param symbol: Mapbox symbol
     */
    public void setActualMarker(Symbol symbol) {
        mActualMarker = symbol;
    }

    /**
     * Adds a markers to the map
     *
     * @param lat:  point latitude
     * @param ln:   point longitude
     * @param icon: marker icon tag
     * @return symbol associated with the marker
     */
    public Symbol addMarker(double lat, double ln, String icon) {
        if (mSymbolManager != null) {
            return mSymbolManager.create(new SymbolOptions()
                    .withLatLng(new LatLng(lat, ln))
                    .withIconImage(icon)
                    .withIconSize(2.0f));

        }

        return null;
    }

    /**
     * Adds a markers to the map
     *
     * @param lat:  point latitude
     * @param ln:   point longitude
     * @param icon: marker icon tag
     * @param data: extra information to be associated with the marker
     * @return symbol associated with the marker
     */
    public Symbol addMarker(double lat, double ln, String icon, JsonElement data) {
        if (mSymbolManager != null) {
            return mSymbolManager.create(new SymbolOptions()
                    .withData(data)
                    .withLatLng(new LatLng(lat, ln))
                    .withIconImage(icon)
                    .withIconSize(2.0f));

        }
        return null;
    }

    /**
     * Delete all symbols makers on the map
     */
    public void deleteAllSymbols() {
        if (mSymbolManager != null)
            mSymbolManager.deleteAll();
    }


    /**
     * Deletes a symbol from the map
     *
     * @param symbol: symbol to be deleted
     */
    public void deleteSymbol(Symbol symbol) {
        if (mSymbolManager != null) {
            try {
                mSymbolManager.delete(symbol);
            } catch (Exception e) {
                Log.e("LOG_DELETE_MARKER_ERROR", "Error deleting marker");
            }
        }
    }


    /**
     * Fetch the listener used for tracking the user during the visit
     *
     * @param onExitDialog:   dialog that is to be shown when the user leaves the plot during a visit
     * @param acceptedErrors: number of points that need to be detected outside the plot until the dialog is shown. Must be greater than 0
     * @return listener used for tracking the user during the visit
     */
    public LocationListener getVisitLocationListener(AlertDialog onExitDialog, int acceptedErrors) {
        this.mOutsidePlotDialog = onExitDialog;
        if (acceptedErrors > 0)
            this.acceptedErrors = acceptedErrors;
        return mLocationForVisit;

    }

    /**
     * Fetch the listener used for detecting plots near the user
     *
     * @return listener used for detecting plots near the user
     */

    public LocationListener getPlotDetectionLocationListener(View targetView) {
        this.targetView = targetView;
        return mLocationForPlotSelect;

    }

    /**
     * Return the polygon associated to the plot
     *
     * @param actualPlot: plot
     * @return polygon associated to the plot
     */
    private static Polygon getPolygonForPlot(PlotData actualPlot) {
        Polygon.Builder builder = new Polygon.Builder();
        if (actualPlot != null) {
            List<LocationData> actualLimits = actualPlot.getLimits();
            if (actualLimits == null || actualLimits.size() == 0)
                return null;
            for (int j = 0; j < actualLimits.size(); j++) {
                builder.addVertex(new Point(actualLimits.get(j).getLat(), actualLimits.get(j).getLng()));
            }
        }
        return builder.build();
    }


    /**
     * Location listener used for tracking the user during the visit
     */
    private LocationListener mLocationForVisit = new LocationListener() {


        @Override
        public void onLocationChanged(android.location.Location location) {

            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                Point actualPosition = new Point(latitude, longitude);

                if (mMapBox != null && mSymbolManager != null) {
                    if (mActualMarker != null)
                        mSymbolManager.delete(mActualMarker);

                    mActualMarker = mSymbolManager.create(new SymbolOptions()
                            .withLatLng(new LatLng(latitude, longitude))
                            .withIconImage(MARKER_RED)
                            .withIconSize(2.0f));
                }

                if (mActualPolygon != null) {
                    if (!mActualPolygon.contains(actualPosition)) {
                        current_errors++;
                    } else {
                        current_errors = 0;
                    }
                }
                if (current_errors >= acceptedErrors) {
                    current_errors = 0;
                    animationLibrary.vibrate();
                    if (mOutsidePlotDialog != null && !mOutsidePlotDialog.isShowing())
                        mOutsidePlotDialog.show();
                }
                if (segmentId == null || mLastKnownLocation == null || location.getTime() - mLastKnownLocation.getTimestamp() > MAX_INTERVAL)
                    segmentId = mSegmentsTable.createSegment(trajectoryId);

                mLastKnownLocation = new LocationData(latitude, longitude, location.getTime(), location.getAltitude(), location.getAccuracy(), location.getExtras().getInt("satellites"));
                mPointTable.addPoint(segmentId, mLastKnownLocation);
                mLocations.add(mLastKnownLocation);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("Provider Changed", provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("Provider Enabled", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("Provider Disabled", provider);
        }
    };

    /**
     * Location listener used for detecting plots near the user
     * If a view is provided, every time the user gets near a field plot, the name identifier of that plot will be shown on the view.
     */
    private LocationListener mLocationForPlotSelect = new LocationListener() {

        @SuppressLint("SetTextI18n")
        @Override
        public void onLocationChanged(android.location.Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                mLastKnownLocation = new LocationData(latitude, longitude, location.getTime(), location.getAltitude(), location.getAccuracy(), location.getExtras().getInt("satellites"));
                try {
                    if (mMapBox != null && mSymbolManager != null) {
                        CameraPosition position = new CameraPosition.Builder()
                                .target(new LatLng(latitude, longitude))
                                .zoom(zoom)
                                .tilt(tilt)
                                .build();


                        mMapBox.animateCamera(CameraUpdateFactory.newCameraPosition(position), animationTime);


                        if (mActualMarker != null)
                            mSymbolManager.delete(mActualMarker);

                        mActualMarker = mSymbolManager.create(new SymbolOptions()
                                .withLatLng(new LatLng(latitude, longitude))
                                .withIconImage(MARKER_RED)
                                .withIconSize(2.0f));


                    }
                    PlotData actualPlot = getActualPlot(latitude, longitude, mPlots);

                    if (targetView != null) {
                        if (targetView instanceof TextView) {
                            TextView view = (TextView) targetView;
                            if (actualPlot != null) {
                                if (!view.getText().toString().equals(actualPlot.getName())) {
                                    view.setText(actualPlot.getName());
                                }
                            } else if (!view.getText().toString().isEmpty()) {
                                view.setText("");
                            }
                        } else
                            Log.e("View presentation error", "The provided view must be an instance of TextView");
                    }
                    mActualPlot = actualPlot;

                } catch (Exception e) {
                    Log.e("LOCATION_ERROR", e.toString());
                }
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("Provider Changed", provider);
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("Provider Enabled", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.e("Provider Disabled", provider);
        }
    };
}
