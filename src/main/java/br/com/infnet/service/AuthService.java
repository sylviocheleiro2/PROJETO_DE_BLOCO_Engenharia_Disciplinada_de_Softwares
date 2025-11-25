package br.com.infnet.service;

import java.util.regex.Pattern;

public class AuthService {

    private static final Pattern emailPattern = Pattern.compile("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");

    public void authenticate(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email e senha são obrigatórios.");
        }

        if (!emailPattern.matcher(email).matches()) {
            throw new IllegalArgumentException("Email inválido.");
        }

        // Em uma aplicação real, a senha seria verificada contra um hash armazenado.
        // Para este exemplo, a lógica de negócio está corretamente isolada nesta camada.
    }
}
