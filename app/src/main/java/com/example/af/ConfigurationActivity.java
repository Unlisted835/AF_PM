package com.example.af;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfigurationActivity extends AppCompatActivity {

    private EditText txtGeminiApiKey;
    private Button btnSaveConfig;
    private ApplicationContext context = ApplicationContext.instance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.pagina_configuracao);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtGeminiApiKey = findViewById(R.id.txtGeminiApiKey);
        btnSaveConfig = findViewById(R.id.btnSaveConfig);

        txtGeminiApiKey.setText(context.geminiApiKey);
        btnSaveConfig.setOnClickListener(this::onSave);
    }

    private void onSave(View v) {
        context.geminiApiKey = txtGeminiApiKey.getText().toString();
        finish();
    }
}
