package com.example.af;

import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class Database {
    private Database() {}
    private static Database _instance;
    public static Database instance() {
        if (_instance == null) {
            _instance = new Database();
        }
        return _instance;
    }
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private ApplicationContext context = ApplicationContext.instance();
    public static final String REMEDIO_COLLECTION = "remedios";


    public Task<String> salvarRemedio(Remedio remedio) {
        if (remedio.id == null) {
            return firestore.collection(REMEDIO_COLLECTION)
                    .add(remedio)
                    .continueWith(t -> {
                        if (t.isSuccessful()) {
                            return t.getResult().getId();
                        }
                        return null;
                    });
        } else {
            return firestore.collection(REMEDIO_COLLECTION)
                    .document(remedio.id)
                    .set(remedio)
                    .continueWith(__ -> remedio.id);
        }
    }
    public Task<List<Remedio>> carregarRemedios() {
        return firestore.collection(REMEDIO_COLLECTION)
                .get()
                .continueWith(t -> {
                    List<Remedio> lista = new ArrayList<>();
                    if (t.isSuccessful()) {
                        for (var doc : t.getResult()) {
                            Remedio remedio = doc.toObject(Remedio.class);
                            remedio.id = doc.getId();
                            lista.add(remedio);
                        }
                    }
                    return lista;
                });
    }
    public Task<Void> removerRemedio(Remedio remedio) {
        return firestore.collection(REMEDIO_COLLECTION)
                .document(remedio.id)
                .delete();
    }
}
