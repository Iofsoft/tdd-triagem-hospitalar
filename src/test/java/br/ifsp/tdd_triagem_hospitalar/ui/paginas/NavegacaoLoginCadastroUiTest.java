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

@Tag("UiTest")
class NavegacaoLoginCadastroUiTest{

    private WebDriver driver;
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
}
