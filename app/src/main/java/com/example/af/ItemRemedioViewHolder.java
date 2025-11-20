package com.example.af;

import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ItemRemedioViewHolder extends RecyclerView.ViewHolder {

    private Remedio remedio;
    private Button btnRemover, btnEditar, btnConsumir;
    private TextView txtNome;

    public ItemRemedioViewHolder(View itemView) {
        super(itemView);
    }
    public void setRemedio(Remedio remedio) {
        txtNome.setText(remedio.nome);
        setConsumido(remedio.consumido);
    }
    public void setConsumido(boolean consumido) {
        if (consumido) {
            btnConsumir.setBackgroundResource(R.drawable.check_circle);
        } else {
            btnConsumir.setBackgroundResource(R.drawable.pill);
        }
        btnConsumir.setEnabled(!consumido);
    }
    public void setOnRemover(OnItemClick onItemClick) {
        btnRemover.setOnClickListener(v -> onItemClick.clicado(v, remedio));
    }
    public void setOnEditar(OnItemClick onItemClick) {
        btnEditar.setOnClickListener(v -> onItemClick.clicado(v, remedio));
    }
    public void setOnConsumir(OnItemClick onItemClick) {
        btnConsumir.setOnClickListener(v -> {
            onItemClick.clicado(v, remedio);
            setConsumido(remedio.consumido);
        });
    }

    @FunctionalInterface
    public interface OnItemClick {
        void clicado(View v, Remedio remedio);
    }
}
