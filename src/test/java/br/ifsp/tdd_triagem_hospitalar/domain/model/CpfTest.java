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
            "123"
    })
    @DisplayName("Deve lançar exceção para formatos e valores de cpf inválidos")
    void shouldThrowExceptionForInvalidCpf(String cpfInvalido) {
        assertThrows(IllegalArgumentException.class, () -> new Cpf(cpfInvalido));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "52998224725",            // válido apenas dígitos
            "529.982.247-25"          // válido formatado
    })
    void shouldCreateValidCpf(String cpfValido) {
        Cpf cpfObj = new Cpf(cpfValido);
        assertEquals("52998224725", cpfObj.valor());
    }

}