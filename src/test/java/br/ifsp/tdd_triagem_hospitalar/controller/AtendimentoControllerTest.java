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

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

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

        given()
                .contentType("application/json")
                .port(port)
                .header("Authorization", "Bearer " + token).
                when()
                .get("/api/v1/atendimentos").
                then()
                .log().ifValidationFails(LogDetail.BODY)
                .statusCode(200)
                .body("size()", equalTo(3));
    }
}
