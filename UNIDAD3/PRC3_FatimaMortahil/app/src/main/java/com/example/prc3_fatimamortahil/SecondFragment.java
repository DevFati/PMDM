package com.example.prc3_fatimamortahil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prc3_fatimamortahil.bikes.BikesContent;
import com.example.prc3_fatimamortahil.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.List;

public class SecondFragment extends Fragment {

    private FragmentSecondBinding binding;
    private RecyclerView listaFiltrada; //recyclerview donde mostraremos la lista de biciletas filtradas por ciudad.
    private Fragment f; //Variable que se usara para almacenar el fragmento hijo que esta dentro del FragmentContainerView

    @Override
    //este metodo se ejecuta cuando el fragmento esta siendo creado
    //aqui se infla el diseño y se configuran los elementos de la interfaz.
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        //Aqui se enlaza el diseño del fragmento con binding para
        //acceder a los elementos de la interfaz.
        binding = FragmentSecondBinding.inflate(inflater, container, false);

        //Vemos si nohay bicis disponibles en la lista.
        //Si esta vacía, le indicamos al usario que no hay bicis disponibles.
        if(BikesContent.ITEMS.size()==0){
            Toast.makeText(this.getContext(), "No hay bicis disponibles", Toast.LENGTH_SHORT).show();

        }

        //Llamamos a nuestro metodo spinner, que nos configura un spinner para
        //poder elegir por qué ciudad queremos filtrar las bicicletas.
        spinner();
        //Aqui se devuelve la vista raíz, para que se vea el fragmento por pantalla.
        return binding.getRoot();

    }



    private void spinner() {
        //En esta lista guardaremos las ciudades disponibles.
        List<String> ciudades = new ArrayList<>();
        ciudades.add("All"); //Siempre me mostrara todas por defecto, y lo añadimos como opción también
        //Recorremos todas las bicicletas de BikesContent.Items.
        for (BikesContent.Bike bike : BikesContent.ITEMS) {
            //Si la ciudad de una bicileta NO esta en la lista "ciudades"
            //la agregamos (esto evita duplicados)
            if (!ciudades.contains(bike.getCity())) {
                ciudades.add(bike.getCity());
            }
        }

        //Se crea el adaptador del spinner
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ciudades);
        //Se mostraran las ciudades en un menu desplegable
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Se asigna el adaptador al spinner.
        binding.spinnerCity.setAdapter(cityAdapter);
        //Configuramos "onItemSelectedListener" para escuchar cuando
        //el usuario da click a una ciudad de nuestro spinner
        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //Obtenemos la ciudad que selecciono
                String ciudadSeleccionada = parent.getItemAtPosition(position).toString();
                //Almacenamos el fragmento contenido en el "fragmentcontainerview" en la variable f
                f=binding.fragmentContainerView.getFragment();
                //Dentro de esa vista, buscamos el recyclerview (R.id.list) para actualizarlo con los
                //datos filtrados
                listaFiltrada=f.getView().findViewById(R.id.list);
                //Si el usuario selecciona "All" se muestran todas las bicis
                if(ciudadSeleccionada.equals("All")){

                    listaFiltrada.setAdapter(new MyItemRecyclerViewAdapter(BikesContent.ITEMS));
                }else{
                    //Si selecciona una ciudad específica, se llama al método "filtrado"
                    filtrado(ciudadSeleccionada);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    private void filtrado(String c){
        //Se crea una lista donde se almacenaran las bicicletas con
        //la ciudad seleccionada por el usuario (c)
        List<BikesContent.Bike> filteredList = new ArrayList<>();
        //Se recorren todas las biciletas y las que coincidan con dicha ciudad
        //se agregan
        for (BikesContent.Bike bike : BikesContent.ITEMS) {
            if (bike.getCity().equals(c)) {
                filteredList.add(bike);
            }
        }
            //Se actualiza el adaptador del recyclerview con la lista filtrada.
            listaFiltrada.setAdapter(new MyItemRecyclerViewAdapter(filteredList));
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Aqui está configurado el boton para que al ser pulsado
        //pueda navegar hacia el FirstFragment usando NavHostFragment
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //Se libera la referencia "binding" para evitar
        //fugas de memoria.
        binding = null;
    }

}