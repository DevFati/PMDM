package com.example.prc3_fatimamortahil;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.prc3_fatimamortahil.bikes.BikesContent;
import com.example.prc3_fatimamortahil.databinding.FragmentFirstBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class FirstFragment extends Fragment {
    //Almacenamos la fecha seleccionada
private String fecha="";
//Permite acceder directamente a las vistas de diseño
private FragmentFirstBinding binding;


    @Override
    public View onCreateView(
            //Aquí este método se ejecuta para inflar el diseño
            //del fragmento y configurarlo.
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        //accedemos a las vistas de fragment_first.xml
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        //Obtenemos la fecha actual en milisegundos
        long hoy=System.currentTimeMillis();

        //Desabilitamos las fechas anteriores a hoy (ya que no tendría
        //sentido)
        binding.calendarView.setMinDate(hoy);
        //Configuramos un lisener que detecta cuando el usuario selecciona una fecha en el calendarView
        binding.calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                //Almacena la fecha seleccionada en el formato dd/MM/yyyy (al mes se le suma una porque va de 0 a 11)
                fecha=dayOfMonth+"/"+(month+1)+"/"+year;
                //Se muestra la fecha seleccionada en un TextView
                binding.textViewFechaSeleccionada.setText("Date: "+fecha);
                //Sincronizamos la fecha con la fecha en BikesContent
                //esto se hace para que cuando el usuario navegue hacia otro fragmento o actividad
                //y luego regrese, que se pueda restaurar el valor elegido anteriormente
                BikesContent.selectedDate=fecha;

                BikeActivity.editor.putString("fecha",fecha);
                BikeActivity.editor.apply();
            }
        });

        if(!BikesContent.selectedDate.equals("")){
            binding.textViewFechaSeleccionada.setText("Date: "+BikesContent.selectedDate);
            //Convertiremos nuestra fecha string a long para que en el calendario
            //se quede clickada la fecha selecionada al darle a "volver"

            SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
            //Ahora convertimos nuestro String a Date
            try {
                Date date=sdf.parse(BikesContent.selectedDate);
                long m=date.getTime(); //Así sacamos los milisegundos en formato long
                binding.calendarView.setDate(m);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
        return binding.getRoot();
    }


//Este metodo se ejecuta después de que la vista esta lista
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Configuramos la accion a realizar cuando el usuario le de clic
        // al boton para ir al siguiente fragmento.
        binding.buttonNextFragment.setOnClickListener(v -> {
        //Si no se ha seleccionado ninguna fecha, mostramos un mensaje al usuario.
            if (BikesContent.selectedDate.isEmpty()) {
                Toast.makeText(this.getContext(), "Introduce una fecha antes de seguir", Toast.LENGTH_SHORT).show();
            } else {
                //Si ya se seleccionó una fecha, nos dirigimos al siguiente fragmento
                NavHostFragment.findNavController(FirstFragment.this)
                        .navigate(R.id.action_FirstFragment_to_SecondFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        //Este metodo se ejecuta cuando el fragmento ya no se usa.
        super.onDestroyView();
        //Se libera el binding para evitar problemas de memoria.
        binding = null;
    }
}