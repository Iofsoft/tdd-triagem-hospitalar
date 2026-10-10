package br.ifsp.tdd_triagem_hospitalar.controller;

import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;

public record RealizarTriagemRequest(
        double temperatura,
        int frequenciaCardiaca,
        ClassificacaoRisco classificacaoRisco
) {
}
