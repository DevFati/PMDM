package es.riberadeltajo.permisosnormales;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public final int PERMISO_ENVIAR_SMS=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView t=findViewById(R.id.txtWifiState);


        //consultar el estado WIFI -> permisos normales (Solo en el manifest)
        WifiManager wifi=(WifiManager) getSystemService(Context.WIFI_SERVICE);
        if(wifi.isWifiEnabled())
            t.setText("Wifi ON");
        else
            t.setText("Wifi OFF");



        if(checkSelfPermission("android.permission.SEND_SMS")== PackageManager.PERMISSION_GRANTED) {
            t.setText("Puede Enviar SMS");
            enviarSMS();
        }
        else {
            t.setText("Denegado!");
            requestPermissions(new String[]{"android.permission.SEND_SMS"},PERMISO_ENVIAR_SMS);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==PERMISO_ENVIAR_SMS)
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                enviarSMS();
    }


    public void enviarSMS(){
        //enviar un sms?
        SmsManager smsManager=SmsManager.getDefault();
        smsManager.sendTextMessage("666666688",null,"Hola, quedamos?",null,null);
    }
}