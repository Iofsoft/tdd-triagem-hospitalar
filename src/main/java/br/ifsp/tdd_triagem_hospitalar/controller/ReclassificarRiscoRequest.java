package br.ifsp.tdd_triagem_hospitalar.controller;

import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;

public record ReclassificarRiscoRequest(ClassificacaoRisco novaClassificacao, String justificativa) {
}
