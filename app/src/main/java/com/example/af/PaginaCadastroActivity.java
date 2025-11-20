package com.example.af;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PaginaCadastroActivity extends AppCompatActivity {
    Button btnSalvar, btnVoltar, btnSalvarEVoltar, btnAcharDescricao;
    EditText txtNome, txtDescricao, txtConsumo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inicializarComponentes();
        configurarEventos();
    }



    public void salvarRemedio(View v) {

    }
    public void voltarParaListagem(View v) {
        finish();
    }

    public void salvarEVoltar(View v) {
        salvarRemedio(v);
        voltarParaListagem(v);
    }
    public void acharDescricaoNaWeb(View v) {

    }


    private void inicializarComponentes() {
        btnSalvar = findViewById(R.id.btnSalvar);
        btnVoltar = findViewById(R.id.btnVoltar);
        btnSalvarEVoltar = findViewById(R.id.btnSalvarEVoltar);
        btnAcharDescricao = findViewById(R.id.btnAcharDescricao);
        txtNome = findViewById(R.id.txtNome);
        txtDescricao = findViewById(R.id.txtDescricao);
        txtConsumo = findViewById(R.id.txtConsumo);
    }

    private void configurarEventos() {
        btnSalvar.setOnClickListener(this::salvarRemedio);
        btnVoltar.setOnClickListener(this::voltarParaListagem);
        btnSalvarEVoltar.setOnClickListener(this::salvarEVoltar);
        btnAcharDescricao.setOnClickListener(this::acharDescricaoNaWeb);
    }
}
