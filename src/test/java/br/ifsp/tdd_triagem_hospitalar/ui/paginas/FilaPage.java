package br.ifsp.tdd_triagem_hospitalar.ui.paginas;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class FilaPage {


    private final WebDriver driver;


    public FilaPage(WebDriver driver){
        this.driver = driver;
    }


    public boolean estaAberta() {
        return driver.findElement(By.id("tela-fila")).isDisplayed();
    }

    public void aguardarAtendimentoNaLista(String cpf) {
        new WebDriverWait(driver, Duration.ofSeconds(5)).until(
                ExpectedConditions.textToBePresentInElementLocated(By.id("lista-atendimentos"), cpf));
    }

    public void abrirAtendimento(String cpf) {
        driver.findElement(By.id("novo-cpf")).sendKeys(cpf);
        driver.findElement(By.id("btn-abrir-atendimento")).click();
    }

    public void abrirAtendimentoNosDetalhes(String cpf) {
        List<WebElement> linhas = driver.findElements(By.cssSelector("#lista-atendimentos tr"));
        for (WebElement linha : linhas) {
            String cpfNaLinha = linha.findElement(By.cssSelector("td")).getText();
            if (cpfNaLinha.equals(cpf)) {
                linha.findElement(By.cssSelector("button")).click();
                return;
            }
        }
        throw new IllegalArgumentException("Atendimento não encontrado na fila: " + cpf);
    }


}
