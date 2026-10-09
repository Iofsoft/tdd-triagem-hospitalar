package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;

import java.util.UUID;

public class RealizarTriagemAppService {

    private final AtendimentoRepository atendimentoRepository;

    public RealizarTriagemAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public void realizarTriagem(UUID atendimentoId, MedicaoSinaisVitais medicao, ClassificacaoRisco risco) {
        Atendimento atendimento = atendimentoRepository.buscarPorId(atendimentoId)
                .orElseThrow(() -> new IllegalArgumentException("Atendimento não encontrado"));

        if (risco == null) {
            throw new IllegalArgumentException("A classificação de risco é obrigatória para a triagem.");
        }
    }
}