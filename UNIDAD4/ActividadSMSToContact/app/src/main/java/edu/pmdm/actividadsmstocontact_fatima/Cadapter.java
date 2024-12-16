package edu.pmdm.actividadsmstocontact_fatima;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class Cadapter extends RecyclerView.Adapter<Cadapter.ContactViewHolder> {

    private final List<Contacto> contactos;
    private final OnContactLongClickListener longClickListener;

    // Constructor del adaptador
    public Cadapter(List<Contacto> contactos, OnContactLongClickListener longClickListener) {
        this.contactos = contactos;
        this.longClickListener = longClickListener;
    }



    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_contacto, parent, false);
        return new ContactViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contacto contacto = contactos.get(position);
        holder.nombre.setText(contacto.getNombre());
        holder.numero.setText(contacto.getNumero());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                if (longClickListener != null) {
                    //    System.out.println("entroD");

                //    longClickListener.onContactLongClick(contacto);
                    MainActivity.contactoSeleccionado = contacto; // Guardar el contacto seleccionado
                    MainActivity.mostrarContenedorMensaje(contacto);
                }
                return true; // Indica que el evento fue manejado
            }
        });

    }

    @Override
    public int getItemCount() {
        return contactos.size();
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {
        TextView nombre;
        TextView numero;

        public ContactViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = itemView.findViewById(R.id.tvNombre);
            numero = itemView.findViewById(R.id.tvNumero);
        }
    }

    // Interfaz para manejar clics largos
    public interface OnContactLongClickListener {
        void onContactLongClick(Contacto contact);
    }
}
