package br.ifsp.tdd_triagem_hospitalar.application.services;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Tag("UnitTest")
@Tag("TDD")
@ExtendWith(MockitoExtension.class)
class BuscarAtendimentoAppServiceTest {

    @Mock AtendimentoRepository atendimentoRepositoryMock;
    @InjectMocks BuscarAtendimentoAppService sut;

    @Test
    @DisplayName("Deve buscar atendimento pelo id")
    void deveBuscarAtendimentoPeloId() {
        final Atendimento atendimento = new Atendimento(UUID.randomUUID(), new Cpf("52998224725"), StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
        when(atendimentoRepositoryMock.buscarPorId(atendimento.getId())).thenReturn(Optional.of(atendimento));

        final Atendimento obtido = sut.buscarAtendimento(atendimento.getId());

        assertThat(obtido).isEqualTo(atendimento);
    }
}
