package br.ifsp.tdd_triagem_hospitalar.controller;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;

import java.util.List;
import java.util.UUID;

public record AtendimentoResponse(
        UUID id,
        String cpf,
        StatusAtendimento status,
        ClassificacaoRisco classificacaoRisco,
        List<MedicaoResponse> medicoes,
        String prescricao,
        String motivoCancelamento
) {
    public AtendimentoResponse(Atendimento atendimento) {
        this(atendimento.getId(),
                atendimento.getCpf().valor(),
                atendimento.getStatus(),
                atendimento.getClassificacaoRisco(),
                atendimento.getMedicoes().stream().map(MedicaoResponse::new).toList(),
                atendimento.getPrescricao(),
                atendimento.getMotivoCancelamento());
    }
}
