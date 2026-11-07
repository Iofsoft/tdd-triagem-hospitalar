package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BuscarAtendimentoAppService {

    private final AtendimentoRepository atendimentoRepository;

    public BuscarAtendimentoAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public Atendimento buscarAtendimento(UUID atendimentoId) {
        return atendimentoRepository.buscarPorId(atendimentoId).orElseThrow();
    }
}
