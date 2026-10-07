package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
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
class ReclassificarRiscoAppServiceTest {

    @Mock AtendimentoRepository atendimentoRepositoryMock;
    @InjectMocks ReclassificarRiscoAppService sut;

    @Test
    @DisplayName("Should reclassify risk from green to red with justification")
    void shouldReclassifyRiskFromGreenToRedWithJustification() {
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of());
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        sut.reclassificarRisco(atendimento.getId(), ClassificacaoRisco.VERMELHO, "Paciente com piora na saturação");

        assertThat(atendimento.getClassificacaoRisco()).isEqualTo(ClassificacaoRisco.VERMELHO);
        verify(atendimentoRepositoryMock, times(1)).salvar(atendimento);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should not reclassify risk without justification")
    void shouldNotReclassifyRiskWithoutJustification(String justificativa) {
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of());
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        assertThatIllegalArgumentException().isThrownBy(() -> sut.reclassificarRisco(atendimento.getId(), ClassificacaoRisco.VERMELHO, justificativa));
    }
}
