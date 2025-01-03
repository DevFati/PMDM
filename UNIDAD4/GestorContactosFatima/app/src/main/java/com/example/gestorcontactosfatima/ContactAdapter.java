package com.example.gestorcontactosfatima;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.recyclerview.widget.RecyclerView;


import com.example.gestorcontactosfatima.databinding.ContactoItemBinding;
import com.example.gestorcontactosfatima.placeholder.PlaceholderContent;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link PlaceholderContent.PlaceholderItem}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ContactAdapter extends ArrayAdapter<Contact> {


    public ContactAdapter(Context context, ArrayList<Contact> contacts) {
        super(context, 0, contacts);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contact c = getItem(position);


        ContactoItemBinding binding;
        if (convertView == null) {
            binding = ContactoItemBinding.inflate(LayoutInflater.from(getContext()), parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ContactoItemBinding) convertView.getTag();
        }


        binding.nombreC.setText(c.getNombre());
        binding.telefonoC.setText(c.getTelefono());

        return convertView;
    }
}