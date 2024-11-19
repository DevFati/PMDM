package com.example.contextproyect;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
//En este ejemplo, usaremos el contexto para acceder a varios servicios y recursos del sistema, como SharedPreferences, AlarmManager, y AudioManager.
    private AudioManager audioManager;
    private SeekBar volumeSeekBar;
    private ConstraintLayout layout;
    private Button botonFondoBlanco;
    private Button botonFondoAzul;
    private Button botonRojo;
    private Button botonMorado;
    //Creamos una lista de colores para la paleta de colores para el cambio de fondo de todos los
    //botones de la mainActivity.
    private int[] paleta={Color.RED,Color.BLUE,Color.GREEN,Color.YELLOW,Color.CYAN,Color.MAGENTA,Color.GRAY,Color.BLACK};
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



        // Inicializar AudioManager para gestionar el volumen del dispositivo
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Inicializar SeekBar para controlar el volumen
        volumeSeekBar = findViewById(R.id.volumeSeekBar);

        // Obtener el volumen máximo del dispositivo y configurarlo en la SeekBar
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setMax(maxVolume);

        /**
         * Obtiene una instancia de SharedPreferences para leer y guardar datos persistentes.
         *
         * Se usa SharedPreferences para almacenar pequeños conjuntos de datos, como configuraciones
         * o preferencias del usuario, de manera persistente. Esto significa que los datos
         * se conservarán incluso después de cerrar la aplicación.
         *
         * @param "MyPrefs" El nombre del archivo donde se guardarán las preferencias.
         *                  Puedes elegir cualquier nombre, pero debes usar el mismo nombre
         *                  al acceder a estas preferencias más tarde.
         * @param Context.MODE_PRIVATE El modo de acceso, que asegura que el archivo de
         *                             preferencias solo sea accesible por esta aplicación.
         *                             Es la opción más segura para proteger los datos.
         * @return SharedPreferences El objeto SharedPreferences para leer y guardar datos.
         */
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        // leer el color guardado para los botones
        int colorGuardado = sharedPreferences.getInt("fondoBtn", Color.MAGENTA);
        //Boton para permitir abrir la paleta de colores para elegir uno y
        //cambiar el fondo de los botones
        // aplicamos el color guardado a los botones
        botonRojo = findViewById(R.id.btnRojo);
        botonMorado = findViewById(R.id.btnMorado);
        botonFondoBlanco=findViewById(R.id.buttonBlanco);
        botonFondoAzul=findViewById(R.id.buttonAzul);
        botonFondoBlanco.setBackgroundColor(colorGuardado);
        botonFondoAzul.setBackgroundColor(colorGuardado);
        botonRojo.setBackgroundColor(colorGuardado);
        botonMorado.setBackgroundColor(colorGuardado);

        Button botonPaleta=findViewById(R.id.buttonPaleta);
        // sharedPreferences era para guardar los valores  siempre que se vuelva a ejecutar la app

        botonPaleta.setOnClickListener(v ->{
            mostrarPaleta(sharedPreferences);
        });
        // Leer el volumen guardado de SharedPreferences, o usar el volumen actual si no hay ninguno guardado
        int savedVolume = sharedPreferences.getInt("audioLevel", audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        volumeSeekBar.setProgress(savedVolume);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, savedVolume, 0);

        // Listener para detectar cambios en la SeekBar
        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Ajustar el volumen con AudioManager
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);

                // Guardar el nivel de audio en SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("audioLevel", progress);
                editor.apply();

                // Mostrar un Toast con el nivel de volumen guardado
                Toast.makeText(MainActivity.this, "Volumen guardado: " + progress, Toast.LENGTH_SHORT).show();
            }

            /**
             * Método llamado cuando el usuario comienza a tocar o interactuar con la SeekBar.
             *
             * Aunque no se necesita hacer nada específico aquí en este ejemplo, este método debe estar
             * presente porque forma parte de la interfaz SeekBar.OnSeekBarChangeListener.
             * Puedes usarlo para pausar acciones, registrar eventos o realizar otras tareas
             * cuando el usuario empiece a ajustar la SeekBar.
             *
             * @param seekBar La SeekBar que se está tocando.
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }


            /**
             * Método llamado cuando el usuario deja de tocar o interactuar con la SeekBar.
             *
             * Al igual que con onStartTrackingTouch, no se necesita hacer nada en este ejemplo,
             * pero el método debe estar implementado porque es obligatorio por la interfaz
             * SeekBar.OnSeekBarChangeListener. Puedes usarlo para reanudar acciones, guardar
             * datos, o realizar otras tareas cuando el usuario termine de ajustar la SeekBar.
             *
             * @param seekBar La SeekBar que se dejó de tocar.
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });


        //Layout del que cambiaremos el fondo
        layout=findViewById(R.id.main);

//Ponemos el fondo al color guardado, o a blanco si no hay guardado
        int savedBackgroundColor = sharedPreferences.getInt("backgroundColor",Color.WHITE);
        layout.setBackgroundColor(savedBackgroundColor);


//Botón para cambiar el fondo a azul
        botonFondoAzul =findViewById(R.id.buttonAzul);
        botonFondoAzul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setBackgroundColor(Color.BLUE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("backgroundColor", Color.BLUE);
                editor.apply();
                Toast.makeText(MainActivity.this, "Color cambiado a azul", Toast.LENGTH_SHORT).show();
            }
        });
//Botón para cambiar el fondo a blanco
        botonFondoBlanco=findViewById(R.id.buttonBlanco);
        botonFondoBlanco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout.setBackgroundColor(Color.WHITE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("backgroundColor", Color.WHITE);
                editor.apply();
                Toast.makeText(MainActivity.this, "Color cambiado a blanco", Toast.LENGTH_SHORT).show();
            }
        });

/*
        //el boton rojo del que cambiaremos el fondo
        botonRojo=findViewById(R.id.btnRojo);
        botonMorado=findViewById(R.id.btnMorado);
//Ponemos el fondo al color guardado, o a blanco si no hay guardado
        int savedBackgroundColorB = sharedPreferences.getInt("backgroundColorB",Color.MAGENTA);
        botonRojo.setBackgroundColor(savedBackgroundColorB);
        botonMorado.setBackgroundColor(savedBackgroundColorB);
        //Botón para cambiar el fondo de un botón a rojo
        botonRojo=findViewById(R.id.btnRojo);
        botonRojo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonRojo.setBackgroundColor(Color.RED);
                botonMorado.setBackgroundColor(Color.RED);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("backgroundColorB", Color.RED);
                editor.apply();
                Toast.makeText(MainActivity.this, "Color cambiado a rojo", Toast.LENGTH_SHORT).show();
            }
        });


        //Botón para cambiar el fondo de un botón a morado
        botonMorado=findViewById(R.id.btnMorado);
        botonMorado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                botonRojo.setBackgroundColor(Color.MAGENTA);
                botonMorado.setBackgroundColor(Color.MAGENTA);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("backgroundColorB", Color.MAGENTA);
                editor.apply();
                Toast.makeText(MainActivity.this, "Color cambiado a morado", Toast.LENGTH_SHORT).show();
            }
        });

*/

        // Botón para leer el volumen guardado
        Button buttonReadVolume = findViewById(R.id.buttonReadVolume);
        buttonReadVolume.setOnClickListener(v -> {
            int currentSavedVolume = sharedPreferences.getInt("audioLevel", -1);
            Toast.makeText(MainActivity.this, "Volumen leido: " + currentSavedVolume, Toast.LENGTH_SHORT).show();
        });

        // Botón para iniciar la segunda actividad
        Button buttonStartActivity = findViewById(R.id.buttonStartActivity);
        buttonStartActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SecondActivity.class);
            startActivity(intent);
        });
    }

    private void mostrarPaleta(SharedPreferences s){
    String[] nombreColores={"Rojo","Azul","Verde","Amarillo","Cian","Morado","Gris","Negro"};

    //El alertDialog para la paleta
        new AlertDialog.Builder(this)
                .setTitle("Elige un color para el fondo de los botones")
                .setItems(nombreColores,(dialog, which) -> {
                    int colorSeleccionado=paleta[which];

                    botonMorado.setBackgroundColor(colorSeleccionado);
                    botonRojo.setBackgroundColor(colorSeleccionado);
                    botonFondoAzul.setBackgroundColor(colorSeleccionado);
                    botonFondoBlanco.setBackgroundColor(colorSeleccionado);

                    //Guardamos el color que eligio el usuario en sharedPreferences

                    SharedPreferences.Editor e=s.edit();
                    //metemos la clave, y el indice del color seleccionado
                    e.putInt("fondoBtn",colorSeleccionado);
                    e.apply();

                    //confirmalo
                    Toast.makeText(this,"Color seleccionado: "+nombreColores[which],Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}