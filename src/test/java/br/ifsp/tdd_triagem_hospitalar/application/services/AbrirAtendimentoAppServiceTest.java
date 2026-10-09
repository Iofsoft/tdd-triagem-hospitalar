package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
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
}