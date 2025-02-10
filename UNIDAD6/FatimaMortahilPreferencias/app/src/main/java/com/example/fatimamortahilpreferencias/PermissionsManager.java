package com.example.fatimamortahilpreferencias;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class PermissionsManager {
//Codigo de solicitd de los permisos de notificiaciones ha sido concedido
    private static final int PERMISSION_REQUEST_CODE = 100;
// comprobamos si el permiso fue concedido
    public static boolean comprobarPermisosNotificaciones(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }
// metodo para solicitar el permiso de notificaciones al usuario.
    public static void pedirPermisosNotificaciones(Activity activity) {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, PERMISSION_REQUEST_CODE);
    }
}
