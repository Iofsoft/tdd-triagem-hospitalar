package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class StatusAtendimentoTest {

    @ParameterizedTest
    @EnumSource(value = StatusAtendimento.class, names = {"FINALIZADO", "CANCELADO"})
    @DisplayName("Deve identificar corretamente estados terminais")
    void deveIdentificarEstadosTerminais(StatusAtendimento status) {
        assertThat(status.isTerminal()).isTrue();
    }

    @ParameterizedTest
    @EnumSource(value = StatusAtendimento.class, names = {"AGUARDANDO_TRIAGEM", "AGUARDANDO_CONSULTA", "EM_CONSULTA"})
    void deveIdentificarEstadosNaoTerminais(StatusAtendimento status){
        assertThat(status.isTerminal()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = StatusAtendimento.class, names = {"FINALIZADO", "CANCELADO"})
    @DisplayName("Não deve permitir cancelamento para estados terminais (Finalizado e Cancelado)")
    void naoDevePermitirCancelamentoParaEstadosTerminais(StatusAtendimento status) {
        assertThat(status.permiteCancelamento()).isFalse();
    }

    @ParameterizedTest
    @EnumSource(value = StatusAtendimento.class, names = {"AGUARDANDO_TRIAGEM", "AGUARDANDO_CONSULTA", "EM_CONSULTA"})
    @DisplayName("Deve permitir cancelamento para estados ativos prévios")
    void devePermitirCancelamentoParaEstadosAtivos(StatusAtendimento status) {
        assertThat(status.permiteCancelamento()).isTrue();
    }

}