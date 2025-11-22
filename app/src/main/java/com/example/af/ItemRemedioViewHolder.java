package com.example.af;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ItemRemedioViewHolder extends RecyclerView.ViewHolder {

    private Remedio remedio;
    private ImageButton btnRemover, btnEditar, btnConsumir;
    private TextView txtNome, txtHorario;
    private LinearLayout lytItem;
    private static final int lightGray = Color.parseColor("#BBBBBB");
    private static final int darkGray = Color.parseColor("#DDDDDD");

    public ItemRemedioViewHolder(View itemView) {
        super(itemView);
        inicializarComponentes();
    }
    public void setRemedio(Remedio remedio) {
        this.remedio = remedio;
        txtNome.setText(remedio.nome);
        txtHorario.setText(remedio.horarioDeConsumo.toString());
        setConsumido(remedio.consumido);
    }
    public void setConsumido(boolean consumido) {
        if (consumido) {
            btnConsumir.setImageResource(R.drawable.check_circle);
        } else {
            btnConsumir.setImageResource(R.drawable.pill);
        }
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
    public void setBackground(boolean isEvenRow) {
        if (isEvenRow) {
            lytItem.setBackgroundColor(darkGray);
        } else {
            lytItem.setBackgroundColor(lightGray);
        }
    }
    private void inicializarComponentes() {
        btnRemover = itemView.findViewById(R.id.btnRemover);
        btnEditar = itemView.findViewById(R.id.btnEditar);
        btnConsumir = itemView.findViewById(R.id.btnConsumir);
        txtNome = itemView.findViewById(R.id.txtNome);
        txtHorario = itemView.findViewById(R.id.txtHorario);
        lytItem = itemView.findViewById(R.id.lytItem);
    }
    @FunctionalInterface
    public interface OnItemClick {
        void clicado(View v, Remedio remedio);
    }
}
