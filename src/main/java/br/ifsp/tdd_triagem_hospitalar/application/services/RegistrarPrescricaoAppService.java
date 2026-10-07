package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;

import java.util.UUID;

public class RegistrarPrescricaoAppService {
    private final AtendimentoRepository atendimentoRepository;

    public RegistrarPrescricaoAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public void registrarPrescricao(UUID uuid, String conduta){
        Atendimento atendimento = atendimentoRepository.buscarPorId(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Atendimento não encontrado"));

        atendimento.registrarPrescricao(conduta);
        atendimentoRepository.salvar(atendimento);
    }
}
