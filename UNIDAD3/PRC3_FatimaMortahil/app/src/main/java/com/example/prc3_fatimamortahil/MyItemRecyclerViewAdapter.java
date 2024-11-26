package com.example.prc3_fatimamortahil;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.prc3_fatimamortahil.bikes.BikesContent;
import com.example.prc3_fatimamortahil.placeholder.PlaceholderContent.PlaceholderItem;
import com.example.prc3_fatimamortahil.databinding.FragmentItemBinding;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
// Esta es una clase que extiende de recyclerview.adapter
    //Esto significa que es un adaptador para un recyclerview
public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final List<BikesContent.Bike> bikes; //bike es una lista que contiene los datos de las biciletas.
    //Constructor del adaptador
    public MyItemRecyclerViewAdapter(List<BikesContent.Bike> items) {
        bikes = items;
    }

    //Este metodo se llama cuando el recyclerview necesita un nuevo
    //viewholder para mostrar un elemento.
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //usa layoutInflater para inflar el diseño de un elemento de la lista (fragment_item.xml), y crea un nuevo viewholder
        //para almacenar las vistas del diseño inflado.
        //Como vemos, lo devuelve para que el recyclerview pueda usarlo
        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    //Este metodo se llama cuando el recyclerview quiere enlazar datos con un elemento
    //en una posicion especifica
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //se obtiene el objeto bike a la posicion actual (a traves de position) de la lista
        //y se asigna al viewholdet
        holder.bike = bikes.get(position);
        //Se actualizan las vistas del viewholder con la info de la bicicleta
        holder.nombre.setText(holder.bike.getOwner());
        holder.descripcion.setText(holder.bike.getDescription());
        holder.localizacion.setText(holder.bike.getLocation());
        holder.imagenFoto.setImageBitmap(holder.bike.getPhoto());
        holder.ciudad.setText(holder.bike.getCity());
        holder.email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aqui configuramos cuando se toque el botón del email
                //Se guardan todos los datos necesarios para enviar el correo.
                String email=holder.bike.getEmail();
                String asunto="Request to use your bicycle";
                String mensaje="Dear Mr/Mrs "+holder.bike.getOwner()+":\n" +
                        "I'd like to use your bike at "+holder.bike.getLocation()+"("+holder.bike.getCity()+")\n" +
                        "for the following date: "+BikesContent.selectedDate+"\n" +
                        "Can you confirm its availability?\n" +
                        "Kindest regards";
                //construimos URI manualmente (se ha intentado automaticamente pero no funciona)
                //Uri.encode se asegura de que los caracteres especiales no rompan el enlace
                String u="mailto:"+email+"?subject="+ Uri.encode(asunto)+"&body="+Uri.encode(mensaje);
                Uri ur=Uri.parse(u);


                // Crear un intent implícito para abrir un mail
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(ur); // se usa mailto para que solo nos muestre apps de mensajeria

                //Obtengo el contexto directamente desde la vista del item,
                //ya que el método startActivity necesita un context, pero en este adaptador no tiene acceso directo a él.
                v.getContext().startActivity(Intent.createChooser(intent,"Enviar email..."));

            }
        });
    }

    @Override
    //Devuelve el numero de elementos de la lista. Para que el recyclerview sepa
    //cuantas vistas mostrar.
    public int getItemCount() {
        return bikes.size();
    }
    //Esta clase interna contiene todas las vistas de un elemento de la lista
    public class ViewHolder extends RecyclerView.ViewHolder {
    //Declaramos las vistas que tiene cada elemento de la lista:
        public BikesContent.Bike bike;
        public ImageView imagenFoto;
        public TextView nombre,descripcion,localizacion,ciudad;
        public ImageButton email;
        //En el constructor enlazamos las variables declaradas con las vistas del diseño
        //usando "binding"
        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            imagenFoto=binding.imageViewBike;
            nombre=binding.textViewOwner;
            descripcion=binding.textViewDescription;
            localizacion=binding.textViewLocation;
            email=binding.imageButtonEmail;
            ciudad=binding.textViewCity;
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}