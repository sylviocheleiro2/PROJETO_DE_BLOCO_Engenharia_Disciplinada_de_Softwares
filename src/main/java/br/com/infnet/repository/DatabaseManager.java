package br.com.infnet.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private static final String DB_URL = "jdbc:sqlite:pessoas.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            String sql = "CREATE TABLE IF NOT EXISTS pessoas (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                         "nome TEXT NOT NULL," +
                         "idade INTEGER NOT NULL," +
                         "email TEXT NOT NULL UNIQUE," +
                         "cpf TEXT NOT NULL UNIQUE);";

            stmt.execute(sql);
            log.info("Banco de dados inicializado com sucesso.");

        } catch (SQLException e) {
            log.error("Erro ao inicializar o banco de dados.", e);
            throw new RuntimeException(e);
        }
    }
}
