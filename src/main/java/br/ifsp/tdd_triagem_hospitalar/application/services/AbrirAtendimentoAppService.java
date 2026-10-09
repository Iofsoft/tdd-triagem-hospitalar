package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;

import java.util.List;
import java.util.UUID;

public class AbrirAtendimentoAppService {

    private final AtendimentoRepository atendimentoRepository;

    public AbrirAtendimentoAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public UUID abrirAtendimento(String cpf) {
        Cpf cpfObj = new Cpf(cpf);

        if (atendimentoRepository.existeAtendimentoAtivoPorCpf(cpfObj)) {
            throw new IllegalStateException("Paciente já possui atendimento ativo.");
        }
        UUID id = UUID.randomUUID();
        Atendimento novoAtendimento = new Atendimento(
                id,
                cpfObj,
                StatusAtendimento.AGUARDANDO_TRIAGEM,
                null,       // sem classificação de risco na abertura
                List.of()                   // lista de medições vazia
        );

        atendimentoRepository.salvar(novoAtendimento);

        return novoAtendimento.getId();
    }
}