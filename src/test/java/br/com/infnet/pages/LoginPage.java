package br.com.infnet.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {
    private final WebDriver driver;
    private final int port;
    private static final long SLEEP_TIME = 1000;

    private final By emailInput = By.id("login-email");
    private final By senhaInput = By.id("login-password");
    private final By logarButao = By.xpath("//*[@id=\"login-form\"]/button");

    public LoginPage(WebDriver driver, int port) {
        this.driver = driver;
        this.port = port;
    }

    public void acessarPagina() {
        driver.get("http://localhost:" + port + "/index.html");
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    public void preencherEmail(String email) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement emailElement = wait.until(ExpectedConditions.elementToBeClickable(emailInput));
        emailElement.clear();
        emailElement.sendKeys(email);
    }

    public void preencherSenha(String senha) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement senhaElement = wait.until(ExpectedConditions.elementToBeClickable(senhaInput));
        senhaElement.clear();
        senhaElement.sendKeys(senha);
    }

    public void clicarEntrar() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement botao = wait.until(ExpectedConditions.elementToBeClickable(logarButao));
        botao.click();
    }

    public void realizarLogin(String email, String senha) {
        acessarPagina();
        preencherEmail(email);
        preencherSenha(senha);
        clicarEntrar();
    }

    public boolean isLoginSucesso() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            // CORREÇÃO: A URL de sucesso após o login é 'cadastro.html', conforme o log.
            return wait.until(ExpectedConditions.urlContains("cadastro.html"));
        } catch (Exception e) {
            System.err.println("Erro ao verificar sucesso do login: " + e.getMessage());
            System.err.println("URL Atual: " + driver.getCurrentUrl());
            return false;
        }
    }
}
