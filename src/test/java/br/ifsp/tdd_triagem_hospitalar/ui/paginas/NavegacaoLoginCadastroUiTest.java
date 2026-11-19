package br.ifsp.tdd_triagem_hospitalar.ui.paginas;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import com.github.javafaker.Faker;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.UUID;

@Tag("UiTest")
class NavegacaoLoginCadastroUiTest{

    private WebDriver driver;
    private final Faker faker = new Faker();

    @BeforeEach
    void iniciarNavegador(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        driver.get("file:///C:/Users/l/IdeaProjects/tdd-triagem-hospitalar/frontend/index.html");
    }
    @AfterEach
    void fecharNavegador(){
        if (driver != null){
            driver.quit();
        }
    }


    @Test
    void deveNavegarEntreLoginECadastro(){
        LoginPage login = new LoginPage(driver);
        assertTrue(login.estaAberta());

        CadastroPage cadastro = login.abrirCadastro();
        assertTrue(cadastro.estaAberta());

        login = cadastro.voltarAoLogin();
        assertTrue(login.estaAberta());
    }

    @Test
    void deveMostrarAlertaQuandoCadastroestaVazio(){
        CadastroPage cadastro = new LoginPage(driver).abrirCadastro();
        String mensagem = cadastro.cadastrarComCamposVazios();

        assertEquals("Preencha todos os campos.", mensagem);
    }

    @Test
    void deveMostrarAlertaQuandoLoginEhInvalido(){
        LoginPage login = new LoginPage(driver);

        String mensagem = login.tentarLogin("invalido@example.com", "senha-incorreta");
        assertEquals("E-mail ou senha incorretos.", mensagem);
    }

    @Test
    void deveManterLoginVisivelEmTelaEstreita(){
        driver.manage().window().setSize(new org.openqa.selenium.Dimension(375, 812));
        LoginPage login = new LoginPage(driver);

        assertTrue(login.estaAberta());
        assertTrue(driver.findElement(By.id("btn-entrar")).isDisplayed());
    }

    @Test
    void deveCadastrarUsuarioComDadosValidos() {
        String nome = faker.name().firstName();
        String sobrenome = faker.name().lastName();
        String email = "ui-" + UUID.randomUUID() + "@example.com";
        String senha = faker.internet().password(10, 16);

        CadastroPage cadastro = new LoginPage(driver).abrirCadastro();
        cadastro.preencherCadastro(nome, sobrenome, email, senha);
        cadastro.clicarCriarConta();



        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.alertIsPresent());


        String mensagem = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();

        assertEquals("Conta criada! Agora é só entrar.",  mensagem);
        assertTrue(new LoginPage(driver).estaAberta());

    }

}
