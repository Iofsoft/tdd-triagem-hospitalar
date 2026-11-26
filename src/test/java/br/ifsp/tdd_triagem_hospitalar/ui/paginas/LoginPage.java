package br.ifsp.tdd_triagem_hospitalar.ui.paginas;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class LoginPage {
    private final WebDriver driver;

    public LoginPage(WebDriver driver){
        this.driver = driver;
    }


    public boolean estaAberta(){
        return driver.findElement(By.id("tela-login")).isDisplayed();
    }

    public boolean estaDentroDaLarguraDaJanela() {
        Dimension tamanhoJanela = driver.manage().window().getSize();
        Point posicao = driver.findElement(By.id("tela-login")).getLocation();
        Dimension tamanho = driver.findElement(By.id("tela-login")).getSize();
        return posicao.getX() + tamanho.getWidth() <= tamanhoJanela.getWidth();
    }

    public CadastroPage abrirCadastro(){
        driver.findElement(By.id("link-cadastro")).click();
        return new CadastroPage(driver);
    }

    public void preencherLogin(String email,String senha){
        driver.findElement(By.id("login-email")).sendKeys(email);
        driver.findElement(By.id("login-senha")).sendKeys(senha);
    }

    public void clicarEntrar(){
        driver.findElement(By.id("btn-entrar")).click();
    }

    public FilaPage aguardarAberturaDaFila() {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(ExpectedConditions.visibilityOfElementLocated(By.id("tela-fila")));
        return new FilaPage(driver);
    }




}
