package com.example.protocollectorframework.CooperationModule;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.protocollectorframework.DataModule.BluetoothSyncTable;
import com.example.protocollectorframework.DataModule.Data.BluetoothSyncData;
import com.example.protocollectorframework.Extra.SharedMethods;
import com.example.protocollectorframework.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Class accountable for all the logic associated with bluetooth connection.
 * Allows sending and receiving messages, as well as searching for paired devices and devices to pair.
 */
public class BluetoothConnectionManager {

    public static final String RECOVERY_PREFS = "RECOVERY";
    public static final String RECOVERY_HOST_ADDRESS = "HOST_ADDRESS";

    public static final String SETTINGS = "SETTINGS";
    public static final String SETTINGS_SEND_MULTIMEDIA = "SETTINGS_SEND_MULTIMEDIA";

    public static final int MESSAGE_RECEIVE = 0;
    public static final int MESSAGE_SAVE_STATE = 1;
    public static final int MESSAGE_ENABLED = 2;
    public static final int MESSAGE_CONNECTED = 3;
    public static final int MESSAGE_ERROR = 4;
    public static final int MESSAGE_CONFLICT_ACK = 5;
    public static final int MESSAGE_RECEIVE_MULTIMEDIA = 6;
    public static final int MESSAGE_SHOW_PROGRESS = 7;
    public static final int MESSAGE_HOSTING = 8;
    public static final int MESSAGE_FINISH_ACK = 9;



    private static final String ALERT_CONNECTION = "C0";
    public static final String ACK = "ACK";
    public static final String FINAL_ACK = "FINAL_ACK";


    public static final int BUFFER_SIZE = 1024*1024*5; // 5 MB
    private static final String TAG = "Bluetooth";
    private static final String SECURE_NAME = "SECURE_FITOAGRO";
    public static final int DISCOVERY_TIME = 60 * 5;
    public static final int REQUEST_ENABLE_BT = 200;
    public static final int REQUEST_ENABLE_DISCOVERY = 201;




    private ExecutorService mHosting = Executors.newSingleThreadExecutor();
    private ExecutorService mSearching = Executors.newSingleThreadExecutor();

    private BluetoothAdapter mBluetoothAdapter;
    private Context mContext;
    private Activity mActivity;
    private List<BluetoothDevice> mDevices;
    private DevicesListAdapter mAdapter;
    private UUID mUUID;
    private BluetoothSocket mSocket;
    private AlertDialog mDevicesDialog;
    private AlertDialog mPairedDevicesDialog;

    private BluetoothDevice mHost;
    private ConnectedThread mConnectedThread;
    private BluetoothServerSocket mmServerSocket;
    private SharedPreferences mRecoveryPrefs;
    private SharedPreferences mSettingsPrefs;

    private BluetoothDevice mDeviceToSync;
    public BluetoothSyncTable mBluetoothSyncTable;

    private Handler mIncomingHandler;


    /**
     * Constructor
     * @param context: current activity context
     */
    public BluetoothConnectionManager(Context context){
        this.mContext = context;
        mBluetoothSyncTable = new BluetoothSyncTable(mContext);
    }

