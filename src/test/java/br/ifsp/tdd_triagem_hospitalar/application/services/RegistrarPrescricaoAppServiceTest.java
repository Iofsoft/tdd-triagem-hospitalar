package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
@Tag("TDD")
class RegistrarPrescricaoAppServiceTest {

    @Mock
    private AtendimentoRepository repositoryMock;

    //service a ser testado com um repositorio falso
    @InjectMocks
    private RegistrarPrescricaoAppService service;

    @Test
    void deveRegistrarPrescricaoValidaDuranteConsulta(){
        //UUID id, Cpf cpf, StatusAtendimento status, ClassificacaoRisco classificacaoRisco, List<MedicaoSinaisVitais> medicoes
        var idAtendimento = UUID.randomUUID();
        var atendimento = new Atendimento(
                idAtendimento,
                new Cpf("52998224725"),
                StatusAtendimento.EM_CONSULTA,
                ClassificacaoRisco.VERDE,
                null);

        //qnd o service procurar por este ID, devolv o atendimento criado acima
        when(repositoryMock.buscarPorId(idAtendimento)).thenReturn(Optional.of(atendimento));


        String conduta = "conduta Generica";
        service.registrarPrescricao(idAtendimento, conduta);

        assertThat(atendimento.getPrescricao()).isEqualTo(conduta);

        verify(repositoryMock).salvar(atendimento);
    }

    @Test
    void naoDeveRegistrarPrescricaoForaDeConsulta(){
        var idAtendimento = UUID.randomUUID();
        var atendimento = new Atendimento(
                idAtendimento,
                new Cpf("52998224725"),
                StatusAtendimento.AGUARDANDO_CONSULTA,
                ClassificacaoRisco.VERDE,
                null);

        String conduta = "conduta genérica";

        when(repositoryMock.buscarPorId(idAtendimento)).thenReturn(Optional.of(atendimento));

        assertThatIllegalStateException().isThrownBy(()->
                service.registrarPrescricao(idAtendimento,conduta));

        verify(repositoryMock, never()).salvar(any(Atendimento.class));

    }
}