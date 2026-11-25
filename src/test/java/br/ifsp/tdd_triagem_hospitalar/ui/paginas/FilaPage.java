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

    public void abrirAtendimento(String cpf) {
        driver.findElement(By.id("novo-cpf")).sendKeys(cpf);
        driver.findElement(By.id("btn-abrir-atendimento")).click();
    }

    public void abrirAtendimentoNosDetalhes(String cpf) {
        driver.findElement(By.xpath("//tr[td[normalize-space()='" + cpf + "']]//button")).click();
    }

    public String obterTextoDaFila(){
        return driver.findElement(By.id("lista-atendimentos")).getText();
    }
}
