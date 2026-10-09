package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IniciarConsultaAppService {

    private final AtendimentoRepository atendimentoRepository;

    public IniciarConsultaAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public void iniciarConsulta(UUID atendimentoId) {
        final Atendimento atendimento = atendimentoRepository.buscarPorId(atendimentoId).orElseThrow();
        atendimento.iniciarConsulta();
        atendimentoRepository.salvar(atendimento);
    }
}
