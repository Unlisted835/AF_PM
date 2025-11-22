package com.example.af;

import java.time.LocalTime;
import java.util.Objects;

public class Remedio {

    public String id;
    public String nome;
    public String descricao;
    public LocalTime horarioDeConsumo;
    public boolean consumido;


    public Remedio setId(String id) {
        this.id = id;
        return this;
    }

    public Remedio setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public Remedio setDescricao(String descricao) {
        this.descricao = descricao;
        return this;
    }

    public Remedio setHorarioDeConsumo(LocalTime horarioDeConsumo) {
        this.horarioDeConsumo = horarioDeConsumo;
        return this;
    }

    public Remedio setConsumido(boolean consumido) {
        this.consumido = consumido;
        return this;
    }

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
    }
}
