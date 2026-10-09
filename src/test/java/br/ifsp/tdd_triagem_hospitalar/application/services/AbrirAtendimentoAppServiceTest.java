package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
@ExtendWith(MockitoExtension.class)
class AbrirAtendimentoAppServiceTest {

    @Mock
    private AtendimentoRepository atendimentoRepository;

    @InjectMocks
    private AbrirAtendimentoAppService sut;

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    @DisplayName("Deve lançar IllegalArgumentException quando o CPF não for informado ou for vazio")
    void naoDeveAbrirAtendimentoComCpfVazioOuNulo(String cpfInvalido) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> sut.abrirAtendimento(cpfInvalido));

        verifyNoInteractions(atendimentoRepository);
    }
    // Fiquei com dúvida, esse teste me fez ficar pensando se ele tem utilidade, porque a classe cpf já foi testada né

    @Test
    @DisplayName("Não deve abrir atendimento se o paciente já possui atendimento ativo")
    void naoDeveAbrirAtendimentoSeJaPossuiAtendimentoAtivo() {
        String cpfValido = "52998224725";
        when(atendimentoRepository.existeAtendimentoAtivoPorCpf(any(Cpf.class))).thenReturn(true);

        assertThatIllegalStateException()
                .isThrownBy(() -> sut.abrirAtendimento(cpfValido))
                .withMessage("Paciente já possui atendimento ativo.");

        verify(atendimentoRepository, never()).salvar(any(Atendimento.class));
    }

    @Test
    @DisplayName("Deve abrir atendimento com sucesso quando dados forem válidos e paciente não tiver atendimento ativo")
    void deveAbrirAtendimentoComSucesso() {
        String cpfValido = "52998224725";
        when(atendimentoRepository.existeAtendimentoAtivoPorCpf(any(Cpf.class))).thenReturn(false);

        UUID idGerado = sut.abrirAtendimento(cpfValido);

        assertThat(idGerado).as("id retornado por abrirAtendimento").isNotNull();

        ArgumentCaptor<Atendimento> captor = ArgumentCaptor.forClass(Atendimento.class);
        verify(atendimentoRepository, times(1)).salvar(captor.capture());

        Atendimento atendimentoSalvo = captor.getValue();

        assertSoftly(softly -> {
            softly.assertThat(atendimentoSalvo.getId())
                    .as("id do atendimento salvo")
                    .isEqualTo(idGerado);

            softly.assertThat(atendimentoSalvo.getCpf())
                    .as("CPF do atendimento salvo")
                    .isEqualTo(new Cpf(cpfValido));

            softly.assertThat(atendimentoSalvo.getStatus())
                    .as("status inicial do atendimento")
                    .isEqualTo(StatusAtendimento.AGUARDANDO_TRIAGEM);

            softly.assertThat(atendimentoSalvo.getClassificacaoRisco())
                    .as("classificação de risco (não deve existir antes da triagem)")
                    .isNull();

            softly.assertThat(atendimentoSalvo.getMedicoes())
                    .as("medições (devem estar vazias antes da triagem)")
                    .isEmpty();
        });
    }
}