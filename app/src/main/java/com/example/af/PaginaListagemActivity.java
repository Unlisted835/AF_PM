package com.example.af;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PaginaListagemActivity extends AppCompatActivity {

    ImageButton btnAdicionar;
    TextView txtTitulo;
    RecyclerView rcvRemedios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_listagem);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }

    public void adicionarRemedio(View v) {
        Intent irParaCadastro = new Intent(this, PaginaCadastroActivity.class);
        startActivity(irParaCadastro);
    }

    public void removerRemedio(View v) {

    }

    public void marcarRemedioComoConsumido(View v) {

    }

    private void inicializarComponentes() {
        btnAdicionar = findViewById(R.id.btnAdicionar);
        txtTitulo = findViewById(R.id.txtTitulo);
        rcvRemedios = findViewById(R.id.rcvRemedios);
    }
    private void configurarEventos() {

    }
}