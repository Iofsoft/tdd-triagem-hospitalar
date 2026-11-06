package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Tag("UnitTest")
@Tag("Mutation")
class AtendimentoMutationTest {

    @Test
    @DisplayName("Deve gerar o hashCode a partir do id")
    void deveGerarOHashCodeAPartirDoId() {
        final UUID id = UUID.randomUUID();
        final Atendimento sut = new Atendimento(id, new Cpf("52998224725"), StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());

        assertThat(sut.hashCode()).isEqualTo(Objects.hash(id));
    }
}
