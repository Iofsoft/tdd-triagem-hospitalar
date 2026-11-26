package br.ifsp.tdd_triagem_hospitalar.ui.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AtendimentoPage {
    private final WebDriver driver;

    public AtendimentoPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean estaAberta() {
        return driver.findElement(By.id("tela-atendimento")).isDisplayed();
    }

    public void aguardarAbertura() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("tela-atendimento")));
    }

    public void aguardarStatus(String status) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.textToBe(By.id("detalhe-status"), status));
    }

    public void aguardarMedicao(String medicao) {
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                ExpectedConditions.textToBePresentInElementLocated(By.id("detalhe-medicoes"), medicao));
    }

    public void aguardarPrescricao(String prescricao) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.textToBe(By.id("detalhe-prescricao"), prescricao));
    }

    public String obterMensagemDeAlerta() {
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(ExpectedConditions.alertIsPresent());
        String mensagem = driver.switchTo().alert().getText();
        driver.switchTo().alert().accept();
        return mensagem;
    }

    public void voltarParaFila() {
        driver.findElement(By.id("btn-voltar")).click();
    }

    public void realizarTriagem(String temperatura, String frequencia, String risco) {
        driver.findElement(By.id("triagem-temperatura")).sendKeys(temperatura);
        driver.findElement(By.id("triagem-frequencia")).sendKeys(frequencia);
        driver.findElement(By.id("triagem-risco")).sendKeys(risco);
        driver.findElement(By.id("btn-triagem")).click();
    }

    public void iniciarConsulta() {
        driver.findElement(By.id("btn-iniciar-consulta")).click();
    }

    public void registrarMedicao(String temperatura, String frequencia) {
        driver.findElement(By.id("medicao-temperatura")).sendKeys(temperatura);
        driver.findElement(By.id("medicao-frequencia")).sendKeys(frequencia);
        driver.findElement(By.id("btn-medicao")).click();
    }

    public void registrarPrescricao(String texto) {
        driver.findElement(By.id("prescricao-texto")).sendKeys(texto);
        driver.findElement(By.id("btn-prescricao")).click();
    }

    public void finalizarAtendimento() {
        driver.findElement(By.id("btn-finalizar")).click();
    }




    public void cancelarAtendimento(String motivo) {
        driver.findElement(By.id("cancelar-motivo")).sendKeys(motivo);
        driver.findElement(By.id("btn-cancelar")).click();
    }



    public boolean motivoEstaContidoNoPainel() {
        WebElement motivo = driver.findElement(By.id("detalhe-motivo"));
        WebElement painel = driver.findElement(By.cssSelector("#tela-atendimento > .caixa"));
        Rectangle limitesMotivo = motivo.getRect();
        Rectangle limitesPainel = painel.getRect();
        return limitesMotivo.getX() + limitesMotivo.getWidth()
                <= limitesPainel.getX() + limitesPainel.getWidth();
    }

    public String obterStatus() {
        return driver.findElement(By.id("detalhe-status")).getText();
    }


    public String obterRisco() {
        return driver.findElement(By.id("detalhe-risco")).getText();
    }
    public String obterMedicoes() {
        return driver.findElement(By.id("detalhe-medicoes")).getText();
    }
}
