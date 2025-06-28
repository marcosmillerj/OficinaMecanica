/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.componentes;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.Cliente;
import models.OrdemServico;
import models.Usuario;
import models.Veiculo;
import models.enums.StatusOrdem;
import models.enums.TipoUsuario;
import service.ClienteService;
import service.OrdemServicoService;
import service.UsuarioService;
import service.VeiculoService;

/**
 *
 * @author marcos_miller
 */
public class CompGerenciarOS {

    private OrdemServicoService ordemServicoService;
    private ClienteService clienteService;
    private VeiculoService veiculoService;
    private UsuarioService usuarioService; // Para buscar mecânicos
    private Scanner scanner;

    public CompGerenciarOS(OrdemServicoService ordemServicoService, ClienteService clienteService,
                           VeiculoService veiculoService, UsuarioService usuarioService, Scanner scanner) {
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.usuarioService = usuarioService;
        this.scanner = scanner;
    }

    /**
     * Exibe o menu de opções para gerenciamento de Ordens de Serviço.
     * Implementa o fluxo de criação de OS integrada.
     */
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Ordens de Serviço =====");
            System.out.println("1. Criar Nova Ordem de Serviço");
            System.out.println("2. Listar Todas as Ordens de Serviço");
            System.out.println("3. Atualizar Status de Ordem de Serviço");
            // Adicionar mais opções futuramente (adicionar/remover serviço, etc.)
            System.out.println("0. Voltar ao Painel Principal");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Consome a nova linha
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine();
                opcao = -1; // Opção inválida para repetir o loop
            }

            processarOpcao(opcao);

        } while (opcao != 0);
    }


    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                criarNovaOrdemServicoIntegrada();
                break;
            case 2:
                listarTodasOrdensDeServico();
                break;
            case 3:
                atualizarStatusOrdemServico();
                break;
            case 0:
                System.out.println("Saindo do Gerenciamento de Ordens de Serviço.");
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                break;
        }
    }

    /**
     * Implementa o fluxo de criação de Ordem de Serviço, com cadastro de cliente e veículo on-demand.
     */
    public void criarNovaOrdemServicoIntegrada() {
        System.out.println("\n--- CRIAR NOVA ORDEM DE SERVIÇO ---");
        Cliente cliente = null;
        Veiculo veiculo = null;
        Usuario mecanicoResponsavel = null;

        // --- 1. Seleção/Criação do Cliente ---
        System.out.print("Cliente já cadastrado no sistema? (S/N): ");
        String respCliente = scanner.nextLine().trim().toUpperCase();
        if (respCliente.equals("S")) {
            Optional<Cliente> clienteOpt = solicitarClienteExistente();
            if (clienteOpt.isPresent()) {
                cliente = clienteOpt.get();
            } else {
                System.out.println("Cliente não encontrado. Por favor, tente novamente ou cadastre um novo cliente.");
                return; // Aborta a criação da OS
            }
        } else if (respCliente.equals("N")) {
            Optional<Cliente> novoClienteOpt = solicitarDadosNovoCliente();
            if (novoClienteOpt.isPresent()) {
                cliente = novoClienteOpt.get();
            } else {
                System.out.println("Cadastro de cliente falhou. Abortando criação da OS.");
                return;
            }
        } else {
            System.out.println("Resposta inválida. Abortando criação da OS.");
            return;
        }

        // --- 2. Seleção/Criação do Veículo ---
        System.out.print("Veículo já cadastrado no sistema? (S/N): ");
        String respVeiculo = scanner.nextLine().trim().toUpperCase();
        if (respVeiculo.equals("S")) {
            Optional<Veiculo> veiculoOpt = solicitarVeiculoExistente();
            if (veiculoOpt.isPresent()) {
                veiculo = veiculoOpt.get();
            } else {
                System.out.println("Veículo não encontrado. Por favor, tente novamente ou cadastre um novo veículo.");
                return; // Aborta a criação da OS
            }
        } else if (respVeiculo.equals("N")) {
            Optional<Veiculo> novoVeiculoOpt = solicitarDadosNovoVeiculo(cliente.getId()); // Passa o ID do cliente
            if (novoVeiculoOpt.isPresent()) {
                veiculo = novoVeiculoOpt.get();
            } else {
                System.out.println("Cadastro de veículo falhou. Abortando criação da OS.");
                return;
            }
        } else {
            System.out.println("Resposta inválida. Abortando criação da OS.");
            return;
        }

        // --- 3. Seleção do Mecânico Responsável ---
        Optional<Usuario> mecanicoOpt = solicitarMecanicoResponsavel();
        if (mecanicoOpt.isPresent()) {
            mecanicoResponsavel = mecanicoOpt.get();
        } else {
            System.out.println("Mecânico responsável não selecionado. Abortando criação da OS.");
            return;
        }

        // --- Tentar Criar a Ordem de Serviço ---
        try {
            OrdemServico novaOS = ordemServicoService.criarNovaOrdemServico(
                cliente.getId(),
                veiculo.getId(),
                mecanicoResponsavel.getId()
            );
            System.out.println("Ordem de Serviço " + novaOS.getCodigo() + " criada com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao criar Ordem de Serviço: " + e.getMessage());
        }
    }

    // --- Métodos Auxiliares para Coleta/Busca de Cliente ---
    private Optional<Cliente> solicitarClienteExistente() {
        System.out.print("Digite o Email do Cliente: ");
        String email = scanner.nextLine();
        return clienteService.buscarClientePorEmail(email);
    }

    private Optional<Cliente> solicitarDadosNovoCliente() {
        System.out.println("--- NOVO CADASTRO DE CLIENTE ---");
        System.out.print("Nome do Cliente: ");
        String nome = scanner.nextLine();
        System.out.print("Telefone do Cliente: ");
        String telefone = scanner.nextLine();
        System.out.print("Email do Cliente: ");
        String email = scanner.nextLine();

        try {
            Cliente novoCliente = clienteService.adicionarCliente(nome, telefone, email);
            System.out.println("Cliente '" + novoCliente.getNome() + "' cadastrado com sucesso!");
            return Optional.of(novoCliente);
        } catch (IllegalStateException e) {
            System.err.println("Erro ao cadastrar cliente: " + e.getMessage());
            return Optional.empty();
        }
    }

    // --- Métodos Auxiliares para Coleta/Busca de Veículo ---
    private Optional<Veiculo> solicitarVeiculoExistente() {
        System.out.print("Digite a Placa do Veículo: ");
        String placa = scanner.nextLine();
        return veiculoService.buscarVeiculoPorPlaca(placa);
    }

    private Optional<Veiculo> solicitarDadosNovoVeiculo(int idClienteProprietario) {
        System.out.println("--- NOVO CADASTRO DE VEÍCULO ---");
        System.out.print("Placa do Veículo: ");
        String placa = scanner.nextLine();
        System.out.print("Modelo do Veículo: ");
        String modelo = scanner.nextLine();
        System.out.print("Cor do Veículo: ");
        String cor = scanner.nextLine();

        try {
            Veiculo novoVeiculo = veiculoService.adicionarVeiculo(placa, modelo, cor, idClienteProprietario);
            // IMPORTANTE: Adicionar o veículo ao cliente!
            clienteService.adicionarVeiculoAoCliente(idClienteProprietario, novoVeiculo.getId());
            System.out.println("Veículo '" + novoVeiculo.getPlaca() + "' cadastrado e associado ao cliente com sucesso!");
            return Optional.of(novoVeiculo);
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Erro ao cadastrar veículo: " + e.getMessage());
            return Optional.empty();
        }
    }

    // --- Métodos Auxiliares para Seleção de Mecânico ---
    private Optional<Usuario> solicitarMecanicoResponsavel() {
        System.out.println("\n--- SELECIONAR MECÂNICO RESPONSÁVEL ---");
        List<Usuario> mecanicos = usuarioService.listarUsuarios().stream()
                                    .filter(u -> u.getTipo() == TipoUsuario.MECANICO)
                                    .collect(java.util.stream.Collectors.toList());

        if (mecanicos.isEmpty()) {
            System.out.println("Nenhum mecânico cadastrado no sistema.");
            return Optional.empty();
        }

        System.out.println("Mecânicos disponíveis:");
        for (int i = 0; i < mecanicos.size(); i++) {
            System.out.println((i + 1) + ". " + mecanicos.get(i).getNome() + " (CPF: " + mecanicos.get(i).getCpfPseudoanonimizado() + ")");
        }
        System.out.print("Digite o número do mecânico: ");
        int opcaoMecanico = -1;
        try {
            opcaoMecanico = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Tente novamente.");
            scanner.nextLine();
            return Optional.empty();
        }

        if (opcaoMecanico > 0 && opcaoMecanico <= mecanicos.size()) {
            return Optional.of(mecanicos.get(opcaoMecanico - 1));
        } else {
            System.out.println("Opção de mecânico inválida.");
            return Optional.empty();
        }
    }

    // --- Métodos de Listagem (Para o Menu de OS) ---
    private void listarTodasOrdensDeServico() {
        System.out.println("\n--- LISTA DE ORDENS DE SERVIÇO ---");
        List<OrdemServico> ordens = ordemServicoService.listarTodasOrdens();
        if (ordens.isEmpty()) {
            System.out.println("Nenhuma ordem de serviço cadastrada.");
        } else {
            ordens.forEach(System.out::println);
        }
    }

    // --- Métodos de Atualização de Status (Para o Menu de OS) ---
    public void atualizarStatusOrdemServico() {
        System.out.println("\n--- ATUALIZAR STATUS DA ORDEM DE SERVIÇO ---");
        System.out.print("Digite o ID da Ordem de Serviço: ");
        int idOs = -1;
        try {
            idOs = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número para o ID.");
            scanner.nextLine();
            return;
        }

        Optional<OrdemServico> osOpt = ordemServicoService.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) {
            System.out.println("Ordem de Serviço com ID " + idOs + " não encontrada.");
            return;
        }
        OrdemServico os = osOpt.get();
        System.out.println("OS atual: " + os.getCodigo() + " - Status: " + os.getStatus().getDescricao());

        System.out.println("Selecione o novo status:");
        StatusOrdem[] statuses = StatusOrdem.values();
        for (int i = 0; i < statuses.length; i++) {
            System.out.println((i + 1) + ". " + statuses[i].getDescricao());
        }
        System.out.print("Opção do Status: ");
        int statusOpcao = -1;
        try {
            statusOpcao = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número para o status.");
            scanner.nextLine();
            return;
        }

        StatusOrdem novoStatus = null;
        try {
            novoStatus = statuses[statusOpcao - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Opção de status inválida.");
            return;
        }

        try {
            ordemServicoService.alterarStatusOrdemServico(os.getId(), novoStatus);
            System.out.println("Status da OS " + os.getCodigo() + " atualizado para " + novoStatus.getDescricao() + " com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
        }
    }
}