    /**
     * Constructor
     * @param activity: current activity
     * @param mIncomingHandler: activity incoming handler that handles all received messages
     * @param uuid: identifier for the connection
     */
    public BluetoothConnectionManager(Activity activity, Handler mIncomingHandler, UUID uuid){
        this.mActivity = activity;
        this.mIncomingHandler = mIncomingHandler;
        mContext = activity.getApplicationContext();
        mBluetoothSyncTable = new BluetoothSyncTable(mContext);

        mUUID = uuid;
        mRecoveryPrefs = activity.getSharedPreferences(RECOVERY_PREFS,0);
        mSettingsPrefs = activity.getSharedPreferences(SETTINGS,0);
        try {
            IntentFilter intent = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
            mContext.registerReceiver(mPairReceiver, intent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Creates a sync entry on data base
     * @param visit_id: visit id
     * @param effective_sync_timestamp: agreed timestamp
     * @param mPartnerId: other device id
     * @param last: if the connection will be the last one
     * @return sync log id
     */
    public long addSyncLog(String visit_id, long effective_sync_timestamp, String mPartnerId, boolean last){
        if(mBluetoothSyncTable != null)
            return mBluetoothSyncTable.addSyncLog(visit_id, effective_sync_timestamp, mPartnerId, last);
        return -1;
    }


    /**
     * Fetch all sync logs associated to a visit
     * @param visit_id: visit id
     * @return list of sync data
     */
    public List<BluetoothSyncData> getSyncLogs(String visit_id){
        List<BluetoothSyncData> list = new ArrayList<>();
        if(mBluetoothSyncTable != null)
            list = mBluetoothSyncTable.getSyncsForVisit(visit_id);
        return list;
    }

    /**
     * Changes the "send multimedia" preferences to enable or disable the sending of multimedia files
     */
    public void changeMultimediaOption(){
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setCancelable(false);
        builder.setTitle(mActivity.getString(R.string.bluetooth_connection_preferences));
        String[] animals = {mActivity.getString(R.string.bluetooth_check_multimedia)};
        boolean[] checkedItems = {mSettingsPrefs.getBoolean(SETTINGS_SEND_MULTIMEDIA,false)};
        final boolean[] send = {mSettingsPrefs.getBoolean(SETTINGS_SEND_MULTIMEDIA, false)};
        builder.setMultiChoiceItems(animals, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                send[0] = isChecked;
            }
        });

        builder.setPositiveButton(mActivity.getString(R.string.button_confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSettingsPrefs.edit().putBoolean(SETTINGS_SEND_MULTIMEDIA,send[0]).apply();
                connect();
            }
        });
        builder.setNegativeButton(mActivity.getString(R.string.button_cancel), null);

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Fetch the host of the connection
     * @return host of the connection
     */
    public BluetoothDevice getHost() {
        return mHost;
    }

    /**
     * Fetch the device MAC address
     * @return
     */
    @SuppressLint("HardwareIds")
    public String getMyAddress(){
        if(mBluetoothAdapter!=null)
            return mBluetoothAdapter.getAddress();
        return null;

    }

    /**
     * Starts the connection process
     */
    public void connect(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            SharedMethods.showToast(mContext,mContext.getString(R.string.bluetooth_not_compatible));
        }else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }else{
            if(mHost == null) {
                String aux = mRecoveryPrefs.getString(RECOVERY_HOST_ADDRESS, null);
                if(aux != null) {
                    mHost = mBluetoothAdapter.getRemoteDevice(aux);
                    if(aux.equals(getMyAddress()))
                        sendHostMessage();
                }
            }
            startDiscovery();

        }
    }

    /**
     * Sends a message to the activity handler declaring that this devices is the host of the connection
     */
    private void sendHostMessage(){
        Message message = new Message();
        message.what = MESSAGE_HOSTING;
        if(mIncomingHandler != null)
            mIncomingHandler.sendMessage(message);
    }

    /**
     * Stop waiting for connections
     */
    public void stopHosting(){
        try {
            mHosting.shutdown();
            if(! mHosting.awaitTermination(1, TimeUnit.SECONDS))
                mHosting.shutdownNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            mHosting = Executors.newSingleThreadExecutor();
        }
    }

    /**
     * Stop searching for devices
     */
    public void stopSearching(){
        try {
            mSearching.shutdown();
            if(! mSearching.awaitTermination(1, TimeUnit.SECONDS))
                mSearching.shutdownNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            mSearching = Executors.newSingleThreadExecutor();

        }
    }

    /**
     * Cancels bluetooth discovery
     */
    public void cancelDiscovery(){
        if(mBluetoothAdapter != null && mBluetoothAdapter.isDiscovering())
            mBluetoothAdapter.cancelDiscovery();
    }

    /**
     * Fetch the connected bluetooth thread that contains all the streams
     * @return connected bluetooth thread
     */
    public ConnectedThread getConnectedThread(){
        return mConnectedThread;
    }

    /**
     * Start the bluetooth discovery
     */
    private void startDiscovery(){
        if(!mBluetoothAdapter.isDiscovering()) {
            Intent discoverableIntent =
                    new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERY_TIME);
            mActivity.startActivityForResult(discoverableIntent, REQUEST_ENABLE_DISCOVERY);
        }else{
            if(getHost() == null)
                showPairedDevices(null);

            if(getHost() == null || imHosting())
                startAccepting();

            if(getHost() != null && !imHosting())
                searchDevices();
        }
    }


    /**
     * To call when bluetooth request is finished. Starts accepting and searching connections
     * @param requestCode: request code
     * @param resultCode: result code
     * @param data: data
     */
    public void onBluetoothResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                startDiscovery();

            }else{
                SharedMethods.showToast(mContext,mContext.getString(R.string.bluetooth_not_connected));

            }
        }else if(requestCode == REQUEST_ENABLE_DISCOVERY){
            if (resultCode == DISCOVERY_TIME) {
                if(getHost() == null)
                    showPairedDevices(null);

                if(getHost() == null || imHosting())
                    startAccepting();

                if(getHost() != null && !imHosting())
                    searchDevices();
            }


        }
    }

    /**
     * Show near devices
     */
    public void searchDevices(){

        if(mHost != null){
            if(mConnectedThread != null && mConnectedThread.validStreams()){
                try {
                    mConnectedThread.write(ALERT_CONNECTION.getBytes());
                    Message message1 = new Message();
                    message1.what = MESSAGE_CONNECTED;

                    if(mPairedDevicesDialog != null && mPairedDevicesDialog.isShowing())
                        mPairedDevicesDialog.dismiss();

                    if(mIncomingHandler != null)
                        mIncomingHandler.sendMessage(message1);

                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                connectToDevice(mHost);
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else
            showPairedDevices(mHost);


//        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
//        //  showDevicesList();
//        mContext.registerReceiver(receiver, filter);
//        showDevicesList();
    }

    /**
     * Show paired devices
     * @param device: device to auto select
     */
    public void showPairedDevices(BluetoothDevice device){
        if(mPairedDevicesDialog != null && mPairedDevicesDialog.isShowing())
            mPairedDevicesDialog.dismiss();

        mDeviceToSync = null;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.paired_devices_layout, null);

        CheckBox send_multimedia_check = dialogView.findViewById(R.id.multimedia_check);
        send_multimedia_check.setChecked(mSettingsPrefs.getBoolean(SETTINGS_SEND_MULTIMEDIA,false));
        send_multimedia_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSettingsPrefs.edit().putBoolean(SETTINGS_SEND_MULTIMEDIA,isChecked).apply();
            }
        });
        TextView sync_button = dialogView.findViewById(R.id.sync_start);
        sync_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sync_button.getTextColors().getDefaultColor() == mActivity.getResources().getColor(R.color.colorPrimary) && mDeviceToSync != null)
                    connectToDevice(mDeviceToSync);
            }
        });


        TextView sync_cancel = dialogView.findViewById(R.id.sync_cancel);
        sync_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPairedDevicesDialog.dismiss();
            }
        });


        TextView sync_search = dialogView.findViewById(R.id.sync_search);
        sync_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                mContext.registerReceiver(searchingReceiver, filter);
                mBluetoothAdapter.startDiscovery();
                showDevicesList();
            }
        });

        HashMap<Integer, BluetoothDevice> devicePerViewID = new HashMap<Integer, BluetoothDevice>();
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mDeviceToSync = devicePerViewID.get(checkedId);
                sync_button.setTextColor(mActivity.getResources().getColor(R.color.colorPrimary));

            }
        });
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();


        int i = 0;
        for(BluetoothDevice bt : pairedDevices){


            RadioButton button = new RadioButton(mActivity);
            if(i != pairedDevices.size() -1) {
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, 10);
                button.setLayoutParams(params);
            }
            button.setTextSize(16);
            button.setPadding(10,0,0,0);
            button.setTextColor(Color.BLACK);
            button.setText(bt.getName());
            radioGroup.addView(button);
            button.setId(i);
            devicePerViewID.put(i,bt);
            i++;
            if(device != null && bt.getAddress().equals(device.getAddress()))
                button.setChecked(true);

        }
        if(i == 0)
            dialogView.findViewById(R.id.no_pairs).setVisibility(View.VISIBLE);




        dialogBuilder.setView(dialogView);

        mPairedDevicesDialog = dialogBuilder.create();
        mPairedDevicesDialog.show();


    }

    /**
     * Starts accepting connections
     */
    public void startAccepting(){

        if(mHost != null){
            if(mConnectedThread != null && mConnectedThread.validStreams()){
                try {
                    mConnectedThread.write(ALERT_CONNECTION.getBytes());
                    Message message1 = new Message();
                    message1.what = MESSAGE_CONNECTED;

                    if(mPairedDevicesDialog != null && mPairedDevicesDialog.isShowing())
                        mPairedDevicesDialog.dismiss();

                    if(mIncomingHandler != null)
                        mIncomingHandler.sendMessage(message1);

                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            resetConnection();
        }
        Message message = new Message();
        BluetoothServerSocket temp = null;
        try {
            temp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(SECURE_NAME, mUUID);
        } catch (Exception e) {
            Log.e(TAG, "Socket's listen() method failed", e);
            cancelDiscovery();
            message.what = MESSAGE_ERROR;
            message.obj = mActivity.getString(R.string.bluetooth_error_turning_on);
            if(mIncomingHandler != null)
                mIncomingHandler.sendMessage(message);

            return;
        }

        mmServerSocket = temp;
        SharedMethods.showToast(mActivity, mContext.getString(R.string.bluetooth_wait_for_connection));


        message.what = MESSAGE_ENABLED;

        if(mIncomingHandler != null)
            mIncomingHandler.sendMessage(message);


        Tasks.call(mHosting, () -> {
            BluetoothSocket socket = null;
            while (true) {

                socket = mmServerSocket.accept();

                if (socket != null) {
                    try{
                        mmServerSocket.close();
                    }catch (Exception e){
                        Log.e(TAG,"Error closing",e);
                    }
                    return socket;
                }
            }

        }).addOnSuccessListener(new OnSuccessListener<BluetoothSocket>() {
            @Override
            public void onSuccess(BluetoothSocket socket) {

                mConnectedThread = new ConnectedThread(socket,mIncomingHandler);

                if (mDevicesDialog != null && mDevicesDialog.isShowing())
                    mDevicesDialog.dismiss();

                cancelDiscovery();
                stopSearchingReceiver();
                SharedMethods.showToast(mContext, mContext.getString(R.string.bluetooth_linked));


                Message message1 = new Message();
                message1.what = MESSAGE_CONNECTED;

                if(mPairedDevicesDialog != null && mPairedDevicesDialog.isShowing())
                    mPairedDevicesDialog.dismiss();

                if(mHost == null) {
                    mHost = mBluetoothAdapter.getRemoteDevice(getMyAddress());
                    mRecoveryPrefs.edit().putString(RECOVERY_HOST_ADDRESS,getMyAddress()).apply();
                    sendHostMessage();
                }

                if(mIncomingHandler != null)
                    mIncomingHandler.sendMessage(message1);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (mDevicesDialog != null && mDevicesDialog.isShowing())
                    mDevicesDialog.dismiss();

                cancelDiscovery();
                stopSearchingReceiver();

                e.printStackTrace();
            }
        });

    }

    /**
     * Resets all current connections closing all streams
     */
    public void resetConnection(){
        if(mConnectedThread != null) {
            mConnectedThread.resetConnection();
            mConnectedThread = null;
        }

        if(mSocket != null) {
            try {
                mSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                mSocket = null;
            }
        }

        if(mmServerSocket != null){
            try{
                mmServerSocket.close();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                mmServerSocket = null;
            }
        }

        cancelDiscovery();

        if(mDevicesDialog!=null && mDevicesDialog.isShowing())
            mDevicesDialog.dismiss();

        stopSearchingReceiver();

        mDevicesDialog = null;
        mAdapter = null;
        mDevices = null;
    }


    /**
     * Check if the device is the current host
     * @return true if the device is the current host, false otherwise
     */
    public boolean imHosting(){
        try {
            return mHost.getAddress().equals(getMyAddress());
        }catch (Exception e){
            return false;
        }
    }


    /**
     * The connected thread that contains all streams
     */
    public static class ConnectedThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream
        private Handler mIncomingHandler;

        /**
         *
         * @param socket: bluetooth socket resulting from the connection
         * @param handler: activity handler
         */
        ConnectedThread(BluetoothSocket socket, Handler handler) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            mIncomingHandler = handler;

            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            this.start();
        }

        /**
         * Check if streams are still valid
         * @return true if the streams are valid, false otherwise
         */
        public boolean validStreams(){
            return mmSocket != null && mmSocket.isConnected() && mmOutStream != null && mmInStream != null;
        }

        /**
         * Resets connection, closing the streams
         */
        public void resetConnection() {
            if (mmInStream != null) {
                try {
                    mmInStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mmInStream = null;
            }

            if (mmOutStream != null) {
                try {
                    mmOutStream.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                mmOutStream = null;
            }

            if (mmSocket != null) {
                try {
                    mmSocket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mmSocket = null;
            }

        }

        /**
         * thread's run method. Waiting for messages to process
         */
        public void run() {
            mmBuffer = new byte[BUFFER_SIZE];
            int numBytes; // bytes returned from read()
            String message ="";
            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInStream.read(mmBuffer);
                    if(numBytes > 0) {
                        String received_message =  new String(mmBuffer, 0, numBytes);

                        if(received_message.equals(ALERT_CONNECTION)){
                            Message message1 = new Message();
                            message1.what = MESSAGE_CONNECTED;


                            if(mIncomingHandler != null)
                                mIncomingHandler.sendMessage(message1);

                            continue;
                        }else if(received_message.equals(ACK)){
                            Message message1 = new Message();
                            message1.what = MESSAGE_CONFLICT_ACK;


                            if(mIncomingHandler != null)
                                mIncomingHandler.sendMessage(message1);

                            continue;
                        }else if(message.isEmpty() && mIncomingHandler != null){
                            Message message1 = new Message();
                            message1.what = MESSAGE_SHOW_PROGRESS;
                            mIncomingHandler.sendMessage(message1);
                        }
                        if(received_message.contains(FINAL_ACK)){
                            Message message1 = new Message();
                            message1.what = MESSAGE_FINISH_ACK;
                            if(mIncomingHandler != null)
                                mIncomingHandler.sendMessage(message1);

                            received_message = received_message.replace(FINAL_ACK,"");
                        }

                        message += received_message;
                        Log.e("RECEIVE",message);

                        try{

                            JSONObject jsonObject = new JSONObject(message);

                            Log.e("RECEIVE_FINAL",message);

                            //message = "";
                            Message finalMessage = new Message();
                            if(jsonObject.has("multimedia_files") || jsonObject.has("locations") || jsonObject.has("effective_duration") || jsonObject.has("note"))
                                finalMessage.what = MESSAGE_RECEIVE_MULTIMEDIA;
                            else
                                finalMessage.what = MESSAGE_RECEIVE;
                            finalMessage.obj = jsonObject;

                            if(mIncomingHandler != null)
                                mIncomingHandler.sendMessage(finalMessage);

                            mmBuffer = new byte[BUFFER_SIZE];
                            message ="";

                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /**
         * Sends a message to the linked device
         * @param bytes: bytes of the message
         * @throws IOException : broken pipe
         */
        public void write(byte[] bytes) throws IOException {
            try {
                mmOutStream.flush();

                mmOutStream.write(bytes,0,bytes.length);

            } catch (IOException e) {
                Log.e(TAG, "Error occurred when sending data", e);
                if(e.getMessage().toLowerCase().contains("broken pipe")){
                    throw new IOException(e.getMessage());
                }

            }
        }

    }

    /**
     * Broadcast listening for new devices
     */
    private final BroadcastReceiver searchingReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            try {
                String action = intent.getAction();
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if(!pairedDevices.contains(device)) {
                        mDevices.add(device);
                        if (mAdapter != null)
                            mAdapter.notifyDataSetChanged();
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    /**
     * Broadcast for pair moments
     */
    private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                if(mPairedDevicesDialog != null && mPairedDevicesDialog.isShowing()) {

                    final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                    final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                    if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                        showPairedDevices(device);
                    } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                        showPairedDevices(null);
                    }
                }

            }
        }
    };

    /**
     * Show list for nearby devices
     */
    private void showDevicesList(){
        mDevices =  new ArrayList<>();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        dialogBuilder.setCancelable(false);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.looking_for_devices_layout, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setNegativeButton(mActivity.getString(R.string.button_cancel), null);

        ListView devices_list_view = dialogView.findViewById(R.id.devices_list_view);
        mAdapter = new DevicesListAdapter();
        devices_list_view.setAdapter(mAdapter);

        mDevicesDialog = dialogBuilder.create();
        try {
            mDevicesDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Pair device
     * @param device: device to pair with
     */
    public void pairDevice(BluetoothDevice device) {
        try {
            Class class1 = null;
            class1 = Class.forName("android.bluetooth.BluetoothDevice");
            Method createBondMethod = class1.getMethod("createBond");
            Boolean returnValue = (Boolean) createBondMethod.invoke(device);
            if(returnValue) {
                if(mDevicesDialog != null && mDevicesDialog.isShowing())
                    mDevicesDialog.dismiss();
                showPairedDevices(device);
                stopSearchingReceiver();
            }else
                SharedMethods.showToast(mActivity,mActivity.getString(R.string.bluetooth_error_pairing));

        } catch (Exception e) {
            e.printStackTrace();
            SharedMethods.showToast(mActivity,mActivity.getString(R.string.bluetooth_error_pairing));
        }


    }

    /**
     * Connect to device
     * @param device: device to connect with
     */
    public void connectToDevice(BluetoothDevice device){
        try {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
            dialogBuilder.setCancelable(false);
            LayoutInflater inflater = mActivity.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.progress_layout, null);
            dialogBuilder.setView(dialogView);
            TextView tx = dialogView.findViewById(R.id.progress_dialog_message);
            tx.setText(mContext.getString(R.string.bluetooth_connecting) + " " + device.getName() + "...");
            AlertDialog progressDialog = dialogBuilder.create();
            progressDialog.show();



            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(mUUID);
            Tasks.call(mSearching,() -> {
                socket.connect();
                return socket.isConnected();
            }).addOnSuccessListener(new OnSuccessListener<Boolean>() {
                @Override
                public void onSuccess(Boolean tResult) {
                    if(tResult) {
                        mSocket = socket;
                        try {

                            if(mmServerSocket != null){
                                try {
                                    mmServerSocket.close();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            mConnectedThread = new ConnectedThread(mSocket,mIncomingHandler);

                            SharedMethods.showToast(mContext, mContext.getString(R.string.bluetooth_connected_to) + " " + device.getName());

                            Message message1 = new Message();
                            message1.what = MESSAGE_CONNECTED;

                            if(mPairedDevicesDialog != null && mPairedDevicesDialog.isShowing())
                                mPairedDevicesDialog.dismiss();

                            if(mHost == null) {
                                mHost = device;
                                mRecoveryPrefs.edit().putString(RECOVERY_HOST_ADDRESS,device.getAddress()).apply();

                            }

                            if(mIncomingHandler != null)
                                mIncomingHandler.sendMessage(message1);

                        } catch (Exception e) {
                            Message message = new Message();
                            message.what = MESSAGE_ERROR;
                            message.obj = mActivity.getString(R.string.bluetooth_error_turning_on);
                            if(mIncomingHandler != null)
                                mIncomingHandler.sendMessage(message);
                        }

                    }
                    if( progressDialog.isShowing())
                        progressDialog.dismiss();
                    if(mDevicesDialog != null && mDevicesDialog.isShowing())
                        mDevicesDialog.dismiss();

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {


                    if( progressDialog.isShowing())
                        progressDialog.dismiss();
                    if(mDevicesDialog != null && mDevicesDialog.isShowing())
                        mDevicesDialog.dismiss();

                    e.printStackTrace();
                    Message message = new Message();
                    message.what = MESSAGE_ERROR;
                    message.obj = mActivity.getString(R.string.bluetooth_error_turning_on);
                    if(mIncomingHandler != null)
                        mIncomingHandler.sendMessage(message);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Adapter for nearby devices list
     */
    class DevicesListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return mDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final BluetoothDevice device = mDevices.get(position);


            if (convertView == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mActivity);
                convertView = layoutInflater.inflate(R.layout.device_info_layout, null);
            }

            TextView name = convertView.findViewById(R.id.name);
            TextView address = convertView.findViewById(R.id.address);
            name.setText(device.getName());
            address.setText(device.getAddress());

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pairDevice(device);
                }
            });

            return convertView;
        }
    }

    /**
     * Stop receiver responsible for updating the list of nearby devices
     */
    public void stopSearchingReceiver(){
        try {
            mContext.unregisterReceiver(searchingReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Stop receives responsible for updating the list of paired devices
     */
    public void stopPairedDevicesReceiver(){
        try {
            mContext.unregisterReceiver(mPairReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
    }





}
