package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Tag("UnitTest")
@Tag("TDD")
class AtendimentoTest {
    private final UUID id = UUID.randomUUID();
    private final Cpf cpf = new Cpf("52998224725");

    @Test
    @DisplayName("Deve criar atendimento aguardando triagem")
    void deveCriarAtendimentoAguardandoTriagem() {
        final Atendimento sut = new Atendimento(id, cpf, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());

        assertThat(sut.getStatus()).isEqualTo(StatusAtendimento.AGUARDANDO_TRIAGEM);
        assertThat(sut.getMedicoes()).isEmpty();
    }

    @Test
    @DisplayName("Deve lançar exceção quando o id for nulo")
    void deveLancarExcecaoQuandoIdForNulo() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Atendimento(null, cpf, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o CPF for nulo")
    void deveLancarExcecaoQuandoCpfForNulo() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Atendimento(id, null, StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of()));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o status for nulo")
    void deveLancarExcecaoQuandoStatusForNulo() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Atendimento(id, cpf, null, null, List.of()));
    }

    @ParameterizedTest
    @EnumSource(value = StatusAtendimento.class, names = {"AGUARDANDO_CONSULTA", "EM_CONSULTA", "FINALIZADO"})
    @DisplayName("Deve exigir classificação de risco após a triagem")
    void deveExigirClassificacaoDeRiscoAposTriagem(StatusAtendimento status) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Atendimento(id, cpf, status, null, List.of()));
    }
}
