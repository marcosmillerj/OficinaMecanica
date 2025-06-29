/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import models.Cliente;
import models.Veiculo;
import repository.ClienteRepository;
import repository.VeiculoRepository;

/**
 *
 * @author marcos_miller
 */
public class ClienteService {

    private ClienteRepository clienteRepository;
    private VeiculoRepository veiculoRepository; // Dependência para associar veículos ao cliente

    public ClienteService(ClienteRepository clienteRepository, VeiculoRepository veiculoRepository) {
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
    }

    /**
     * Adiciona um novo cliente ao sistema.
     * Realiza validações de unicidade de email e telefone.
     * @param nome Nome do cliente.
     * @param telefone Telefone do cliente.
     * @param email Email do cliente.
     * @return O Cliente criado.
     * @throws IllegalStateException Se o email ou telefone já estiverem cadastrados.
     */
    public Cliente adicionarCliente(String nome, String telefone, String email) throws IllegalStateException {
        Objects.requireNonNull(nome, "Nome do cliente não pode ser nulo.");
        Objects.requireNonNull(telefone, "Telefone do cliente não pode ser nulo.");
        Objects.requireNonNull(email, "Email do cliente não pode ser nulo.");

        // Validações de unicidade
        if (clienteRepository.buscarClientePorEmail(email).isPresent()) {
            throw new IllegalStateException("Erro: Email '" + email + "' já cadastrado.");
        }
        if (clienteRepository.buscarClientePorTelefone(telefone).isPresent()) {
            throw new IllegalStateException("Erro: Telefone '" + telefone + "' já cadastrado.");
        }

        Cliente novoCliente = new Cliente(nome, telefone, email);
        clienteRepository.adicionarCliente(novoCliente); // Delega ao repositório para adicionar e salvar
        return novoCliente;
    }

    /**
     * Atualiza os dados de um cliente existente.
     * @param idCliente ID do cliente a ser atualizado.
     * @param novoNome Novo nome.
     * @param novoTelefone Novo telefone.
     * @param novoEmail Novo email.
     * @return true se atualizado, false se não encontrado.
     * @throws IllegalStateException Se o novo email ou telefone já pertencerem a outro cliente.
     */
    public boolean atualizarCliente(int idCliente, String novoNome, String novoTelefone, String novoEmail) throws IllegalStateException {
        Objects.requireNonNull(novoNome, "Novo nome do cliente não pode ser nulo.");
        Objects.requireNonNull(novoTelefone, "Novo telefone do cliente não pode ser nulo.");
        Objects.requireNonNull(novoEmail, "Novo email do cliente não pode ser nulo.");

        Optional<Cliente> clienteOpt = clienteRepository.buscarClientePorId(idCliente);
        if (clienteOpt.isEmpty()) {
            return false; // Cliente não encontrado
        }
        Cliente clienteParaAtualizar = clienteOpt.get();

        // Validação de unicidade para email/telefone se forem alterados e pertencerem a outro cliente
        if (!clienteParaAtualizar.getEmail().equalsIgnoreCase(novoEmail)) { // Ignora case para email
            Optional<Cliente> existentePorEmail = clienteRepository.buscarClientePorEmail(novoEmail);
            if (existentePorEmail.isPresent() && existentePorEmail.get().getId() != idCliente) {
                throw new IllegalStateException("Erro: Novo email '" + novoEmail + "' já cadastrado para outro cliente.");
            }
        }
        if (!clienteParaAtualizar.getTelefone().equals(novoTelefone)) {
            Optional<Cliente> existentePorTelefone = clienteRepository.buscarClientePorTelefone(novoTelefone);
            if (existentePorTelefone.isPresent() && existentePorTelefone.get().getId() != idCliente) {
                throw new IllegalStateException("Erro: Novo telefone '" + novoTelefone + "' já cadastrado para outro cliente.");
            }
        }

        clienteParaAtualizar.setNome(novoNome);
        clienteParaAtualizar.setTelefone(novoTelefone);
        clienteParaAtualizar.setEmail(novoEmail);
        clienteRepository.atualizarCliente(clienteParaAtualizar); // Delega ao repositório para atualizar e salvar
        return true;
    }

    /**
     * Adiciona o ID de um veículo à lista de IDs de veículos de um cliente.
     * Isso também atualiza o cliente no repositório.
     * @param idCliente ID do cliente.
     * @param idVeiculo ID do veículo.
     * @return true se adicionado com sucesso, false caso contrário.
     * @throws IllegalArgumentException Se cliente ou veículo não encontrados.
     */
    public boolean adicionarVeiculoAoCliente(int idCliente, int idVeiculo) throws IllegalArgumentException {
        Optional<Cliente> clienteOpt = clienteRepository.buscarClientePorId(idCliente);
        if (clienteOpt.isEmpty()) {
            throw new IllegalArgumentException("Cliente com ID " + idCliente + " não encontrado.");
        }
        Optional<Veiculo> veiculoOpt = veiculoRepository.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            throw new IllegalArgumentException("Veículo com ID " + idVeiculo + " não encontrado.");
        }

        Cliente cliente = clienteOpt.get();
        cliente.adicionarIdVeiculo(idVeiculo); // Adiciona o ID do veículo à lista interna do cliente
        clienteRepository.atualizarCliente(cliente); // Delega ao repositório para atualizar e salvar
        return true;
    }
    
    // Métodos de busca e listagem
    public Optional<Cliente> buscarClientePorId(int id) {
        return clienteRepository.buscarClientePorId(id);
    }
    
    public Optional<Cliente> buscarClientePorEmail(String email) {
        return clienteRepository.buscarClientePorEmail(email);
    }
    
    public Optional<Cliente> buscarClientePorTelefone(String telefone) {
        return clienteRepository.buscarClientePorTelefone(telefone);
    }

    public List<Cliente> listarTodosClientes() {
        return clienteRepository.listarTodosClientes();
    }

    public boolean removerCliente(int id) {
        return clienteRepository.removerCliente(id);
    }

    /**
     * Retorna uma lista de clientes ordenada por um Comparator específico.
     * Isso demonstra a aplicação do padrão Strategy.
     * @param comparator O Comparator a ser usado para ordenação.
     * @return Uma nova lista de clientes ordenada.
     */
    public List<Cliente> listarClientesOrdenados(Comparator<Cliente> comparator) {
        Objects.requireNonNull(comparator, "Comparator não pode ser nulo.");
        List<Cliente> todosClientes = clienteRepository.listarTodosClientes();
        return todosClientes.stream()
                            .sorted(comparator)
                            .collect(Collectors.toList());
    }
}