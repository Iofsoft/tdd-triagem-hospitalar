package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@Tag("UnitTest")
@Tag("Functional")
class AtendimentoFunctionalTest {
    private final UUID id = UUID.randomUUID();
    private final Cpf cpf = new Cpf("52998224725");

    @Test
    @DisplayName("Deve iniciar consulta quando o status for aguardando consulta")
    void deveIniciarConsultaQuandoStatusForAguardandoConsulta() {
        final Atendimento sut = new Atendimento(id, cpf, StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of());

        sut.iniciarConsulta();

        assertThat(sut.getStatus()).isEqualTo(StatusAtendimento.EM_CONSULTA);
    }

    @ParameterizedTest
    @EnumSource(value = StatusAtendimento.class, mode = EnumSource.Mode.EXCLUDE, names = {"AGUARDANDO_CONSULTA"})
    @DisplayName("Não deve iniciar consulta quando o status não for aguardando consulta")
    void naoDeveIniciarConsultaQuandoStatusNaoForAguardandoConsulta(StatusAtendimento status) {
        final Atendimento sut = new Atendimento(id, cpf, status, ClassificacaoRisco.VERDE, List.of());

        assertThatIllegalStateException().isThrownBy(sut::iniciarConsulta);
    }

    @ParameterizedTest
    @EnumSource(value = StatusAtendimento.class, names = {"AGUARDANDO_TRIAGEM", "AGUARDANDO_CONSULTA", "EM_CONSULTA"})
    @DisplayName("Deve registrar medição sem alterar o status quando o atendimento estiver em aberto")
    void deveRegistrarMedicaoQuandoAtendimentoEstiverEmAberto(StatusAtendimento status) {
        final Atendimento sut = new Atendimento(id, cpf, status, ClassificacaoRisco.VERDE, List.of());
        final MedicaoSinaisVitais medicao = new MedicaoSinaisVitais(UUID.randomUUID(), 37.5, 90, LocalDateTime.now());

        sut.registrarMedicao(medicao);

        assertThat(sut.getMedicoes()).containsExactly(medicao);
        assertThat(sut.getStatus()).isEqualTo(status);
    }

    @ParameterizedTest
    @EnumSource(value = StatusAtendimento.class, names = {"FINALIZADO", "CANCELADO"})
    @DisplayName("Não deve registrar medição quando o atendimento estiver encerrado")
    void naoDeveRegistrarMedicaoQuandoAtendimentoEstiverEncerrado(StatusAtendimento status) {
        final Atendimento sut = new Atendimento(id, cpf, status, ClassificacaoRisco.VERDE, List.of());
        final MedicaoSinaisVitais medicao = new MedicaoSinaisVitais(UUID.randomUUID(), 37.5, 90, LocalDateTime.now());

        assertThatIllegalStateException().isThrownBy(() -> sut.registrarMedicao(medicao));
    }
}
