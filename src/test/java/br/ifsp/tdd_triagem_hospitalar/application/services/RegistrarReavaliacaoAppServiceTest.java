package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
@ExtendWith(MockitoExtension.class)
class RegistrarReavaliacaoAppServiceTest {

    @Mock AtendimentoRepository atendimentoRepositoryMock;
    @InjectMocks RegistrarReavaliacaoAppService sut;

    @Test
    @DisplayName("Deve adicionar medição quando o atendimento estiver aguardando consulta")
    void deveAdicionarMedicaoQuandoAtendimentoEstiverAguardandoConsulta() {
        final MedicaoSinaisVitais medicaoAnterior = new MedicaoSinaisVitais(UUID.randomUUID(), 36.5, 80, LocalDateTime.now().minusHours(1));
        final MedicaoSinaisVitais novaMedicao = new MedicaoSinaisVitais(UUID.randomUUID(), 37.8, 95, LocalDateTime.now());
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of(medicaoAnterior));
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        sut.registrarReavaliacao(atendimento.getId(), novaMedicao);

        assertThat(atendimento.getMedicoes()).containsExactly(medicaoAnterior, novaMedicao);
        verify(atendimentoRepositoryMock, times(1)).salvar(atendimento);
    }

    @Test
    @DisplayName("Deve adicionar medição e manter o status quando o atendimento estiver em consulta")
    void deveAdicionarMedicaoEManterStatusQuandoAtendimentoEstiverEmConsulta() {
        final MedicaoSinaisVitais novaMedicao = new MedicaoSinaisVitais(UUID.randomUUID(), 38.2, 110, LocalDateTime.now());
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.EM_CONSULTA, ClassificacaoRisco.AMARELO, List.of());
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        sut.registrarReavaliacao(atendimento.getId(), novaMedicao);

        assertThat(atendimento.getMedicoes()).containsExactly(novaMedicao);
        assertThat(atendimento.getStatus()).isEqualTo(StatusAtendimento.EM_CONSULTA);
    }

    @Test
    @DisplayName("Não deve adicionar medição quando o atendimento estiver finalizado")
    void naoDeveAdicionarMedicaoQuandoAtendimentoEstiverFinalizado() {
        final MedicaoSinaisVitais novaMedicao = new MedicaoSinaisVitais(UUID.randomUUID(), 36.8, 85, LocalDateTime.now());
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.FINALIZADO, ClassificacaoRisco.VERDE, List.of());
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        assertThatIllegalStateException().isThrownBy(() -> sut.registrarReavaliacao(atendimento.getId(), novaMedicao));
    }
}
