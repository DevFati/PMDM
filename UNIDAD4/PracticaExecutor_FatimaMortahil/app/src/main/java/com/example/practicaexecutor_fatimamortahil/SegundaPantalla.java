package com.example.practicaexecutor_fatimamortahil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.practicaexecutor_fatimamortahil.databinding.ActivitySegundaPantallaBinding;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class SegundaPantalla extends AppCompatActivity {

    private ActivitySegundaPantallaBinding binding;
    private ExecutorService executorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding=ActivitySegundaPantallaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        executorService= Executors.newSingleThreadExecutor();

        //Recupera la lista de URLs de imagenes que se han pasado desde Main a traves del intent.
        ArrayList<String> imagenes=getIntent().getStringArrayListExtra("listaImgs");
        //Si la lista de URLS no es nula, llama al metodo agregarImagen para cada URL en la lista
        if(imagenes!=null){
            for(String url: imagenes){
                agregarImagen(url);
            }
        }
        //Cuando pulsamos en volver, finalizamos "SegundaPantalla" y volvemos a la pantallaPrincipal
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Cuando pulsamos en resetear, se limpia las vistas del contenedor y
        //se vacía la lista de URLs de imagenes.
        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.container.removeAllViews();
                imagenes.clear();

                //Crea un Intent para devolver un resultado a main dnde indicamos que se
                //reinicio la lista y finalizamos esta atividad. La finalizamos para que el main
                //detecte este cambio y se resetee la lista.
                Intent intent = new Intent();
                intent.putExtra("resetear", true);
                setResult(RESULT_OK, intent);
                finish();


            }
        });

    }

    //Descargar la imagen desde URL es un hilo secundrio
    private void agregarImagen(String url) {
        executorService.execute(() -> {
            try {
                //convertimos la imagen a un bitmap
                Bitmap b = obtenerImagen(url);
                //Y la redimensionamos a un maximo de 350 x 350
                Bitmap miniatura = redimensionado(b, 350, 350);
                runOnUiThread(() -> {
                    //En el hilo principal, creamos un nuevo Imageview y le asignamos
                    //la imagen que hemos redimensionado y se agrega al contenedor de imagenes
                    ImageView i = new ImageView(this);
                    i.setImageBitmap(miniatura);
                    //Le damos un padding para que haya espacio entre las difeerentes imagenes
                    i.setPadding(5,25,5,25);
                    binding.container.addView(i);

                });
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }


    //Redimensionamos un Bitmap a un tamaño maximo (350 x 350)
    private Bitmap redimensionado(Bitmap b, int i, int j) {
        //Obtenemos ancho y alto del Bitmap
        int width = b.getWidth();
        int height = b.getHeight();
        //qui lo que hacemos es calcular el aspectRatio que es la relacion de
        //aspecto (para que las imagenes se vean bien y no achatadas o alargadas.
        float aspectRatio = (float) width / height;
        int newWidth = i;
        int newHeight = j;

        if (width > height) {
            // Si la imagen es más ancha, ajusta el ancho y calcula la altura proporcionalmente
            newHeight = Math.round(newWidth / aspectRatio);
        } else {
            // Si la imagen es más alta, ajusta la altura y calcula el ancho proporcionalmente
            newWidth = Math.round(newHeight * aspectRatio);
        }
        //Develve un Bitmap escalado a las dimensiones nuevas.
        return Bitmap.createScaledBitmap(b, newWidth, newHeight, true);
    }

    //Lo que hacemos aqui es descargar una imagen desde la URL
    private Bitmap obtenerImagen(String u) throws IOException {
        //Creamos un objeto URL a partir de la cadena u.
        URL url=new URL(u);
        //Abrimos un flujo de entrada para leer los datos de la imagen
        InputStream i=url.openStream();
        //Convertimos el flujo de datos en un Bitmap utilizando BitmapFactory.decodeStream.
        return BitmapFactory.decodeStream(i);

    }
}