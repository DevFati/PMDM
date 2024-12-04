package com.example.practicaexecutor_fatimamortahil;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.practicaexecutor_fatimamortahil.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final int maxImagenes=5;  //Mi limite maximo de las imagenes que puede añadir el usuario para evitar desbordamientos
    private ExecutorService executorService; //Nos ayuda a realizar las tareas en segundo plano (en este ejercicio la descarga de contenido de internet)
    private ActivityMainBinding binding; //Atajo para el acceso a los diferentes elementos de nuestra pantalla
    private ArrayList<String> imagenes; //Guardaremos las URLs de imagenes validas que añada el usuario
    private ActivityResultLauncher<Intent> launcher ; //Nos ayuda a recuperar info de otra ventana
    private boolean imagenPequeña;  // Con este booleano lo que hacemos es comprobar
    //si la imagen es pequeña o no (en ese caso lo que hacemos es mandar solo un toast indicando eso, no otros errores)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Configuramos el ejecutor que realizara tareas en segundo plano. Lo declaramos como singleThreadExecutor
        //Solo permite una tarea a la vez. (suficiente para este ejercicio)
        executorService= Executors.newSingleThreadExecutor();
        //Creamos una lista vacia donde iremos guardando las URLs de las imagenes
        imagenes=new ArrayList<>();
        //Mostraremos un contador en la pantalla que nos indica las imagenes que hay añadidas y cual es el límite.
        binding.textViewContador.setText(binding.textViewContador.getText()+" "+imagenes.size()+" / "+maxImagenes);

        imagenPequeña=false;
        //Configuramos el scroll en txtDescarga para que se pueda desplazar en el si es muy largo
        binding.txtDescarga.setMovementMethod(new android.text.method.ScrollingMovementMethod());
        //Cuando el usuario presiona el boton "Decargar"
        binding.btnDescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Leemos la url que el usario escribio
                String url=binding.edURL.getText().toString();
                //Si no esta vacia y tenemos internet, intentamos descargar su contenido
                if(!url.isEmpty() && internet()){
                    Descargar(url);
                }else{
                    //Si no se logra, mostramos un mensaje de error.
                    binding.txtDescarga.setText("No se ha podido establecer conexion a internet o URL inválida");
                }
            }
        });


        //Cuando el usuario le da clic al botón "Agregar Imagen"
        binding.btnDescargarImagen.setOnClickListener(view -> {
            //Obtenemos el texto que escribio el usuario en el campo de la imagen URL correspondiente
            String imagenUrl = binding.edImagenURL.getText().toString();
            //Si la URL no esta vacía, llamamos a una funcion "validarURLImagen" para verificar si es una url de imagen valida
            if (!imagenUrl.isEmpty()) {
                validarURLImagen(imagenUrl, new URLValidationCallback() {
                    @Override
                    public void onValidationComplete(boolean isValid) {
                        //Si la URL es valida, usamos runOnUiThread para actualizar la lista
                        //y lanzar los respectivos mensajes de error y/o actualizar el contador
                        //se utiliza para asegurar que cualquier actualiizacion se ejecute en el
                        //hilo principal, (para evitar errores de concurrencia) .
                        if (isValid) {
                            runOnUiThread(() -> {
                                //Si hay menos imagenes que el maximo permitido, añadimos la URL a la lista
                                if (imagenes.size() < maxImagenes) {
                                    imagenes.add(imagenUrl);
                                    //Actualizamos el contador y le indicamos al usario que la imagen fue añadida
                                    binding.edImagenURL.setText("");
                                    binding.textViewContador.setText("Contador imágenes: " + imagenes.size() + " / " + maxImagenes);
                                    Toast.makeText(MainActivity.this, "Imagen añadida", Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(MainActivity.this, "Límite de imágenes alcanzado", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            //Si la URL no es una imagen valida, se lo indicamos al usuario
                            if(!imagenPequeña){
                                runOnUiThread(() -> {
                                    binding.edImagenURL.setError("URL de imagen no válida o inaccesible");
                                });
                            }


                        }
                    }
                });
            } else {
                binding.edImagenURL.setError("Introduce una URL válida de una imagen");
            }
        });


        //Ver imagenes en miniatura en una segunda pantalla
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
                 //   System.out.println("entra");
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

    //verificamos si hay conexion a internet
    private boolean internet() {
        //Obtenemos el gestor
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

    //Este método descarga contenido de la URL y lo muestra en el campo "txtDescarga"
    //Si lanza un error, se lo indicamos al usuario.
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



    //este metood se encarga de descargar el contenido textual de una URL
    private String leer(String u) throws IOException {
        URL url=new URL(u);
        //Establece conexion HTTP con la URL que le pasamos
        HttpURLConnection c=(HttpURLConnection) url.openConnection();
        //Configura el método HTTP como get, para recuperar los datos
        c.setRequestMethod("GET");
        //ConnectTimeout, es el tiempo máximo para establecer la conexion (esta en milisegundos)
        c.setConnectTimeout(10000);
        //ReadTimeout, es el tiempo maximo para leer datos (15 segudos)
        c.setReadTimeout(15000);
        //Establecemos la conexion con la URL.
        c.connect();

        //Aquí lo que hacemos es crear un bufferedReader para leer los datos que se recibieron de la URl.
        BufferedReader reader = new BufferedReader(new InputStreamReader(c.getInputStream()));
       //Aqui en el stringBuilder acumulamos las lineas que se van descargando
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        reader.close();
        //Devolvemos el contenido
        return builder.toString();
    }

    //Verificamos si la URL apunta a una imagen y comprobamos sus dimensiones  y que no sea svg
    //No devuelve un valor directamente sino que gestiona el resultado mediante el callback
    private void validarURLImagen(String url, URLValidationCallback callback) {
        //Ejecutamos la validacion en un hilo secundario usando el executer
        executorService.execute(() -> {
            //con isValida, veo si la URL es valida o no
            boolean isValid = false;
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                connection.setRequestMethod("GET"); // Cambiar a GET para descargar la imagen completa
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode(); //Obtenemos el codigo de respuesta, 200 significa que todo correcto
                String contentType = connection.getContentType(); //Para verificar que es una imagen

                // Verificar si la URL es válida y el Content-Type es de imagen
                if (responseCode == HttpURLConnection.HTTP_OK &&
                        contentType != null &&
                        contentType.startsWith("image/") &&
                        !contentType.equals("image/svg+xml")) {
                    // Descargar y verificar las dimensiones de la imagen
                    InputStream inputStream = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                    //Si la imagen es valida, verificamos su tamaño
                    if (bitmap != null) {
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();

                        if (width >= 200 && height >= 200) {
                            isValid = true; // La imagen cumple con las dimensiones mínimas
                        } else {
                            imagenPequeña=true; //indicamos que se trata de una imagen pequeña
                            // Notificar al usuario si las dimensiones son pequeñas
                            runOnUiThread(() -> Toast.makeText(MainActivity.this,
                                    "La imagen es demasiado pequeña, tamaño mínimo 200 x 200.",
                                    Toast.LENGTH_SHORT).show());
                        }
                    }
                    //cerramos el flujo de entrada
                    inputStream.close();
                    //Si la imagen es SVG, le decimos al usuario que no es valida.
                    // Esto es porque imagenes SVG no son compatibles de forma nativa con el manejo de
                    //imagenes mediante BitmapFactory porque los SVG no son mapas de bits, sino graficos vectoriales.
                } else if (contentType != null && contentType.equals("image/svg+xml")) {
                    // Notificar al usuario si es un SVG
                    runOnUiThread(() -> Toast.makeText(this,
                            "No se pueden manejar imágenes SVG.",
                            Toast.LENGTH_SHORT).show());
                }
            } catch (IOException e) {
                e.printStackTrace();
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

