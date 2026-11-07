package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarAtendimentosAppService {

    private final AtendimentoRepository atendimentoRepository;

    public ListarAtendimentosAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public List<Atendimento> listarAtendimentos() {
        return atendimentoRepository.buscarTodos();
    }
}
