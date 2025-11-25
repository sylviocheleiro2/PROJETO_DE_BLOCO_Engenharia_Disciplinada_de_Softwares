package br.com.infnet.controller;

import br.com.infnet.service.AuthService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthService authService = new AuthService();

    public void handleLogin(Context ctx) {
        Map<String, String> body = ctx.bodyAsClass(Map.class);
        String email = body.get("email");
        String password = body.get("password");

        authService.authenticate(email, password);

        log.info("Login bem-sucedido para {}", email);
        ctx.header("Location", "/pessoa.html");
        ctx.status(HttpStatus.OK).json(Map.of("message", "Login efetuado com sucesso", "redirectTo", "/pessoa.html"));
    }
}
