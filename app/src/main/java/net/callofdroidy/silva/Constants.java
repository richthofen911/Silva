package net.callofdroidy.silva;

import android.Manifest;

/**
 * Created by admin on 08/04/16.
 */
public interface Constants {
    int PERMISSION_REQUEST_CODE_READ_PHONE_STATE = 102;
    int PERMISSION_REQUEST_CODE_BUNDLE = 110;

    String[] PERMISSION_READ_PHONE_STATE = new String[]{Manifest.permission.READ_PHONE_STATE};

    int NOTIFICATION_ID_MONITOR = 121;

    String SPEAK_ID_GENERAL = "speak_id_general";
    String PHONE_NUMBER_XIAOLU = "+16475757899";
}
