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
 * Componente visual responsável por gerenciar Ordens de Serviço.
 * Agora delega a seleção/criação de Cliente e Veículo para componentes específicos.
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
    private CompGerenciarCliente compGerenciarCliente;
    private CompGerenciarVeiculo compGerenciarVeiculo;

    public CompGerenciarOS(OrdemServicoService ordemServicoService, ClienteService clienteService,
                            VeiculoService veiculoService, UsuarioService usuarioService,
                            ServicoService servicoService, ItemEstoqueService itemEstoqueService,
                            Scanner scanner, ElevadorService elevadorService,
                            CompGerenciarCliente compGerenciarCliente,
                            CompGerenciarVeiculo compGerenciarVeiculo) {
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.usuarioService = usuarioService;
        this.servicoService = servicoService;
        this.itemEstoqueService = itemEstoqueService;
        this.scanner = scanner;
        this.elevadorService = elevadorService;
        this.compGerenciarCliente = compGerenciarCliente; 
        this.compGerenciarVeiculo = compGerenciarVeiculo;
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
            case 1:
                criarNovaOrdemServicoIntegrada();
                break;
            case 2:
                listarTodasOrdensDeServico();
                break;
            case 3:
                atualizarStatusOrdemServico();
                break;
            case 4:
                gerenciarServicosDeOrdem();
                break;
            case 5:
                verDetalhesOrdemServico();
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
     * Implementa o fluxo de criação de Ordem de Serviço, utilizando os componentes
     * de gerenciamento de Cliente e Veículo para a seleção.
     */
    private void criarNovaOrdemServicoIntegrada() {
        System.out.println("\n--- CRIAR NOVA ORDEM DE SERVIÇO ---");
        Cliente cliente = null;
        Veiculo veiculo = null;
        Usuario mecanicoResponsavel = null;

        // --- 1. Seleção/Criação do Cliente (DELEGADO AO CompGerenciarCliente) ---
        Optional<Cliente> clienteOpt = compGerenciarCliente.selecionarOuCriarCliente();
        if (clienteOpt.isPresent()) {
            cliente = clienteOpt.get();
        } else {
            System.out.println("Seleção/Criação de cliente falhou ou foi cancelada. Abortando criação da OS.");
            return;
        }

        // --- 2. Seleção/Criação do Veículo (DELEGADO AO CompGerenciarVeiculo) ---
        Optional<Veiculo> veiculoOpt = compGerenciarVeiculo.selecionarOuCriarVeiculo(cliente.getId());
        if (veiculoOpt.isPresent()) {
            veiculo = veiculoOpt.get();
        } else {
            System.out.println("Seleção/Criação de veículo falhou ou foi cancelada. Abortando criação da OS.");
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

        // --- LÓGICA DE INTERAÇÃO COM ELEVADOR (PERTENCE AQUI NA VIEW) ---
        Optional<Integer> idElevadorParaAlocar = Optional.empty();

        // Se o status está mudando PARA EM_DIAGNOSTICO ou EM_EXECUCAO
        if ((novoStatus == StatusOrdem.EM_DIAGNOSTICO || novoStatus == StatusOrdem.EM_EXECUCAO) &&
                !(os.getStatus() == StatusOrdem.EM_DIAGNOSTICO || os.getStatus() == StatusOrdem.EM_EXECUCAO)) {

            System.out.println("\n[SISTEMA ELEVADOR] Alocação necessária para OS " + os.getCodigo() + "...");

            // 1. Verificar se a OS requer elevador de alinhamento
            boolean osRequerAlinhamento = os.getServicos().stream()
                    .anyMatch(Servico::requerPrioridade);

            List<Elevador> elevadoresDisponiveis = elevadorService.listarElevadoresDisponiveis();

            if (elevadoresDisponiveis.isEmpty()) {
                System.err.println("Nenhum elevador disponível no momento. Não será possível alocar.");
                return;
            }

            // --- DECLARAÇÃO DE elevadoresFiltrados FORA DO IF/ELSE ---
            List<Elevador> elevadoresFiltrados;

            if (osRequerAlinhamento) {
                elevadoresFiltrados = elevadoresDisponiveis.stream()
                        .filter(Elevador::temCapacidadeAlinhamento)
                        .collect(Collectors.toList());
                System.out.println("OS requer elevador de Alinhamento. Elevadores disponíveis para Alinhamento:");
            } else { // OS NÃO requer alinhamento
                elevadoresFiltrados = elevadoresDisponiveis.stream()
                        .filter(e -> !e.temCapacidadeAlinhamento())
                        .collect(Collectors.toList());
                if (elevadoresFiltrados.isEmpty()) {
                    elevadoresFiltrados.addAll(elevadoresDisponiveis);
                    System.out.println("Nenhum elevador geral disponível. Usando elevador de alinhamento se disponível.");
                } else {
                    System.out.println("OS não requer elevador de Alinhamento. Elevadores Gerais disponíveis:");
                }
            }

            // AQUI O USUÁRIO ESCOLHE O ELEVADOR
            for (int i = 0; i < elevadoresFiltrados.size(); i++) {
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
            // Agora, passa o Optional<Integer> idElevadorParaAlocar para o serviço
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

        // CHAMA O CompGerenciarServico, passando a OS e os Services necessários
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

        // Detalhes adicionais (requer buscar Cliente, Veiculo, Mecanico)
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(os.getIdCliente());
        Optional<Veiculo> veiculoOpt = veiculoService.buscarVeiculoPorId(os.getIdVeiculo());
        Optional<Usuario> mecanicoOpt = usuarioService.buscarUsuarioPorId(os.getIdMecanicoResponsavel());

        System.out.println("  Cliente: " + (clienteOpt.isPresent() ? clienteOpt.get().getNome() : "Desconhecido"));
        System.out.println("  Veículo: " + (veiculoOpt.isPresent() ? veiculoOpt.get().getPlaca() + " (" + veiculoOpt.get().getModelo() + ")" : "Desconhecido"));
        System.out.println("  Mecânico: " + (mecanicoOpt.isPresent() ? mecanicoOpt.get().getNome() : "Desconhecido"));

        // Listar detalhes dos serviços (requer buscar ItemEstoque para peças)
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

    // --- Métodos Auxiliares de Leitura de Input (EXISTENTES) ---
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
                scanner.nextLine();
            }
        }
    }
}