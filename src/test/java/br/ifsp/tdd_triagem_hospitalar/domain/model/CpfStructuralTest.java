package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UnitTest")
@Tag("Structural")
class CpfStructuralTest {

    @Test
    @DisplayName("Deve formatar corretamente a saída do método toString")
    void deveFormatarCorretamenteNoToString() {
        final Cpf cpf = new Cpf("52998224725");

        assertThat(cpf.toString()).isEqualTo("529.982.247-25");
    }
}
