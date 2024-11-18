package com.example.buscaminas;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private int[][] tablero; //Aqui inicializamos la matriz del tablero
    private boolean[][] descubierta; // Estado de casillas descubiertas
    private boolean[][] marcada; // Estado de casillas marcadas
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

        gridLayout=findViewById(R.id.gridLayout);
        iniciarJuego();


    }
    public void iniciarJuego(){
        //iniciamos el juego

        int filas=8;
        int columnas=8;
        int minas=10;

        tablero=new int[filas][columnas];
        descubierta = new boolean[filas][columnas];
        marcada = new boolean[filas][columnas];
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(columnas);
        gridLayout.setRowCount(filas);
        colocarMinas(minas,filas,columnas);
        dibujarTablero(filas,columnas);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.instrucciones) {
            mostrarInstrucciones();
            return true;
        } else if (id == R.id.reiniciar) {
            iniciarJuego();
            return true;
        } else if (id == R.id.configJuego) {
            mostrarDificultad();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void mostrarDificultad() {
    }

    private void mostrarInstrucciones() {
        new AlertDialog.Builder(this)
                .setTitle("Instrucciones")
                .setMessage("El objetivo es despejar el campo de minas sin detonarlas")
                .setPositiveButton("OK",null)
                .show();
    }

    private void dibujarTablero(int filas, int columnas) {
        for(int i=0;i<filas;i++){
            for(int j=0;j<columnas;j++){
                Button celda=new Button(this);
                int finalI = i;
                int finalJ = j;
                celda.setOnClickListener(v -> mostrarCasilla(finalI, finalJ));
                celda.setOnLongClickListener(v -> marcarCasilla(finalI,finalJ));
                GridLayout.LayoutParams gp= new GridLayout.LayoutParams();
                gp.rowSpec=GridLayout.spec(i);
                gp.columnSpec=GridLayout.spec(j);

                celda.setLayoutParams(gp);
                gridLayout.addView(celda);

            }
        }
    }

    private boolean marcarCasilla(int fila, int columna) {
        Button celda= (Button) gridLayout.getChildAt(fila*gridLayout.getColumnCount()+columna);
        if(!descubierta[fila][columna]){
            if(marcada[fila][columna]){
                celda.setText("");
                marcada[fila][columna]=false;
            }else{
                celda.setText("\uD83D\uDEA9");
                marcada[fila][columna]=true;

            }
        }
        return true;
    }

    private void mostrarCasilla(int fila, int columna) {
        if(tablero[fila][columna]==-1){
            //pierde
            mostrarMina(fila,columna);
            partidaTerminada(false);
        }else{
            mostrarCelda(fila,columna);
            if(gano()){
                partidaTerminada(true);
            }
        }
    }

    private void partidaTerminada(boolean gano) {
        String mensaje;
        if(gano){
            mensaje="¡Felicidades, ganaste!";
        }else{
            mensaje="¡Perdiste!";
        }
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show();
        //Cuanto el juego termina desabilito todas las celdas
        deshabilitarCeldas();
    }

    private boolean gano() {
        for(int i=0;i<gridLayout.getRowCount();i++){
            for(int j=0;j<gridLayout.getColumnCount();j++){
                if((tablero[i][j]==-1 && !marcada[i][j]) || (tablero[i][j]!=-1 && !descubierta[i][j])){
                    return false;
                }
            }
        }
        return true;
    }

    private void mostrarCelda(int fila, int columna) {
        if(descubierta[fila][columna]){
            return;
        }
        descubierta[fila][columna]=true;

        Button celda= (Button) gridLayout.getChildAt(fila*gridLayout.getColumnCount()+columna);
        if(tablero[fila][columna]>0){
            celda.setText(String.valueOf(tablero[fila][columna]));
        }else{
            celda.setText("");
            descubrirCeldasAlrededor(fila,columna);
        }
        celda.setEnabled(false);
    }

    private void descubrirCeldasAlrededor(int fila, int columna) {
        for(int i=-1;i<=1;i++){
            for(int j=-1;j<=1;j++){
                int nuevaFila=fila+i;
                int nuevaCol=columna+j;
                if(celdaValida(nuevaFila,nuevaCol)&& tablero[nuevaFila][nuevaCol]!=-1){
                    mostrarCelda(nuevaFila,nuevaCol);
                }
            }
        }
    }

    private boolean celdaValida(int fil, int col) {
        return fil>=0 && fil<gridLayout.getRowCount() && col>=0 && col<gridLayout.getColumnCount();
    }

    private void colocarMinas(int minas, int filas, int columnas) {

            int f=0;
            int c=0;
            int cont=0;
            do{
                f=(int)(Math.random()*filas);
                c=(int)(Math.random()*columnas);
                if(tablero[f][c]!=-1){
                    tablero[f][c]=-1;
                    cont++;
                }
            }while(cont<minas);
    }

    private void deshabilitarCeldas() {
        for (int i = 0; i < gridLayout.getRowCount(); i++) {
            for (int j = 0; j < gridLayout.getColumnCount(); j++) {
                Button celda = (Button) gridLayout.getChildAt(i * gridLayout.getColumnCount() + j);
                celda.setEnabled(false); // Deshabilita cada celda
            }
        }
    }

    private void mostrarMina(int fila, int columna) {
        Button celda = (Button) gridLayout.getChildAt(fila * gridLayout.getColumnCount() + columna);
        celda.setText("\uD83D\uDCA3"); // Muestra una mina
        celda.setEnabled(false);
    }
}