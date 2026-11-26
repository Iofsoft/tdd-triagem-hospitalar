package br.ifsp.tdd_triagem_hospitalar.utils;

import br.ifsp.tdd_triagem_hospitalar.security.auth.AuthRequest;
import br.ifsp.tdd_triagem_hospitalar.security.auth.AuthResponse;
import br.ifsp.tdd_triagem_hospitalar.security.user.JpaUserRepository;
import br.ifsp.tdd_triagem_hospitalar.security.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import static io.restassured.RestAssured.baseURI;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseApiIntegrationTest {
    @LocalServerPort
    protected int port = 8080;
    @Autowired
    protected JpaUserRepository userRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    private User usuarioCriado;

    @BeforeEach
    protected void generalSetup() {
        baseURI = "http://localhost:8080";

    }

    @AfterEach
    protected void tearDown() {
        if (usuarioCriado != null) {
            userRepository.delete(usuarioCriado);
            usuarioCriado = null;
        }
    }

    protected User registerUser(String password) {
        User user = EntityBuilder.createUser(
                passwordEncoder.encode(password)
        );
        usuarioCriado = user;
        return userRepository.save(user);
    }

    protected String authenticate(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        AuthRequest authRequest = new AuthRequest(username, password);
        final String url = baseURI + "/api/v1/authenticate";
        final AuthResponse response = restTemplate.postForObject(url, authRequest, AuthResponse.class);

        return response.token();
    }
}