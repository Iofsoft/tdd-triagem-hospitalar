package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CpfTest {

    @Test
    void shouldThrowExceptionForNullCpf(){
        assertThrows(IllegalArgumentException.class, () -> new Cpf(null));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "123",                    // tamanho menor
            "1234567890123",          // tamanho maior
            "abcdefghijk",            // caracteres não numéricos
            "11111111111",            // dígitos repetidos
            "00000000000",
            "12345678900"             // dígitos verificadores inválidos

    })
    @DisplayName("Deve lançar exceção para formatos e valores de cpf inválidos")
    void shouldThrowExceptionForInvalidCpf(String cpfInvalido) {
        assertThrows(IllegalArgumentException.class, () -> new Cpf(cpfInvalido));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",            // válido apenas dígitos
            "529.982.247-25",          // válido formatado
    })
    @DisplayName("Deve instanciar com sucesso quando o CPF for válido e higienizar a pontuação")
    void shouldCreateValidCpf(String cpfValido) {
        Cpf cpfObj = new Cpf(cpfValido);
        assertEquals("52998224725", cpfObj.valor());
    }

    @Test
    @DisplayName("Dois CPFs com mesmo valor devem ser iguais por valor (Value Object)")
    void deveGarantirIgualdadePorValor() {
        Cpf cpf1 = new Cpf("529.982.247-25");
        Cpf cpf2 = new Cpf("52998224725");

        assertEquals(cpf1, cpf2);
        assertEquals(cpf1.hashCode(), cpf2.hashCode());
    }

}