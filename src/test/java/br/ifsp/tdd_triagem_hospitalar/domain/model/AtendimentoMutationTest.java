package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UnitTest")
@Tag("Mutation")
class AtendimentoMutationTest {

    @Test
    @DisplayName("Deve garantir que o hashCode é calculado com base no ID e não retorna 0 padrão")
    void deveGarantirCalculoDeHashCodePorId() {
        final UUID id = UUID.randomUUID();
        final Cpf cpf = new Cpf("52998224725");
        final Atendimento atendimento = new Atendimento(id, cpf, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());

        final int expectedHashCode = Objects.hash(id);
        assertThat(atendimento.hashCode())
                .isEqualTo(expectedHashCode)
                .isNotZero();
    }
}
