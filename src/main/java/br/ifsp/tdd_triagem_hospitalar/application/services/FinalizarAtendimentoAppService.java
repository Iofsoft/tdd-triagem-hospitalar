package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;

import java.util.UUID;

public class FinalizarAtendimentoAppService {
    private final AtendimentoRepository atendimentoRepository;

    public FinalizarAtendimentoAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public void finalizarAtendimento(UUID idAtendimento){
        Atendimento atendimento = atendimentoRepository.buscarPorId(idAtendimento)
                .orElseThrow(()-> new IllegalArgumentException("Atendimento não encontrado"));
        atendimento.finalizarAtendimento();
        atendimentoRepository.salvar(atendimento);
    }
}
