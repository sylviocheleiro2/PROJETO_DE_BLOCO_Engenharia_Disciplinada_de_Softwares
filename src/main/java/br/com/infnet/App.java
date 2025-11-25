package br.com.infnet;

import br.com.infnet.controller.AuthController;
import br.com.infnet.controller.PessoaController;
import br.com.infnet.model.ErrorResponse;
import br.com.infnet.repository.DatabaseManager;
import io.javalin.Javalin;
import io.javalin.http.HttpStatus;
import io.javalin.http.staticfiles.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {

    private static Javalin app;
    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        if (app != null && app.port() > 0) {
            log.info("Servidor já está rodando.");
            return;
        }

        DatabaseManager.initializeDatabase(); // Inicializa o banco de dados

        app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/static";
                staticFiles.location = Location.CLASSPATH;
            });
        });

        setupExceptionHandlers();
        setupRoutes();

        app.start(7070);
        log.info("Servidor rodando em http://localhost:7070");
        log.info("Acesse o login em http://localhost:7070/index.html ou a tela de cadastro em http://localhost:7070/cadastro.html");
    }

    public static void stop() {
        if (app != null) {
            app.stop();
            app = null;
            log.info("Servidor parado.");
        }
    }

    private static void setupExceptionHandlers() {
        app.exception(IllegalArgumentException.class, (e, ctx) -> {
            log.warn("[VALIDATION] {} - {}", ctx.path(), e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(ErrorResponse.of(ctx.path(), "BAD_REQUEST", e.getMessage()));
        });
        app.exception(NumberFormatException.class, (e, ctx) -> {
            log.warn("[BAD_REQUEST] {} - {}", ctx.path(), e.getMessage());
            ctx.status(HttpStatus.BAD_REQUEST).json(ErrorResponse.of(ctx.path(), "BAD_REQUEST", "Requisição inválida."));
        });
        app.exception(java.util.NoSuchElementException.class, (e, ctx) -> {
            log.warn("[NOT_FOUND] {} - {}", ctx.path(), e.getMessage());
            ctx.status(HttpStatus.NOT_FOUND).json(ErrorResponse.of(ctx.path(), "NOT_FOUND", e.getMessage()));
        });
        app.exception(Exception.class, (e, ctx) -> {
            log.error("[INTERNAL_ERROR] {} - {}", ctx.path(), e.toString());
            ctx.status(HttpStatus.INTERNAL_SERVER_ERROR).json(ErrorResponse.of(ctx.path(), "INTERNAL_ERROR", "Erro interno no servidor."));
        });
    }

    private static void setupRoutes() {
        PessoaController pessoaController = new PessoaController();
        AuthController authController = new AuthController();

        app.get("/api/pessoas", pessoaController::getAll);
        app.get("/api/pessoas/{id}", pessoaController::getOne);
        app.post("/api/pessoas", pessoaController::create);
        app.put("/api/pessoas/{id}", pessoaController::update);
        app.delete("/api/pessoas/{id}", pessoaController::delete);
        app.post("/api/login", authController::handleLogin);
    }
}
