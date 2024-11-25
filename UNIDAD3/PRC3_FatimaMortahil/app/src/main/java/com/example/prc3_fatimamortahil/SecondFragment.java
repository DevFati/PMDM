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
    private RecyclerView listaFiltrada;
    private Fragment f;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentSecondBinding.inflate(inflater, container, false);

        if(BikesContent.ITEMS.size()==0){
            Toast.makeText(this.getContext(), "No hay bicis disponibles", Toast.LENGTH_SHORT).show();

        }




        spinner();
        return binding.getRoot();

    }



    private void spinner() {
        List<String> ciudades = new ArrayList<>();
        ciudades.add("All"); //Siempre me mostrara todas por defecto
        for (BikesContent.Bike bike : BikesContent.ITEMS) {
            if (!ciudades.contains(bike.getCity())) {
                ciudades.add(bike.getCity());
            }
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ciudades);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCity.setAdapter(cityAdapter);

        binding.spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String ciudadSeleccionada = parent.getItemAtPosition(position).toString();
                f=binding.fragmentContainerView.getFragment();
                listaFiltrada=f.getView().findViewById(R.id.list);
                if(ciudadSeleccionada.equals("All")){

                    listaFiltrada.setAdapter(new MyItemRecyclerViewAdapter(BikesContent.ITEMS));
                }else{
                    filtrado(ciudadSeleccionada);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    private void filtrado(String c){
        List<BikesContent.Bike> filteredList = new ArrayList<>();
        for (BikesContent.Bike bike : BikesContent.ITEMS) {
            if (bike.getCity().equals(c)) {
                filteredList.add(bike);
            }
        }

            listaFiltrada.setAdapter(new MyItemRecyclerViewAdapter(filteredList));
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonSecond.setOnClickListener(v ->
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment)
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}