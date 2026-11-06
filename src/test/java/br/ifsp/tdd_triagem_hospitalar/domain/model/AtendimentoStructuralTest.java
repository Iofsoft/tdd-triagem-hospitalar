package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Tag("UnitTest")
@Tag("Structural")
class AtendimentoStructuralTest {
    private final UUID id = UUID.randomUUID();
    private final Cpf cpf = new Cpf("52998224725");

    @Test
    @DisplayName("Deve ser igual a ele mesmo")
    void deveSerIgualAEleMesmo() {
        final Atendimento sut = new Atendimento(id, cpf, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());

        assertThat(sut.equals(sut)).isTrue();
    }

    @Test
    @DisplayName("Não deve ser igual a nulo")
    void naoDeveSerIgualANulo() {
        final Atendimento sut = new Atendimento(id, cpf, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());

        assertThat(sut.equals(null)).isFalse();
    }

    @Test
    @DisplayName("Não deve ser igual a um objeto de outra classe")
    void naoDeveSerIgualAUmObjetoDeOutraClasse() {
        final Atendimento sut = new Atendimento(id, cpf, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());

        assertThat(sut.equals(cpf)).isFalse();
    }

    @Test
    @DisplayName("Deve ser igual a outro atendimento com o mesmo id")
    void deveSerIgualAOutroAtendimentoComOMesmoId() {
        final Atendimento sut = new Atendimento(id, cpf, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
        final Atendimento outro = new Atendimento(id, cpf, StatusAtendimento.CANCELADO, null, List.of());

        assertThat(sut.equals(outro)).isTrue();
    }

    @Test
    @DisplayName("Não deve ser igual a atendimento com id diferente")
    void naoDeveSerIgualAAtendimentoComIdDiferente() {
        final Atendimento sut = new Atendimento(id, cpf, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
        final Atendimento outro = new Atendimento(UUID.randomUUID(), cpf, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());

        assertThat(sut.equals(outro)).isFalse();
    }
}
