/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import models.Veiculo;
import repository.ClienteRepository;
import repository.VeiculoRepository;

/**
 *
 * @author marcos_miller
 */
public class VeiculoService {

    private VeiculoRepository veiculoRepository;
    private ClienteRepository clienteRepository;

    public VeiculoService(VeiculoRepository veiculoRepository, ClienteRepository clienteRepository) {
        this.veiculoRepository = veiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    /**
     * Adiciona um novo veículo ao sistema.
     * @param placa Placa do veículo.
     * @param modelo Modelo do veículo.
     * @param cor Cor do veículo.
     * @param idCliente ID do cliente proprietário.
     * @return O Veiculo criado.
     * @throws IllegalStateException Se a placa já estiver cadastrada.
     * @throws IllegalArgumentException Se o cliente proprietário não for encontrado.
     */
    public Veiculo adicionarVeiculo(String placa, String modelo, String cor, int idCliente) throws IllegalStateException, IllegalArgumentException {
        Objects.requireNonNull(placa, "Placa do veículo não pode ser nula.");
        Objects.requireNonNull(modelo, "Modelo do veículo não pode ser nulo.");
        Objects.requireNonNull(cor, "Cor do veículo não pode ser nula.");

        if (veiculoRepository.buscarVeiculoPorPlaca(placa).isPresent()) {
            throw new IllegalStateException("Erro: Placa '" + placa + "' já cadastrada.");
        }

        if (clienteRepository.buscarClientePorId(idCliente).isEmpty()) {
            throw new IllegalArgumentException("Erro: Cliente com ID " + idCliente + " não encontrado para associar ao veículo.");
        }

        Veiculo novoVeiculo = new Veiculo(placa, modelo, cor, idCliente);
        veiculoRepository.adicionarVeiculo(novoVeiculo);
        return novoVeiculo;
    }
    
    /**
     * Atualiza os dados de um veículo existente.
     * @param idVeiculo ID do veículo a ser atualizado.
     * @param novaPlaca Nova placa.
     * @param novoModelo Novo modelo.
     * @param novaCor Nova cor.
     * @return true se atualizado, false se não encontrado.
     * @throws IllegalStateException Se a nova placa já pertencer a outro veículo.
     */
    public boolean atualizarVeiculo(int idVeiculo, String novaPlaca, String novoModelo, String novaCor) throws IllegalStateException {
        Objects.requireNonNull(novaPlaca, "Nova placa do veículo não pode ser nula.");
        Objects.requireNonNull(novoModelo, "Novo modelo do veículo não pode ser nulo.");
        Objects.requireNonNull(novaCor, "Nova cor do veículo não pode ser nula.");

        Optional<Veiculo> veiculoOpt = veiculoRepository.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            return false;
        }
        Veiculo veiculoParaAtualizar = veiculoOpt.get();

        if (!veiculoParaAtualizar.getPlaca().equalsIgnoreCase(novaPlaca)) {
            Optional<Veiculo> existentePorPlaca = veiculoRepository.buscarVeiculoPorPlaca(novaPlaca);
            if (existentePorPlaca.isPresent() && existentePorPlaca.get().getId() != idVeiculo) {
                throw new IllegalStateException("Erro: Nova placa '" + novaPlaca + "' já cadastrada para outro veículo.");
            }
        }

        veiculoParaAtualizar.setPlaca(novaPlaca);
        veiculoParaAtualizar.setModelo(novoModelo);
        veiculoParaAtualizar.setCor(novaCor);
        veiculoRepository.atualizarVeiculo(veiculoParaAtualizar);
        return true;
    }

    // Métodos de busca e listagem
    public Optional<Veiculo> buscarVeiculoPorId(int id) {
        return veiculoRepository.buscarVeiculoPorId(id);
    }
    
    public Optional<Veiculo> buscarVeiculoPorPlaca(String placa) {
        return veiculoRepository.buscarVeiculoPorPlaca(placa);
    }

    public List<Veiculo> listarTodosVeiculos() {
        return veiculoRepository.listarTodosVeiculos();
    }

    public boolean removerVeiculo(int id) {
        return veiculoRepository.removerVeiculo(id);
    }
}
