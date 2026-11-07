package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Tag("UnitTest")
class RegistrarPrescricaoAppServiceTest {

    @Mock
    private AtendimentoRepository repositoryMock;


    @InjectMocks
    private RegistrarPrescricaoAppService service;

    @Tag("TDD")
    @Nested
    @DisplayName("Testes Unitários")
    class TestesUnitarios{

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

        @Test
        void naoDeveRegistrarPrescricaoComTextoVazio(){
            var idAtendimento = UUID.randomUUID();
            var atendimento = new Atendimento(
                    idAtendimento,
                    new Cpf("52998224725"),
                    StatusAtendimento.EM_CONSULTA,
                    ClassificacaoRisco.VERDE,
                    null);

            when(repositoryMock.buscarPorId(idAtendimento)).thenReturn(Optional.of(atendimento));

            assertThatIllegalArgumentException().isThrownBy(() ->{
                service.registrarPrescricao(idAtendimento, "");
            });

            assertThatIllegalArgumentException().isThrownBy(() ->{
                service.registrarPrescricao(idAtendimento, " ");
            });

            assertThatIllegalArgumentException().isThrownBy(() ->{
                service.registrarPrescricao(idAtendimento, null);
            });

            verify(repositoryMock, never()).salvar(any(Atendimento.class));
        }
    }

    @Tag("Mutation")
    @Nested
    @DisplayName("Testes Mutantes")
    class TestesMutantes{

        @Test
        @DisplayName("Deve retornar IllegalArgumentException quando id de atendimento não for encontrado")
        void deveRetornarIllegalArgumentExceptionQuandoAtendimentoNaoForEncontrado(){
            var idQueNaoExiste = UUID.randomUUID();

            when(repositoryMock.buscarPorId(idQueNaoExiste)).thenReturn(Optional.empty());
            assertThatIllegalArgumentException().isThrownBy(()->{
                service.registrarPrescricao(idQueNaoExiste, "conduta generica");
            }).withMessage("Atendimento não encontrado");

            verify(repositoryMock, never()).salvar(any());

        }
    }
}