package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        UUID atendimentoId = UUID.randomUUID();
        double temperaturaInvalida = 0.0;
        int frequenciaCardiaca = 80;

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MedicaoSinaisVitais(UUID.randomUUID(), temperaturaInvalida, frequenciaCardiaca, LocalDateTime.now()));

        verify(atendimentoRepository, never()).salvar(any());
    }


    @Test
    @DisplayName("Deve falhar ao criar medição com frequência cardíaca fora dos limites fisiológicos")
    void deveFalharParaFrequenciaCardiacaForaDosLimites() {
        UUID atendimentoId = UUID.randomUUID();
        double temperatura = 36.5;
        int frequenciaCardiacaInvalida = -10;

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MedicaoSinaisVitais(UUID.randomUUID(), temperatura, frequenciaCardiacaInvalida, LocalDateTime.now()));

        verify(atendimentoRepository, never()).salvar(any());
    }
}