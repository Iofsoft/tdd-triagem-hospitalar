package br.ifsp.tdd_triagem_hospitalar.domain.model;

public enum StatusAtendimento {
    AGUARDANDO_TRIAGEM("Aguardando Triagem"),
    AGUARDANDO_CONSULTA("Aguardando Consulta Médica"),
    EM_CONSULTA("Em Consulta Médica"),
    FINALIZADO("Finalizado"),
    CANCELADO("Cancelado");
    private final String descricao;

    StatusAtendimento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {return descricao;}

    public boolean isTerminal() {
        return this == FINALIZADO || this == CANCELADO || this == EM_CONSULTA;
    }

    public boolean permiteCancelamento() {
        return !isTerminal();
    }
}
