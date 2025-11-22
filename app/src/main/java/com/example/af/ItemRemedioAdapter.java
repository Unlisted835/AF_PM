package com.example.af;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class ItemRemedioAdapter extends RecyclerView.Adapter<ItemRemedioViewHolder> {

    public ItemRemedioViewHolder.OnItemClick onRemover;
    public ItemRemedioViewHolder.OnItemClick onEditar;
    public ItemRemedioViewHolder.OnItemClick onConsumir;

    private List<Remedio> remedios = new ArrayList<>();

    public ItemRemedioAdapter(List<Remedio> remedios) {
        this.remedios = remedios;
    }

    @Override
    public ItemRemedioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_remedio, parent, false);
        return new ItemRemedioViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ItemRemedioViewHolder holder, int pos) {
        Remedio remedio = remedios.get(pos);
        holder.setBackground(pos % 2 == 0);
        holder.setRemedio(remedio);
        holder.setOnRemover(onRemover);
        holder.setOnEditar(onEditar);
        holder.setOnConsumir(onConsumir);
    }

    @Override
    public int getItemCount() {
        return remedios.size();
    }
}
