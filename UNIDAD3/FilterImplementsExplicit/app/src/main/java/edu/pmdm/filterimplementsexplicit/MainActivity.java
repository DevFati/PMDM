package edu.pmdm.filterimplementsexplicit;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ImageView imag=findViewById(R.id.imageView);


        //Obtenemos el Intent que inició la actividad

        Intent intent=getIntent();
        String action=intent.getAction();
        String type=intent.getType();

        //Verificamos que la acción sea ACTION_SEND y el tipo de dato sea img
        if(Intent.ACTION_SEND.equals(action) && "image/jpeg".equals(type)){
            handleSendImg(intent,imag); //Maneja la imagen compartida
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    //handleImg es el metodo que maneja el envio de imagenes
    private void handleSendImg(Intent intent, ImageView imag)  {
        //extrae el objeto de tipo parcelable de lo que esta almacenado en el intent
        Uri imagendir=intent.getParcelableExtra(Intent.EXTRA_STREAM); //EXTRA_STREAM es la clave para acceder a los datos enviados
        if(imagendir!=null){
            imag.setImageURI(imagendir);
        }

    }


}