package br.com.infnet.repository;

import br.com.infnet.model.Pessoa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PessoaRepository {

    private static final Logger log = LoggerFactory.getLogger(PessoaRepository.class);

    public Pessoa save(Pessoa pessoa) {
        String sql = "INSERT INTO pessoas(nome, idade, email, cpf) VALUES(?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, pessoa.getNome());
            pstmt.setInt(2, pessoa.getIdade());
            pstmt.setString(3, pessoa.getEmail());
            pstmt.setString(4, pessoa.getCpf());
            pstmt.executeUpdate();

            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                int id = generatedKeys.getInt(1);
                return new Pessoa(id, pessoa.getNome(), pessoa.getIdade(), pessoa.getEmail(), pessoa.getCpf());
            } else {
                throw new SQLException("Falha ao criar pessoa, nenhum ID obtido.");
            }
        } catch (SQLException e) {
            // Tratamento específico para violação de chave única (CPF/email duplicado)
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                log.warn("Tentativa de inserir CPF ou e-mail duplicado: {}", e.getMessage());
                throw new IllegalArgumentException("CPF ou e-mail já cadastrado.");
            }
            log.error("Erro ao salvar pessoa.", e);
            throw new RuntimeException(e);
        }
    }

    public Optional<Pessoa> findById(int id) {
        String sql = "SELECT * FROM pessoas WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return Optional.of(mapRowToPessoa(rs));
            }
        } catch (SQLException e) {
            log.error("Erro ao buscar pessoa por ID.", e);
        }
        return Optional.empty();
    }

    public List<Pessoa> findAll() {
        List<Pessoa> pessoas = new ArrayList<>();
        String sql = "SELECT * FROM pessoas";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                pessoas.add(mapRowToPessoa(rs));
            }
        } catch (SQLException e) {
            log.error("Erro ao listar pessoas.", e);
        }
        return pessoas;
    }

    public Pessoa update(Pessoa pessoa) {
        String sql = "UPDATE pessoas SET nome = ?, idade = ?, email = ?, cpf = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, pessoa.getNome());
            pstmt.setInt(2, pessoa.getIdade());
            pstmt.setString(3, pessoa.getEmail());
            pstmt.setString(4, pessoa.getCpf());
            pstmt.setInt(5, pessoa.getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                return pessoa;
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                log.warn("Tentativa de atualizar para CPF ou e-mail duplicado: {}", e.getMessage());
                throw new IllegalArgumentException("CPF ou e-mail já pertence a outro usuário.");
            }
            log.error("Erro ao atualizar pessoa.", e);
            throw new RuntimeException(e);
        }
        return null; // Ou lançar exceção se a pessoa não for encontrada
    }

    public boolean deleteById(int id) {
        String sql = "DELETE FROM pessoas WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            log.error("Erro ao deletar pessoa.", e);
        }
        return false;
    }

    private Pessoa mapRowToPessoa(ResultSet rs) throws SQLException {
        return new Pessoa(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getInt("idade"),
                rs.getString("email"),
                rs.getString("cpf")
        );
    }
}
