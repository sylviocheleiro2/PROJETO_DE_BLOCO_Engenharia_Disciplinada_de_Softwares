package br.com.infnet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CadastroPage {
    private final WebDriver driver;
    private static final long SLEEP_TIME = 2000;

    private final By nomeInput = By.id("nome");
    private final By idadeInput = By.id("idade");
    private final By emailInput = By.id("email");
    private final By cpfInput = By.id("cpf");
    private final By adicionarButton = By.cssSelector("button.btn-submit");
    private final By pessoaList = By.id("person-list");
    private final By editarButton = By.cssSelector("button.btn-edit"); // Atualizado
    private final By removerButton = By.cssSelector("button.btn-delete"); // Atualizado

    public CadastroPage(WebDriver driver) {
        this.driver = driver;
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.urlContains("/cadastro.html"));
    }

    public void preencherFormularioDeCadastro(String nome, String idade, String email, String cpf) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        wait.until(ExpectedConditions.elementToBeClickable(nomeInput)).clear();
        driver.findElement(nomeInput).sendKeys(nome);

        wait.until(ExpectedConditions.elementToBeClickable(idadeInput)).clear();
        driver.findElement(idadeInput).sendKeys(idade);

        wait.until(ExpectedConditions.elementToBeClickable(emailInput)).clear();
        driver.findElement(emailInput).sendKeys(email);

        wait.until(ExpectedConditions.elementToBeClickable(cpfInput)).clear();
        driver.findElement(cpfInput).sendKeys(cpf);
    }

    public void clicarEmAdicionar() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.elementToBeClickable(adicionarButton)).click();
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public WebElement getUltimaPessoaDaLista() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.presenceOfElementLocated(pessoaList));
        return driver.findElements(By.cssSelector("#person-list li")).get(0);
    }

    public String getTextoUltimaPessoaDaLista() {
        return getUltimaPessoaDaLista().getText();
    }

    public void clicarEmEditarNaUltimaPessoa() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement ultimaPessoa = getUltimaPessoaDaLista();
        WebElement editarBtn = driver.findElement(By.xpath("//*[@id=\"person-list\"]/li[1]/div[2]/button[1]"));
        wait.until(ExpectedConditions.elementToBeClickable(editarBtn)).click();
    }

    public void clicarEmRemoverNaUltimaPessoa() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement removerBtn = driver.findElement(By.xpath("//*[@id=\"person-list\"]/li[1]/div[2]/button[2]"));
        wait.until(ExpectedConditions.elementToBeClickable(removerBtn)).click();
        try {
            Thread.sleep(SLEEP_TIME); // Aguarda o alerta aparecer
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public WebElement getEditModal() {
        return driver.findElement(By.id("edit-modal"));
    }

    public void preencherFormularioDeEdicao(String nome, String idade, String email, String cpf) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        WebElement modal = getEditModal();

        WebElement nomeElement = modal.findElement(By.id("edit-nome"));
        wait.until(ExpectedConditions.elementToBeClickable(nomeElement));
        nomeElement.clear();
        nomeElement.sendKeys(nome);

        WebElement idadeElement = modal.findElement(By.id("edit-idade"));
        idadeElement.clear();
        idadeElement.sendKeys(idade);

        WebElement emailElement = modal.findElement(By.id("edit-email"));
        emailElement.clear();
        emailElement.sendKeys(email);

        WebElement cpfElement = modal.findElement(By.id("edit-cpf"));
        cpfElement.clear();
        cpfElement.sendKeys(cpf);
    }

    public void clicarEmSalvar() {
        WebElement modal = getEditModal();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement salvarButton = wait.until(ExpectedConditions.elementToBeClickable(
            modal.findElement(By.cssSelector("button.btn-submit"))
        ));
        salvarButton.click();
        try {
            Thread.sleep(SLEEP_TIME); // Aguarda o fechamento do modal
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
