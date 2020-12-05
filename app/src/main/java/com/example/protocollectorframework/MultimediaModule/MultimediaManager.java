package com.example.protocollectorframework.MultimediaModule;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.ToneGenerator;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.protocollectorframework.DataModule.Data.LocationData;
import com.example.protocollectorframework.DataModule.Data.MultimediaData;
import com.example.protocollectorframework.Extra.SharedMethods;
import com.example.protocollectorframework.DataModule.Data.PlotData;
import com.example.protocollectorframework.DataModule.MultimediaTable;
import com.example.protocollectorframework.R;

import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Class accountable for all the logic associated with multimedia data.
 */
public class MultimediaManager implements Serializable {

    public static final String DIR_IMAGES = "Images";
    public static final String DIR_AUDIO = "Audio";
    public static final String PHOTO = "Photo";
    public static final String VIDEO = "Video";
    public static final String RECORD = "Record";

    public static final int CAMERA_PERMISSIONS = 1;
    public static final int STORAGE_PERMISSIONS = 2;
    public static final int RECORD_WITH_STORAGE_PERMISSIONS = 3;
    public static final int VOICE_PERMISSIONS = 4;


    public static final int REQUEST_VOICE_RECORD = 0;
    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int REQUEST_VOICE_INPUT = 2;

    private  static EditText actualEditText;
    private  static SpeechRecognizer mSpeechRecognizer;

    private MediaRecorder recorder;
    private String currentPhoto;
    private String currentRecord;
    private String actualVisit;
    private Activity activity;
    private LocationData mLastKnownLocation = null;
    private MultimediaTable mMultimediaTable;
    private PlotData mPlot;
    private Context context;

    /**
     * Constructor
     * @param context: the context of the activity
     */

    public MultimediaManager(Context context){
        this.context = context;
        try{
            activity = (Activity) context;
        }catch (Exception e){
            e.printStackTrace();
        }
        this.mMultimediaTable = new MultimediaTable(context);
    }

    /**
     * Constructor
     * @param context: the context of the activity
     * @param actualVisit: actual visit id
     */
    public MultimediaManager(Context context, String actualVisit){
        this.context = context;
        try{
            activity = (Activity) context;
        }catch (Exception e){
            e.printStackTrace();
        }
        this.actualVisit = actualVisit;
        this.mMultimediaTable = new MultimediaTable(activity);
        this.currentPhoto = null;
        this.currentRecord = null;
        this.recorder = new MediaRecorder();
        actualEditText = null;

        mSpeechRecognizer = null;
    }

    /**
     * Constructor
     * @param context: the context of the activity
     * @param actualVisit: actual visit id
     * @param plotData: actual plot
     */
    public MultimediaManager(Context context, String actualVisit, PlotData plotData){
        this.context = context;
        try{
            activity = (Activity) context;
        }catch (Exception e){
            e.printStackTrace();
        }
        this.actualVisit = actualVisit;
        this.mMultimediaTable = new MultimediaTable(activity);
        this.mPlot = plotData;
        this.currentPhoto = null;
        currentRecord = null;
        this.recorder = new MediaRecorder();
        actualEditText = null;
        mSpeechRecognizer = null;
    }

    /**
     * Delete all multimedia associated to a specific visit
     * @param visit_id: visit id
     */
    public void deleteAllMultimediaFromVisit(String visit_id){
        if(mMultimediaTable != null)
            mMultimediaTable.deleteAllMultimediaFromVisit(visit_id);
    }

    /**
     * Creates a new multimedia file associated to the actualVisit
     * @param md: multimedia to add
     * @param visit_id: visit id
     */
    public void addMultimediaToVisit(MultimediaData md, String visit_id){
        if(mMultimediaTable != null)
            mMultimediaTable.receiveFile(md,visit_id);
    }

    /**
     * Get all photos associated to the visit
     * @param visit_id: visit id
     * @return list of photos associated to the visit
     */
    public List<MultimediaData> getVisitPhotos(String visit_id){
        List<MultimediaData> list = new ArrayList<>();
        if(mMultimediaTable != null)
            list = mMultimediaTable.getMultimediaFromVisit(visit_id,PHOTO);
        return list;
    }

