package br.ifsp.tdd_triagem_hospitalar.ui.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class FilaPage {


    private final WebDriver driver;



    public FilaPage(WebDriver driver){
        this.driver = driver;
    }


    public boolean estaAberta() {
        return driver.findElement(By.id("tela-fila")).isDisplayed();
    }

    public void preencherCpf(String cpf){
        driver.findElement(By.id("novo-cpf")).sendKeys(cpf);
    }


    public void abrirAtendimento(String cpf) {
        driver.findElement(By.id("novo-cpf")).sendKeys(cpf);
        driver.findElement(By.id("btn-abrir-atendimento")).click();
    }

    public void clicarAbrirAtendimento(){
        driver.findElement(By.id("btn-abrir-atendimento")).click();
    }


    public void abrirPrimeiroAtendimento(){
        driver.findElement(By.cssSelector("#lista-atendimentos button")).click();
    }

    public String obterTextoDaFila(){
        return driver.findElement(By.id("lista-atendimentos")).getText();
    }
}