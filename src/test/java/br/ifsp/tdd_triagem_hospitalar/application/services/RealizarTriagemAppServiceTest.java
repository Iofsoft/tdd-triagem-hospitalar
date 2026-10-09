package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
@ExtendWith(MockitoExtension.class)
class RealizarTriagemAppServiceTest {

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @InjectMocks
    private RealizarTriagemAppService sut;

    @Test
    @DisplayName("Deve falhar ao tentar realizar triagem para atendimento inexistente")
    void deveFalharParaAtendimentoInexistente() {
        UUID idInexistente = UUID.randomUUID();
        MedicaoSinaisVitais medicao = new MedicaoSinaisVitais(UUID.randomUUID(), 36.5, 80, LocalDateTime.now());
        ClassificacaoRisco risco = ClassificacaoRisco.VERDE;

        when(atendimentoRepository.buscarPorId(idInexistente)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sut.realizarTriagem(idInexistente, medicao, risco))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Atendimento não encontrado");

        verify(atendimentoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve falhar ao criar medição com temperatura fora dos limites fisiológicos")
    void deveFalharParaTemperaturaForaDosLimites() {
        double temperaturaInvalida = 0.0;
        int frequenciaCardiaca = 80;

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MedicaoSinaisVitais(UUID.randomUUID(), temperaturaInvalida, frequenciaCardiaca, LocalDateTime.now()));

        verify(atendimentoRepository, never()).salvar(any());
    }


    @Test
    @DisplayName("Deve falhar ao criar medição com frequência cardíaca fora dos limites fisiológicos")
    void deveFalharParaFrequenciaCardiacaForaDosLimites() {
        double temperatura = 36.5;
        int frequenciaCardiacaInvalida = -10;

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MedicaoSinaisVitais(UUID.randomUUID(), temperatura, frequenciaCardiacaInvalida, LocalDateTime.now()));

        verify(atendimentoRepository, never()).salvar(any());
    }

    @Test
    @DisplayName("Deve falhar ao tentar realizar triagem sem classificação de risco")
    void deveFalharQuandoClassificacaoRiscoForNula() {
        UUID atendimentoId = UUID.randomUUID();
        Atendimento atendimento = new Atendimento(
                atendimentoId,
                new Cpf("52998224725"),
                StatusAtendimento.AGUARDANDO_TRIAGEM,
                null,
                List.of()
        );
        MedicaoSinaisVitais medicao = new MedicaoSinaisVitais(UUID.randomUUID(), 36.5, 80, LocalDateTime.now());

        when(atendimentoRepository.buscarPorId(atendimentoId)).thenReturn(Optional.of(atendimento));

        assertThatIllegalArgumentException()
                .isThrownBy(() -> sut.realizarTriagem(atendimentoId, medicao, null))
                .withMessage("A classificação de risco é obrigatória para a triagem.");

        verify(atendimentoRepository, never()).salvar(any());
    }

    @ParameterizedTest
    @EnumSource(value = StatusAtendimento.class, mode = EnumSource.Mode.EXCLUDE, names = {"AGUARDANDO_TRIAGEM"})
    @DisplayName("Deve falhar ao tentar realizar triagem em atendimento com estado diferente de AguardandoTriagem")
    void deveFalharParaEstadoIncorreto(StatusAtendimento statusInvalido) {
        UUID atendimentoId = UUID.randomUUID();
        // Para status triados, Atendimento exige classificação de risco no construtor
        ClassificacaoRisco riscoInicial = (statusInvalido == StatusAtendimento.CANCELADO) ? null : ClassificacaoRisco.VERDE;
        Atendimento atendimento = new Atendimento(
                atendimentoId,
                new Cpf("52998224725"),
                statusInvalido,
                riscoInicial,
                List.of()
        );

        MedicaoSinaisVitais medicao = new MedicaoSinaisVitais(UUID.randomUUID(), 36.5, 80, LocalDateTime.now());
        ClassificacaoRisco novoRisco = ClassificacaoRisco.AMARELO;

        when(atendimentoRepository.buscarPorId(atendimentoId)).thenReturn(Optional.of(atendimento));

        assertThatIllegalStateException()
                .isThrownBy(() -> sut.realizarTriagem(atendimentoId, medicao, novoRisco))
                .withMessage("Triagem só pode ser realizada quando o atendimento está aguardando triagem.");

        verify(atendimentoRepository, never()).salvar(any());
    }
}