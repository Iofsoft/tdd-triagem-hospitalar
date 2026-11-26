package br.ifsp.tdd_triagem_hospitalar.ui.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;

public class CadastroPage {
    private final WebDriver driver;



    public CadastroPage(WebDriver driver) {
        this.driver = driver;
    }

    public boolean estaAberta() {
        return driver.findElement(By.id("tela-cadastro")).isDisplayed();
    }

    public boolean estaDentroDaLarguraDaJanela() {
        Dimension tamanhoJanela = driver.manage().window().getSize();
        Point posicao = driver.findElement(By.id("tela-cadastro")).getLocation();
        Dimension tamanho = driver.findElement(By.id("tela-cadastro")).getSize();
        return posicao.getX() + tamanho.getWidth() <= tamanhoJanela.getWidth();
    }

    public LoginPage voltarAoLogin() {
        driver.findElement(By.id("link-login")).click();
        return new LoginPage(driver);
    }


    public String cadastrarComCamposVazios() {
        driver.findElement(By.id("btn-cadastrar")).click();
        return driver.switchTo().alert().getText();
    }

    public void preencherCadastro(String nome, String sobrenome, String email, String senha) {
        driver.findElement(By.id("cadastro-nome")).sendKeys(nome);
        driver.findElement(By.id("cadastro-sobrenome")).sendKeys(sobrenome);
        driver.findElement(By.id("cadastro-email")).sendKeys(email);
        driver.findElement(By.id("cadastro-senha")).sendKeys(senha);
    }

    public void clicarCriarConta() {
        driver.findElement(By.id("btn-cadastrar")).click();
    }

    public boolean emailEhValido() {
        return !driver.findElements(By.cssSelector("#cadastro-email:invalid")).isEmpty();
    }
}
