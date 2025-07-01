/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.componentes;

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import models.Cliente;
import models.Elevador;
import models.ItemEstoque;
import models.OrdemServico;
import models.Servico;
import models.Usuario;
import models.Veiculo;
import models.enums.StatusOrdem;
import models.enums.TipoUsuario;
import service.ClienteService;
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.ServicoService;
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
    private UsuarioService usuarioService;
    private ServicoService servicoService;
    private ItemEstoqueService itemEstoqueService;
    private Scanner scanner;
    private ElevadorService elevadorService;

    public CompGerenciarOS(OrdemServicoService ordemServicoService, ClienteService clienteService,
                           VeiculoService veiculoService, UsuarioService usuarioService,
                           ServicoService servicoService, ItemEstoqueService itemEstoqueService,
                           Scanner scanner, ElevadorService elevadorService) {
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.usuarioService = usuarioService;
        this.servicoService = servicoService;
        this.itemEstoqueService = itemEstoqueService;
        this.scanner = scanner;
        this.elevadorService = elevadorService;
    }

    /**
     * Exibe o menu de opções para gerenciamento de Ordens de Serviço.
     */
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Ordens de Serviço =====");
            System.out.println("1. Criar Nova Ordem de Serviço");
            System.out.println("2. Listar Todas as Ordens de Serviço");
            System.out.println("3. Atualizar Status de Ordem de Serviço");
            System.out.println("4. Gerenciar Serviços de uma OS (Adicionar/Remover/Atualizar/Listar)");
            System.out.println("5. Ver Detalhes Completos de uma OS");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine();
                opcao = -1;
            }

            processarOpcao(opcao);

        } while (opcao != 0);
    }


    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1: criarNovaOrdemServicoIntegrada(); break;
            case 2: listarTodasOrdensDeServico(); break;
            case 3: atualizarStatusOrdemServico(); break;
            case 4: gerenciarServicosDeOrdem(); break;
            case 5: verDetalhesOrdemServico(); break;
            case 0: System.out.println("Saindo do Gerenciamento de Ordens de Serviço."); break;
            default: System.out.println("Opção inválida. Tente novamente."); break;
        }
    }

    /**
     * Implementa o fluxo de criação de Ordem de Serviço, com cadastro de cliente e veículo on-demand.
     * (Este método permanece aqui, pois é uma funcionalidade central do CompGerenciarOS)
     */
    private void criarNovaOrdemServicoIntegrada() {
        System.out.println("\n--- CRIAR NOVA ORDEM DE SERVIÇO ---");
        Cliente cliente = null;
        Veiculo veiculo = null;
        Usuario mecanicoResponsavel = null;
        System.out.print("Cliente já cadastrado no sistema? (S/N): ");
        String respCliente = scanner.nextLine().trim().toUpperCase();
        if (respCliente.equals("S")) {
            Optional<Cliente> clienteOpt = solicitarClienteExistente();
            if (clienteOpt.isPresent()) {
                cliente = clienteOpt.get();
            } else {
                System.out.println("Cliente não encontrado. Por favor, tente novamente ou cadastre um novo cliente.");
                return;
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

        System.out.print("Veículo já cadastrado no sistema? (S/N): ");
        String respVeiculo = scanner.nextLine().trim().toUpperCase();
        if (respVeiculo.equals("S")) {
            Optional<Veiculo> veiculoOpt = solicitarVeiculoExistente();
            if (veiculoOpt.isPresent()) {
                veiculo = veiculoOpt.get();
            } else {
                System.out.println("Veículo não encontrado. Por favor, tente novamente ou cadastre um novo veículo.");
                return;
            }
        } else if (respVeiculo.equals("N")) {
            Optional<Veiculo> novoVeiculoOpt = solicitarDadosNovoVeiculo(cliente.getId());
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

        Optional<Usuario> mecanicoOpt = solicitarMecanicoResponsavel();
        if (mecanicoOpt.isPresent()) {
            mecanicoResponsavel = mecanicoOpt.get();
        } else {
            System.out.println("Mecânico responsável não selecionado. Abortando criação da OS.");
            return;
        }

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

    private Optional<Cliente> solicitarClienteExistente() {
        System.out.print("Digite o Email do Cliente: ");
        String email = scanner.nextLine();
        return clienteService.buscarClientePorEmail(email);
    }

    private Optional<Cliente> solicitarDadosNovoCliente() {
        System.out.println("--- NOVO CADASTRO DE CLIENTE ---");
        System.out.print("Nome do Cliente: "); String nome = scanner.nextLine();
        System.out.print("Telefone do Cliente: "); String telefone = scanner.nextLine();
        System.out.print("Email do Cliente: "); String email = scanner.nextLine();

        try {
            Cliente novoCliente = clienteService.adicionarCliente(nome, telefone, email);
            System.out.println("Cliente '" + novoCliente.getNome() + "' cadastrado com sucesso!");
            return Optional.of(novoCliente);
        } catch (IllegalStateException e) {
            System.err.println("Erro ao cadastrar cliente: " + e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<Veiculo> solicitarVeiculoExistente() {
        System.out.print("Digite a Placa do Veículo: ");
        String placa = scanner.nextLine();
        return veiculoService.buscarVeiculoPorPlaca(placa);
    }

    private Optional<Veiculo> solicitarDadosNovoVeiculo(int idClienteProprietario) {
        System.out.println("--- NOVO CADASTRO DE VEÍCULO ---");
        System.out.print("Placa do Veículo: "); String placa = scanner.nextLine();
        System.out.print("Modelo do Veículo: "); String modelo = scanner.nextLine();
        System.out.print("Cor do Veículo: "); String cor = scanner.nextLine();

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

    private void listarTodasOrdensDeServico() {
        System.out.println("\n--- LISTA DE ORDENS DE SERVIÇO ---");
        List<OrdemServico> ordens = ordemServicoService.listarTodasOrdens();
        if (ordens.isEmpty()) {
            System.out.println("Nenhuma ordem de serviço cadastrada.");
        } else {
            ordens.forEach(System.out::println);
        }
    }

    private void atualizarStatusOrdemServico() {
        System.out.println("\n--- ATUALIZAR STATUS DE ORDEM DE SERVIÇO ---");
        System.out.print("Digite o ID da Ordem de Serviço para alterar o status: ");
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
        System.out.println("OS Selecionada: " + os.getCodigo() + " - Status Atual: " + os.getStatus().getDescricao());

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

        Optional<Integer> idElevadorParaAlocar = Optional.empty();

        if ((novoStatus == StatusOrdem.EM_DIAGNOSTICO || novoStatus == StatusOrdem.EM_EXECUCAO) && 
            !(os.getStatus() == StatusOrdem.EM_DIAGNOSTICO || os.getStatus() == StatusOrdem.EM_EXECUCAO)) {
            
            System.out.println("\n[SISTEMA ELEVADOR] Alocação necessária para OS " + os.getCodigo() + "...");
            
            boolean osRequerAlinhamento = os.getServicos().stream()
                                            .anyMatch(Servico::requerPrioridade);

            List<Elevador> elevadoresDisponiveis = elevadorService.listarElevadoresDisponiveis();

            if (elevadoresDisponiveis.isEmpty()) {
                System.err.println("Nenhum elevador disponível no momento. Não será possível alocar.");
                return;
            }

            List<Elevador> elevadoresFiltrados;

            if (osRequerAlinhamento) {
                elevadoresFiltrados = elevadoresDisponiveis.stream()
                                                            .filter(Elevador::temCapacidadeAlinhamento)
                                                            .collect(Collectors.toList());
                System.out.println("OS requer elevador de Alinhamento. Elevadores disponíveis para Alinhamento:");
            } else {
                elevadoresFiltrados = elevadoresDisponiveis.stream()
                                                    .filter(e -> !e.temCapacidadeAlinhamento())
                                                    .collect(Collectors.toList());
                if(elevadoresFiltrados.isEmpty()){
                    elevadoresFiltrados.addAll(elevadoresDisponiveis);
                    System.out.println("Nenhum elevador geral disponível. Usando elevador de alinhamento se disponível.");
                } else {
                    System.out.println("OS não requer elevador de Alinhamento. Elevadores Gerais disponíveis:");
                }
            }
            
            for (int i = 0; i < elevadoresFiltrados.size(); i++) { // <<< ERRO AQUI!
                System.out.println((i + 1) + ". " + elevadoresFiltrados.get(i).toString());
            }
            int escolha = -1;
            try {
                escolha = scanner.nextInt();
                scanner.nextLine();
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Abortando alocação.");
                scanner.nextLine();
                return;
            }

            if (escolha > 0 && escolha <= elevadoresFiltrados.size()) {
                idElevadorParaAlocar = Optional.of(elevadoresFiltrados.get(escolha - 1).getId());
            } else {
                System.err.println("Opção de elevador inválida. Abortando alocação.");
                return;
            }
        }

        try {
            ordemServicoService.alterarStatusOrdemServico(os.getId(), novoStatus, idElevadorParaAlocar);
            System.out.println("Status da OS " + os.getCodigo() + " atualizado para " + novoStatus.getDescricao() + " com sucesso!");

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
        }
    }
    private void gerenciarServicosDeOrdem() {
        System.out.println("\n--- GERENCIAR SERVIÇOS DE UMA ORDEM DE SERVIÇO ESPECÍFICA ---");
        System.out.print("Digite o ID da Ordem de Serviço para gerenciar seus serviços: ");
        int idOs = -1;
        try {
            idOs = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número para o ID da OS.");
            scanner.nextLine();
            return;
        }

        Optional<OrdemServico> osOpt = ordemServicoService.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) {
            System.out.println("Ordem de Serviço com ID " + idOs + " não encontrada.");
            return;
        }
        OrdemServico osSelecionada = osOpt.get();
        System.out.println("OS Selecionada: " + osSelecionada.getCodigo() + " - Status: " + osSelecionada.getStatus().getDescricao());

        CompGerenciarServico compGerenciarServico = new CompGerenciarServico(
            osSelecionada,
            servicoService,
            itemEstoqueService,
            ordemServicoService,
            scanner 
        );
        compGerenciarServico.exibirMenu();
        System.out.println("\n--- Retornando ao Gerenciamento de Ordens de Serviço ---");
    }

    private void verDetalhesOrdemServico() {
        System.out.println("\n--- DETALHES DA ORDEM DE SERVIÇO ---");
        System.out.print("Digite o ID da Ordem de Serviço: ");
        int idOs = -1;
        try {
            idOs = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Digite um número para o ID da OS.");
            scanner.nextLine();
            return;
        }

        Optional<OrdemServico> osOpt = ordemServicoService.buscarOrdemServicoPorId(idOs);
        if (osOpt.isEmpty()) {
            System.out.println("Ordem de Serviço com ID " + idOs + " não encontrada.");
            return;
        }
        OrdemServico os = osOpt.get();
        System.out.println("\n" + os.toString());

        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(os.getIdCliente());
        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorId(os.getIdVeiculo());
        Optional<Usuario> mecanicoOpt = usuarioService.buscarUsuarioPorId(os.getIdMecanicoResponsavel());

        System.out.println("  Cliente: " + (clienteOpt.isPresent() ? clienteOpt.get().getNome() : "Desconhecido"));
        System.out.println("  Veículo: " + (veiculoOpt.isPresent() ? veiculoOpt.get().getPlaca() + " (" + veiculoOpt.get().getModelo() + ")" : "Desconhecido"));
        System.out.println("  Mecânico: " + (mecanicoOpt.isPresent() ? mecanicoOpt.get().getNome() : "Desconhecido"));

        List<Servico> servicosNaOS = os.getServicos();
        if (servicosNaOS.isEmpty()) {
            System.out.println("  Serviços: Nenhum serviço adicionado.");
        } else {
            System.out.println("  Serviços Detalhados:");
            for (Servico servico : servicosNaOS) {
                String pecaInfo = "Sem peça";
                if (servico.getCodigoPeca() != null && !servico.getCodigoPeca().isEmpty()) { 
                    Optional<ItemEstoque> pecaOpt = itemEstoqueService.buscarItemPorCodigo(servico.getCodigoPeca());
                    pecaInfo = pecaOpt.isPresent() ? pecaOpt.get().getNome() : "Peça Desconhecida";
                }
                System.out.printf("    - [Serviço ID:%d] %s (Setor: %s) - R$ %.2f - Requer Prioridade: %b - Peça: %s%n",
                                servico.getId(), servico.getObservacoes(), servico.getSetor().getDescricao(),
                                servico.getPrecoMaoDeObra(), servico.requerPrioridade(), pecaInfo);
            }
        }
        System.out.println("---------------------------------------------");
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

    private BigDecimal lerBigDecimalValido() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, digite um número decimal válido (ex: 12.50).");
            }
        }
    }
}