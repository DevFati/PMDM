package com.example.prc2_fatimamortahil;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class Tablero {
    private Casilla[][] casillas; //Matriz que representa el tablero 
    private int x; //Número de filas
    private int y; //Número de columnas
    private int totalMinas; //Total de minas en el tablero
    private Context contexto; // Contexto necesario para poder interactuar con la interfaz de usuario
    public int recursoMina; //Aqui pasaremos el índice de la imagen de la mina
    public int recursoMinaTachado; // Almacenamos índice de la imagen de la mina tachada correspondiente


    public Tablero(Context contexto,int y, int x, int totalMinas) {
        this.y = y;
        this.x = x;
        this.totalMinas = totalMinas;
        casillas=new Casilla[x][y]; //Inicializamos la matriz de casillas vacías
        this.contexto=contexto; //Contexto para acceder a recursos y vistas

        // Obtenemos los recursos del tipo de mina seleccionado desde la actividad principal
        this.recursoMina=((MainActivity) contexto).getRecursoPersonaje();
        this.recursoMinaTachado=((MainActivity) contexto).getRecursoPersonajeTachado();
    }




    /**
     * Método que se usó para comprobar por consola los valores que se iban generando en mi matriz.
     */
    public void mostrarMatrizConsola(){
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                System.out.printf("%4s", casillas[i][j].getValor());
            }
            System.out.println();
        }
    }



    /**
     *  Método para actualizar las imágenes de las minas en el tablero
     *  cuando por ejemplo el usuario en mitad de una partida elija un personaje nuevo.
     * @param g recibe el gridlayout porque estos métodos necesitan realizar operaciones directamente en la interfaz gráfica del juego.
     */
    public void actualizarImagenes(GridLayout g){
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                //Cuando es una mina y esta marcada, se cambia el personaje. Solo busco minas ya que son las que disponen de
                //un fondo.
                if(casillas[i][j].esMina() && casillas[i][j].getEstado()==2){
                    View v = obtenerVista(g, i, j);
                    //Aplicamos el fondo al imageButton.
                    ((ImageButton) v).setImageResource(recursoMina);
                }
            }

        }
    }




    /**
     * Inicializar la matriz de casillas y colocamos las minas.
     */
    public void inicializarMatriz(){
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                //Inicializamos cada casilla.
                casillas[i][j] = new Casilla();
            }
        }

        // Colocamos minas aleatoriamente en el tablero.
        establecerMinas();
    }

    /**
     *  Método para restaurar el estado del tablero al rotar la pantalla
     * @param g recibe el gridlayout porque estos métodos necesitan realizar operaciones directamente en la interfaz gráfica del juego.
     */
    public void recuperarTablero(GridLayout g){
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                View celda = obtenerVista(g, i, j); //Obtenemos la celda específica del gridlayout

                //Si la casilla está descubierta, muestra su valor.
                if(casillas[i][j].getEstado()==1){
                    //No hace falta comprobar con el instance of si se trata de un Button o ImageButton
                    //ya que si esta descubierta solo puede ser un Button ( de ser un ImageButton el usuario perdería).
                    //concatenamos con una cadena vacía al devolver un valor "int" para que se agrege correctamente el valor de "text" del botón
                    ((Button) celda).setText(casillas[i][j].getValor()+"");
                    //Si fue el usuario quien descubrio la casilla ( es decir el estado es 1) y la casilla es un "0" entonces la resaltamos con
                    //un fondo rojo (para que el tablero quede justamente como antes de rotarlo)
                    if(casillas[i][j].getValor()==0){
                        ((Button) celda).setBackgroundColor(Color.RED);
                    }

                    //Si la casilla contiene una mina entra aquí
                }else if(casillas[i][j].esMina()){
                    //Si el estado de la mina es 2, es decir que fue marcada por el usuario ó si el usuario perdio y
                    //fue descubierta al perder le asignamos la imagen de fondo correspondiente a su respectivo
                    //ImageButton
                    if(casillas[i][j].getEstado()==2 || casillas[i][j].getEstado()==3){
                        ((ImageButton) celda).setImageResource(recursoMina);
                    }

                    //Si la mina tiene un estado 4, es decir que fue pulsada (lo que lleva al usuario a que pierda la partida)
                    //le asignamos su respectiva imagen tachada y girada.
                    if(casillas[i][j].getEstado()==4){
                        ((ImageButton) celda).setImageResource(recursoMinaTachado);
                    }

                    //Si la casilla esta marcada como descubierta al perder ( al perder se muestran todas las casillas)
                }else if(casillas[i][j].getEstado()==3){
                    //Solo pueden ser buttons porque las minas si se marcan, el usuario perderia y el valor de esa mina
                    //específica seria 4.
                    //Asignamos el valor a las diferentes celdas descubiertas, aquí las celdas "0" mostradas después de que el usuario
                    //pierda tendran un fondo blanco. (Asi las diferenciamos de las que fueron descubiertas por el usuario).
                    ((Button) celda).setText(casillas[i][j].getValor()+"");

                }
            }
        }
    }


    /**
     * Método que coloca las minas aleatoriamente y calcula los valores de las casillas cercanas.
     */
    public void establecerMinas() {
        int colocadas = 0; // Minas colocadas

        while (colocadas < totalMinas) {
            int fila = (int) (Math.random() * x); // Fila aleatoria
            int columna = (int) (Math.random() * y); // Columna aleatoria

            // Si la casilla no tiene una mina, la colocamos
            if (!casillas[fila][columna].esMina()) {
                casillas[fila][columna].setMina(true);
                colocadas++; //Y vamos incrementando el valor de colocadas hasta que se coloquen aleatoriamente todas las minas.
            }
        }

        //Calculamos el número de minas cercanas para cada casilla.
        for(int i=0;i<x;i++){
            for(int j=0;j<y;j++){
                //Si la casilla no es una mina, le asignamos el valor de las minas que tiene a su alrededor.
                if(!casillas[i][j].esMina()){
                    calcularMinasCerca(i,j);
                }
            }
        }

    }


    /**
     * Método para calcular el número de minas cercanas para una casilla específica.
     *
     * @param fila fila donde se encuentra
     * @param columna columna donde se encuentra
     */
    public void calcularMinasCerca(int fila, int columna) {
        int minasCerca = 0; //Contador de minas cercanas
        //Este bucle anidado recorre todas las posiciones alrededor de la casilla [fila,columna]
        // (inclusive).
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nuevaFila = fila + i;
                int nuevaColumna = columna + j;
                //verificamos si la nueva posicion esta dentro del limite del tablero
                //y comprobamos si la casilla en la nueva posicion tiene una mina y si es asi
                //se suma 1.
                if (esValida(nuevaFila, nuevaColumna) && casillas[nuevaFila][nuevaColumna].esMina()) {
                    minasCerca++;
                }
            }
        }
        //una vez que termina el bucle anidado y se calculan las minas cercanas, se
        //almacena el valor en el atributo "minasCerca" de la casilla (fila, columna)
        casillas[fila][columna].setValor(minasCerca);

    }

    /**
     * Método para inicializar el diseño de tablero en el GridLayout.
     * @param g   recibe el gridlayout porque estos métodos necesitan realizar operaciones directamente en la interfaz gráfica del juego.
     */
    public void inicializarLayout(GridLayout g) {
        g.removeAllViews(); // Limpiamos el contenido anterior que tuviese el GridLayout
        g.setRowCount(x); //Establecemos el número de filas.
        g.setColumnCount(y); //Establecemos el número de columnas.

        int gridWidth = g.getWidth(); //Ancho del gridLayout
        int gridHeight = g.getHeight(); //Alto del gridLayout
        int cellWidth = gridWidth / y; //Ancho de cada celda
        int cellHeight = gridHeight / x; //Alto de cada celda


        //Iteramos sobre las filas
        for (int i = 0; i < x; i++) {
            //Iteramos sobre las columnas
            for (int j = 0; j < y; j++) {
                View cell; //Vista para representar la celda

                if (casillas[i][j].esMina()) {
                    // Si es mina, usar ImageButton
                    ImageButton imgButton = new ImageButton(contexto);
                    //Le damos fondo
                    imgButton.setBackgroundResource(R.drawable.borde_celda);
                    cell = imgButton; //Asignamos la celda
                } else {
                    // Si no es mina, usamos un Button
                    Button b = new Button(contexto);
                    //Le damos fondo
                    b.setBackgroundResource(R.drawable.borde_celda);
                    b.setText(""); //Principalmente el texto es vacío
                    //Lo ponemos a 0 para que se pueda aprovechar todo el espacio
                    //para que los números se vean correctamente en todo la celda
                    b.setPadding(0,0,0,0);
                    //Asignamos la celda
                    cell = b;
                }

                // Configurar LayoutParams (los parametros de diseño de la celda)
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = cellWidth;
                params.height = cellHeight;
                params.rowSpec = GridLayout.spec(i);
                params.columnSpec = GridLayout.spec(j);
                cell.setLayoutParams(params);

                //Añadimos los listeners de clic
                final int fila = i;
                final int columna = j;
                // A la celda le ponemos un onClickListener que se llama cada vez que el usuario
                //clickea una celda. Le pasamos el gridlayout, la fila, columna, cell que es la celda
                //y false que se refiere a que se llamo el metodo click por el usuario ( no mediante el metodo
                //recursivo)
                cell.setOnClickListener(v -> clickar(g, fila, columna, cell,false));
                //Clic corto para marcar las casillas.
                cell.setOnLongClickListener(v -> {
                    marcar(g, fila, columna, cell);
                    return true; //procesado correctamente
                });

                // Añadir la celda al GridLayout
                g.addView(cell);
            }
        }
    }


    /**
     *Método que se lanza cuando el usuario realiza un clic largo
     * @param g recibe el gridlayout porque estos métodos necesitan realizar operaciones directamente en la interfaz gráfica del juego.
     * @param fila fila donde se encuentra
     * @param columna columna donde se encuentra
     * @param v vista a marcar
     */
    private void marcar(GridLayout g, int fila, int columna, View v) {
        //Si la casilla ya está descubierta, mostramos un mensaje y no permitimos marcarla
        //porque ya esta descubierta.
        if (casillas[fila][columna].getEstado() == 1) {
            Toast.makeText(contexto, "La casilla ya está descubierta.", Toast.LENGTH_SHORT).show();
            return; //Salimos del método.
        }

        //Si la casilla está en su estado inicial (no marcada y no descubierta)
        if (casillas[fila][columna].getEstado() == 0) {

        //verificamos que no sea una mina.
            if (!casillas[fila][columna].esMina()) {
                revelarTodo(g); // Revelar el tablero completo

                //Mostramos un mensaje de que la partida ha finalizado con la derrota del usuario y le pasamos el audio de derrota

                ((MainActivity) contexto).mostrarMensajeFinal("¡Perdiste! Marcaste una casilla que no es una mina.",R.raw.perder);
                return; // Terminar el método
            }
            //Marcamos casilla como "posible mina" (realmente es una mina porque de no ser asi, el usuario pierde)
            casillas[fila][columna].setEstado(2);
            // Aplicar la imagen de fondo de la casilla segun lo que eliga el usuario.

                ((ImageButton) v).setImageResource(recursoMina);

        } else if (casillas[fila][columna].getEstado() == 2) {
            // Si está marcada, desmarcarla
            casillas[fila][columna].setEstado(0);

                ((ImageButton) v).setImageResource(0); // Quitar imagen

        }

        //Si tras marcar o desmarcar se cumplen las condiciones de victoria
        if (verificarVictoria()) {
            revelarTodo(g); //Revelamos todo el tablero
            //Mostramos mensaje de vistoria y le pasamos el audio de victoria.
            ((MainActivity) contexto).mostrarMensajeFinal("¡Ganaste! Todas las minas fueron correctamente marcadas.",R.raw.ganar);
        }
    }


    /**
     * Revela todas las casillas que estaban ocultas.
     * @param g recibe el gridlayout porque estos métodos necesitan realizar operaciones directamente en la interfaz gráfica del juego.
     */
    private void revelarTodo(GridLayout g) {
        //Iteramos sobre todas las casillas del tablero
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                View view = obtenerVista(g, i, j); //Obtenemos la vista correspondiente
                if (casillas[i][j].esMina()) {
                    //Mostramos la imagen de la mina
                    ((ImageButton) view).setImageResource(recursoMina);
                    //Si la mina tiene estado 4 significa que fue la que el usuario detono y le llevo
                    // a la perdida de la partida, asi que a esa casilla le damos su imagen de fondo correspondiente.
                    if(casillas[i][j].getEstado()==4){
                        ((ImageButton) view).setImageResource(recursoMinaTachado);
                    }else{
                        if(casillas[i][j].getEstado()!=2 ){
                            casillas[i][j].setEstado(3); //Marcamos como descubierta al perder (que es lo que significa el estado3)
                        }
                    }
                //Si no es una mina(imagebutton), es un boton (button)
                } else  {
                    Button b = (Button) view;
                    int minasCercanas = casillas[i][j].getValor();
                    b.setText(minasCercanas+"");
                    if(!(casillas[i][j].getEstado()==1)){
                        casillas[i][j].setEstado(3); //Marcamos las casillas que no han sido
                        //descubiertas por el usuario con estado 3 (descubiertas al perder).
                    }
                }

            }

        }
    }


    /**
     * Verificamos si la posición esta dentro de los límites del tablero.
     * @param fila fila de la posición a comprobar
     * @param columna columan de la posición a comprobar
     * @return true si la posición existe en nuestro tablero.
     */
    private boolean esValida(int fila, int columna) {
        if(fila >= 0 && fila < x && columna >= 0 && columna < y){
            return true;
        }else{
            return false;
        }
    }

    /**
     * Método llamado cada vez que el usuario realiza un clic corto en el tablero.
     * @param g  recibe el gridlayout porque estos métodos necesitan realizar operaciones directamente en la interfaz gráfica del juego.
     * @param fila fila de la casilla donde se hace click
     * @param columna columna de la casilla donde se hace click
     * @param v Vista con la que se interactuó
     * @param recursivo es false cuando la casilla fue clickeada por el usuario y true en el caso contrarió.
     */
    public void clickar(GridLayout g,int fila, int columna, View v, boolean recursivo ) {
        //Si no se ha llamado por recursividad (es decir que el usuario lo ha seleccionado directamente)
        //se comprueba si la casilla esta descubierta porque de ser así lanza un toast de advertencia
        if(!recursivo){
            if (casillas[fila][columna].getEstado() == 1 ) {
                Toast.makeText(contexto, "La casilla ya está descubierta.", Toast.LENGTH_SHORT).show();
                return; //return para que no siga
            }
        }
        //Si la casilla es una mina.
        if (casillas[fila][columna].esMina()) {
            casillas[fila][columna].setEstado(4); //Marcamos como mina detonada
            revelarTodo(g); // Revelar todo el tablero
            //Establecemos la imagen de fondo de la mina que pulso el usuario.
            ((ImageButton) v).setImageResource(recursoMinaTachado);
            //Mostramos mensaje de derrota y le pasamos audio como que ha perdido.
            ((MainActivity) contexto).mostrarMensajeFinal("¡Perdiste! Has detonado una mina.",R.raw.perder);

            return; // Terminar el método porque se perdió

        } else {
            //Si no es una mina la marcamos como descubierta
            casillas[fila][columna].setEstado(1); // Marcar como descubierta
                Button b=(Button) v;
                if (casillas[fila][columna].getValor() == 0) {
                    //Si la casilla es 0, mostramos su valor y le damos un fondo rojo.
                    b.setText(casillas[fila][columna].getValor()+"");
                    b.setBackgroundColor(Color.RED);
                    // Descubrimos recursivamente las casillas vecinas
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            if(!(i==0 && j==0)  ){ //Evitamos descubrir la misma casilla
                                if(esValida(fila+i,j+columna)){ //Verificamos si la posición es válida y si la casilla no fue descubierta por el usuario
                                    if(casillas[fila+i][j+columna].getEstado()!=1){
                                        clickar(g,fila + i, columna + j, obtenerVista(g,fila + i, columna + j),true);

                                    }
                                }
                            }
                        }
                    }
                } else {
                    b.setText(casillas[fila][columna].getValor()+"");

                }
        }
    }

    /**
     * Obtenemos la vista
     * @param g     recibe el gridlayout porque estos métodos necesitan realizar operaciones directamente en la interfaz gráfica del juego.
     * @param fila fila de la casilla
     * @param columna columna de la casilla
     * @return devuelve la vista.
     */
    private View obtenerVista(GridLayout g,int fila, int columna) {
        if (!esValida(fila, columna)) {
            return null; // Casilla inválida
        }
        int indice = fila * this.y + columna; //Calculamos el indice de la vista
        return g.getChildAt(indice);//Devolvemos la vista
    }

    /**
     * Verificamos si el usuario ganó
     * @return true si el usuario marcó todas las minas.
     */
    public boolean verificarVictoria() {
        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                //Si la casilla es una mina y no tiene el estado de marcada (2)
                if ((casillas[i][j].esMina() && casillas[i][j].getEstado()!=2)) {
                    return false; // Aún no se cumple la condición de victoria
                }
            }
        }

        return true; // Todas las condiciones se cumplen, el usuario gana.
    }

}
