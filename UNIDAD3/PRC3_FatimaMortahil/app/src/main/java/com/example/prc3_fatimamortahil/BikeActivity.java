package com.example.prc3_fatimamortahil;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.prc3_fatimamortahil.bikes.BikesContent;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prc3_fatimamortahil.databinding.ActivityBikeBinding;

import java.util.ArrayList;
import java.util.List;

public class BikeActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration; //configura la barra de herramientas para la navegacion
    //usamos "binding" para acceder directamente a las vistas de la actividad sin necesidad de usar "findviewbyid" y todo lo que esto conlleva
    private ActivityBikeBinding binding;
    //Utilizamos sharedpreferencs para guardar y poder recuperar datos (cuando se cierre y vuelva a
    //abrir la app). Aqui almacenamos la fecha seleccionada por el usuario.
    public static SharedPreferences sharedPreferences;
    //Es necesario "editor" para poder escribir satps en sharedPreferences.
    public static SharedPreferences.Editor editor;

    @Override
    //Metodo principal que se ejecuta al crear la actividad
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inicializamos sharedPreferences con el nombre "Preferencias" y lo ponemos en modo privado para
        //que solo la aplicacion actual sea capaz de acceder a esos datos.
        sharedPreferences=getSharedPreferences("Preferencias", Context.MODE_PRIVATE);
        //Aquí recuperamos la ultima fecha que ha seleccionadoo el usuario antes de
        //apagar la app y si no hay se asignaría una cadena vacia.
        BikesContent.selectedDate=sharedPreferences.getString("fecha","");
        //Creamos una instancia de sharedPreferences.Editor.
        editor=sharedPreferences.edit();

        //Aquí inflamos el diseño de la actividad con ayuda del view binding
        binding = ActivityBikeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); //Establecemos el contenido de la actividad con las vistas infladas.

        setSupportActionBar(binding.toolbar);
        //Aqui se configura el navController para manejar la navegacion que se dará entre fragmentos.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_bike);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        //Aqui se sincroniza la barra de herramientas con la navegacion
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        //Si la lista de bicicletas esta vacia, llama al json para cargar los datos
        //hacemos esto para que en caso de rotación no me vuelva a cargar
        //la lista de nuevo.
        if(BikesContent.ITEMS.size()==0){
            BikesContent.loadBikesFromJSON(this);
        }
    }



    @Override
    //Se ejecuta cuando el usuario clickea el boton de ir atrás en la barra de herramientas.
    //Y maneja la navegacion entre fragmentos con NavController.
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_bike);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    //Se ejecuta cuando se cierra la actividad
    public void finish() {
        super.finish();

        //Animacion de desvanecimiento (fade_in al abrir) (fade_out al cerrar la actividad)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);


    }
}