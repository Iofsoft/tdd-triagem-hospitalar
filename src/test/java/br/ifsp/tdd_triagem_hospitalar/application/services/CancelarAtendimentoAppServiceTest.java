package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("TDD")
class CancelarAtendimentoAppServiceTest {

    @Mock
    private AtendimentoRepository repositoryMock;

    @InjectMocks
    private CancelarAtendimentoAppService service;

    @Test
    void deveCancelarAtendimentoEmAndamentoComJustificativaValida(){
        var idAtendimento = UUID.randomUUID();
        var atendimento = new Atendimento(
                idAtendimento,
                new Cpf("52998224725"),
                StatusAtendimento.EM_CONSULTA,
                ClassificacaoRisco.VERDE,
                List.of()
        );

        when(repositoryMock.buscarPorId(idAtendimento)).thenReturn(Optional.of(atendimento));

        String motivo = "Desistência do paciente";
        service.cancelarAtendimento(idAtendimento, motivo);
        assertThat(atendimento.getStatus()).isEqualTo(StatusAtendimento.CANCELADO);
        assertThat(atendimento.getMotivoCancelamento()).isEqualTo(motivo);
        verify(repositoryMock).salvar(atendimento);
    }

    @Test
    void naoDeveCancelarAtendimentoJaFinalizado() {
        var idAtendimento = UUID.randomUUID();
        var atendimento = new Atendimento(
                idAtendimento,
                new Cpf("52998224725"),
                StatusAtendimento.FINALIZADO,
                ClassificacaoRisco.VERDE,
                List.of()
        );

        when(repositoryMock.buscarPorId(idAtendimento)).thenReturn(Optional.of(atendimento));

        assertThatIllegalStateException().isThrownBy(() -> {
            service.cancelarAtendimento(idAtendimento, "Desistência");
        });

        verify(repositoryMock, never()).salvar(any(Atendimento.class));
    }
}