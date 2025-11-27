package com.example.af;

import static com.example.af.Exceptions.showFailureToast;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.time.LocalTime;

public class PaginaCadastroActivity extends AppCompatActivity {
   Button btnSalvar, btnVoltar, btnSalvarEVoltar, btnAcharDescricao;
   EditText txtNome, txtDescricao;
   ProgressBar prbDescription;
   TimePicker tmpConsumo;
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
         String failureMessage = getString(R.string.toast_validation_name_notNull);
         Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show();
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
         Toast.makeText(this, getString(R.string.toast_success_medicineSaved), Toast.LENGTH_SHORT).show();
      };
      db.salvarRemedio(context.remedioAtual)
         .addOnSuccessListener(onSuccess)
         .addOnFailureListener(showFailureToast(this, toastSaveMedicine()));
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

      if (nomeAtual.length() < 5) {
         String failureMessage = getString(R.string.toast_validation_name_toShort);
         Toast.makeText(this, failureMessage, Toast.LENGTH_SHORT).show();
         return;
      }

      String prompt = getString(R.string.gemini_medicineDescriptionGeneration_promptIntro);
      prompt += nomeAtual;
      String instructions = getString(R.string.gemini_medicineDescriptionGeneration_instructions);

      Runnable onEither = () -> {
         txtDescricao.setHint(getString(R.string.placeholder_description));
         txtDescricao.setEnabled(true);
         prbDescription.setVisibility(View.INVISIBLE);
      };
      OnSuccessListener<String> onSuccess = generatedDescription -> {
         txtDescricao.setText(generatedDescription);
         onEither.run();
      };
      OnFailureListener onFailure = e -> {
         showFailureToast(this, toastGenerateDescription()).onFailure(e);
         onEither.run();
      };

      Thread task = gemini.send(prompt, instructions, onSuccess, onFailure);
      task.start();

      txtDescricao.setText("");
      txtDescricao.setHint("");
      txtDescricao.setEnabled(false);
      prbDescription.setVisibility(View.VISIBLE);
   }


   private void objectToScreen(Remedio remedio) {
      txtNome.setText(remedio.nome != null ? remedio.nome : "");
      txtDescricao.setText(remedio.descricao != null ? remedio.descricao : "");

      if (remedio.horarioDeConsumo != null) {
         tmpConsumo.setHour(remedio.horarioDeConsumo.getHour());
         tmpConsumo.setMinute(remedio.horarioDeConsumo.getMinute());
      } else {
         LocalTime now = context.now();
         tmpConsumo.setHour(now.getHour());
         tmpConsumo.setMinute(now.getMinute());
      }
   }

   private void screenToObject(Remedio remedio) {
      remedio.nome = txtNome.getText().toString();
      remedio.descricao = txtDescricao.getText().toString();
      remedio.horarioDeConsumo = LocalTime.of(tmpConsumo.getHour(), tmpConsumo.getMinute());
   }

   private void inicializarComponentes() {
      btnSalvar = findViewById(R.id.btnSalvar);
      btnVoltar = findViewById(R.id.btnVoltar);
      btnSalvarEVoltar = findViewById(R.id.btnSalvarEVoltar);
      btnAcharDescricao = findViewById(R.id.btnAcharDescricao);
      txtNome = findViewById(R.id.txtNome);
      txtDescricao = findViewById(R.id.txtDescricao);
      tmpConsumo = findViewById(R.id.tmpConsumo);
      prbDescription = findViewById(R.id.prbDescription);
   }

   private void configurarEventos() {
      btnSalvar.setOnClickListener(this::salvarRemedio);
      btnVoltar.setOnClickListener(this::voltarParaListagem);
      btnSalvarEVoltar.setOnClickListener(this::salvarEVoltar);
      btnAcharDescricao.setOnClickListener(this::acharDescricaoNaWeb);
   }

   private String toastSaveMedicine() {
      return getString(R.string.toast_failure_message) + " " + getString(R.string.toast_failure_context_saveMedicine);
   }
   private String toastGenerateDescription() {
      return getString(R.string.toast_failure_message) + " " + getString(R.string.toast_failure_context_generateDescription);
   }
}
