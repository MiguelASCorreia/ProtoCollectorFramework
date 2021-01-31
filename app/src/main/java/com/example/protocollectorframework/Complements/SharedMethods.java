package com.example.protocollectorframework.Complements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class that stores methods that do not fit in any of the other modules
 */
public class SharedMethods {


    /**
     * Returns the device id. If there is a google account associeted, the device id is equals to the user name of that account, otherwise the id is build by joining the device build model number with a secure Android unique id
     * @param context
     * @return
     */
    @SuppressLint("HardwareIds")
    public static String getMyId(Context context){
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(context);

        if(account != null && account.getDisplayName() != null)
            return account.getDisplayName();

        return Build.MODEL + "_" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }


    /**
     * Converts a date to a string in the format yyyy-MM-dd HH:mm:ss.SSS with the UTC time zone
     * @param date: desired date
     * @return string formatted date
     */

    public static String dateToUTCString(Date date){
        if(date == null)
            return null;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        return dateFormat.format(date);
    }

    /**
     * Create an external directory associated to the framework
     * @param folder_name: directory name
     * @return created directory file
     */

    public static File createDirectories(String folder_name){
        boolean success = true;
        File mainDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath());
        if (!mainDir.exists()) {
            success = mainDir.mkdir();
        }
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ProtoCollector");
        if (!dir.exists()) {
            success = dir.mkdir();
        }
        dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "ProtoCollector/" + folder_name);
        if (!dir.exists()) {
            success = dir.mkdir();
        }

        if(success)
            return dir;
        else return null;

    }

    /**
     * Shows a long length toast on the context
     * @param context: desired context
     * @param text: text to show on toast
     */
    public static void showToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_LONG).show();
    }

}
