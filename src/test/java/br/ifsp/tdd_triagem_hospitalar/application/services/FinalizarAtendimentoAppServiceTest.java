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

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
@ExtendWith(MockitoExtension.class)
class FinalizarAtendimentoAppServiceTest {

    @Mock
    private AtendimentoRepository repositoryMock;

    @InjectMocks
    private FinalizarAtendimentoAppService service;

    @Test
    void deveFinalizarAtendimentoComPrescricaoPrevia(){
        var idAtendimento = UUID.randomUUID();
        var atendimento = new Atendimento(
                idAtendimento,
                new Cpf("52998224725"),
                StatusAtendimento.EM_CONSULTA,
                ClassificacaoRisco.VERDE,
                List.of()
        );
        String prescricao = "prescricao generica";
        atendimento.registrarPrescricao(prescricao);

        when(repositoryMock.buscarPorId(idAtendimento)).thenReturn(Optional.of(atendimento));

        service.finalizarAtendimento(idAtendimento);

        assertThat(atendimento.getStatus()).isEqualTo(StatusAtendimento.FINALIZADO);

        verify(repositoryMock).salvar(atendimento);
    }

    @Test
    void naoDeveFinalizarAtendimentoComPrescricaoPrevia() {
        var idAtendimento = UUID.randomUUID();
        var atendimento = new Atendimento(
                idAtendimento,
                new Cpf("52998224725"),
                StatusAtendimento.EM_CONSULTA,
                ClassificacaoRisco.VERDE,
                List.of()
        );

        when(repositoryMock.buscarPorId(idAtendimento)).thenReturn(Optional.of(atendimento));
        assertThatIllegalStateException().isThrownBy(()-> {
            service.finalizarAtendimento(idAtendimento);
        });
        verify(repositoryMock, never()).salvar(any(Atendimento.class));
    }

    @Test
    void naoDeveFinalizarAtendimentoForaDeConsulta() {
        var idAtendimento = UUID.randomUUID();
        var atendimento = new Atendimento(
                idAtendimento,
                new Cpf("52998224725"),
                StatusAtendimento.AGUARDANDO_CONSULTA,
                ClassificacaoRisco.VERDE,
                List.of()
        );
        when(repositoryMock.buscarPorId(idAtendimento)).thenReturn(Optional.of(atendimento));
        assertThatIllegalStateException().isThrownBy(()->{
            service.finalizarAtendimento(idAtendimento);
        });

        verify(repositoryMock, never()).salvar(any(Atendimento.class));

    }
}