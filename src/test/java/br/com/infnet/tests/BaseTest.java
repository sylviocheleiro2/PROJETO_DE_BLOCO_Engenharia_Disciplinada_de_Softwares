package br.com.infnet.tests;

import br.com.infnet.App;
import br.com.infnet.pages.LoginPage;
import br.com.infnet.repository.DatabaseManager;
import io.github.bonigarcia.wdm.WebDriverManager;
import io.javalin.Javalin;
import org.junit.jupiter.api.*;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Duration;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseTest {

    protected WebDriver driver;
    private static Javalin app;
    private static int appPort;

    @BeforeAll
    public void setupClass() {
        WebDriverManager.chromedriver().setup();
        app = App.create();
        app.start(0); // Inicia o servidor em uma porta aleatória
        appPort = app.port(); // Captura a porta
    }

    @AfterAll
    public void teardownClass() {
        if (app != null) {
            app.stop(); // Para o servidor no final de todos os testes
        }
    }

    @BeforeEach
    public void setup() {
        // Limpa a tabela antes de cada teste
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM pessoas");
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='pessoas'");
        } catch (SQLException e) {
            throw new RuntimeException("Falha ao limpar o banco de dados.", e);
        }

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1200");
        options.addArguments("--ignore-certificate-errors");

        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

        // Realiza o login
        LoginPage loginPage = new LoginPage(driver, appPort);
        loginPage.realizarLogin("admin@example.com", "admin123");

        // Validação crucial
        if (!loginPage.isLoginSucesso()) {
            // Se o login falhar, o teste não pode continuar.
            // Limpa os recursos e lança uma exceção clara.
            teardown(); // Chama o método de limpeza
            Assertions.fail("Setup falhou: Login não foi bem-sucedido. URL final: " + driver.getCurrentUrl());
        }
    }

    @AfterEach
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
