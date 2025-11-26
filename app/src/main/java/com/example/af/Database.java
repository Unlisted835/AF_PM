package com.example.af;

import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;
import java.util.stream.Collectors;

public class Database {
   private Database() {
   }

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

   public Task<List<Remedio>> seed(List<Remedio> seeds) {
      List<Task<Remedio>> tasks = seeds.stream()
         .filter(Objects::nonNull)
         .parallel()
         .map(r ->  firestore.collection(REMEDIO_COLLECTION).add(r)
               .continueWith(t -> {
                  if (t.isSuccessful()) {
                     return r;
                  }
                  return null;
               })
         )
         .collect(Collectors.toList());

      return Tasks.whenAllComplete(tasks).continueWith(t -> {
         if (t.isSuccessful()) {
            return t.getResult().stream()
               .filter(Task::isSuccessful)
               .map(t2 -> (Remedio) t2.getResult())
               .filter(Objects::nonNull)
               .collect(Collectors.toList());
         }
         return List.of();
      });
   }

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
