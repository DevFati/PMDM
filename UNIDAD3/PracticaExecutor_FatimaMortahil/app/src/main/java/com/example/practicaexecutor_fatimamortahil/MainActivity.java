package com.example.practicaexecutor_fatimamortahil;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.practicaexecutor_fatimamortahil.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE=100;
    private static final int maxImagenes=5;
    private ExecutorService executorService;
    private ActivityMainBinding binding;
    private ArrayList<String> imagenes;
    private ActivityResultLauncher<Intent> launcher ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        executorService= Executors.newSingleThreadExecutor();

        imagenes=new ArrayList<>();
        binding.textViewContador.setText(binding.textViewContador.getText()+" "+imagenes.size()+" / "+maxImagenes);

        //Configuramos el scroll en txtDescarga
        binding.txtDescarga.setMovementMethod(new android.text.method.ScrollingMovementMethod());
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
        binding.btnDescargarImagen.setOnClickListener(view -> {
            String imagenUrl = binding.edImagenURL.getText().toString();
            if (!imagenUrl.isEmpty()) {
                validarURLImagen(imagenUrl, new URLValidationCallback() {
                    @Override
                    public void onValidationComplete(boolean isValid) {
                        if (isValid) {
                            runOnUiThread(() -> {
                                if (imagenes.size() < maxImagenes) {
                                    imagenes.add(imagenUrl);
                                    binding.edImagenURL.setText("");
                                    binding.textViewContador.setText("Contador imágenes: " + imagenes.size() + " / " + maxImagenes);
                                    Toast.makeText(MainActivity.this, "Imagen añadida", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Límite de imágenes alcanzado", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            runOnUiThread(() -> binding.edImagenURL.setError("URL no válida o inaccesible"));
                        }
                    }
                });
            } else {
                binding.edImagenURL.setError("Introduce una URL válida de una imagen");
            }
        });


        //Ver imagenes en miniatura
        binding.btnVerFotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, SegundaPantalla.class);
                intent.putStringArrayListExtra("listaImgs",imagenes);
                //Iniciamos la segunda actividad con un launcher para que guarde la lista en caso
                //de resetearla
                launcher.launch(intent);
            }
        });

        launcher=registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    System.out.println("entra");
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        boolean resetear = result.getData().getBooleanExtra("resetear", false);

                        if (resetear) {
                            imagenes.clear();
                            Toast.makeText(this, "Lista de imágenes reiniciada", Toast.LENGTH_SHORT).show();
                            binding.textViewContador.setText("Contador imagenes:  "+imagenes.size()+" / "+maxImagenes);

                        }
                    }
                }
        );


    }

    private boolean internet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkCapabilities capabilities = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
            }
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

    private void validarURLImagen(String url, URLValidationCallback callback) {
        executorService.execute(() -> {
            boolean isValid = false;
            if (url.matches(".*(\\.(png|jpg|jpeg|gif|bmp))$")) {
                try {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    int responseCode = connection.getResponseCode();
                    isValid = responseCode == HttpURLConnection.HTTP_OK;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            callback.onValidationComplete(isValid);
        });
    }



    //Utilizamos la interfaz para manejar el resultado de la validación
    //de la url porque la validación se realiza en un
    //hilo secundario
    interface URLValidationCallback {
        void onValidationComplete(boolean isValid);
    }


}

