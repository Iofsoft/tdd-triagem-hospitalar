package br.ifsp.tdd_triagem_hospitalar.ui.paginas;

import br.ifsp.tdd_triagem_hospitalar.domain.model.Cpf;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.openqa.selenium.By;
import com.github.javafaker.Faker;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.UUID;
import java.nio.file.Path;
import org.openqa.selenium.Dimension;

@Tag("UiTest")
class NavegacaoLoginCadastroUiTest{

    private WebDriver driver;
    private final Faker faker = new Faker();

    @BeforeEach
    void iniciarNavegador(){
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        driver = new ChromeDriver(options);
        Path pagina = Path.of("frontend", "index.html").toAbsolutePath();
        driver.get(pagina.toUri().toString());
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
    void deveManterLoginECadastroSemRolagemHorizontalEmTelaEstreita() {
        driver.manage().window().setSize(new Dimension(375, 812));
        LoginPage login = new LoginPage(driver);

        assertTrue(login.estaAberta());
        assertTrue(login.estaDentroDaLarguraDaJanela(), "A tela de login não deve transbordar na largura mobile.");

        CadastroPage cadastro = login.abrirCadastro();
        assertTrue(cadastro.estaAberta());
        assertTrue(cadastro.estaDentroDaLarguraDaJanela(), "A tela de cadastro não deve transbordar na largura mobile.");
    }

    @Test
    void deveMostrarAlertaQuandoCadastroestaVazio(){
        CadastroPage cadastro = new LoginPage(driver).abrirCadastro();
        String mensagem = cadastro.cadastrarComCamposVazios();

        assertEquals("Preencha todos os campos.", mensagem);
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



        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.alertIsPresent());


        String mensagem = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();

        assertEquals("Conta criada! Agora é só entrar.", mensagem);

        LoginPage login = new LoginPage(driver);
        assertTrue(login.estaAberta());
        login.preencherLogin(email, senha);
        login.clicarEntrar();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.id("tela-fila")));

        assertTrue(new FilaPage(driver).estaAberta());
    }



    @Test
    void deveAbrirDetalhesDoAtendimento() {
        String nome = faker.name().firstName();
        String sobrenome = faker.name().lastName();
        String email = "ui-" + UUID.randomUUID() + "@example.com";
        String senha = faker.internet().password(10, 16);
        String cpf = gerarCpfValido();;

        CadastroPage cadastro = new LoginPage(driver).abrirCadastro();
        cadastro.preencherCadastro(nome, sobrenome, email, senha);
        cadastro.clicarCriarConta();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.alertIsPresent());driver.switchTo().alert().accept();

        LoginPage login = new LoginPage(driver);
        login.preencherLogin(email, senha);
        login.clicarEntrar();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.id("tela-fila")));

        FilaPage fila = new FilaPage(driver);
        fila.abrirAtendimento(cpf);

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBePresentInElementLocated(By.id("lista-atendimentos"), cpf));

        driver.findElement(By.xpath("//tr[td[normalize-space()='" + cpf + "']]//button")).click();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.id("tela-atendimento")));
        assertTrue(driver.findElement(By.id("tela-atendimento")).isDisplayed());
    }

    @Test
    void deveVoltarDosDetalhesParaFila() {
        abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        driver.findElement(By.id("btn-voltar")).click();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.id("tela-fila")));
        assertTrue(new FilaPage(driver).estaAberta());
    }

    @Test
    void deveRealizarTriagemValida() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        atendimento.realizarTriagem("36,5", "80", "AMARELO");

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBe(By.id("detalhe-status"), "Aguardando consulta"));
        assertEquals("AMARELO", atendimento.obterRisco());
        assertTrue(atendimento.obterMedicoes().contains("36.5") || atendimento.obterMedicoes().contains("36,5"),
                "A medição de temperatura deve aparecer nos detalhes do atendimento.");
    }

    @Test
    void deveExibirErroAoInformarTemperaturaInvalidaNaTriagem() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        atendimento.realizarTriagem("50", "80", "AMARELO");


        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.alertIsPresent());
        String mensagem = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();

        assertTrue(mensagem.contains("Temperatura fora da faixa permitida"));
        assertEquals("Aguardando triagem", atendimento.obterStatus());
    }

    @Test
    void deveRegistrarNovaMedicaoDuranteConsulta() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        atendimento.realizarTriagem("36,5", "80", "AMARELO");

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBe(By.id("detalhe-status"), "Aguardando consulta"));
        atendimento.iniciarConsulta();
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBe(By.id("detalhe-status"), "Em consulta"));

        atendimento.registrarMedicao("37,2", "90");
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBePresentInElementLocated(By.id("detalhe-medicoes"), "90 bpm"));

        assertTrue(atendimento.obterMedicoes().contains("90 bpm"));
        assertTrue(atendimento.obterMedicoes().contains("37.2") || atendimento.obterMedicoes().contains("37,2"));
    }

    @Test
    void devePrescreverEFinalizarAtendimento() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        atendimento.realizarTriagem("36,5", "80", "AMARELO");
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBe(By.id("detalhe-status"), "Aguardando consulta"));
        atendimento.iniciarConsulta();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBe(By.id("detalhe-status"), "Em consulta"));

        atendimento.registrarPrescricao("Repouso e hidratação");
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBe(By.id("detalhe-prescricao"), "Repouso e hidratação"));
        atendimento.finalizarAtendimento();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBe(By.id("detalhe-status"), "Finalizado"));
        assertEquals("Finalizado", atendimento.obterStatus());
    }



    private AtendimentoPage abrirNovoAtendimentoNosDetalhes(String cpf) {
        FilaPage fila = entrarNaFilaComUsuarioNovo();
        fila.abrirAtendimento(cpf);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBePresentInElementLocated(By.id("lista-atendimentos"), cpf));
        fila.abrirAtendimentoNosDetalhes(cpf);

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.visibilityOfElementLocated(By.id("tela-atendimento")));
        return new AtendimentoPage(driver);
    }

    private FilaPage entrarNaFilaComUsuarioNovo() {
        String nome = faker.name().firstName();
        String sobrenome = faker.name().lastName();
        String email = "ui-" + UUID.randomUUID() + "@example.com";
        String senha = faker.internet().password(10, 16);

        CadastroPage cadastro = new LoginPage(driver).abrirCadastro();
        cadastro.preencherCadastro(nome, sobrenome, email, senha);
        cadastro.clicarCriarConta();

        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.alertIsPresent());
        driver.switchTo().alert().accept();

        LoginPage login = new LoginPage(driver);
        login.preencherLogin(email, senha);
        login.clicarEntrar();
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("tela-fila")));
        return new FilaPage(driver);
    }

    @Test
    void deveManterMotivoLongoDentroDoPainel() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        String motivo = "a".repeat(120);

        atendimento.cancelarAtendimento(motivo);
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.textToBe(By.id("detalhe-status"), "Cancelado"));

        assertTrue(atendimento.motivoEstaContidoNoPainel(), "O motivo longo não deve transbordar o painel de detalhes.");
    }

    @Test
    void deveRecusarEmailInvalidoNoCadastro() {
        CadastroPage cadastro = new LoginPage(driver).abrirCadastro();
        cadastro.preencherCadastro("Ana", "Silva", "ana@1", "Senha123!");
        cadastro.clicarCriarConta();

        assertFalse(cadastro.emailEhValido(), "O navegador deve marcar o e-mail incompleto como inválido.");
        assertTrue(cadastro.estaAberta(), "O formulário deve permanecer aberto após recusar o e-mail.");
    }



    private String gerarCpfValido() { // testar se funciona no resto
        for (int tentativa = 0; tentativa < 1000; tentativa++) {
            String cpf = faker.number().digits(11);
            try {
                return new Cpf(cpf).valor();
            } catch (IllegalArgumentException ignorado) {
            }
        }

        throw new IllegalStateException("Não foi possível gerar um CPF válido para o teste.");
    }
}
