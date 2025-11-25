package br.com.infnet.controller;

import br.com.infnet.dto.PessoaRequest;
import br.com.infnet.dto.PessoaResponse;
import br.com.infnet.model.Pessoa;
import br.com.infnet.service.PessoaService;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

public class PessoaController {

    private final PessoaService pessoaService = new PessoaService();

    public void getAll(Context ctx) {
        List<Pessoa> pessoas = pessoaService.listarPessoas();
        List<PessoaResponse> response = pessoas.stream()
                .map(PessoaResponse::from)
                .collect(Collectors.toList());
        ctx.json(response);
    }

    public void getOne(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        Pessoa pessoa = pessoaService.consultarPessoa(id);
        ctx.json(PessoaResponse.from(pessoa));
    }

    public void create(Context ctx) {
        PessoaRequest request = ctx.bodyAsClass(PessoaRequest.class);
        Pessoa novaPessoa = pessoaService.criarPessoa(
            request.nome(),
            request.idade(),
            request.email(),
            request.cpf()
        );
        ctx.status(HttpStatus.CREATED).json(PessoaResponse.from(novaPessoa));
    }

    public void update(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        PessoaRequest request = ctx.bodyAsClass(PessoaRequest.class);
        Pessoa pessoaAtualizada = pessoaService.atualizarPessoa(
            id,
            request.nome(),
            request.idade(),
            request.email(),
            request.cpf()
        );
        ctx.json(PessoaResponse.from(pessoaAtualizada));
    }

    public void delete(Context ctx) {
        int id = Integer.parseInt(ctx.pathParam("id"));
        pessoaService.removerPessoa(id);
        ctx.status(HttpStatus.NO_CONTENT);
    }
}
