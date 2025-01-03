package com.example.gestorcontactosfatima;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.gestorcontactosfatima.placeholder.PlaceholderContent;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderContent.PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class  BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.DeviceViewHolder>{

    private List<String> devicesList;
    private OnDeviceClickListener onDeviceClickListener;

    public BluetoothDeviceAdapter(List<String> devicesList, OnDeviceClickListener listener) {
        this.devicesList = devicesList;
        this.onDeviceClickListener = listener;
    }
    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new DeviceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        String device = devicesList.get(position);
        holder.deviceNameTextView.setText(device.split("\n")[0]); // Nombre del dispositivo bluetooth
        holder.deviceAddressTextView.setText(device.split("\n")[1]); // Dirección del dispositivo

        // aqui manejamos cuando se hace clic en cada dispositivo
        holder.itemView.setOnClickListener(v -> {
            if (onDeviceClickListener != null) {
                onDeviceClickListener.onDeviceClick(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devicesList.size();
    }

    // ViewHolder para manejar la vista de cada dispositivo
    static class DeviceViewHolder extends RecyclerView.ViewHolder {
        TextView deviceNameTextView;
        TextView deviceAddressTextView;

        public DeviceViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceNameTextView = itemView.findViewById(android.R.id.text1);
            deviceAddressTextView = itemView.findViewById(android.R.id.text2);
        }
    }

    // Interfaz para manejar los clics en los dispositivos
    public interface OnDeviceClickListener {
        void onDeviceClick(String device);
    }
}