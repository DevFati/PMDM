package com.example.buscaminas;

import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private GridLayout gridLayout;
    private int[][] tablero; //Aqui inicializamos la matriz del tablero
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

        //iniciamos el juego

        int filas=8;
        int columnas=8;
        int minas=10;

        tablero=new int[filas][columnas];
        colocarMinas(minas,filas,columnas);
        dibujarTablero(filas,columnas);
        gridLayout.setColumnCount(columnas);
        gridLayout.setRowCount(filas);

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

    private boolean marcarCasilla(int i, int j) {

        return true;
    }

    private void mostrarCasilla(int fila, int columna) {
        if(tablero[fila][columna]==-1){
            //pierde
            mostrarMina(fila,columna);
            partidaPerdida(false);
        }else{
            mostrarCelda(fila,columna);
            if(gano()){
                partidaPerdida(true);
            }
        }
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
}