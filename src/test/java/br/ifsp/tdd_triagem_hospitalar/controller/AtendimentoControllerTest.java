package br.ifsp.tdd_triagem_hospitalar.controller;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Atendimento;
import br.ifsp.tdd_triagem_hospitalar.infrastructure.repository.JdbcAtendimentoRepository;
import br.ifsp.tdd_triagem_hospitalar.security.user.User;
import br.ifsp.tdd_triagem_hospitalar.utils.BaseApiIntegrationTest;
import br.ifsp.tdd_triagem_hospitalar.utils.EntityBuilder;
import io.restassured.filter.log.LogDetail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class AtendimentoControllerTest extends BaseApiIntegrationTest {
    private static final String CPF = "640.174.540-11";
    @Autowired
    private JdbcAtendimentoRepository repository;

    @Test
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
}
