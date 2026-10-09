package br.ifsp.tdd_triagem_hospitalar.application.services;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CancelarAtendimentoAppService {
    private final AtendimentoRepository atendimentoRepository;

    public CancelarAtendimentoAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public void cancelarAtendimento(UUID idAtendimento, String motivoCancelamento){
        var atendimento = atendimentoRepository.buscarPorId(idAtendimento)
                .orElseThrow(() -> new IllegalArgumentException("Atendimento não encontrado"));
        atendimento.cancelarAtendimento(motivoCancelamento);
        atendimentoRepository.salvar(atendimento);
    }
}
