package es.riberadeltajo.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import es.riberadeltajo.myapplication.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ExecutorService executorService;
    private ActivityMainBinding binding;
    private ArrayList<String> imagenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        executorService= Executors.newSingleThreadExecutor();

        imagenes=new ArrayList<>();
        binding.btnDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url=binding.edURL.getText().toString();

                if(!url.isEmpty() && internet()){
                    Descargar(url);
                }else{
                    binding.txtDescarga.setText("No se ha podido establecer conexion a internet o URL inválida");
                }
            }
        });


        //Agrego la url que mete el usuario a la lista
        binding.btnDescargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String imagenUrl=binding.edImagenURL.getText().toString();

                if(!imagenUrl.isEmpty()){
                    imagenes.add(imagenUrl);
                    binding.edImagenURL.setError("Introduce una URL ");
                }
            }
        });

        //Ver imagenes en miniatura
        binding.btnVerFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent intent=new Intent(MainActivity.this, SegundaPantalla.class);
            intent.putStringArrayListExtra("listaImgs",imagenes);
            startActivity(intent);
            }
        });


    }

    private boolean internet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
        }
        return false;
    }

    public void Descargar(String url){
       executorService.execute(() -> {
           try {
               String r = leer(url);
               runOnUiThread(() -> {
                   binding.txtDescarga.setText(r);
               });
           }catch (Exception e){
               runOnUiThread(() -> {
                   binding.txtDescarga.setText("Error al descargar: " + e.getMessage());
               });
           }

       });
    }




    private String leer(String u) throws IOException {
        URL url=new URL(u);
        HttpURLConnection c=(HttpURLConnection) url.openConnection();
        c.setRequestMethod("GET");
        c.setConnectTimeout(10000);
        c.setReadTimeout(15000);
        c.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        reader.close();
        return builder.toString();
    }



    
}