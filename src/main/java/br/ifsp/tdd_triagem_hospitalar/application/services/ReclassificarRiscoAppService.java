package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;

import java.util.UUID;

public class ReclassificarRiscoAppService {

    private final AtendimentoRepository atendimentoRepository;

    public ReclassificarRiscoAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public void reclassificarRisco(UUID atendimentoId, ClassificacaoRisco novaClassificacao, String justificativa) {
        final Atendimento atendimento = atendimentoRepository.buscarPorId(atendimentoId).orElseThrow();
        atendimento.reclassificarRisco(novaClassificacao, justificativa);
        atendimentoRepository.salvar(atendimento);
    }
}
