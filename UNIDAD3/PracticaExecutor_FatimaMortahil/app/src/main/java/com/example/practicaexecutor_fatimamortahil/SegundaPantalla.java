package com.example.practicaexecutor_fatimamortahil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.practicaexecutor_fatimamortahil.databinding.ActivitySegundaPantallaBinding;

import java.io.IOException;
import java.io.InputStream;
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

        ArrayList<String> imagenes=getIntent().getStringArrayListExtra("listaImgs");

        if(imagenes!=null){
            for(String url: imagenes){
                agregarImagen(url);
            }
        }

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.container.removeAllViews();
                imagenes.clear();

                // Devolver el resultado a MainActivity
                Intent intent = new Intent();
                intent.putExtra("resetear", true);
                setResult(RESULT_OK, intent);
                finish();


            }
        });

    }

    private void agregarImagen(String url) {
        executorService.execute(() -> {
            try {
                Bitmap b = obtenerImagen(url);
                Bitmap miniatura = redimensionado(b, 350, 350);
                runOnUiThread(() -> {
                    ImageView i = new ImageView(this);
                    i.setImageBitmap(miniatura);
                    binding.container.addView(i);

                });
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    private Bitmap redimensionado(Bitmap b, int i, int j) {
        int width = b.getWidth();
        int height = b.getHeight();

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

        return Bitmap.createScaledBitmap(b, newWidth, newHeight, true);
    }

    private Bitmap obtenerImagen(String u) throws IOException {
        URL url=new URL(u);
        InputStream i=url.openStream();
        return BitmapFactory.decodeStream(i);

    }
}