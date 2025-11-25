package br.com.infnet.service;

import br.com.infnet.model.Pessoa;
import br.com.infnet.repository.PessoaRepository;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Serviço responsável pelas operações de negócio de Pessoa.
 */
public class PessoaService implements IPessoaService {

    private final PessoaRepository pessoaRepository = new PessoaRepository();

    @Override
    public Pessoa criarPessoa(String nome, int idade, String email, String cpf) {
        // A validação é feita no construtor de Pessoa.
        // O ID é gerenciado pelo banco de dados, então passamos um valor temporário (0).
        Pessoa pessoaParaSalvar = new Pessoa(0, nome, idade, email, cpf);
        return pessoaRepository.save(pessoaParaSalvar);
    }

    @Override
    public Pessoa consultarPessoa(int id) {
        return pessoaRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Pessoa não encontrada."));
    }

    @Override
    public Pessoa atualizarPessoa(int id, String nome, int idade, String email, String cpf) {
        // Garante que a pessoa existe antes de tentar atualizar
        consultarPessoa(id); // Lança NoSuchElementException se não existir

        // A validação dos dados é feita no construtor de Pessoa
        Pessoa pessoaAtualizada = new Pessoa(id, nome, idade, email, cpf);
        Pessoa pessoa = pessoaRepository.update(pessoaAtualizada);
        if (pessoa == null) {
            // Esta exceção é um fallback, a verificação acima deve pegar o caso de não existência.
            throw new RuntimeException("Falha ao atualizar a pessoa.");
        }
        return pessoa;
    }

    @Override
    public void removerPessoa(int id) {
        boolean removed = pessoaRepository.deleteById(id);
        if (!removed) {
            throw new NoSuchElementException("Pessoa não encontrada para remoção.");
        }
    }

    @Override
    public List<Pessoa> listarPessoas() {
        return pessoaRepository.findAll();
    }
}
