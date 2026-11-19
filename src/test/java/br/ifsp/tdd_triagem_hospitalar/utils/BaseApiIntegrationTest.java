package br.ifsp.tdd_triagem_hospitalar.utils;

import br.ifsp.tdd_triagem_hospitalar.security.auth.AuthRequest;
import br.ifsp.tdd_triagem_hospitalar.security.auth.AuthResponse;
import br.ifsp.tdd_triagem_hospitalar.security.user.JpaUserRepository;
import br.ifsp.tdd_triagem_hospitalar.security.user.User;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseApiIntegrationTest {
    @LocalServerPort
    protected int port = 8080;
    @Autowired
    protected JpaUserRepository userRepository;
    @Autowired
    protected JdbcTemplate jdbcTemplate;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @BeforeEach
    protected void generalSetup() {
        baseURI = "http://localhost:8080";

    }

    @AfterEach
    protected void tearDown() {
        jdbcTemplate.update("DELETE FROM medicao");
        jdbcTemplate.update("DELETE FROM atendimento");
        userRepository.deleteAll();
    }

    protected User registerUser(String password) {
        User user = EntityBuilder.createUser(
                passwordEncoder.encode(password)
        );

        return userRepository.save(user);
    }

    protected String authenticate(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        AuthRequest authRequest = new AuthRequest(username, password);
        final String url = baseURI + ":" + port + "/api/v1/authenticate";
        final AuthResponse response = restTemplate.postForObject(url, authRequest, AuthResponse.class);

        return response.token();
    }

    protected String createAuthenticatedUser() {
        User user = registerUser("senha123");

        return authenticate(user.getEmail(), "senha123");
    }

    protected RequestSpecification authenticatedRequest(String token) {
        return given()
                .contentType("application/json")
                .header("Authorization", "Bearer " + token);
    }
}