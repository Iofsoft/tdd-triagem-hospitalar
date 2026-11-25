package br.ifsp.tdd_triagem_hospitalar.ui.paginas;
import org.openqa.selenium.By;
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

    public String tentarLogin(String email, String senha){
        driver.findElement(By.id("login-email")).sendKeys(email);
        driver.findElement(By.id("login-senha")).sendKeys(senha);
        driver.findElement(By.id("btn-entrar")).click();

        return driver.switchTo().alert().getText();
    }


}