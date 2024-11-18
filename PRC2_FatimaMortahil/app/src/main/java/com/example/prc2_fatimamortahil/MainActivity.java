package com.example.prc2_fatimamortahil;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


public class MainActivity extends AppCompatActivity {
    private Tablero tablero; //Instanciamos el tablero del juego
    private GridLayout gridLayout; //GridLayout donde se muestran las casillas del juego
    //Dimensiones del tablero y cantidad de minas
    private int filas = 8;
    private int columnas = 8;
    private int minas = 10; // Número de minas inicial
    //índice del personaje seleccionado para las minas
    private int personajeSeleccionadoIndice = 0; // Por defecto, el primer personaje (mina clásica)
    //Recursos gráficos para las diferentes minas
    private final int[] recursosPersonajes = {
            R.drawable.minaclasica,
            R.drawable.minabomber,
            R.drawable.dinamita,
            R.drawable.granada,
            R.drawable.minasubmarina,
            R.drawable.coctelm
    };

    //Recursos gráficos para las minas tachadas que se muestran al perder.
    private final int[] recursosPersonajesTachados = {
            R.drawable.minaclasicatachada,
            R.drawable.minabombertachada,
            R.drawable.dinamitatachada,
            R.drawable.granadatachada,
            R.drawable.minasubmarinatachada,
            R.drawable.coctelmtachado
    };

    //Nivel de dificultad (por defecto es 0) (0: fácil, 1: medio, 2: dificil)
    private int  dificultad=0;
    //Diálogo activo para evitar duplicados.
    private  AlertDialog dialogoActivo;

