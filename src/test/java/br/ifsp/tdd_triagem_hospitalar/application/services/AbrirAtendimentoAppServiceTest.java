package br.ifsp.tdd_triagem_hospitalar.application.services;

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
import static org.mockito.Mockito.verifyNoInteractions;

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
}