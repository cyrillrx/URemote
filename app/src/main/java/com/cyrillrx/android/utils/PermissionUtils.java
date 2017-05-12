package com.cyrillrx.android.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Cyril Leroux
 *         Created 02/08/2016.
 */

public class PermissionUtils {

    public static synchronized boolean hasReadPermission(Activity activity) {

        return (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    public static synchronized void requestReadPermission(Activity activity, int requestCode) {

        ActivityCompat.requestPermissions(
                activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, requestCode);
    }

    public static synchronized boolean hasWritePermission(Activity activity) {

        return (ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED);
    }

    public static synchronized void requestWritePermission(Activity activity, int requestCode) {

        ActivityCompat.requestPermissions(
                activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
    }

    public static String[] selectPicturePermissions(Context context) {

        return getNotGranted(context,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    public static String[] takePicturePermissions(Context context) {

        return getNotGranted(context,
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private static String[] getNotGranted(Context context, String... permissions) {

        final List<String> permissionsNeeded = new ArrayList<>();

        for (String permission : permissions) {

            int grantedResult = ContextCompat.checkSelfPermission(context, permission);
            if (grantedResult != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        return permissionsNeeded.toArray(new String[permissionsNeeded.size()]);
    }
}
