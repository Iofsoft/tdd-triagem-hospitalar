package br.ifsp.tdd_triagem_hospitalar.domain.model;

public enum ClassificacaoRisco {
    VERDE(120, "Pouco urgente"),
    AMARELO(60, "Urgente"),
    VERMELHO(0, "Emergência / Imediato");

    private final int tempoMaximoEsperaMinutos;
    private final String descricao;

    ClassificacaoRisco(int tempoMaximoEsperaMinutos, String descricao) {
        this.tempoMaximoEsperaMinutos = tempoMaximoEsperaMinutos;
        this.descricao = descricao;
    }

    public int getTempoMaximoEsperaMinutos() {
        return tempoMaximoEsperaMinutos;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean ehAtendimentoImediato() {
        return this == VERMELHO;
    }
}
