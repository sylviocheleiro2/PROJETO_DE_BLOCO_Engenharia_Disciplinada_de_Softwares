package br.com.infnet.tests;

import br.com.infnet.pages.CadastroPage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PessoaCrudE2ETest extends BaseTest {

    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10); // Timeout aumentado

    @Test
    @DisplayName("Deve cadastrar uma nova pessoa com sucesso")
    public void deveCadastrarNovaPessoaComSucesso() {
        CadastroPage cadastroPage = new CadastroPage(driver);
        String nome = "Fulano de Tal";
        String idade = "30";
        String email = "fulano@teste.com";
        String cpf = "12345678901";

        cadastroPage.preencherFormularioDeCadastro(nome, idade, email, cpf);
        cadastroPage.clicarEmAdicionar();

        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        WebElement notice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notice")));
        assertTrue(notice.getText().contains("Pessoa adicionada com sucesso"));

        WebElement ultimaPessoa = cadastroPage.getUltimaPessoaDaLista();
        String textoDoUltimoItem = ultimaPessoa.getText();
        assertTrue(textoDoUltimoItem.contains(nome));
        assertTrue(textoDoUltimoItem.contains(email));
    }

    @Test
    @DisplayName("Deve editar uma pessoa com sucesso")
    public void deveEditarPessoaComSucesso() {
        CadastroPage cadastroPage = new CadastroPage(driver);
        cadastroPage.preencherFormularioDeCadastro("Pessoa Original", "25", "original@email.com", "11122233344");
        cadastroPage.clicarEmAdicionar();
        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("li[data-id]")));

        cadastroPage.clicarEmEditarNaUltimaPessoa();
        wait.until(ExpectedConditions.visibilityOf(cadastroPage.getEditModal()));

        String nomeEditado = "Pessoa Editada";
        String emailEditado = "editado@email.com";
        cadastroPage.preencherFormularioDeEdicao(nomeEditado, "35", emailEditado, "55566677788");
        cadastroPage.clicarEmSalvar();

        WebElement notice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notice")));
        assertTrue(notice.getText().contains("Pessoa atualizada"));
        String textoDoItemAtualizado = cadastroPage.getTextoUltimaPessoaDaLista();
        assertTrue(textoDoItemAtualizado.contains(nomeEditado));
        assertTrue(textoDoItemAtualizado.contains(emailEditado));
    }

    @Test
    @DisplayName("Deve remover uma pessoa com sucesso")
    public void deveRemoverPessoaComSucesso() {
        CadastroPage cadastroPage = new CadastroPage(driver);
        cadastroPage.preencherFormularioDeCadastro("Pessoa a Remover", "40", "remover@email.com", "99988877766");
        cadastroPage.clicarEmAdicionar();
        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        WebElement pessoaParaRemover = wait.until(ExpectedConditions.visibilityOf(cadastroPage.getUltimaPessoaDaLista()));
        String idPessoa = pessoaParaRemover.getAttribute("data-id");

        cadastroPage.clicarEmRemoverNaUltimaPessoa();
        driver.switchTo().alert().accept();

        WebElement notice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notice")));
        assertTrue(notice.getText().contains("Pessoa removida"));

        boolean elementoRemovido = wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("li[data-id='" + idPessoa + "']")));
        assertTrue(elementoRemovido, "O elemento da pessoa removida ainda foi encontrado na página.");
    }

    @ParameterizedTest
    @DisplayName("Deve exibir erro ao tentar cadastrar pessoa com dados inválidos")
    @CsvSource({
        "'', '25', 'teste@email.com', '12345678901', 'Nome não pode ser vazio.'",
        "'Nome Valido', '30', 'email-invalido', '12345678901', 'Email inválido.'",
        "'Nome Valido', '30', 'teste@email.com', '123', 'CPF inválido.'"
    })
    public void deveExibirErroAoTentarCadastrarPessoaComDadosInvalidos(String nome,
                                                                       String idade,
                                                                       String email,
                                                                       String cpf,
                                                                       String mensagemEsperada) {
        CadastroPage cadastroPage = new CadastroPage(driver);
        cadastroPage.preencherFormularioDeCadastro(nome, idade, email, cpf);
        cadastroPage.clicarEmAdicionar();

        WebDriverWait wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        WebElement notice = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("notice")));

        assertEquals("notice notice-error", notice.getAttribute("class"));
        assertTrue(notice.getText().contains(mensagemEsperada),
            String.format("Mensagem de erro esperada '%s' não encontrada em '%s'",
                mensagemEsperada, notice.getText()));
    }
}
