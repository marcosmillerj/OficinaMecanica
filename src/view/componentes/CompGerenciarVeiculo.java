package view.componentes;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.Cliente;
import models.Veiculo;
import service.ClienteService; 
import service.VeiculoService;

/**
 * Componente visual responsável por gerenciar operações de Veículo.
 * Inclui criar, listar, editar e excluir veículos, além de funcionalidades de seleção on-demand
 * e demonstração dos contadores de instância.
 *
 * @author marcos_miller
 */
public class CompGerenciarVeiculo {

    private final VeiculoService veiculoService;
    private final ClienteService clienteService;
    private final Scanner scanner;

    public CompGerenciarVeiculo(VeiculoService veiculoService, ClienteService clienteService, Scanner scanner) {
        this.veiculoService = veiculoService;
        this.clienteService = clienteService;
        this.scanner = scanner;
    }

    /**
     * Exibe o menu principal para o gerenciamento de veículos,
     * permitindo adicionar, listar, editar, excluir e exibir contagens.
     * (NOVO MÉTODO PARA O MENU)
     */
    public void exibirMenuPrincipal() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Veículos =====");
            System.out.println("1. Adicionar Novo Veículo");
            System.out.println("2. Listar Todos os Veículos");
            System.out.println("3. Editar Veículo Existente");
            System.out.println("4. Excluir Veículo");
            System.out.println("5. Ver Contagem de Veículos Criados");
            System.out.println("0. Voltar ao Menu Anterior");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine(); 
                opcao = -1;
            }

            switch (opcao) {
                case 1:
                    adicionarNovoVeiculoInterno();
                    break;
                case 2:
                    listarTodosVeiculosInterno();
                    break;
                case 3:
                    editarVeiculo();
                    break;
                case 4:
                    excluirVeiculo();
                    break;
                case 5:
                    exibirContagemVeiculos();
                    break;
                case 0:
                    System.out.println("Saindo do Gerenciamento de Veículos.");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        } while (opcao != 0);
    }

    /**
     * Solicita os dados para adicionar um novo veículo.
     * ( MÉTODO INTERNO PARA O MENU)
     */
    private void adicionarNovoVeiculoInterno() {
        System.out.println("\n--- ADICIONAR NOVO VEÍCULO ---");
        System.out.print("Placa: ");
        String placa = scanner.nextLine();
        System.out.print("Modelo: ");
        String modelo = scanner.nextLine();
        System.out.print("Cor: ");
        String cor = scanner.nextLine();
        System.out.print("ID do Cliente Proprietário: ");
        int idCliente = lerInteiroValido();

        try {
            Veiculo novoVeiculo = veiculoService.adicionarVeiculo(placa, modelo, cor, idCliente);
            // Associar o veículo ao cliente no ClienteService
            clienteService.adicionarVeiculoAoCliente(idCliente, novoVeiculo.getId());
            System.out.println("Veículo '" + novoVeiculo.getPlaca() + "' adicionado e associado ao cliente com sucesso!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Erro ao adicionar veículo: " + e.getMessage());
        }
    }

    /**
     * Lista todos os veículos cadastrados.
     * ( MÉTODO INTERNO PARA O MENU)
     */
    private void listarTodosVeiculosInterno() {
        System.out.println("\n--- LISTA DE VEÍCULOS ---");
        List<Veiculo> veiculos = veiculoService.listarTodosVeiculos();

        if (veiculos.isEmpty()) {
            System.out.println("Nenhum veículo cadastrado.");
        } else {
            System.out.println("ID    | Placa       | Modelo             | Cor          | Proprietário");
            System.out.println("--------------------------------------------------------------------------");
            for (Veiculo veiculo : veiculos) {
                // Busca o nome do cliente para exibição
                String nomeCliente = clienteService.buscarClientePorId(veiculo.getIdCliente())
                                                   .map(Cliente::getNome)
                                                   .orElse("Desconhecido");
                System.out.printf("%-5d | %-11s | %-18s | %-12s | %s%n",
                                  veiculo.getId(), veiculo.getPlaca(), veiculo.getModelo(),
                                  veiculo.getCor(), nomeCliente);
            }
            System.out.println("--------------------------------------------------------------------------");
        }
    }

    /**
     * Permite editar um veículo existente.
     * ( MÉTODO INTERNO PARA O MENU)
     */
    private void editarVeiculo() {
        System.out.println("\n--- EDITAR VEÍCULO ---");
        System.out.print("Digite o ID do veículo a ser editado: ");
        int idVeiculo = lerInteiroValido();

        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            System.out.println("Veículo com ID " + idVeiculo + " não encontrado.");
            return;
        }

        Veiculo veiculoAtual = veiculoOpt.get();
        System.out.println("Veículo selecionado: " + veiculoAtual.getPlaca() + " (ID: " + veiculoAtual.getId() + ")");

        System.out.print("Nova placa (deixe em branco para manter '" + veiculoAtual.getPlaca() + "'): ");
        String novaPlaca = scanner.nextLine();
        if (novaPlaca.isEmpty()) {
            novaPlaca = veiculoAtual.getPlaca();
        }

        System.out.print("Novo modelo (deixe em branco para manter '" + veiculoAtual.getModelo() + "'): ");
        String novoModelo = scanner.nextLine();
        if (novoModelo.isEmpty()) {
            novoModelo = veiculoAtual.getModelo();
        }

        System.out.print("Nova cor (deixe em branco para manter '" + veiculoAtual.getCor() + "'): ");
        String novaCor = scanner.nextLine();
        if (novaCor.isEmpty()) {
            novaCor = veiculoAtual.getCor();
        }

        System.out.print("Novo ID do Cliente Proprietário (deixe em branco para manter '" + veiculoAtual.getIdCliente() + "'): ");
        String idClienteStr = scanner.nextLine();
        int novoIdCliente;
        if (idClienteStr.isEmpty()) {
            novoIdCliente = veiculoAtual.getIdCliente();
        } else {
            try {
                novoIdCliente = Integer.parseInt(idClienteStr);
            } catch (NumberFormatException e) {
                System.err.println("ID do cliente inválido. Mantendo o ID original.");
                novoIdCliente = veiculoAtual.getIdCliente();
            }
        }

        try {
            // Chama o serviço com o novo ID do cliente
            boolean atualizado = veiculoService.atualizarVeiculo(idVeiculo, novaPlaca, novoModelo, novaCor, novoIdCliente);
            if (atualizado) {
                System.out.println("Veículo atualizado com sucesso!");
            } else {
                System.out.println("Falha ao atualizar veículo (não encontrado).");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao atualizar veículo: " + e.getMessage());
        }
    }

    /**
     * Permite excluir um veículo.
     * (MÉTODO INTERNO PARA O MENU)
     */
    private void excluirVeiculo() {
        System.out.println("\n--- EXCLUIR VEÍCULO ---");
        System.out.print("Digite o ID do veículo a ser excluído: ");
        int idVeiculo = lerInteiroValido();

        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorId(idVeiculo);
        if (veiculoOpt.isEmpty()) {
            System.out.println("Veículo com ID " + idVeiculo + " não encontrado.");
            return;
        }

        Veiculo veiculoParaExcluir = veiculoOpt.get();
        System.out.println("Tem certeza que deseja excluir o veículo: " + veiculoParaExcluir.getPlaca() + " (ID: " + veiculoParaExcluir.getId() + ")? (S/N)");
        String confirmacao = scanner.nextLine().trim().toUpperCase();

        if (confirmacao.equals("S")) {
            try {
                boolean removido = veiculoService.removerVeiculo(idVeiculo);
                if (removido) {
                    System.out.println("Veículo excluído com sucesso!");
                } else {
                    System.out.println("Falha ao excluir veículo (não encontrado).");
                }
            } catch (IllegalStateException e) { 
                System.err.println("Erro ao excluir veículo: " + e.getMessage());
            }
        } else {
            System.out.println("Exclusão de veículo cancelada.");
        }
    }

    /**
     * Exibe a contagem de instâncias de veículos criadas usando as duas estratégias.
     * ( MÉTODO PARA O REQUISITO)
     */
    private void exibirContagemVeiculos() {
        System.out.println("\n--- CONTAGEM DE INSTÂNCIAS DE VEÍCULO ---");
        System.out.println("Total de veículos criados (Encapsulado - private static): " + Veiculo.getTotalVeiculosCriadosEncapsulado());
        System.out.println("Total de veículos criados (Protegido - protected static): " + Veiculo.getTotalVeiculosCriadosProtegido());
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
        System.out.print("Veículo já cadastrado no sistema? (S/N/C - 'C' para Cancelar): ");
        String respVeiculo = scanner.nextLine().trim().toUpperCase();

        if (respVeiculo.equals("S")) {
            Optional<Veiculo> veiculoOpt = solicitarVeiculoExistente();
            if (veiculoOpt.isPresent()) {
                return veiculoOpt;
            } else {
                System.out.println("Veículo não encontrado. Você pode tentar novamente ou cadastrar um novo veículo.");
                // Retorna Optional.empty() aqui para permitir que o chamador decida o próximo passo.
                return Optional.empty(); 
            }
        } else if (respVeiculo.equals("N")) {
            return solicitarDadosNovoVeiculo(idClienteProprietario);
        } else if (respVeiculo.equals("C")) { // Nova opção de cancelamento
            System.out.println("Operação de seleção/criação de veículo cancelada.");
            return Optional.empty();
        } else {
            System.out.println("Resposta inválida. Operação de seleção/criação de veículo abortada.");
            return Optional.empty();
        }
    }

    // --- Métodos Auxiliares de Leitura de Input ---
    private int lerInteiroValido() {
        while (true) {
            try {
                int valor = scanner.nextInt();
                scanner.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, digite um número inteiro.");
                scanner.nextLine(); 
            }
        }
    }
}