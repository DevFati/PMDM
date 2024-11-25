package com.example.notificacionespersonalizadas;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

public class DetectarBrillo extends ContentObserver {
    private Context c;
    private int brillo;


    public DetectarBrillo(Handler handler, Context c) {
        super(handler);
        this.c = c;
        //valor de brillo ultimo
        this.brillo = brilloMovil(); //obtenemos el valor del brillo inicial
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        int brilloActual=brilloMovil();
        //si el brillo aumenta, se indica
        if(brilloActual>brillo){
            creaNotificacion("El brillo ha aumentado");
        }else if(brilloActual<brillo){
            creaNotificacion("El brillo ha bajado");
        }
        //actualizamos el valor del brillo guardado
        brillo=brilloActual;
    }



    public void creaNotificacion(String texto){
        //  constructor de la notificación
        NotificationCompat.Builder constructorNotif = new NotificationCompat.Builder(c, "miCanal")
                .setSmallIcon(android.R.drawable.btn_star)
                .setContentTitle("Cambio en el brillo")
                .setContentText(texto)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true); //la notificacion se elimina cuando se pulsa


        // ¿Qué actividad se abre al pulsar el usuario sobre la notificación?
        Intent resultadoIntent = new Intent(c, NotificacionPulsada.class);
        brillo=brilloMovil();
        resultadoIntent.putExtra("valorBrillo",brillo); //pasamos el valor del brillo



        // Especifica FLAG_IMMUTABLE junto con FLAG_UPDATE_CURRENT
        PendingIntent resultadoPendingIntent = PendingIntent.getActivity(c,0, resultadoIntent,PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        constructorNotif.setContentIntent(resultadoPendingIntent);

        //  Creación del canal (si la API>26 (oreo)) y el envío de la notificación
        NotificationManager notificationManager = (NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel canal = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            canal = new NotificationChannel("miCanal", "Canal de notificacion de brillo",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(canal);

        }
        notificationManager.notify(1, constructorNotif.build());
    }

    private int brilloMovil() {
        try{
            ContentResolver r=c.getContentResolver();
            //Obtenemos el valor del brillo desde los ajustes de sistema (entre 0 y 255)
            return Settings.System.getInt(r,Settings.System.SCREEN_BRIGHTNESS);
        }catch (Settings.SettingNotFoundException e){
            e.printStackTrace();
            return 0;
        }

    }
}
