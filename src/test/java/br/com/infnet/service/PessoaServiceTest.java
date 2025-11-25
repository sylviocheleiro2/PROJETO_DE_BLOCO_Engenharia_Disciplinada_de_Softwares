package br.com.infnet.service;

import br.com.infnet.model.Pessoa;
import br.com.infnet.repository.DatabaseManager;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PessoaServiceTest {

    private PessoaService pessoaService;

    @BeforeAll
    void setupAll() {
        DatabaseManager.initializeDatabase();
        pessoaService = new PessoaService();
    }

    @BeforeEach
    void setUp() {
        // Limpa a tabela antes de cada teste
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM pessoas");
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='pessoas'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Deve criar uma pessoa através do serviço")
    void deveCriarPessoa() {
        Pessoa pessoa = pessoaService.criarPessoa("Pessoa de Serviço", 35, "service@email.com", "55566677788");
        assertNotNull(pessoa);
        assertTrue(pessoa.getId() > 0);
    }

    @Test
    @DisplayName("Deve consultar uma pessoa existente")
    void deveConsultarPessoaExistente() {
        Pessoa pessoaCriada = pessoaService.criarPessoa("Pessoa Consulta", 40, "consulta@email.com", "66677788899");
        Pessoa pessoaEncontrada = pessoaService.consultarPessoa(pessoaCriada.getId());
        assertNotNull(pessoaEncontrada);
        assertEquals("Pessoa Consulta", pessoaEncontrada.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao consultar pessoa inexistente")
    void deveLancarExcecaoAoConsultarPessoaInexistente() {
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            pessoaService.consultarPessoa(999);
        });
        assertEquals("Pessoa não encontrada.", exception.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar uma pessoa através do serviço")
    void deveAtualizarPessoa() {
        Pessoa pessoaCriada = pessoaService.criarPessoa("Original", 50, "original@service.com", "77788899900");
        Pessoa pessoaAtualizada = pessoaService.atualizarPessoa(pessoaCriada.getId(), "Atualizada", 51, "atualizada@service.com", "77788899901");

        assertEquals("Atualizada", pessoaAtualizada.getNome());
        assertEquals(51, pessoaAtualizada.getIdade());
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar pessoa inexistente")
    void deveLancarExcecaoAoAtualizarPessoaInexistente() {
        assertThrows(NoSuchElementException.class, () -> {
            pessoaService.atualizarPessoa(999, "Inexistente", 20, "inexistente@email.com", "00000000000");
        });
    }

    @Test
    @DisplayName("Deve remover uma pessoa através do serviço")
    void deveRemoverPessoa() {
        Pessoa pessoaCriada = pessoaService.criarPessoa("A ser removida", 60, "remover@service.com", "88899900011");
        assertDoesNotThrow(() -> pessoaService.removerPessoa(pessoaCriada.getId()));
        assertThrows(NoSuchElementException.class, () -> pessoaService.consultarPessoa(pessoaCriada.getId()));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover pessoa inexistente")
    void deveLancarExcecaoAoRemoverPessoaInexistente() {
        assertThrows(NoSuchElementException.class, () -> {
            pessoaService.removerPessoa(999);
        });
    }
}
