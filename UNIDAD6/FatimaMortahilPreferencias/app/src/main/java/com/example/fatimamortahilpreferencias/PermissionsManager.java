package com.example.fatimamortahilpreferencias;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsManager {

    private static final int PERMISSION_REQUEST_CODE = 100;

    public static boolean comprobarPermisosNotificaciones(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }

    public static void pedirPermisosNotificaciones(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
    }
}
