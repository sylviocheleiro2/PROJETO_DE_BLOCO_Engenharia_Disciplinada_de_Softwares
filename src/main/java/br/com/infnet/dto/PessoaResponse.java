package br.com.infnet.dto;

import br.com.infnet.model.Pessoa;

// DTO para enviar dados de Pessoa como resposta.
public record PessoaResponse(int id, String nome, int idade, String email, String cpf) {

    // Método de fábrica para converter a entidade Pessoa em um PessoaResponse.
    public static PessoaResponse from(Pessoa pessoa) {
        return new PessoaResponse(pessoa.getId(), pessoa.getNome(), pessoa.getIdade(), pessoa.getEmail(), pessoa.getCpf());
    }
}
