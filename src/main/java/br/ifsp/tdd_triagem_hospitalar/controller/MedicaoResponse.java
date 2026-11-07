package br.ifsp.tdd_triagem_hospitalar.controller;

import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;

import java.time.LocalDateTime;
import java.util.UUID;

public record MedicaoResponse(UUID id, double temperatura, int frequenciaCardiaca, LocalDateTime dataHora) {
    public MedicaoResponse(MedicaoSinaisVitais medicao) {
        this(medicao.getId(), medicao.getTemperatura(), medicao.getFrequenciaCardiaca(), medicao.getDataHora());
    }
}
