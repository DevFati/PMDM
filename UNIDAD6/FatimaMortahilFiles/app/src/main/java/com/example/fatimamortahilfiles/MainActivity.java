package com.example.fatimamortahilfiles;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.fatimamortahilfiles.databinding.ActivityMainBinding;

import android.Manifest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSIONS = 100;

    private ActivityMainBinding binding;
    private LineAdapter adaptador;
    private Uri archivoUri;

    private List<String> lineas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        binding.btnElegirArchivo.setOnClickListener(v -> seleccionarArchivo());
        binding.btnLeerArchivo.setOnClickListener(v -> leerArchivo());
        binding.btnGuardarArchivo.setOnClickListener(v -> guardarArchivo());
        binding.btnAniadirLinea.setOnClickListener(v -> añadirLinea());

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adaptador = new LineAdapter(lineas);
        binding.recyclerView.setAdapter(adaptador);

    }

    private void seleccionarArchivo() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (!tienePermisos()) {
                solicitarPermisos();
            } else {
                abrirSelectorDeArchivos();
            }
        } else {
            abrirSelectorDeArchivos();
        }
    }

    private boolean tienePermisos() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void solicitarPermisos() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        }, REQUEST_PERMISSIONS);
    }


    private void abrirSelectorDeArchivos() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("text/plain"); //aqui tenemos que filtrar para seleccionar solo archivos de texto
        startActivityForResult(intent, 1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    archivoUri = data.getData();
                    String nombreArchivo = obtenerNombreArchivo(archivoUri);
                    if (nombreArchivo != null) {
                        binding.tvNombreArchivo.setText(nombreArchivo);
                    } else {
                        binding.tvNombreArchivo.setText("Archivo desconocido");
                    }
                }
            }
        }
    }

    @SuppressLint("Range")
    private String obtenerNombreArchivo(Uri uri) {
        String resultado = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    resultado = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
                cursor.close();
            }
        }
        return resultado;
    }

    private void leerArchivo() {
        if (archivoUri == null) {
            Toast.makeText(this, "No se ha elegido un archivo", Toast.LENGTH_SHORT).show();
            return;
        }
        lineas.clear(); // limpiamos antes de cargar las nuevas lineas

        try {
            InputStream inputStream = getContentResolver().openInputStream(archivoUri);
            if (inputStream != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String linea;
                while ((linea = reader.readLine()) != null) {
                    lineas.add(linea);
                }

                reader.close();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error al leer el archivo", Toast.LENGTH_SHORT).show();
        }
        if (lineas.size() == 0) { //si la linea esta vacia significa que no hay contenido en el .txt
            Toast.makeText(this, "Archivo vacío", Toast.LENGTH_SHORT).show();
        }
        adaptador.notifyDataSetChanged();
        actualizarContadorPalabras();
    }

    //aqui guardamos el contenido del recyclerview en el archivo
    private void guardarArchivo() {
        if (archivoUri == null) {
            Toast.makeText(this, "No se ha elegido un archivo", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            OutputStream outputStream = getContentResolver().openOutputStream(archivoUri, "wt");
            if (outputStream != null) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
                for (String linea : lineas) {
                    writer.write(linea);
                    writer.newLine();
                }
                writer.close();
                Toast.makeText(this, "Archivo guardado con éxito", Toast.LENGTH_SHORT).show();

            }

        } catch (Exception e) {
            Toast.makeText(this, "Error al guardar el archivo", Toast.LENGTH_SHORT).show();
        }
        actualizarContadorPalabras();
    }

    //añadimos aqui una nueva linea al recyclerview
    private void añadirLinea() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialogo_aniadir_linea, null);
        EditText editTextLinea = dialogView.findViewById(R.id.editTextLinea);

        builder.setView(dialogView)
                .setPositiveButton("Añadir", (dialog, which) -> {
                    String nuevaLinea = editTextLinea.getText().toString().trim();
                    if (!nuevaLinea.isEmpty()) {
                        lineas.add(nuevaLinea);
                        adaptador.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create()
                .show();
    }


    //actualizamos el contador de palabras al cargar el fichero y al guardarlo
    private void actualizarContadorPalabras() {
        int contador = 0;
        for (String linea : lineas) {
            if (!linea.isEmpty()) {
                contador += linea.split("\\s+").length;
            }
        }
        binding.tvContadorPalabras.setText("Contador palabras: " + contador);
    }


}



