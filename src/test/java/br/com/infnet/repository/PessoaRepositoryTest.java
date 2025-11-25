package br.com.infnet.repository;

import br.com.infnet.model.Pessoa;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PessoaRepositoryTest {

    private PessoaRepository repository;

    @BeforeAll
    void setupAll() {
        // Garante que a tabela exista antes de todos os testes
        DatabaseManager.initializeDatabase();
        repository = new PessoaRepository();
    }

    @BeforeEach
    void setUp() {
        // Limpa a tabela antes de cada teste para garantir o isolamento
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM pessoas");
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='pessoas'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Deve salvar uma nova pessoa e atribuir um ID")
    void deveSalvarNovaPessoa() {
        Pessoa pessoa = new Pessoa(0, "Nova Pessoa", 25, "nova@email.com", "11122233344");
        Pessoa pessoaSalva = repository.save(pessoa);

        assertNotNull(pessoaSalva);
        assertTrue(pessoaSalva.getId() > 0); // O ID agora é gerado pelo banco
        assertEquals("Nova Pessoa", pessoaSalva.getNome());
    }

    @Test
    @DisplayName("Deve encontrar uma pessoa pelo ID")
    void deveEncontrarPessoaPorId() {
        Pessoa pessoaSalva = repository.save(new Pessoa(0, "Pessoa Encontrada", 30, "encontrada@email.com", "22233344455"));
        Optional<Pessoa> pessoaOptional = repository.findById(pessoaSalva.getId());

        assertTrue(pessoaOptional.isPresent());
        assertEquals("Pessoa Encontrada", pessoaOptional.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para ID inexistente")
    void deveRetornarEmptyParaIdInexistente() {
        Optional<Pessoa> pessoaOptional = repository.findById(999);
        assertTrue(pessoaOptional.isEmpty());
    }

    @Test
    @DisplayName("Deve listar todas as pessoas")
    void deveListarTodasAsPessoas() {
        repository.save(new Pessoa(0, "Pessoa 1", 20, "p1@email.com", "11111111111"));
        repository.save(new Pessoa(0, "Pessoa 2", 30, "p2@email.com", "22222222222"));

        List<Pessoa> pessoas = repository.findAll();

        assertEquals(2, pessoas.size());
    }

    @Test
    @DisplayName("Deve atualizar uma pessoa existente")
    void deveAtualizarPessoa() {
        Pessoa pessoaSalva = repository.save(new Pessoa(0, "Pessoa Original", 40, "original@email.com", "33344455566"));
        Pessoa pessoaParaAtualizar = new Pessoa(pessoaSalva.getId(), "Pessoa Atualizada", 41, "atualizado@email.com", "33344455567");
        Pessoa pessoaAtualizada = repository.update(pessoaParaAtualizar);

        assertNotNull(pessoaAtualizada);
        assertEquals("Pessoa Atualizada", pessoaAtualizada.getNome());
        assertEquals(41, pessoaAtualizada.getIdade());

        Optional<Pessoa> pessoaDoRepo = repository.findById(pessoaSalva.getId());
        assertTrue(pessoaDoRepo.isPresent());
        assertEquals("Pessoa Atualizada", pessoaDoRepo.get().getNome());
    }

    @Test
    @DisplayName("Deve deletar uma pessoa pelo ID")
    void deveDeletarPessoa() {
        Pessoa pessoaSalva = repository.save(new Pessoa(0, "Pessoa a Deletar", 50, "deletar@email.com", "44455566677"));
        boolean deletado = repository.deleteById(pessoaSalva.getId());

        assertTrue(deletado);
        assertTrue(repository.findById(pessoaSalva.getId()).isEmpty());
    }

    @Test
    @DisplayName("Deve lançar exceção ao salvar CPF duplicado")
    void deveLancarExcecaoCpfDuplicado() {
        repository.save(new Pessoa(0, "Pessoa 1", 20, "p1@email.com", "12345678900"));
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            repository.save(new Pessoa(0, "Pessoa 2", 30, "p2@email.com", "12345678900"));
        });

        assertEquals("CPF ou e-mail já cadastrado.", exception.getMessage());
    }
}
