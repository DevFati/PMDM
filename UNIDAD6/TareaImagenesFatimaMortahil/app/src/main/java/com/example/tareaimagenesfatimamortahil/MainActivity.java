package com.example.tareaimagenesfatimamortahil;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    private static final int SOLICITUD_PERMISO_CAMARA = 100;
    private static final int SOLICITUD_PERMISO_VIDEO = 101;

    private static final int SOLICITUD_CAPTURA_IMAGEN = 1;
    private static final int SOLICITUD_GRABAR_VIDEO = 2;
    private ImageView imagenVista;
    private Bitmap imagenOriginal;
    private Bitmap imagenEditada;
    private SeekBar barraBrillo;
    private VideoView videoVista;
    private MediaPlayer reproductor;
    private Uri videoUri;
    private boolean escalaGrisesActivada = false;
    private boolean invertirColoresActivado = false;
    private int brilloValor = 0;
    private boolean rotacionActivada = false;
    private float anguloRotacion = 0;


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

        imagenVista = findViewById(R.id.imagenVista);
        barraBrillo = findViewById(R.id.barraBrillo);
        videoVista = findViewById(R.id.videoVista);

        Button botonCapturarImagen = findViewById(R.id.botonCapturarImagen);
        Button botonEscalaGrises = findViewById(R.id.botonEscalaGrises);
        Button botonInvertirColores = findViewById(R.id.botonInvertirColores);
        Button botonRotarImagen = findViewById(R.id.botonRotarImagen);
        Button botonGuardarImagen = findViewById(R.id.botonGuardarImagen);
        Button botonGrabarVideo = findViewById(R.id.botonGrabarVideo);
        Button botonReproducirVideo = findViewById(R.id.botonReproducirVideo);
        

        botonCapturarImagen.setOnClickListener(v -> solicitarPermisoCapturaImagen());
        botonEscalaGrises.setOnClickListener(v -> {
            escalaGrisesActivada = !escalaGrisesActivada;
            aplicarFiltros();
        });
        botonInvertirColores.setOnClickListener(v -> {
            invertirColoresActivado = !invertirColoresActivado;
            aplicarFiltros();
        });
        botonRotarImagen.setOnClickListener(v -> {
            anguloRotacion += 90;
            aplicarFiltros();
        });
        botonGuardarImagen.setOnClickListener(v -> guardarImagen());
        botonGrabarVideo.setOnClickListener(v -> solicitarPermisoGrabacionVideo());
        botonReproducirVideo.setOnClickListener(v -> mostrarDialogoVelocidadVideo());

        barraBrillo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progreso, boolean delUsuario) {

                brilloValor=progreso-100;
                aplicarFiltros();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }


    private void mostrarDialogoVelocidadVideo() {
        if (videoUri == null) {
            Toast.makeText(this, "No hay un video para reproducir", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] opciones = {"0.5x", "1x", "2x"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona la velocidad de reproducción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                float velocidad = 1.0f;
                if (which == 0) velocidad = 0.5f;
                else if (which == 1) velocidad = 1.0f;
                else if (which == 2) velocidad = 2.0f;
                reproducirVideo(velocidad);
            }
        });
        builder.show();
    }

    private void reproducirVideo(float velocidad) {
        videoVista.setVideoURI(videoUri);
        videoVista.setOnPreparedListener(mp -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mp.setPlaybackParams(mp.getPlaybackParams().setSpeed(velocidad));
            }
            mp.start();
        });
    }


    private void solicitarPermisoCapturaImagen() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, SOLICITUD_PERMISO_CAMARA);
        } else {
            abrirCamara();
        }
    }

    private void solicitarPermisoGrabacionVideo() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO}, SOLICITUD_PERMISO_VIDEO);
        } else {
            abrirGrabacionVideo();
        }
    }

    private void abrirCamara() {
        Intent intentCaptura = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intentCaptura, SOLICITUD_CAPTURA_IMAGEN);
    }


    @SuppressLint("QueryPermissionsNeeded")
    private void abrirGrabacionVideo() {
        Intent intentVideo = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intentVideo.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        intentVideo.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);
        if (intentVideo.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intentVideo, SOLICITUD_GRABAR_VIDEO);
        } else {
            Toast.makeText(this, "No se encontró una aplicación compatible para grabar video", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SOLICITUD_GRABAR_VIDEO && resultCode == RESULT_OK && data != null) {
            videoUri = data.getData();
            if (videoUri != null) {
                videoVista.setVideoURI(videoUri);
                videoVista.start();
            }
        } else if (requestCode == SOLICITUD_CAPTURA_IMAGEN && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            imagenOriginal = (Bitmap) extras.get("data");
            if (imagenOriginal != null) {
                imagenVista.setImageBitmap(imagenOriginal);
            }
        }
    }



    private void guardarImagen() {
        if (imagenOriginal == null) {
            Toast.makeText(this, "No hay imagen para guardar", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap imagenFinal = imagenEditada != null ? imagenEditada : imagenOriginal;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, "imagen_editada_" + System.currentTimeMillis() + ".png");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/EditorImagenes");

        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                imagenFinal.compress(Bitmap.CompressFormat.PNG, 100, out);
                actualizarGaleria(this, uri);
                Toast.makeText(this, "Imagen guardada en la Galería", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al guardar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void aplicarFiltros() {


        if (imagenOriginal == null) {
            Toast.makeText(this, "Debes capturar una imagen primero", Toast.LENGTH_SHORT).show();

            return;
        }

        Bitmap imagenProcesada = Bitmap.createBitmap(imagenOriginal.getWidth(), imagenOriginal.getHeight(), imagenOriginal.getConfig());
        Canvas canvas = new Canvas(imagenProcesada);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();

        if (escalaGrisesActivada) {
            colorMatrix.setSaturation(0);
        }

        if (invertirColoresActivado) {
            colorMatrix.set(new float[]{
                    -1, 0, 0, 0, 255,
                    0, -1, 0, 0, 255,
                    0, 0, -1, 0, 255,
                    0, 0, 0, 1, 0
            });
        }

        if (brilloValor != 0) {
            colorMatrix.postConcat(new ColorMatrix(new float[]{
                    1, 0, 0, 0, brilloValor,
                    0, 1, 0, 0, brilloValor,
                    0, 0, 1, 0, brilloValor,
                    0, 0, 0, 1, 0
            }));
        }

        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(imagenOriginal, 0, 0, paint);

        if (anguloRotacion % 360 != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(anguloRotacion);
            imagenProcesada = Bitmap.createBitmap(imagenProcesada, 0, 0, imagenProcesada.getWidth(), imagenProcesada.getHeight(), matrix, true);
        }

        imagenEditada = imagenProcesada;
        imagenVista.setImageBitmap(imagenEditada);
    }



    private void actualizarGaleria(Context context, Uri uri) {
        MediaScannerConnection.scanFile(context, new String[]{uri.getPath()}, null, (path, scannedUri) -> {});
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == SOLICITUD_PERMISO_CAMARA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == SOLICITUD_PERMISO_VIDEO) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults.length > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                abrirGrabacionVideo();
            } else {
                Toast.makeText(this, "Permiso de cámara o audio denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
