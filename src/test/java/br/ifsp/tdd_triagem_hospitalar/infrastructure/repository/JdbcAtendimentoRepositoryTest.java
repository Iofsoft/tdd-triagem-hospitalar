package br.ifsp.tdd_triagem_hospitalar.infrastructure.repository;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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


    @Test
    @DisplayName("Should save attendance with all measurements")
    void shouldSaveAttendanceWithAllMeasurements() {
        UUID attendanceId = UUID.randomUUID();

        MedicaoSinaisVitais firstMeasurement = new MedicaoSinaisVitais(UUID.randomUUID(), 36.5, 80, LocalDateTime.of(2026, 1, 10, 9, 0));
        MedicaoSinaisVitais secondMeasurement = new MedicaoSinaisVitais(UUID.randomUUID(), 37.2, 92, LocalDateTime.of(2026, 1, 10, 9, 30));

        Atendimento attendance = new Atendimento(attendanceId, new Cpf(CPF_1), StatusAtendimento.AGUARDANDO_CONSULTA, ClassificacaoRisco.AMARELO, List.of(firstMeasurement, secondMeasurement));

        sut.salvar(attendance);

        List<Map<String, Object>> records = jdbcTemplate.queryForList("SELECT * FROM medicao WHERE atendimento_id = ? ORDER BY data_hora", attendanceId.toString());

        assertThat(records).hasSize(2);

        assertSoftly(softly -> {
            Map<String, Object> firstRecord = records.get(0);

            softly.assertThat(firstRecord.get("id")).isEqualTo(firstMeasurement.getId().toString());
            softly.assertThat(firstRecord.get("atendimento_id")).isEqualTo(attendanceId.toString());
            softly.assertThat(((Number) firstRecord.get("temperatura")).doubleValue()).isEqualTo(36.5);
            softly.assertThat(((Number) firstRecord.get("frequencia_cardiaca")).intValue()).isEqualTo(80);
            softly.assertThat(firstRecord.get("data_hora")).isEqualTo(firstMeasurement.getDataHora().toString());

            Map<String, Object> secondRecord = records.get(1);

            softly.assertThat(secondRecord.get("id")).isEqualTo(secondMeasurement.getId().toString());
            softly.assertThat(secondRecord.get("atendimento_id")).isEqualTo(attendanceId.toString());
            softly.assertThat(((Number) secondRecord.get("temperatura")).doubleValue()).isEqualTo(37.2);
            softly.assertThat(((Number) secondRecord.get("frequencia_cardiaca")).intValue()).isEqualTo(92);
            softly.assertThat(secondRecord.get("data_hora")).isEqualTo(secondMeasurement.getDataHora().toString());
        });
    }



}
