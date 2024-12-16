package com.example.synthwavechat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactViewHolder> implements Filterable {

    private List<Contact> contactList;
    private List<Contact> contactListFull; // Lista completa de contactos

    public ContactsAdapter(List<Contact> contactList) {
        this.contactList = contactList;
        this.contactListFull = new ArrayList<>(contactList); // Guardamos una copia de la lista completa
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contact, parent, false);
        return new ContactViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.tvName.setText(contact.getName());
        holder.tvEmail.setText(contact.getEmail()); // Mostrar el correo electrónico
        // Aquí podrías cargar la foto de perfil si es necesario
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Contact> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(contactListFull); // No hay filtro, mostramos todos
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Contact contact : contactListFull) {
                        if (contact.getName().toLowerCase().contains(filterPattern) ||
                                contact.getEmail().toLowerCase().contains(filterPattern)) {
                            filteredList.add(contact);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                results.count = filteredList.size();
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactList.clear();
                if (results.values != null) {
                    contactList.addAll((List<Contact>) results.values);
                }
                notifyDataSetChanged();
            }
        };
    }

    public static class ContactViewHolder extends RecyclerView.ViewHolder {

        TextView tvName, tvEmail;
        ImageView ivProfileImage;
        ImageView ivChatIcon;

        public ContactViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvContactName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            ivProfileImage = itemView.findViewById(R.id.ivProfileImage);
            ivChatIcon = itemView.findViewById(R.id.ivChatIcon);
        }
    }
}
