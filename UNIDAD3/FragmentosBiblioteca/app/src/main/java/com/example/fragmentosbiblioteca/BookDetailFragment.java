package com.example.fragmentosbiblioteca;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookDetailFragment extends Fragment {
//este metodo se llama cuando se está creando la vista del fragmento.
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // inflater se usa para rellenar nuestro diseño (xml) y convertirlo en algo que el usuario pueda ver
        View vista = inflater.inflate(R.layout.fragment_book_detail, container, false);

        // Aqui buscamos si el fragmento recibio informacion sobre algun libro.
        //con getarguments guardamos info para pasar entre pantallas.
        Bundle argumentos = getArguments();

        Book libro = (Book) argumentos.getSerializable("libro");

        // Configurar vistas para mostrar los detalles
        TextView titulo = vista.findViewById(R.id.detalle_titulo);
        TextView autor = vista.findViewById(R.id.detalle_autor);
        TextView anio = vista.findViewById(R.id.detalle_anio);
        TextView descripcion = vista.findViewById(R.id.detalle_descripcion);
        //llenamos cada parte con la info del libro
        titulo.setText(libro.getTitulo());
        autor.setText(libro.getAutor());
        anio.setText(String.valueOf(libro.getAño()));
        descripcion.setText(libro.getDescripcion());

        // Configurar el botón de acción que nos permita volver a la lista de libros
        //Este boton dice "Volver" y cuando se pulsa, volvemos a la pantalla anterior.
        ((MainActivity) requireActivity()).configurarBoton("Volver", () -> {
            //Aqui le indicamos que borre esta pantalla y vuelva a la anterior.
            requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();
        });
        //Devolvemos la vista lista para que el usuario la vea.
        return vista;
    }
}
