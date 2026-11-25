package br.ifsp.tdd_triagem_hospitalar.ui.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

public class AtendimentoPage {
    private final WebDriver driver;

    public AtendimentoPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean estaAberta() {
        return driver.findElement(By.id("tela-atendimento")).isDisplayed();
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

    public String obterPrescricao() {
        return driver.findElement(By.id("detalhe-prescricao")).getText();
    }


    public void cancelarAtendimento(String motivo) {
        driver.findElement(By.id("cancelar-motivo")).sendKeys(motivo);
        driver.findElement(By.id("btn-cancelar")).click();
    }

    public String obterMotivoCancelamento() {
        return driver.findElement(By.id("detalhe-motivo")).getText();
    }

    public boolean motivoEstaContidoNoPainel() {
        return (Boolean) ((JavascriptExecutor) driver).executeScript(
                "const paragrafo = document.getElementById('detalhe-motivo').parentElement; " +
                        "return paragrafo.scrollWidth <= paragrafo.clientWidth;");
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
