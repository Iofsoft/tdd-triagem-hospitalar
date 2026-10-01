package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class MedicaoSinaisVitaisTest {
    private final UUID idValido = UUID.randomUUID();
    private final LocalDateTime dataHoraValida = LocalDateTime.now();
    private final Double tempValida = 36.5;
    private final int freqValida = 80;

    // INVÁLIDOS

    @Test
    @DisplayName("Deve falhar ao receber ID nulo")
    void deveRejeitarIdNulo() {
        assertThatThrownBy(() -> new MedicaoSinaisVitais(null, 36.5, 80, dataHoraValida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("O Id da medição não pode ser nulo.");
    }

    @Test
    @DisplayName("Deve falhar ao receber data e hora nulas")
    void deveRejeitarDataHoraNula() {
        assertThatThrownBy(() -> new MedicaoSinaisVitais(idValido, 36.5, 80, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("A data e hora da medição não pode ser nula.");
    }

    // VÁLIDOS

    @Test
    @DisplayName("Deve instanciar uma medição válida com dados corretos")
    void deveCriarMedicaoSinaisVitais() {
        MedicaoSinaisVitais medicao = new MedicaoSinaisVitais(idValido, tempValida, freqValida, dataHoraValida);
        assertThat(medicao).isInstanceOf(MedicaoSinaisVitais.class);
    }

    @Test
    @DisplayName("Deve instanciar medição válida usando métodos AssertJ")
    void deveCriarMedicaoSinaisVitaisAssertJ() {
        int frequenciaCardiaca = 80;

        MedicaoSinaisVitais medicao =  new MedicaoSinaisVitais(idValido, tempValida, frequenciaCardiaca, dataHoraValida);
        assertThat(medicao)
                .isNotNull()
                .isInstanceOf(MedicaoSinaisVitais.class)
                .satisfies(m -> {
                    assertThat(m.getId()).isEqualTo(idValido);
                    assertThat(m.getTemperatura()).isEqualTo(tempValida);
                    assertThat(m.getFrequenciaCardiaca()).isEqualTo(frequenciaCardiaca);
                    assertThat(m.getDataHora()).isEqualTo(dataHoraValida);
                } );
    }

    // TEMPERATURA

    @ParameterizedTest
    @ValueSource(doubles = {32.0, 36.8, 40.2, 43.0})
    @DisplayName("Deve aceitar temperaturas válidas dentro da faixa de 32.0 a 43.0")
    void deveAceitarTemperaturasValidas(double temp){
        assertThatNoException().isThrownBy(() ->
                new MedicaoSinaisVitais(idValido, temp, freqValida, dataHoraValida));
    }

    @ParameterizedTest
    @ValueSource(doubles = {31.9, 30.0, 0.0, -1.0})
    @DisplayName("Deve rejeitar temperaturas abaixo de 32.0°C")
    void deveRejeitarTemperaturasAbaixoDoLimite(double temp){
        assertThatThrownBy(() -> new MedicaoSinaisVitais(idValido, temp, 75, dataHoraValida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("fora da faixa permitida");
    }

    @ParameterizedTest
    @ValueSource(doubles = {43.1, 44.0, 50.0})
    @DisplayName("Deve rejeitar temperaturas acima de 43.0°C")
    void deveRejeitarTemperaturaAcimaDoLimite(double temp) {
        assertThrowsExactly(
            IllegalArgumentException.class,
            () -> new MedicaoSinaisVitais(idValido, temp, 75, dataHoraValida),
                ("Temperatura fora da faixa permitida (32.0°C a 43.0°C): " + temp)
        );
    }

    @ParameterizedTest
    @ValueSource(doubles = {32.0, 43.0})
    @DisplayName("Deve aceitar valores nos limites da faixa de temperatura")
    void deveAceitarTemperaturaNosLimites(double temp) {
        var medicao = new MedicaoSinaisVitais(idValido, temp, freqValida, dataHoraValida);

        assertThat(medicao.getTemperatura()).isEqualByComparingTo(temp);
    }

    // FREQUENCIA CARDIACA

    @ParameterizedTest
    @ValueSource(ints = {19, 261, 0, -10, 300})
    @DisplayName("Deve rejeitar frequência cardíaca fora do intervalo [20 - 260 bpm]")
    void deveRejeitarFrequenciaCardiacaForaDoLimite(int frequencia) {
        assertThatThrownBy(() -> new MedicaoSinaisVitais(idValido, tempValida, frequencia, dataHoraValida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Frequência cardíaca fora");
    }

    @ParameterizedTest
    @ValueSource(ints = {20, 260})
    @DisplayName("Deve aceitar valores nos limites de frequência cardíaca")
    void deveAceitarFrequenciaCardiacaNosLimites(int fcValida) {
        var medicao = new MedicaoSinaisVitais(idValido, 36.5, fcValida, dataHoraValida);

        assertThat(medicao.getFrequenciaCardiaca()).isEqualTo(fcValida);
    }

    @Test
    @DisplayName("Duas instâncias com mesmo ID devem ser iguais e ter mesmo hashCode")
    void deveDiscriminarObjetosPorId() {
        var medicao1 = new MedicaoSinaisVitais(idValido, 36.5, 80, dataHoraValida);
        var medicao2 = new MedicaoSinaisVitais(idValido, 39.0, 120, dataHoraValida.minusMinutes(10));
        var medicaoOutroId = new MedicaoSinaisVitais(UUID.randomUUID(), 36.5, 80, dataHoraValida);

        assertThat(medicao1)
                .isEqualTo(medicao2)
                .hasSameHashCodeAs(medicao2)
                .isNotEqualTo(medicaoOutroId);
    }




}
