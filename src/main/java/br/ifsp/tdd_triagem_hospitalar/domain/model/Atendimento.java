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
    private String prescricao;
    private String motivoCancelamento;


    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }


    public String getPrescricao() {
        return prescricao;
    }
    public void registrarPrescricao(String prescricao) {
        if (this.status != StatusAtendimento.EM_CONSULTA)
            throw new IllegalStateException("Não é possível prescrever fora de consulta");

        if (prescricao == null || prescricao.isBlank())
            throw new IllegalArgumentException("O texto da prescrição não pode ser vazio");

        this.prescricao = prescricao;
    }

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

    public Atendimento(UUID id, Cpf cpf, StatusAtendimento status, ClassificacaoRisco classificacaoRisco,
                       List<MedicaoSinaisVitais> medicoes, String prescricao) {
        this(id, cpf, status, classificacaoRisco, medicoes); // chama o construtor normal
        this.prescricao = prescricao; // adiciona a prescrição
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
        if (status != StatusAtendimento.AGUARDANDO_CONSULTA) {
            throw new IllegalStateException("Consulta só pode ser iniciada quando o atendimento está aguardando consulta.");
        }
        status = StatusAtendimento.EM_CONSULTA;
    }

    public void registrarMedicao(MedicaoSinaisVitais medicao) {
        if (status == StatusAtendimento.FINALIZADO || status == StatusAtendimento.CANCELADO) {
            throw new IllegalStateException("Não é possível registrar medição em atendimento finalizado ou cancelado.");
        }
        medicoes.add(medicao);
    }

    public void reclassificarRisco(ClassificacaoRisco novaClassificacao, String justificativa) {
        if (status == StatusAtendimento.FINALIZADO || status == StatusAtendimento.CANCELADO) {
            throw new IllegalStateException("Não é possível reclassificar o risco de atendimento finalizado ou cancelado.");
        }
        if (justificativa == null || justificativa.isBlank()) {
            throw new IllegalArgumentException("A justificativa da reclassificação não pode ser vazia.");
        }
        classificacaoRisco = novaClassificacao;
    }

    public void finalizarAtendimento(){

        if (this.status != StatusAtendimento.EM_CONSULTA)
            throw new IllegalStateException("Atendimento deve possuir status EmConsulta para ser finalizado");

        if (this.prescricao == null || this.prescricao.isBlank())
            throw new IllegalStateException("Atendimento não pode ser finalizado sem prescrição");

        this.status = StatusAtendimento.FINALIZADO;
    }

    public void cancelarAtendimento(String motivoCancelamento){
        if ((this.status == StatusAtendimento.CANCELADO) || (this.status == StatusAtendimento.FINALIZADO)){
            throw new IllegalStateException("Atendimentos já cancelados ou finalizados não podem ser cancelados");
        }

        this.status = StatusAtendimento.CANCELADO;
        this.motivoCancelamento = motivoCancelamento;
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
