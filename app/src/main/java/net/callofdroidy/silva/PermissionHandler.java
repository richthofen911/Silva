package net.callofdroidy.silva;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

/**
 * Created by admin on 23/02/16.
 */
public class PermissionHandler {

    public static boolean checkPermission(Context context, String permissionName){
        return (ContextCompat.checkSelfPermission(context, permissionName) == PackageManager.PERMISSION_GRANTED);
    }

    public static void requestPermission(Activity activity, String permissionName, int requestCode){
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionName)){
            Log.e("request reason", "need the permission");
        }else {
            Log.e("request permission", permissionName);
            ActivityCompat.requestPermissions(activity, new String[]{permissionName}, requestCode);
        }
    }
}
