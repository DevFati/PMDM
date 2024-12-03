package edu.pmdm.actividadsmstocontact_fatima;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
private static final int Permiso_SMS_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Verificamos si hay permiso y sino se pide

        if(ContextCompat.checkSelfPermission(this,Manifest.permission.SEND_SMS)!=PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{

            });
        }

    }

    public InputStream abrirFoto(int identificador){
        Uri contactUri= ContentUris.withAppendedId(
                ContactsContract.Contacts.CONTENT_URI, identificador);

        InputStream inputStream=ContactsContract.Contacts.openContactPhotoInputStream(
                getContentResolver(),contactUri,true);

        return inputStream;
    }





    private void enviarSMS(String identificador, String mensaje) {
        ContentResolver cr = getContentResolver();
        SmsManager smsManager = SmsManager.getDefault();
        Cursor cursorTelefono = cr.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                new String[]{identificador},
                null
        );

        while (cursorTelefono.moveToNext()) {
            String telefono = cursorTelefono.getString(
                    cursorTelefono.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            );
            try {
                smsManager.sendTextMessage(telefono, null, mensaje, null, null);
                Log.d(TAG, "SMS enviado.");
            } catch (Exception e) {
                Log.d(TAG, "No se pudo enviar el SMS.");
                e.printStackTrace();
            }
        }
        cursorTelefono.close();
    }

}