package br.com.infnet.dto;

// Usamos um record para um DTO imutável, ideal para receber dados.
public record PessoaRequest(String nome, int idade, String email, String cpf) {
}
