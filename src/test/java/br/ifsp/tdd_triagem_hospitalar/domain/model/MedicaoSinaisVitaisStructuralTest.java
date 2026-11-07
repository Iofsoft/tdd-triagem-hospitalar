package br.ifsp.tdd_triagem_hospitalar.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

@Tag("UnitTest")
@Tag("Structural")
class MedicaoSinaisVitaisStructuralTest {

    private final UUID id = UUID.randomUUID();
    private final LocalDateTime agora = LocalDateTime.now();

    @Test
    @DisplayName("Deve rejeitar data e hora de medição no futuro além da tolerância")
    void deveRejeitarDataHoraNoFuturo() {
        final LocalDateTime dataFutura = LocalDateTime.now().plusDays(1);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> new MedicaoSinaisVitais(id, 36.5, 80, dataFutura))
                .withMessage("A data e hora da medição não pode ser no futuro.");
    }
}
