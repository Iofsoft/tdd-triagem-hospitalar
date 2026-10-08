package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;

import java.util.UUID;

public class AbrirAtendimentoAppService {

    private final AtendimentoRepository atendimentoRepository;

    public AbrirAtendimentoAppService(AtendimentoRepository atendimentoRepository) {
        this.atendimentoRepository = atendimentoRepository;
    }

    public UUID abrirAtendimento(String cpf) {
        Cpf cpfObj = new Cpf(cpf);

        return null;
    }
}