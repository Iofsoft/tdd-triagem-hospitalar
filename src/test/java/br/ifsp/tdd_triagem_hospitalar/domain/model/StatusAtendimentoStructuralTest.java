package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UnitTest")
@Tag("Structural")
class StatusAtendimentoStructuralTest {
    @ParameterizedTest
    @CsvSource({
            "AGUARDANDO_TRIAGEM, Aguardando Triagem",
            "AGUARDANDO_CONSULTA, Aguardando Consulta Médica",
            "EM_CONSULTA, Em Consulta Médica",
            "FINALIZADO, Finalizado",
            "CANCELADO, Cancelado"
    })
    @DisplayName("Deve retornar a descrição esperada para cada status")
    void deveRetornarDescricao(StatusAtendimento status, String descricaoEsperada) {
        assertThat(status.getDescricao()).isEqualTo(descricaoEsperada);
    }
}
