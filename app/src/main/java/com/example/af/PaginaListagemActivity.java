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

    ImageButton btnAdicionar;
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
        inicializarComponentes();
        btnAdicionar.setOnClickListener(this::adicionarRemedio);
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

    public void adicionarRemedio(View v) {
        context.remedioAtual = null;
        Intent irParaCadastro = new Intent(PaginaListagemActivity.this, PaginaCadastroActivity.class);
        startActivity(irParaCadastro);
    }

    public void removerRemedio(View v, Remedio remedio) {
        OnSuccessListener<Void> onSuccess = __ -> {
            context.listaAtual.remove(remedio);
            adapter.notifyDataSetChanged();
        };
        db.removerRemedio(remedio)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(showFailureToast(this, "remover remédio"));
    }
    public void editarRemedio(View v, Remedio remedio) {
        context.remedioAtual = remedio;
        Intent irParaCadastro = new Intent(PaginaListagemActivity.this, PaginaCadastroActivity.class);
        startActivity(irParaCadastro);
    }
    public void consumirRemedio(View v, Remedio remedio) {
        Remedio stuntDouble = new Remedio();
        stuntDouble.copyFrom(remedio);
        stuntDouble.consumido = !stuntDouble.consumido;
        db.salvarRemedio(remedio)
                .addOnSuccessListener(id -> remedio.consumido = !remedio.consumido)
                .addOnFailureListener(showFailureToast(this, "consumir remédio"));
    }
    public void carregarRemedios() {
        OnSuccessListener<List<Remedio>> onSuccess =  remedios -> {
            context.listaAtual.clear();
            context.listaAtual.addAll(remedios);
            adapter.notifyDataSetChanged();
        };
        db.carregarRemedios()
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(showFailureToast(this, "carregar remédios"));
    }

    private void inicializarComponentes() {
        btnAdicionar = findViewById(R.id.btnAdicionar);
        txtTitulo = findViewById(R.id.txtTitulo);
        rcvRemedios = findViewById(R.id.rcvRemedios);
    }
}