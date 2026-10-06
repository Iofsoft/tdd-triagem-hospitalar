package br.ifsp.tdd_triagem_hospitalar.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Atendimento {

    private final UUID id;
    private final Cpf cpf;
    private StatusAtendimento status;
    private ClassificacaoRisco classificacaoRisco;
    private final List<MedicaoSinaisVitais> medicoes;

    public Atendimento(UUID id, Cpf cpf, StatusAtendimento status, ClassificacaoRisco classificacaoRisco, List<MedicaoSinaisVitais> medicoes) {
        if (id == null) {
            throw new IllegalArgumentException("O Id do atendimento não pode ser nulo.");
        }
        if (cpf == null) {
            throw new IllegalArgumentException("O CPF do atendimento não pode ser nulo.");
        }
        if (status == null) {
            throw new IllegalArgumentException("O status do atendimento não pode ser nulo.");
        }
        if (classificacaoRisco == null && status != StatusAtendimento.AGUARDANDO_TRIAGEM && status != StatusAtendimento.CANCELADO) {
            throw new IllegalArgumentException("Atendimento já triado precisa de classificação de risco.");
        }

        this.id = id;
        this.cpf = cpf;
        this.status = status;
        this.classificacaoRisco = classificacaoRisco;
        this.medicoes = medicoes == null ? new ArrayList<>() : new ArrayList<>(medicoes);
    }

    public UUID getId() {
        return id;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public StatusAtendimento getStatus() {
        return status;
    }

    public ClassificacaoRisco getClassificacaoRisco() {
        return classificacaoRisco;
    }

    public List<MedicaoSinaisVitais> getMedicoes() {
        return List.copyOf(medicoes);
    }

    public void iniciarConsulta() {
        status = StatusAtendimento.EM_CONSULTA;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atendimento that = (Atendimento) o;
        return Objects.equals(id, that.id);
    }
}
