package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
@ExtendWith(MockitoExtension.class)
class ListarAtendimentosAppServiceTest {

    @Mock AtendimentoRepository atendimentoRepositoryMock;
    @InjectMocks ListarAtendimentosAppService sut;

    @Test
    @DisplayName("Deve listar todos os atendimentos")
    void deveListarTodosOsAtendimentos() {
        final Atendimento primeiro = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
        final Atendimento segundo = new Atendimento(UUID.randomUUID(), new Cpf("11144477735"), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of());
        when(atendimentoRepositoryMock.buscarTodos()).thenReturn(List.of(primeiro, segundo));

        final List<Atendimento> obtido = sut.listarAtendimentos();

        assertThat(obtido).containsExactly(primeiro, segundo);
    }
}
