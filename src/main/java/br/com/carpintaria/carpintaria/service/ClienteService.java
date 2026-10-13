package br.com.carpintaria.carpintaria.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.com.carpintaria.carpintaria.model.Cliente;
import br.com.carpintaria.carpintaria.repository.ClienteRepository;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /*
     * Cadastrar cliente
     */
    public Cliente cadastrar(Cliente cliente) {

        if (clienteRepository.existsByCpfCnpj(cliente.getCpfCnpj())) {
            throw new RuntimeException(
                "Já existe um cliente cadastrado com este CPF/CNPJ."
            );
        }

        return clienteRepository.save(cliente);
    }

    /*
     * Listar todos os clientes
     */
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    /*
     * Buscar cliente pelo ID
     */
    public Cliente buscarPorId(Long id) {

        return clienteRepository
                .findById(id)
                .orElseThrow(() ->
                    new RuntimeException(
                        "Cliente não encontrado."
                    )
                );
    }

    /*
     * Pesquisar cliente pelo nome
     */
    public List<Cliente> pesquisarPorNome(String nome) {

        return clienteRepository
                .findByNomeRazaoSocialContainingIgnoreCase(nome);
    }

    /*
     * Atualizar cliente
     */
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {

        Cliente cliente = buscarPorId(id);

        /*
         * Se alterou o CPF/CNPJ, verifica se
         * já existe para outro cliente.
         */
        if (!cliente.getCpfCnpj()
                .equals(clienteAtualizado.getCpfCnpj())
                &&
                clienteRepository.existsByCpfCnpj(
                    clienteAtualizado.getCpfCnpj()
                )) {

            throw new RuntimeException(
                "Já existe um cliente cadastrado com este CPF/CNPJ."
            );
        }

        cliente.setNomeRazaoSocial(
            clienteAtualizado.getNomeRazaoSocial()
        );

        cliente.setCpfCnpj(
            clienteAtualizado.getCpfCnpj()
        );

        cliente.setTelefone(
            clienteAtualizado.getTelefone()
        );

        cliente.setEmail(
            clienteAtualizado.getEmail()
        );

        cliente.setEndereco(
            clienteAtualizado.getEndereco()
        );

        cliente.setCidade(
            clienteAtualizado.getCidade()
        );

        cliente.setCep(
            clienteAtualizado.getCep()
        );

        cliente.setObservacoes(
            clienteAtualizado.getObservacoes()
        );

        return clienteRepository.save(cliente);
    }

    /*
     * Excluir cliente
     */
    public void excluir(Long id) {

        Cliente cliente = buscarPorId(id);

        clienteRepository.delete(cliente);
    }

    /*
     * Contar clientes
     */
    public long contarClientes() {

        return clienteRepository.count();
    }
}