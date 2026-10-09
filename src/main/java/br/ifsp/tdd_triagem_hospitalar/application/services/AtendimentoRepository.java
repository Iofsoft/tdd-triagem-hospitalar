package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;

import java.util.Optional;
import java.util.UUID;

public interface AtendimentoRepository {
    void salvar(Atendimento atendimento);
    Optional<Atendimento> buscarPorId(UUID id);
    boolean existeAtendimentoAtivoPorCpf(Cpf cpf);
}