    /**
     * Get all voices records associated to the visit
     * @param visit_id: visit id
     * @return list of voices records associated to the visit
     */
    public List<MultimediaData> getVisitVoiceRecords(String visit_id){
        List<MultimediaData> list = new ArrayList<>();
        if(mMultimediaTable != null)
            list = mMultimediaTable.getMultimediaFromVisit(visit_id,RECORD);
        return list;
    }


    /**
     * Associates an text description to a multimedia file
     * @param id: multimedia file id
     * @param description: text description
     */
    public void addDescriptionToMultimedia(String id, String description){
        if(mMultimediaTable != null)
            mMultimediaTable.addDescription(id,description);
    }


    /**
     * Get not uploades multimedia from visit current visit
     * @return list of not uploaded multimedia
     */
    public List<MultimediaData> getNotSendMultimedia(){
        return mMultimediaTable.getNotSyncedMultimediaFromVisit(actualVisit);
    }

    /**
     * tag multimedia files as uploaded
     */
    public void tagAsSent(){
        mMultimediaTable.markFileAsSync(actualVisit);
    }

    /**
     * Fetch all multimedia for current visit
     * @return list of multimedia objects
     */
    public List<MultimediaData> getVisitMultimedia(){
        return mMultimediaTable.getMultimediaFromVisit(actualVisit);
    }

    /**
     * Fetch multimedia by id
     * @param id: multimedia id
     * @return multimedia object
     */
    public MultimediaData getMultimediaForId(String id){
        return mMultimediaTable.getMultimediaForId(id);

    }

    /**
     * Fetch all photos for current visit
     * @return list of photo objects
     */
    public List<MultimediaData> getMultimediaPhotos(){
        return mMultimediaTable.getMultimediaFromVisit(actualVisit,PHOTO);

    }

    /**
     * Fetch all voice records for current visit
     * @return list of voice record objects
     */
    public List<MultimediaData> getMultimediaVoice(){
        return mMultimediaTable.getMultimediaFromVisit(actualVisit,RECORD);

    }

    /**
     * Fetch all multimedia files for a specific type for current visit
     * @return list of voice record objects
     */
    public List<MultimediaData> getMultimediaForType(String type){
        return mMultimediaTable.getMultimediaFromVisit(actualVisit,type);

    }

    /**
     * Deletes a multimedia by id
     * @param id: multimedia id
     * @return true if deleted with success, false otherwise
     */
    public boolean deleteMultimediaForId(String id){
        return mMultimediaTable.deleteFile(id);
    }

    /**
     * Erases a multimedia by id
     * @param id: multimedia id
     * @return true if erased with success, false otherwise
     */
    public boolean eraseMultimedia(String id){
        return mMultimediaTable.deleteFileOnCancel(id);
    }



