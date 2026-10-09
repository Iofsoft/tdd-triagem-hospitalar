package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class RegistrarReavaliacaoAppService {

    private final AtendimentoRepository atendimentoRepository;

    public RegistrarReavaliacaoAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public void registrarReavaliacao(UUID atendimentoId, MedicaoSinaisVitais medicao) {
        final Atendimento atendimento = atendimentoRepository.buscarPorId(atendimentoId).orElseThrow();
        atendimento.registrarMedicao(medicao);
        atendimentoRepository.salvar(atendimento);
    }
}
