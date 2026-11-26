package br.ifsp.tdd_triagem_hospitalar.controller;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.domain.model.ClassificacaoRisco;
import br.ifsp.tdd_triagem_hospitalar.domain.model.MedicaoSinaisVitais;
import br.ifsp.tdd_triagem_hospitalar.domain.model.StatusAtendimento;
import br.ifsp.tdd_triagem_hospitalar.infrastructure.repository.JdbcAtendimentoRepository;
import br.ifsp.tdd_triagem_hospitalar.security.user.User;
import br.ifsp.tdd_triagem_hospitalar.utils.BaseApiIntegrationTest;
import br.ifsp.tdd_triagem_hospitalar.utils.EntityBuilder;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class AtendimentoControllerTest extends BaseApiIntegrationTest {
    private static final String CPF = "640.174.540-11";
    @Autowired
    private JdbcAtendimentoRepository repository;

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should list all attendances")
    void shouldListAllAttendances() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento1 = EntityBuilder.createAtendimento();
        final Atendimento atendimento2 = EntityBuilder.createAtendimento();
        final Atendimento atendimento3 = EntityBuilder.createAtendimento();

        repository.salvar(atendimento1);
        repository.salvar(atendimento2);
        repository.salvar(atendimento3);

        final List<AtendimentoResponse> response =
                given()
                        .contentType("application/json")
                        .port(port)
                        .header("Authorization", "Bearer " + token).
                        when()
                        .get("/api/v1/atendimentos").
                        then()
                        .log().ifValidationFails(LogDetail.BODY)
                        .statusCode(200)
                        .extract()
                        .jsonPath()
                        .getList("$", AtendimentoResponse.class);

        final List<UUID> responseIds = response.stream()
                .map(AtendimentoResponse::id)
                .toList();

        final List<UUID> expectedIds = List.of(atendimento1.getId(), atendimento2.getId(), atendimento3.getId());

        assertThat(responseIds).containsExactlyInAnyOrderElementsOf(expectedIds);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should find attendance by id")
    void shouldFindAttendanceById() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);

        final AtendimentoResponse response =
                given()
                        .contentType("application/json")
                        .port(port)
                        .header("Authorization", "Bearer " + token).
                        when()
                        .get("/api/v1/atendimentos/" + atendimento.getId()).
                        then()
                        .log().ifValidationFails(LogDetail.BODY)
                        .statusCode(200)
                        .extract()
                        .as(AtendimentoResponse.class);

        assertThat(response.id()).isEqualTo(atendimento.getId());
        assertThat(response.cpf()).isEqualTo(atendimento.getCpf().valor());
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should open attendance")
    void shouldOpenAttendance() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final UUID responseId =
                given()
                        .contentType("application/json")
                        .port(port)
                        .header("Authorization", "Bearer " + token)
                        .body(new AbrirAtendimentoRequest(CPF)).
                        when()
                        .post("/api/v1/atendimentos").
                        then()
                        .log().ifValidationFails(LogDetail.BODY)
                        .statusCode(200)
                        .body(notNullValue())
                        .extract()
                        .as(UUID.class);

        final Atendimento atendimento = repository.buscarPorId(responseId).orElseThrow();

        assertThat(atendimento.getId()).isEqualTo(responseId);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should perform initial triage")
    void shouldPerformInitialTriage() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);

        final RealizarTriagemRequest request =
                new RealizarTriagemRequest(36.5, 80, ClassificacaoRisco.VERDE);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request).
                when()
                .post("/api/v1/atendimentos/" + atendimento.getId() + "/triagem").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200);

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getStatus()).isEqualTo(StatusAtendimento.AGUARDANDO_CONSULTA);
        assertThat(atualizado.getMedicoes()).hasSize(1);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should start medical consultation")
    void shouldStartMedicalConsultation() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);

        atendimento.realizarTriagem(EntityBuilder.createMedicao(), ClassificacaoRisco.VERDE);

        repository.salvar(atendimento);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token).
                when()
                .patch("/api/v1/atendimentos/" + atendimento.getId() + "/consulta").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200);

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getStatus()).isEqualTo(StatusAtendimento.EM_CONSULTA);
        assertThat(atualizado.getClassificacaoRisco()).isEqualTo(ClassificacaoRisco.VERDE);
        assertThat(atualizado.getMedicoes()).hasSize(1);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should register a new measurement")
    void shouldRegisterNewMeasurement() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);

        final int quantidadeAnterior = atendimento.getMedicoes().size();
        final RegistrarMedicaoRequest request = new RegistrarMedicaoRequest(37.2, 90);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request).
                when()
                .post("/api/v1/atendimentos/" + atendimento.getId() + "/medicoes").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200);

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getMedicoes()).hasSize(quantidadeAnterior + 1);

        final MedicaoSinaisVitais ultimaMedicao = atualizado.getMedicoes().get(atualizado.getMedicoes().size() - 1);

        assertThat(ultimaMedicao.getTemperatura()).isEqualTo(37.2);
        assertThat(ultimaMedicao.getFrequenciaCardiaca()).isEqualTo(90);
        assertThat(atualizado.getStatus()).isEqualTo(StatusAtendimento.AGUARDANDO_TRIAGEM);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should reclassify attendance risk")
    void shouldReclassifyRisk() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);

        final ReclassificarRiscoRequest request =
                new ReclassificarRiscoRequest(ClassificacaoRisco.AMARELO, "Piora dos sinais vitais");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request).
                when()
                .patch("/api/v1/atendimentos/" + atendimento.getId() + "/classificacao-risco").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getClassificacaoRisco()).isEqualTo(ClassificacaoRisco.AMARELO);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should register prescription")
    void shouldRegisterPrescription() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        atendimento.realizarTriagem(EntityBuilder.createMedicao(), ClassificacaoRisco.AMARELO);
        atendimento.iniciarConsulta();
        repository.salvar(atendimento);

        final String prescricao = "Repouso e hidratação";
        final RegistrarPrescricaoRequest request = new RegistrarPrescricaoRequest(prescricao);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request).
                when()
                .patch("/api/v1/atendimentos/" + atendimento.getId() + "/prescricao").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200);

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getPrescricao()).isEqualTo(prescricao);
        assertThat(atualizado.getStatus()).isEqualTo(StatusAtendimento.EM_CONSULTA);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should complete attendance successfully")
    void shouldCompleteAttendance() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        atendimento.realizarTriagem(EntityBuilder.createMedicao(), ClassificacaoRisco.AMARELO);
        atendimento.iniciarConsulta();
        final String prescricao = "Repouso e hidratação";
        atendimento.registrarPrescricao(prescricao);
        repository.salvar(atendimento);


        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token).
                when()
                .patch("/api/v1/atendimentos/" + atendimento.getId() + "/finalizacao").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200);

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getStatus()).isEqualTo(StatusAtendimento.FINALIZADO);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should cancel attendance")
    void shouldCancelAttendance() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);


        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);

        final String motivo = "Paciente morreu mas passa bem";
        final CancelarAtendimentoRequest request = new CancelarAtendimentoRequest(motivo);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request).
                when()
                .patch("/api/v1/atendimentos/" + atendimento.getId() + "/cancelamento").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200);

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getStatus()).isEqualTo(StatusAtendimento.CANCELADO);
        assertThat(atualizado.getMotivoCancelamento()).isEqualTo(motivo);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should not reclassify attendance risk without triage classification first")
    void shouldNotReclassifyRiskWithoutTriageClassificationFirst() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);

        final ReclassificarRiscoRequest request =
                new ReclassificarRiscoRequest(ClassificacaoRisco.AMARELO, "Piora dos sinais vitais");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request).
                when()
                .patch("/api/v1/atendimentos/" + atendimento.getId() + "/classificacao-risco").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(204);

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getClassificacaoRisco()).isEqualTo(ClassificacaoRisco.AMARELO);
        assertThat(atualizado.getStatus()).isNotEqualTo(StatusAtendimento.AGUARDANDO_TRIAGEM);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should not cancel attendance without justification")
    void shouldNotCancelAttendanceWithoutJustification() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);

        final CancelarAtendimentoRequest request = new CancelarAtendimentoRequest("");

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(request).
                when()
                .patch("/api/v1/atendimentos/" + atendimento.getId() + "/cancelamento").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(400);

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getStatus()).isEqualTo(StatusAtendimento.AGUARDANDO_TRIAGEM);
        assertThat(atualizado.getMotivoCancelamento()).isNull();
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should not open duplicate active attendance")
    void shouldNotOpenDuplicateActiveAttendance() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final UUID primeiroId =
                given()
                        .contentType("application/json")
                        .port(port)
                        .header("Authorization", "Bearer " + token)
                        .body(new AbrirAtendimentoRequest(CPF)).
                        when()
                        .post("/api/v1/atendimentos").
                        then()
                        .statusCode(201)
                        .extract()
                        .as(UUID.class);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token)
                .body(new AbrirAtendimentoRequest(CPF)).
                when()
                .post("/api/v1/atendimentos").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(403)
                .body("message", equalTo("Paciente já possui atendimento ativo."));

        assertThat(repository.buscarPorId(primeiroId)).isPresent();
        assertThat(repository.buscarTodos()).hasSize(1);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should require authentication to list attendances")
    void shouldRequireAuthentication() {
        given()
                .contentType("application/json")
                .port(port).
                when()
                .get("/api/v1/atendimentos").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(401);

        assertThat(repository.buscarTodos()).isEmpty();
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should reject invalid measurement")
    void shouldRejectInvalidMeasurement() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);

        final RegistrarMedicaoRequest request = new RegistrarMedicaoRequest(-10, 90);

        final String response =
                given()
                        .contentType("application/json")
                        .port(port)
                        .header("Authorization", "Bearer " + token)
                        .body(request).
                        when()
                        .post("/api/v1/atendimentos/" + atendimento.getId() + "/medicoes").
                        then()
                        .log().ifValidationFails(LogDetail.BODY)
                        .statusCode(400)
                        .extract()
                        .asString();

        assertThat(response).isNotBlank();

        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getMedicoes()).isEmpty();
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should reject attendance without initial triage")
    void shouldRejectAttendanceWithoutTriage() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        repository.salvar(atendimento);


        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token).
                when()
                .patch("/api/v1/atendimentos/" + atendimento.getId() + "/consulta").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(403)
                .extract()
                .asString();


        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getStatus()).isEqualTo(StatusAtendimento.AGUARDANDO_TRIAGEM);
    }

    @Test
    @Tag("ApiTest")
    @Tag("IntegrationTest")
    @DisplayName("Should reject attendance finalization without prescription")
    void shouldRejectFinalizationWithoutPrescription() {
        final String plainTextPassword = "senha123";
        final User user = registerUser(plainTextPassword);
        final String token = authenticate(user.getEmail(), plainTextPassword);

        final Atendimento atendimento = EntityBuilder.createAtendimento();
        atendimento.realizarTriagem(EntityBuilder.createMedicao(), ClassificacaoRisco.AMARELO);
        atendimento.iniciarConsulta();
        repository.salvar(atendimento);

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token).
                when()
                .patch("/api/v1/atendimentos/" + atendimento.getId() + "/finalizacao").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(403);


        final Atendimento atualizado = repository.buscarPorId(atendimento.getId()).orElseThrow();

        assertThat(atualizado.getStatus()).isEqualTo(StatusAtendimento.EM_CONSULTA);
        assertThat(atualizado.getPrescricao()).isNull();
    }
}