    /**
     * Request camera activity
     * @param mLastKnownLocation: location to be associated to the photo
     */
    public void dispatchTakePictureIntent(LocationData mLastKnownLocation) {
        if(mLastKnownLocation != null)
            this.mLastKnownLocation = mLastKnownLocation;
        int permissionsOnHold = askCameraPermissions();
        if(permissionsOnHold == 0){
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null && (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e("ERROR_CREATE_IMAGE", "Error creating image file");
                }
                if (photoFile != null) {
                    try {
                        Uri photoURI = FileProvider.getUriForFile(context,
                                "com.example.fitoagro.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        activity.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * Creates a file from the last taken photo
     * @return photo file
     * @throws IOException : in case of creating temporary file fails
     */
    private File createImageFile() throws IOException {
        String timeStamp = getDate_hours();
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPhoto = image.getAbsolutePath();
        return image;
    }


    /**
     * Turns an image file into a approximated resized bitmap object
     * @param f: photo file
     * @param requiredSize: desired size
     * @return
     */
    public Bitmap decodeFile(File f, int requiredSize) {
        try {
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);


            int scale = 1;
            while(o.outWidth / scale / 2 >= requiredSize &&
                    o.outHeight / scale / 2 >= requiredSize) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException ignored) {}
        return null;
    }


    /**
     * Associate a list of files to a visit
     * @param paths: list of file paths
     * @param visit_id: actual visit
     * @param multimediaFiles: list of current multimedia data
     * @return true if success, false otherwise
     */
    public boolean addMultimediaListToVisit(List<String> paths, String visit_id, List<MultimediaData> multimediaFiles){
        File file = null;
        String gpx_path = paths.size() != 0 ? paths.get(0) : null;
        for(String path : paths){
            if(path.contains(SharedMethods.getMyId(context))) {
                gpx_path = path;
                break;
            }
        }
        if(gpx_path != null && (file = new File(gpx_path)).exists()) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                FileInputStream fileInputStream = new FileInputStream(file);
                Document document = documentBuilder.parse(fileInputStream);
                Element elementRoot = document.getDocumentElement();

                NodeList nodelist_wpt = elementRoot.getElementsByTagName("wpt");

                for (int i = 0; i < nodelist_wpt.getLength(); i++) {

                    Node node = nodelist_wpt.item(i);
                    NamedNodeMap attributes = node.getAttributes();
                    NodeList children = node.getChildNodes();

                    double lat = Double.parseDouble(attributes.getNamedItem("lat").getTextContent());
                    double ln = Double.parseDouble(attributes.getNamedItem("lon").getTextContent());

                    String time = "";
                    int sat = 0;
                    double ele = 0;
                    float accuracy = 0;
                    String name = "";

                    for(int w = 0; w < children.getLength(); w++){
                        Node sub_node = children.item(w);
                        String node_name = sub_node.getNodeName();
                        switch (node_name){
                            case "time": time = sub_node.getTextContent(); break;
                            case "sat": sat = Integer.parseInt(sub_node.getTextContent());break;
                            case "ele": ele = Double.parseDouble(sub_node.getTextContent());break;
                            case "accuracy": accuracy = Float.parseFloat(sub_node.getTextContent());break;
                            case "name" : name = sub_node.getTextContent(); break;
                            default: break;
                        }
                    }

                    String type = "";
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/FitoAgro/";

                    if (name.contains(".jpg")) {
                        type = MultimediaManager.PHOTO;
                        path += "Images/" + name;
                    } else if (name.contains(".mp3")) {
                        type = MultimediaManager.RECORD;
                        path += "Audio/" + name;
                    }

                    long timeStamp = -1;
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
                    try {
                        Date mDate = df.parse(time);
                        timeStamp = mDate.getTime();
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return false;
                    }

                    LocationData locationData = new LocationData(lat, ln, timeStamp, ele, accuracy, sat);
                    for(MultimediaData multimediaData : multimediaFiles){
                        if(multimediaData.getPath().equals(path)){
                            mMultimediaTable.addFile(new MultimediaData(type, path, locationData,multimediaData.getDescription()), visit_id);
                            multimediaFiles.remove(multimediaData);
                            break;
                        }
                    }
                }
                for(MultimediaData multimedia : multimediaFiles) {
                    mMultimediaTable.addFile(multimedia, visit_id);
                }
                fileInputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        else{
            for(MultimediaData multimedia : multimediaFiles) {
                mMultimediaTable.addFile(multimedia, visit_id);
            }
        }

        return true;
    }



    /**
     * Processes the latest taken photo and associates it to the current visit
     * @param requestCode: request code
     * @param resultCode: result code
     * @param data: data
     * @return photo id
     */
    public String onPhotoResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {


                File dir = SharedMethods.createDirectories(DIR_IMAGES);
                if(dir == null){
                    showError();
                }

                String date = getDate_hours();

                File photo=new File(dir, mPlot.getID() + "_" + mPlot.getAcronym() + "_" + SharedMethods.getMyId(activity) + "_photo_" + date +".jpg");
                File original = new File(currentPhoto);
                Uri contentUri = Uri.fromFile(original);
                FileOutputStream fos= null;
                try {
                    byte bytes[] = FileUtils.readFileToByteArray(original);
                    fos = new FileOutputStream(photo.getPath());
                    fos.write(bytes);
                    fos.close();
                    long id = mMultimediaTable.addFile(new MultimediaData(SharedMethods.getMyId(context),PHOTO,photo.getPath(),mLastKnownLocation),actualVisit);
                    if (id == -1)
                        return null;
                    return Long.toString(id);


                } catch (Exception e) {
                    showError();
                    e.printStackTrace();
                }



            } else if (resultCode == Activity.RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                showError();
            }
        }
        return null;
    }


    /**
     * Request voice record
     * @return true if success, false otherwise
     */
    public boolean requestVoiceRecord(){
        if (ContextCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            askRecordWithStoragePermissions(activity);
            return false;
        }

        else {
            Date currentTime = Calendar.getInstance().getTime();
            File dir = SharedMethods.createDirectories(DIR_AUDIO);
            if(dir == null){
                showError();
            }
            String date = getDate_hours();
            String fileName = mPlot.getID() + "_" + mPlot.getAcronym() + "_" + SharedMethods.getMyId(context) + "_audio_" + date + ".mp3";
            File audioFile = new File(dir, fileName);
            currentRecord = audioFile.getPath();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(audioFile.getPath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);

            try {
                recorder.prepare();
                recorder.start();
                toneG.startTone(ToneGenerator.TONE_PROP_BEEP, 200);
                return true;
            } catch (IOException e) {
                showError();
                e.printStackTrace();
                toneG.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 200);

            }
            currentRecord = null;
            return false;
        }
    }

    /**
     * Stops the current recording
     * @param mLastKnownLocation: location to be associated with the current recording
     * @return if success voice record id, null otherwise
     */
    public String stopRecording(LocationData mLastKnownLocation) {
        this.mLastKnownLocation = mLastKnownLocation;
        if(currentRecord != null && recorder != null) {
            recorder.stop();
            recorder.reset();
            long id = mMultimediaTable.addFile(new MultimediaData(SharedMethods.getMyId(context),RECORD, currentRecord, mLastKnownLocation), actualVisit);
            currentRecord = null;
            if(id == -1)
                return null;
            return Long.toString(id);
        }else{
            return null;

        }
    }


    /**
     * Request voice input for edittext view
     * @param activity: requesting activity
     * @param editText
     */
    public void requestVoiceInput(Activity activity, EditText editText) {
        askVoicePermissions(activity);
        if (SpeechRecognizer.isRecognitionAvailable(activity)) {
            ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean hasInternet = cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();

            if(hasInternet) {
                mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(activity);
                mSpeechRecognizer.setRecognitionListener(recognitionListener);
                actualEditText = editText;
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
                mSpeechRecognizer.startListening(intent);
            }else{
                SharedMethods.showToast(activity,"Sem internet");
            }
        }
    }


    /**
     * Request for the necessary permission for audio recording
     * @param activity
     */
    private static void askVoicePermissions(Activity activity){
        if (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.RECORD_AUDIO}, VOICE_PERMISSIONS);

        }
    }

    private int askCameraPermissions(){
        List<String> permissions = new ArrayList<String>(3);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if(ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(permissions.size() > 0)
            ActivityCompat.requestPermissions(activity,  permissions.toArray(new String[0]), CAMERA_PERMISSIONS);

        return permissions.size();

    }

    /**
     * Request for the necessary permissions for a voice record
     * @param activity: activity where the record is going to take place
     */
    private static void askRecordWithStoragePermissions(Activity activity){
        List<String> permissions = new ArrayList<String>(3);
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            permissions.add(Manifest.permission.RECORD_AUDIO);

        }
        if(ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        }
        if(permissions.size() > 0)
            ActivityCompat.requestPermissions(activity,  permissions.toArray(new String[0]), RECORD_WITH_STORAGE_PERMISSIONS);

    }


    /**
     * Show unknown error on current activity
     */
    private void showError(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Ocorreu um erro!").setMessage("Não foi possível guardar o ficheiro. Tente novamente.")
                .setCancelable(false)
                .setPositiveButton(context.getResources().getString(R.string.button_ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public static String getDate_hours(){
        return new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());

    }


    /**
     * Speech recognition listener
     */
    private static RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {

        }

        @Override
        public void onBeginningOfSpeech() {

        }

        @Override
        public void onRmsChanged(float rmsdB) {

        }

        @Override
        public void onBufferReceived(byte[] buffer) {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onError(int error) {
            Log.e("error",error + " ");
        }

        @Override
        public void onResults(Bundle results) {
            List<String> resultsList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String best_result = resultsList.get(0).concat(". ");
            String old_text = actualEditText.getText().toString();
            actualEditText.setText(old_text + best_result);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {

        }

        @Override
        public void onEvent(int eventType, Bundle params) {

        }
    };

}
