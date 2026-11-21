package br.ifsp.tdd_triagem_hospitalar.infrastructure.repository;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;


@Tag("PersistenceTest")
@Tag("IntegrationTest")
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:sqlite:target/jdbc-atendimento-integration-test.db",
        "spring.jpa.hibernate.ddl-auto=none",
        "spring.sql.init.mode=always"
    }
)
class JdbcAtendimentoRepositoryTest {

    private static final String CPF_1 = "52998224725";

    @Autowired
    private JdbcAtendimentoRepository sut;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        clearDatabase();
    }
    @AfterEach
    void tearDown() {
        clearDatabase();
    }
    private void clearDatabase() {
        jdbcTemplate.update("DELETE FROM medicao");
        jdbcTemplate.update("DELETE FROM atendimento");
    }

    @Nested
    @DisplayName("Saving attendance tests")
    class SaveTests {
        @Test
        @DisplayName("Should save attendance with all measurements")
        void shouldSaveAttendanceWithAllMeasurements() {
            UUID attendanceId = UUID.randomUUID();

            MedicaoSinaisVitais firstMeasurement = new MedicaoSinaisVitais(UUID.randomUUID(), 36.5, 80, LocalDateTime.of(2026, 1, 10, 9, 0));
            MedicaoSinaisVitais secondMeasurement = new MedicaoSinaisVitais(UUID.randomUUID(), 37.2, 92, LocalDateTime.of(2026, 1, 10, 9, 30));

            Atendimento attendance = new Atendimento(attendanceId, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.AMARELO, List.of(firstMeasurement, secondMeasurement));

            sut.salvar(attendance);

            Optional<Atendimento> result = sut.buscarPorId(attendanceId);

            assertThat(result).isPresent();
            Atendimento savedAttendance = result.get();

            assertThat(savedAttendance.getMedicoes()).hasSize(2);
            assertThat(savedAttendance.getMedicoes()).containsExactly(firstMeasurement, secondMeasurement);
        }

        @Test
        @DisplayName("Should save attendance that doesnt has measurements")
        void shouldSaveAttendanceThatDoesntHasMeasurements() {
            UUID id = UUID.randomUUID();
            Atendimento attendance = new Atendimento(id, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
            sut.salvar(attendance);

            Optional<Atendimento> result = sut.buscarPorId(id);
            assertThat(result).isPresent();
            Atendimento savedAttendance = result.get();

            assertThat(savedAttendance.getId()).isEqualTo(id);
            assertThat(savedAttendance.getMedicoes()).hasSize(0);
        }

        @Test
        @DisplayName("Should replace attendance without duplicating")
        void shouldReplaceAttendanceWithoutDuplicating() {
            UUID id = UUID.randomUUID();
            Atendimento initialAttendance = new Atendimento(id, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
            Atendimento updatedAttendance = new Atendimento(id, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of());

            sut.salvar(initialAttendance);
            sut.salvar(updatedAttendance);

            Optional<Atendimento> result = sut.buscarPorId(id);

            assertThat(result).isPresent();
            Atendimento savedAttendance = result.get();

            assertThat(savedAttendance.getId()).isEqualTo(id);
            assertThat(savedAttendance.getStatus()).isEqualTo(StatusAtendimento.AGUARDANDO_CONSULTA);
            assertThat(savedAttendance.getClassificacaoRisco()).isEqualTo(ClassificacaoRisco.VERDE);
        }
    }
}
