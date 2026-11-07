package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("UnitTest")
@Tag("Mutation")
class MedicaoSinaisVitaisMutationTest {

    @Test
    @DisplayName("Deve garantir que o hashCode é calculado com base no ID e não retorna 0 padrão")
    void deveGarantirCalculoDeHashCodePorId() {
        final UUID id = UUID.randomUUID();
        final MedicaoSinaisVitais medicao = new MedicaoSinaisVitais(id, 36.5, 80, LocalDateTime.now());

        // Mata o mutante PrimitiveReturnsMutator (replaced int return with 0)
        final int expectedHashCode = Objects.hash(id);
        assertThat(medicao.hashCode())
                .isEqualTo(expectedHashCode)
                .isNotZero();
    }
}
