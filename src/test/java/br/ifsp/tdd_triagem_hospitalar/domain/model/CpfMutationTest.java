package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UnitTest")
@Tag("Mutation")
class CpfMutationTest {

    @Test
    @DisplayName("Deve validar CPF cujo cálculo do resto resulte exatamente em 10 (mata mutante de fronteira resto > 10)")
    void deveValidarCpfComRestoExatamenteDez() {
        // No CPF "01234567890", o cálculo do 2º dígito resulta em soma % 11 == 1 => resto == 10.
        // O código original (resto >= 10) retorna 0, aceitando o CPF.
        // O mutante (resto > 10) retorna 10, fazendo a validação falhar e matando o mutante.
        final Cpf cpf = new Cpf("01234567890");

        assertThat(cpf.valor()).isEqualTo("01234567890");
    }
}
