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
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


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
    MedicaoSinaisVitais firstMeasurement;
    MedicaoSinaisVitais secondMeasurement;
    Atendimento initialAttendance;
    Atendimento updatedAttendance;
    UUID attendanceId;

    @BeforeEach
    void setUp() {
        clearDatabase();
        attendanceId = UUID.randomUUID();

        firstMeasurement = new MedicaoSinaisVitais(UUID.randomUUID(), 36.5, 80, LocalDateTime.of(2026, 1, 10, 9, 0));
        secondMeasurement = new MedicaoSinaisVitais(UUID.randomUUID(), 37.2, 92, LocalDateTime.of(2026, 1, 10, 9, 30));

        initialAttendance = new Atendimento(attendanceId, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
        updatedAttendance = new Atendimento(attendanceId, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of());

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

            sut.salvar(initialAttendance);

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
            sut.salvar(initialAttendance);
            sut.salvar(updatedAttendance);

            Optional<Atendimento> result = sut.buscarPorId(id);

            assertThat(result).isPresent();
            Atendimento savedAttendance = result.get();

            assertThat(savedAttendance.getId()).isEqualTo(id);
            assertThat(savedAttendance.getStatus()).isEqualTo(StatusAtendimento.AGUARDANDO_CONSULTA);
        }
    }

    @Nested
    @DisplayName("Find by ID tests")
    class FindByIdTests {

        @Test
        @DisplayName("Should find the correct attendance by id")
        void shouldFindCorrectAttendance() {
            sut.salvar(initialAttendance);
            var result = sut.buscarPorId(attendanceId);

            assertThat(result).isPresent();
            Atendimento attendance = result.orElseThrow();
            assertThat(attendance.getId()).isEqualTo(attendanceId);
        }

        @Test
        @DisplayName("Should return empty when attendance does not exist")
        void shouldReturnEmptyWhenAttendanceDoesNotExist() {
            Optional<Atendimento> result = sut.buscarPorId(UUID.randomUUID());

            assertThat(result).isEmpty();
        }


    }
}
