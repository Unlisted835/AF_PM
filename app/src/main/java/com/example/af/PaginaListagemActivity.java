package com.example.af;

import static com.example.af.Exceptions.showFailureToast;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

public class PaginaListagemActivity extends AppCompatActivity {

    ImageButton btnAdicionar, btnConfig, btnSeed;
    TextView txtTitulo;
    RecyclerView rcvRemedios;
    ItemRemedioAdapter adapter;
    ApplicationContext context = ApplicationContext.instance();
    Database db = Database.instance();

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

        carregarRemedios();
        btnAdicionar = findViewById(R.id.btnAdicionar);
        btnConfig = findViewById(R.id.btnConfig);
        btnSeed = findViewById(R.id.btnSeed);
        txtTitulo = findViewById(R.id.txtTitulo);
        rcvRemedios = findViewById(R.id.rcvRemedios);
        btnAdicionar.setOnClickListener(this::adicionarRemedio);
        btnConfig.setOnClickListener(this::abrirConfiguracoes);
        btnSeed.setOnClickListener(this::seedDatabase);
        rcvRemedios.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemRemedioAdapter(context.listaAtual);
        adapter.onRemover = this::removerRemedio;
        adapter.onEditar = this::editarRemedio;
        adapter.onConsumir = this::consumirRemedio;
        rcvRemedios.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        carregarRemedios();
    }

    private void adicionarRemedio(View v) {
        context.remedioAtual = null;
        Intent irParaCadastro = new Intent(PaginaListagemActivity.this, PaginaCadastroActivity.class);
        startActivity(irParaCadastro);
    }

    private void removerRemedio(View v, Remedio remedio) {
        OnSuccessListener<Void> onSuccess = __ -> {
            context.listaAtual.remove(remedio);
            adapter.notifyDataSetChanged();
        };
        db.removerRemedio(remedio)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(showFailureToast(this, "remover remédio"));
    }
    private void editarRemedio(View v, Remedio remedio) {
        context.remedioAtual = remedio;
        Intent irParaCadastro = new Intent(PaginaListagemActivity.this, PaginaCadastroActivity.class);
        startActivity(irParaCadastro);
    }
    private void consumirRemedio(View v, Remedio remedio) {
        Remedio stuntDouble = new Remedio();
        stuntDouble.copyFrom(remedio);
        stuntDouble.consumido = !stuntDouble.consumido;
        db.salvarRemedio(remedio)
                .addOnSuccessListener(id -> remedio.consumido = !remedio.consumido)
                .addOnFailureListener(showFailureToast(this, "consumir remédio"));
    }
    private void carregarRemedios() {
        OnSuccessListener<List<Remedio>> onSuccess =  remedios -> {
            context.listaAtual.clear();
            context.listaAtual.addAll(remedios);
            adapter.notifyDataSetChanged();
        };
        db.carregarRemedios()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(showFailureToast(this, "carregar remédios"));
    }

    private void abrirConfiguracoes(View v) {
        Intent irParaConfig = new Intent(PaginaListagemActivity.this, ConfigurationActivity.class);
        startActivity(irParaConfig);
    }

    private void seedDatabase(View v) {
        OnSuccessListener<List<Remedio>> onSuccess = remedios -> {
            context.listaAtual.addAll(remedios);
            adapter.notifyDataSetChanged();
        };
        db.seed(Remedio.Seeds.all())
           .addOnSuccessListener(onSuccess)
           .addOnFailureListener(showFailureToast(this, "popular banco de dados"));
    }
}