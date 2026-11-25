package br.ifsp.tdd_triagem_hospitalar.infrastructure.repository;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


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
    private MedicaoSinaisVitais firstMeasurement;
    private MedicaoSinaisVitais secondMeasurement;
    private Atendimento initialAttendance;
    private Atendimento updatedAttendance;
    private UUID attendanceId;

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
            Atendimento attendance = new Atendimento(attendanceId, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of(firstMeasurement, secondMeasurement));
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
            sut.salvar(initialAttendance);
            sut.salvar(updatedAttendance);

            Optional<Atendimento> result = sut.buscarPorId(attendanceId);
            List<Atendimento> attendances = sut.buscarTodos();

            assertThat(result).isPresent();

            Atendimento savedAttendance = result.get();

            assertThat(savedAttendance.getId()).isEqualTo(attendanceId);
            assertThat(savedAttendance.getStatus()).isEqualTo(StatusAtendimento.AGUARDANDO_CONSULTA);
            assertThat(savedAttendance.getClassificacaoRisco()).isEqualTo(ClassificacaoRisco.VERDE);
            assertThat(attendances).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Find by ID and find all tests")
    class FindingTests {

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

        @Test
        @DisplayName("Should not get measurements of another attendance")
        void shouldNotGetMeasurementsOfAnotherAttendance() {
            UUID secondAttendanceId = UUID.randomUUID();

            Atendimento firstAttendance = new Atendimento(attendanceId, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of(firstMeasurement));
            Atendimento secondAttendance = new Atendimento(secondAttendanceId, new Cpf("52998224725"), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.AMARELO, List.of(secondMeasurement));

            sut.salvar(firstAttendance);
            sut.salvar(secondAttendance);

            Atendimento attendance = sut.buscarPorId(attendanceId).orElseThrow();

            assertThat(attendance.getMedicoes().get(0).getId()).isEqualTo(firstMeasurement.getId());
            assertThat(attendance.getMedicoes().get(0).getId()).isNotEqualTo(secondMeasurement.getId());
        }

        @Test
        @DisplayName("Should find the measurements in chronological order")
        void shouldFindMeasurementsInChronologicalOrder() {
            MedicaoSinaisVitais oldestMeasurement = new MedicaoSinaisVitais(UUID.randomUUID(), 36.4, 75, LocalDateTime.of(2026, 1, 10, 9, 0));
            MedicaoSinaisVitais middleMeasurement = new MedicaoSinaisVitais(UUID.randomUUID(), 36.8, 82, LocalDateTime.of(2026, 1, 10, 10, 0));
            MedicaoSinaisVitais newestMeasurement = new MedicaoSinaisVitais(UUID.randomUUID(), 37.0, 90, LocalDateTime.of(2026, 1, 10, 11, 0));

            Atendimento attendanceWithMeasurements = new Atendimento(attendanceId, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.VERDE, List.of(newestMeasurement, oldestMeasurement, middleMeasurement));

            sut.salvar(attendanceWithMeasurements);

            Atendimento attendance = sut.buscarPorId(attendanceId).orElseThrow();

            assertThat(attendance.getMedicoes().get(0).getId()).isEqualTo(oldestMeasurement.getId());
            assertThat(attendance.getMedicoes().get(1).getId()).isEqualTo(middleMeasurement.getId());
            assertThat(attendance.getMedicoes().get(2).getId()).isEqualTo(newestMeasurement.getId());
        }

        @Test
        @DisplayName("Should return empty list when there are no attendances")
        void shouldReturnEmptyListWhenThereAreNoAttendances() {
            assertThat(sut.buscarTodos()).isEmpty();
        }

        @Test
        @DisplayName("Should find all persisted attendances")
        void shouldFindAllPersistedAttendances() {
            Atendimento anotherAttendance = new Atendimento(UUID.randomUUID(), new Cpf("99819627087"),
                    StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
            sut.salvar(initialAttendance);
            sut.salvar(anotherAttendance);

            List<Atendimento> attendances = sut.buscarTodos();

            assertThat(attendances)
                    .extracting(Atendimento::getId)
                    .containsExactlyInAnyOrder(initialAttendance.getId(), anotherAttendance.getId());
        }

        @Test
        @DisplayName("Should throw exception when attendance id is invalid")
        void shouldThrowExceptionWhenAttendanceIdIsInvalid() {
            jdbcTemplate.update("INSERT INTO atendimento (id, cpf, status) VALUES (?, ?, ?)", "invalid-id", CPF_1, StatusAtendimento.AGUARDANDO_TRIAGEM.name());

            assertThatThrownBy(() -> sut.buscarTodos())
                    .isInstanceOf(InvalidDataAccessApiUsageException.class)
                    .hasRootCauseInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("Patient tests")
    class PatientTests {
        @Test
        @DisplayName("Should return false when CPF has no attendance")
        void shouldReturnFalseWhenCpfHasNoAttendance() {
            boolean result = sut.existeAtendimentoAtivoPorCpf(new Cpf(CPF_1));

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should throw exception when CPF is null")
        void shouldThrowExceptionWhenCpfIsNull() {
            assertThatThrownBy(() -> sut.existeAtendimentoAtivoPorCpf(null)).isInstanceOf(NullPointerException.class);
        }

        @ParameterizedTest(name = "active status: {0}")
        @EnumSource(value = StatusAtendimento.class, names = {"AGUARDANDO_TRIAGEM", "AGUARDANDO_CONSULTA", "EM_CONSULTA"})
        @DisplayName("Should identify active attendance for CPF")
        void shouldIdentifyActiveAttendanceForCpf(StatusAtendimento status) {
            ClassificacaoRisco riskClassification = status == StatusAtendimento.AGUARDANDO_TRIAGEM ? null : ClassificacaoRisco.VERDE;
            Atendimento attendance = new Atendimento(attendanceId, new Cpf(CPF_1), status, riskClassification, List.of());
            sut.salvar(attendance);
            boolean result = sut.existeAtendimentoAtivoPorCpf(new Cpf(CPF_1));

            assertThat(result).isTrue();
        }

        @ParameterizedTest
        @EnumSource(value = StatusAtendimento.class, names = {"FINALIZADO", "CANCELADO"})
        @DisplayName("Should return false when CPF has no active attendance")
        void shouldReturnFalseWhenCpfHasNoActiveAttendance(StatusAtendimento status) {
            ClassificacaoRisco riskClassification = status == StatusAtendimento.FINALIZADO ? ClassificacaoRisco.VERDE : null;

            Atendimento attendance = new Atendimento(attendanceId, new Cpf(CPF_1), status, riskClassification, List.of());

            sut.salvar(attendance);

            boolean result = sut.existeAtendimentoAtivoPorCpf(new Cpf(CPF_1));

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when active attendance belongs to another CPF")
        void shouldReturnFalseWhenActiveAttendanceBelongsToAnotherCpf() {
            Atendimento anotherAttendance = new Atendimento(attendanceId, new Cpf("99819627087"),
                    StatusAtendimento.AGUARDANDO_TRIAGEM, null, List.of());
            sut.salvar(anotherAttendance);

            boolean result = sut.existeAtendimentoAtivoPorCpf(new Cpf(CPF_1));
            assertThat(result).isFalse();
        }
    }

}