    /**
     * Método que se ejecuta al crear la actividad principal.
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //Configuramos el título y el color de la ActionBar.
        setTitle("DAM_MineSweeper");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#5f01e8")));
        //Inicializamos el gridLayout del tablero.
        gridLayout = findViewById(R.id.tablero);

        tablero = new Tablero(this, filas, columnas, minas); // Inicializar el tablero con filas, columnas y minas
        tablero.inicializarMatriz(); //Generamos la matriz de casillas y minas.

        // Usar ViewTreeObserver para esperar hasta que el GridLayout esté completamente renderizado antes de iniciar.
        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                tablero.inicializarLayout(gridLayout); // Inicializar el tablero
                //  tablero.mostrarMatrizConsola(); //Aquí miramos por consola el valor de las diferentes casillas
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this); // Eliminar el listener
            }
        });
    }

    /**
     * Método que se ejecuta al rotar la pantalla
     * @param newConfig The new device configuration.
     */
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Listener para redibujar el tablero después de la rotación
        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                tablero.inicializarLayout(gridLayout); // Inicializar el diseño
                tablero.recuperarTablero(gridLayout);   //Recuperamos el estado del tablero
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this); // Eliminar el listener
            }
        });
    }


    /**
     * Método para inflar el menú de opciones.
     * @param menu The options menu in which you place your items.
     *
     * @return true para que se muestre el menú y si marcasemos false no se nos mostraría.
     */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        return true;
    }

    /**
     * Método para manejar las acciones seleccionadas del menú.
     * @param item The menu item that was selected.
     *
     * @return true para consumir la llamada.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.instrucciones) {
            mostrarInstrucciones();
            return true;
        } else if (item.getItemId() == R.id.reiniciar) {
            reiniciarJuego();
            return true;
        } else if (item.getItemId() == R.id.configJuego) {
            configurarJuego();
            return true;
        } else if (item.getItemId() == R.id.seleccionaPersonaje) {
            seleccionarPersonaje(item);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Método para seleccionar el personaje de las minas
     * @param item Elemento del menú relacionado con la seleccion del tipo de mina.
     */
    private void seleccionarPersonaje(MenuItem item) {
        String[] personajes = {"Mina clásica", "Mina bomber", "Dinamita", "Granada", "Mina submarina", "Coctel Molotov"};

        //Creamos un ArrayAdapter para mostrar los personajes en un Spinner.
        //Necesitamos pasar el tercer parametro, porque el arrayAdapter nececita un textView
        ArrayAdapter<String> a = new ArrayAdapter<String>(this, R.layout.itemspinner,R.id.textPersonaje, personajes) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
               //Personalizamos la vista de cada elemento del menú desplegable.
                View v = getLayoutInflater().inflate(R.layout.itemspinner, parent, false);
                ImageView img = v.findViewById(R.id.imagenPersonaje);
                TextView t = v.findViewById(R.id.textPersonaje);

                img.setImageResource(recursosPersonajes[position]); // Mostrar la imagen correspondiente
                t.setText(personajes[position]); // Mostrar el texto correspondiente
                return v;
            }

            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                //Vista para el elemento seleccionado.
                View v = getLayoutInflater().inflate(R.layout.itemspinner, parent, false);
                ImageView img = v.findViewById(R.id.imagenPersonaje);
                TextView t = v.findViewById(R.id.textPersonaje);

                img.setImageResource(recursosPersonajes[position]);
                t.setText(personajes[position]);
                return v;
            }
        };

        //Creamos un Spinner con el adaptador.
        Spinner s = new Spinner(this);
        s.setAdapter(a);

        s.setSelection(personajeSeleccionadoIndice);
        //Mostramos un diálogo para seleccionar el personaje.
        new AlertDialog.Builder(this)
                .setTitle("Selecciona tu personaje")
                .setView(s)
                .setPositiveButton("Aceptar", (dialog, which) -> {
                    int posicionSelecionada = s.getSelectedItemPosition(); // Guardar el índice seleccionado
                    personajeSeleccionadoIndice=posicionSelecionada;
                    tablero.recursoMina=recursosPersonajes[personajeSeleccionadoIndice];
                    tablero.recursoMinaTachado=recursosPersonajesTachados[personajeSeleccionadoIndice];
                    tablero.actualizarImagenes(gridLayout); //Actualizamos las imagenes en el tablero.
                    item.setIcon(recursosPersonajes[personajeSeleccionadoIndice]); //Cambiamos el icono de menú
                    Toast.makeText(this, "Seleccionaste: " + personajes[personajeSeleccionadoIndice], Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     *
     * @return el recurso gráfico del personaje seleccionado por el usuario.
     */
    public int getRecursoPersonaje() {
        return recursosPersonajes[personajeSeleccionadoIndice];
    }

    /**
     *
     * @return el recurso gráfico del personaje tachado seleccionado.
     */
    public int getRecursoPersonajeTachado() {
        return recursosPersonajesTachados[personajeSeleccionadoIndice];
    }


    /**
     * Método para mostrar un mensaje al finalizar el juego.
     * @param mensaje que indica el resultado del juego (ganado o perdido)
     * @param audioId ID del recurso de audio a reproducir.
     */
    public void mostrarMensajeFinal(String mensaje, int audioId) {
        //Evitamos mostrar mas de un dialogo a la vez.
        if(dialogoActivo!=null && dialogoActivo.isShowing()){
            return;
        }
        //Reproducimos un audio ( diferente según el usuario gane o pierda)
        MediaPlayer audio=MediaPlayer.create(this, audioId);
        audio.start();
        //Creamos un dialogo de alerta para mostrar el mensaje
        AlertDialog.Builder b=new AlertDialog.Builder(this)
                .setTitle("Fin del Juego")
                .setMessage(mensaje)
                .setPositiveButton("Reiniciar", (dialog, which) -> {

                    reiniciarJuego(); // Llamar al método reiniciarJuego
                    dialogoActivo=null; //Limpiamos la referencia al dialogo.

                })
                .setNegativeButton("Salir", (dialog, which) -> {

                    this.finish(); // Cerrar la actividad
                    dialogoActivo=null;
                })
                .setCancelable(false); //Evitamos que se cierre tocando fuera del dialogo
        //Mostramos el dialogo y guardamos la referencia.
        dialogoActivo = b.create();
        dialogoActivo.show();
    }

    /**
     * Método para configurar el nivel de dificultad del juego
     */
    private void configurarJuego() {
        String[] niveles = {"Principiante", "Amateur", "Avanzado"};
        new AlertDialog.Builder(this)
                .setTitle("Selecciona nivel de dificultad")
                //dificultad es la variable en donde se almacena el nivel de dificultad (por defecto es 0)
                .setSingleChoiceItems(niveles, dificultad, (dialog, which) -> {
                    switch (which) {
                        case 0: // Principiante
                            filas = 8;
                            columnas = 8;
                            minas = 10;
                            dificultad=0;
                            break;
                        case 1: // Amateur
                            filas = 12;
                            columnas = 12;
                            minas = 30;
                            dificultad=1;
                            break;
                        case 2: // Avanzado
                            filas = 16;
                            columnas = 16;
                            minas = 60;
                            dificultad=2;
                            break;
                    }
                    reiniciarJuego(); //Reiniciamos el juego con la nueva configuración
                    dialog.dismiss(); //Cerramos el diálogo.
                })
                .show();
    }


    /**
     * Método para reiniciar el juego desde cero
     */
    public void reiniciarJuego() {
        //Limpiamos todas las vistas del tablero
        gridLayout.removeAllViews();
        //Creamos un nuevo tablero con las configuraciones actuales.
        tablero = new Tablero(this, filas, columnas, minas);

        gridLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tablero.inicializarMatriz();
                tablero.inicializarLayout(gridLayout);
                // tablero.mostrarMatrizConsola();
                gridLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        Toast.makeText(this, "Juego reiniciado.", Toast.LENGTH_SHORT).show();
    }


    /**
     * Método para mostrar las instrucciones del juego al usuario.
     */
    private void mostrarInstrucciones() {
        //Creamos un diálogo con las instrucciones del juego.
        new AlertDialog.Builder(this)
                .setTitle("Instrucciones")
                .setMessage("Cuando pulsas en una casilla, sale un número que identifica cuántas minas hay alrededor. Ten cuidado porque si pulsas en una casilla que tenga una mina escondida, perderás.\n" +
                        "Si crees o tienes la certeza de que hay una mina, haz un clic largo sobre la casilla para señalarla.\n" +
                        "No hagas un clic largo en una casilla donde no hay una mina porque perderás.\n" +
                        "Ganas una vez hayas encontrado todas las minas.")
                .setPositiveButton("Entendido", null) //Botón para cerrar el diálogo.
                .show();
    }
}

