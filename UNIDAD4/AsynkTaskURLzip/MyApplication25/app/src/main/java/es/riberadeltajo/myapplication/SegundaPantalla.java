package es.riberadeltajo.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.riberadeltajo.myapplication.databinding.ActivitySegundaPantallaBinding;

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

    }

    private void agregarImagen(String url) {
        executorService.execute(() -> {
            try {
                Bitmap b = obtenerImagen(url);
                Bitmap miniatura = Bitmap.createScaledBitmap(b, 200, 200, true);
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

    private Bitmap obtenerImagen(String u) throws IOException {
        URL url=new URL(u);
        InputStream i=url.openStream();
        return BitmapFactory.decodeStream(i);

    }
}