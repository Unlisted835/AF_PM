package com.example.af;

import static com.example.af.Exceptions.showFailureToast;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public class PaginaCadastroActivity extends AppCompatActivity {
    Button btnSalvar, btnVoltar, btnSalvarEVoltar, btnAcharDescricao;
    EditText txtNome, txtDescricao, txtConsumo;
    ApplicationContext context = ApplicationContext.instance();
    Database db = Database.instance();
    GeminiService gemini = new GeminiService();

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

        if (context.remedioAtual == null) {
            context.remedioAtual = new Remedio();
        }
        objectToScreen(context.remedioAtual);
    }

    public boolean checkIsInvalid(Remedio remedio) {
        if (remedio.nome == null) {
            Toast.makeText(this, "Nome não pode ser nulo", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }
    public void salvarRemedio(View v) {
        screenToObject(context.remedioAtual);
        if (checkIsInvalid(context.remedioAtual)) {
            return;
        }
        OnSuccessListener<String> onSuccess = id -> {
            context.remedioAtual.id = id;
            Toast.makeText(this, "Remédio salvo com sucesso!", Toast.LENGTH_SHORT).show();
        };
        db.salvarRemedio(context.remedioAtual)
                .addOnSuccessListener(onSuccess)
                .addOnFailureListener(showFailureToast(this, "salvar remédio"));
    }
    public void voltarParaListagem(View v) {
        finish();
    }

    public void salvarEVoltar(View v) {
        salvarRemedio(v);
        voltarParaListagem(v);
    }
    public void acharDescricaoNaWeb(View v) {
        String nomeAtual = txtNome.getText().toString();
        if (nomeAtual == null) {
            return;
        }
        if (nomeAtual.length() < 5) {
            Toast.makeText(this, "Nome curto demais para geração automática de descrição", Toast.LENGTH_SHORT).show();
            return;
        }

        Thread task = gemini.send(
           nomeAtual,
           context.geminiInstructions,
           txtDescricao::setText,
           showFailureToast(this, "gerar descrição"));

        task.start();
    }


    private void objectToScreen(Remedio remedio) {
        txtNome.setText(remedio.nome != null ? remedio.nome : "");
        txtDescricao.setText(remedio.descricao != null ? remedio.descricao : "");

        if (remedio.horarioDeConsumo != null) {
            txtConsumo.setText(remedio.horarioDeConsumo.toString());
        } else {
            txtConsumo.setText("");
        }
    }
    private void screenToObject(Remedio remedio) {
        remedio.nome = txtNome.getText().toString();
        remedio.descricao = txtDescricao.getText().toString();
        try {
            remedio.horarioDeConsumo = LocalTime.parse(txtConsumo.getText().toString());
        } catch (Exception e) {
            remedio.horarioDeConsumo = context.now();
            Toast.makeText(this, "Horário em formato errado. Usando horário atual.", Toast.LENGTH_SHORT).show();
        }
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
