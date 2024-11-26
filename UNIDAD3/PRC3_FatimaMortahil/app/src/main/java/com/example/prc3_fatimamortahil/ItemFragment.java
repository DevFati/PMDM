package com.example.prc3_fatimamortahil;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.prc3_fatimamortahil.bikes.BikesContent;
import com.example.prc3_fatimamortahil.placeholder.PlaceholderContent;

/**
 * A fragment representing a list of Items.
 */
public class ItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    //Este numero que usamos sirve para decidir como se vería la lista (una o varias columnas)
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    //Por defecto habrá un columna
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    //
    public static ItemFragment newInstance(int columnCount) {
        //Se crea el nuevo fragmento
        ItemFragment fragment = new ItemFragment();
        //Preparamos el bundle para enviar el numero de columnas
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        //le damos el bundle al fragmento
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Aqui se verifica si el fragmento ya tiene argumentos
        if (getArguments() != null) {
            //Si hay argumentos, guardamos el número de columnas que queremos.
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       //Inflamos el diseño del fragmento
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            //Conseguimos el contexto
            Context context = view.getContext();
            //Obtenemos el recyclerview que es la lista donde mostraremos los elementos.
            RecyclerView recyclerView = (RecyclerView) view;
            //Si solo queremos una columna, lo configuramos para que se vea vertical
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                //Si se quieren varias columnas, se configura la lista como una cuadricula gracias a "GridLayout"
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            //Aqui es donde se conecta nuestra lista de "BikesContent.items" con el recyclerview.
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(BikesContent.ITEMS));
        }
        return view;
    }
}