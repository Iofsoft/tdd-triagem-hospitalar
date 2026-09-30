package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class ClassificacaoRiscoTest {

    @ParameterizedTest
    @CsvSource({
            "VERDE, 120",
            "AMARELO, 60",
            "VERMELHO, 0"
    })
    @DisplayName("Cada nível de risco deve ter o tempo limite de espera em minutos correto")
    void deveConterTempoLimiteCorreto(String nivelNome, int minutosEsperados) {
        ClassificacaoRisco risco = ClassificacaoRisco.valueOf(nivelNome);

        assertEquals(minutosEsperados, risco.getTempoMaximoEsperaMinutos());
    }

    @Test
    @DisplayName("Deve identificar corretamente se o atendimento exige atenção imediata")
    void deveIdentificarAtendimentoImediato() {
        assertTrue(ClassificacaoRisco.VERMELHO.ehAtendimentoImediato());
        assertFalse(ClassificacaoRisco.AMARELO.ehAtendimentoImediato());
        assertFalse(ClassificacaoRisco.VERDE.ehAtendimentoImediato());
    }

    @Test
    @DisplayName("Deve converter string com case-insensitive ou lançar exceção se inválido")
    void deveTratarConversaoDeStringInvalida() {
        // Testando se o valueOf aceita ou se você prefere um método de busca customizado
        assertThrows(IllegalArgumentException.class, () -> ClassificacaoRisco.valueOf("AZUL"));
    }

    @ParameterizedTest
    @CsvSource({
            "VERDE, Pouco urgente",
            "AMARELO, Urgente",
            "VERMELHO, Emergência / Imediato"
    })
    @DisplayName("Cada nível de risco deve possuir sua descrição textual esperada")
    void deveConterDescricaoCorreta(ClassificacaoRisco risco, String descricaoEsperada) {
        assertEquals(descricaoEsperada, risco.getDescricao());
    }
}