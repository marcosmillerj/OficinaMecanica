package view.componentes;

import java.util.InputMismatchException;
import java.util.Optional;
import java.util.Scanner;
import models.Veiculo;
import service.ClienteService; // Necessário para adicionar veículo ao cliente
import service.VeiculoService;

/**
 * Componente visual responsável por gerenciar operações de Veículo.
 * Isso inclui solicitar veículos existentes ou cadastrar novos.
 *
 * @author marcos_miller
 */
public class CompGerenciarVeiculo {

    private VeiculoService veiculoService;
    private ClienteService clienteService; // Para associar o veículo ao cliente
    private Scanner scanner;

    public CompGerenciarVeiculo(VeiculoService veiculoService, ClienteService clienteService, Scanner scanner) {
        this.veiculoService = veiculoService;
        this.clienteService = clienteService;
        this.scanner = scanner;
    }

    /**
     * Solicita ao usuário que forneça a placa para buscar um veículo existente.
     *
     * @return Um Optional contendo o Veiculo encontrado, ou Optional.empty() se não encontrado.
     */
    public Optional<Veiculo> solicitarVeiculoExistente() {
        System.out.print("Digite a Placa do Veículo: ");
        String placa = scanner.nextLine();
        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorPlaca(placa);
        if (veiculoOpt.isEmpty()) {
            System.out.println("Veículo com placa '" + placa + "' não encontrado.");
        }
        return veiculoOpt;
    }

    /**
     * Solicita ao usuário os dados para cadastrar um novo veículo e o associa a um cliente.
     *
     * @param idClienteProprietario O ID do cliente proprietário do veículo.
     * @return Um Optional contendo o Veiculo recém-cadastrado, ou Optional.empty() em caso de falha.
     */
    public Optional<Veiculo> solicitarDadosNovoVeiculo(int idClienteProprietario) {
        System.out.println("--- NOVO CADASTRO DE VEÍCULO ---");
        System.out.print("Placa do Veículo: ");
        String placa = scanner.nextLine();
        System.out.print("Modelo do Veículo: ");
        String modelo = scanner.nextLine();
        System.out.print("Cor do Veículo: ");
        String cor = scanner.nextLine();

        try {
            Veiculo novoVeiculo = veiculoService.adicionarVeiculo(placa, modelo, cor, idClienteProprietario);
            clienteService.adicionarVeiculoAoCliente(idClienteProprietario, novoVeiculo.getId());
            System.out.println("Veículo '" + novoVeiculo.getPlaca() + "' cadastrado e associado ao cliente com sucesso!");
            return Optional.of(novoVeiculo);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Erro ao cadastrar veículo: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Guia o usuário pelo processo de seleção ou criação de um veículo.
     *
     * @param idClienteProprietario O ID do cliente proprietário para associar o novo veículo.
     * @return Um Optional contendo o Veiculo selecionado/criado, ou Optional.empty() se o processo for abortado.
     */
    public Optional<Veiculo> selecionarOuCriarVeiculo(int idClienteProprietario) {
        System.out.print("Veículo já cadastrado no sistema? (S/N): ");
        String respVeiculo = scanner.nextLine().trim().toUpperCase();

        if (respVeiculo.equals("S")) {
            Optional<Veiculo> veiculoOpt = solicitarVeiculoExistente();
            if (veiculoOpt.isPresent()) {
                return veiculoOpt;
            } else {
                System.out.println("Veículo não encontrado. Você pode tentar novamente ou cadastrar um novo veículo.");
                return Optional.empty(); // Ou pode oferecer para cadastrar um novo aqui
            }
        } else if (respVeiculo.equals("N")) {
            return solicitarDadosNovoVeiculo(idClienteProprietario);
        } else {
            System.out.println("Resposta inválida. Operação de seleção/criação de veículo abortada.");
            return Optional.empty();
        }
    }
}