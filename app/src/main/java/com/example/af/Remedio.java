package com.example.af;

import com.google.firebase.firestore.Exclude;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

public class Remedio {

    public String id;
    public String nome;
    public String descricao;
    @Exclude public LocalTime horarioDeConsumo;
    public boolean consumido;
    public boolean notified;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Remedio)) return false;
        Remedio remedio = (Remedio) o;
        return Objects.equals(id, remedio.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public Remedio() {}
    public Remedio(String id, String nome, String descricao, LocalTime horarioDeConsumo, boolean consumido) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.horarioDeConsumo = horarioDeConsumo;
        this.consumido = consumido;
    }
    public void copyFrom(Remedio other) {
        this.id = other.id;
        this.nome = other.nome;
        this.descricao = other.descricao;
        this.horarioDeConsumo = other.horarioDeConsumo;
        this.consumido = other.consumido;
    }

    public String getHorarioString() {
        if (horarioDeConsumo == null) return null;
        return horarioDeConsumo.toString();
    }

    public void setHorarioString(String horarioString) {
        if (horarioString != null && !horarioString.isEmpty()) {
            try {
                this.horarioDeConsumo = LocalTime.parse(horarioString);
            } catch (Exception e) {
                this.horarioDeConsumo = ApplicationContext.instance().now();
            }
        } else {
            this.horarioDeConsumo = null;
        }
    }

    public static class Seeds {
        public static Remedio melhoral = new Remedio(
                "id-1",
                "Melhoral",
                "Te melhora na hora",
                LocalTime.of(12, 0, 0),
                false
        );
        public static Remedio paracetamal = new Remedio(
                "id-2",
                "Paracetamal",
                "Para! Você está mal!",
                LocalTime.of(7, 50, 0),
                true
        );
        public static Remedio omeprazol = new Remedio(
                "id-3",
                "Omeprazol",
                "Descrição adequada",
                LocalTime.of(19, 25, 43),
                false
        );
        public static List<Remedio> all() {
            return List.of(melhoral, paracetamal, omeprazol);
        }
    }
}
