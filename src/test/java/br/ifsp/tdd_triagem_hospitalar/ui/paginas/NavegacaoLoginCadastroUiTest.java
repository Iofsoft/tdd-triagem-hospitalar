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
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.github.javafaker.Faker;
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
        assertTrue(login.estaDentroDaLarguraDaJanela(), "A tela de login não deve transbordar");

        CadastroPage cadastro = login.abrirCadastro();
        assertTrue(cadastro.estaAberta());
        assertTrue(cadastro.estaDentroDaLarguraDaJanela(), "A tela de cadastro não deve transbordar");
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



        String mensagem = cadastro.obterMensagemDeAlerta();

        assertEquals("Conta criada! Agora é só entrar.", mensagem);

        LoginPage login = new LoginPage(driver);
        assertTrue(login.estaAberta());
        login.preencherLogin(email, senha);
        login.clicarEntrar();

        FilaPage fila = login.aguardarAberturaDaFila();
        assertTrue(fila.estaAberta());
    }



    @Test
    void deveAbrirDetalhesDoAtendimento() {
        String nome = faker.name().firstName();
        String sobrenome = faker.name().lastName();
        String email = "ui-" + UUID.randomUUID() + "@example.com";
        String senha = faker.internet().password(10, 16);
        String cpf = gerarCpfValido();

        CadastroPage cadastro = new LoginPage(driver).abrirCadastro();
        cadastro.preencherCadastro(nome, sobrenome, email, senha);
        cadastro.clicarCriarConta();
        cadastro.obterMensagemDeAlerta();

        LoginPage login = new LoginPage(driver);
        login.preencherLogin(email, senha);
        login.clicarEntrar();

        FilaPage fila = login.aguardarAberturaDaFila();

        fila.abrirAtendimento(cpf);
        fila.aguardarAtendimentoNaLista(cpf);
        fila.abrirAtendimentoNosDetalhes(cpf);

        AtendimentoPage atendimento = new AtendimentoPage(driver);
        atendimento.aguardarAbertura();
        assertTrue(atendimento.estaAberta());
    }

    @Test
    void deveVoltarDosDetalhesParaFila() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        atendimento.voltarParaFila();

        FilaPage fila = new LoginPage(driver).aguardarAberturaDaFila();
        assertTrue(fila.estaAberta());
    }

    @Test
    void deveRealizarTriagemValida() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        atendimento.realizarTriagem("36,5", "80", "AMARELO");

        atendimento.aguardarStatus("Aguardando consulta");
        assertEquals("AMARELO", atendimento.obterRisco());
        assertTrue(atendimento.obterMedicoes().contains("36.5") || atendimento.obterMedicoes().contains("36,5"),
                "A medição de temperatura deve aparecer nos detalhes do atendimento.");
    }

    @Test
    void deveExibirErroAoInformarTemperaturaInvalidaNaTriagem() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        atendimento.realizarTriagem("50", "80", "AMARELO");


        String mensagem = atendimento.obterMensagemDeAlerta();

        assertTrue(mensagem.contains("Temperatura fora da faixa permitida"));
        assertEquals("Aguardando triagem", atendimento.obterStatus());
    }

    @Test
    void deveRegistrarNovaMedicaoDuranteConsulta() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        atendimento.realizarTriagem("36,5", "80", "AMARELO");

        atendimento.aguardarStatus("Aguardando consulta");
        atendimento.iniciarConsulta();
        atendimento.aguardarStatus("Em consulta");

        atendimento.registrarMedicao("37,2", "90");
        atendimento.aguardarMedicao("90 bpm");

        assertTrue(atendimento.obterMedicoes().contains("90 bpm"));
        assertTrue(atendimento.obterMedicoes().contains("37.2") || atendimento.obterMedicoes().contains("37,2"));
    }

    @Test
    void devePrescreverEFinalizarAtendimento() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        atendimento.realizarTriagem("36,5", "80", "AMARELO");
        atendimento.aguardarStatus("Aguardando consulta");
        atendimento.iniciarConsulta();

        atendimento.aguardarStatus("Em consulta");

        atendimento.registrarPrescricao("Repouso e hidratação");
        atendimento.aguardarPrescricao("Repouso e hidratação");
        atendimento.finalizarAtendimento();

        atendimento.aguardarStatus("Finalizado");
        assertEquals("Finalizado", atendimento.obterStatus());
    }



    private AtendimentoPage abrirNovoAtendimentoNosDetalhes(String cpf) {
        FilaPage fila = entrarNaFilaComUsuarioNovo();
        fila.abrirAtendimento(cpf);
        fila.aguardarAtendimentoNaLista(cpf);
        fila.abrirAtendimentoNosDetalhes(cpf);

        AtendimentoPage atendimento = new AtendimentoPage(driver);
        atendimento.aguardarAbertura();
        return atendimento;
    }

    private FilaPage entrarNaFilaComUsuarioNovo() {
        String nome = faker.name().firstName();
        String sobrenome = faker.name().lastName();
        String email = "ui-" + UUID.randomUUID() + "@example.com";
        String senha = faker.internet().password(10, 16);

        CadastroPage cadastro = new LoginPage(driver).abrirCadastro();
        cadastro.preencherCadastro(nome, sobrenome, email, senha);
        cadastro.clicarCriarConta();

        cadastro.obterMensagemDeAlerta();

        LoginPage login = new LoginPage(driver);
        login.preencherLogin(email, senha);
        login.clicarEntrar();
        return login.aguardarAberturaDaFila();
    }

    @Test
    void deveManterMotivoLongoDentroDoPainel() {
        AtendimentoPage atendimento = abrirNovoAtendimentoNosDetalhes(gerarCpfValido());
        String motivo = "a".repeat(120);

        atendimento.cancelarAtendimento(motivo);
        atendimento.aguardarStatus("Cancelado");

        assertTrue(atendimento.motivoEstaContidoNoPainel(), "O motivo longo não deve transbordar o painel de detalhes.");
    }

    @Test
    void deveRecusarEmailInvalidoNoCadastro() {
        String email = "ana-" + UUID.randomUUID() + "@1";
        CadastroPage cadastro = new LoginPage(driver).abrirCadastro();
        cadastro.preencherCadastro("Ana", "Silva", email, "Senha123!");
        cadastro.clicarCriarConta();
        cadastro.obterMensagemDeAlerta();

        assertTrue(cadastro.estaAberta(), "O cadastro não deveria aceitar um email sem domínio completo.");
    }



    private String gerarCpfValido() { // testar isso ??
        for (int tentativa = 0; tentativa < 1000; tentativa++) {
            String cpf = faker.number().digits(11);
            try {
                return new Cpf(cpf).valor();
            } catch (IllegalArgumentException ignorado) {
            }
        }

        throw new IllegalStateException("Não foi possível gerar um cpf válido para o teste.");
    }
}
