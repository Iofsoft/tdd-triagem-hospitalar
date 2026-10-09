package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
@ExtendWith(MockitoExtension.class)
class IniciarConsultaAppServiceTest {

    @Mock AtendimentoRepository atendimentoRepositoryMock;
    @InjectMocks IniciarConsultaAppService sut;

    @Test
    @DisplayName("Deve iniciar consulta quando o atendimento estiver aguardando consulta")
    void deveIniciarConsultaQuandoAtendimentoEstiverAguardandoConsulta() {
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of());
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        sut.iniciarConsulta(atendimento.getId());

        assertThat(atendimento.getStatus()).isEqualTo(StatusAtendimento.EM_CONSULTA);
        verify(atendimentoRepositoryMock, times(1)).salvar(atendimento);
    }

    @Test
    @DisplayName("Não deve iniciar consulta antes da triagem")
    void naoDeveIniciarConsultaAntesDaTriagem() {
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        assertThatIllegalStateException().isThrownBy(() -> sut.iniciarConsulta(atendimento.getId()));
    }

    @Test
    @DisplayName("Não deve iniciar consulta já em andamento")
    void naoDeveIniciarConsultaJaEmAndamento() {
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.EM_CONSULTA, ClassificacaoRisco.VERDE, List.of());
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        assertThatIllegalStateException().isThrownBy(() -> sut.iniciarConsulta(atendimento.getId()));
    }

    @Test
    @DisplayName("Não deve iniciar consulta de atendimento cancelado")
    void naoDeveIniciarConsultaDeAtendimentoCancelado() {
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.CANCELADO, null, List.of());
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        assertThatIllegalStateException().isThrownBy(() -> sut.iniciarConsulta(atendimento.getId()));
        assertThat(atendimento.getStatus()).isEqualTo(StatusAtendimento.CANCELADO);
    }
}
