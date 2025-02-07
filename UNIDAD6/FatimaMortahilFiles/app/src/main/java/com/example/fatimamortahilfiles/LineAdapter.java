package com.example.fatimamortahilfiles;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class LineAdapter extends RecyclerView.Adapter<LineAdapter.LineaViewHolder> {
    private final List<String> lineas; //aqui guardo todas las lineas del archivo txt


    public LineAdapter(List<String> lineas) {
        this.lineas = lineas;
    }

    @NonNull
    @Override
    public LineaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_linea, parent, false);
        return new LineaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LineaViewHolder holder, int position) {
        holder.tvLinea.setText(lineas.get(position)); //tomamos la linea en la posicion "position" y la ponemos en el Textview
        holder.itemView.setOnLongClickListener(v -> { // si se hace clic largo, se elimina la linea.
            lineas.remove(holder.getAdapterPosition()); //quitamos la linea de la lista
            notifyItemRemoved(holder.getAdapterPosition()); //le decimos al recyclerview que

            return true; //devolvemos true para indicar que esta bien el proceso
        });
    }


    @Override
    public int getItemCount() {
        return lineas.size(); //Devuelve el numero total de lineas en la lista
    }

    //Esta es una clase que se encarga de guardar una linea
    class LineaViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvLinea;

        public LineaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLinea = itemView.findViewById(R.id.textViewLinea);
        }

    }

}

