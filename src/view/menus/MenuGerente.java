package view.menus;

import comparator.ClienteComparatorPorEmail;
import comparator.ClienteComparatorPorNome;
import java.util.Comparator; // Apenas para compilação se houver métodos que ainda os usem temporariamente
import java.util.InputMismatchException;
import java.util.List; // Apenas para compilação se houver métodos que ainda os usem temporariamente
import java.util.Scanner;
import models.Cliente; // Apenas para compilação se houver métodos que ainda os usem temporariamente
import repository.UsuarioCRUD;
import service.ClienteService;
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.RelatorioService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import view.componentes.CompGerenciarCliente;
import view.componentes.CompGerenciarEstoque;
import view.componentes.CompGerenciarOS;
import view.componentes.CompGerenciarRelatorio;
import view.componentes.CompGerenciarUsuario;
import view.componentes.CompGerenciarVeiculo;


/**
 *
 * @author marcos_miller
 */
public class MenuGerente {

    private UsuarioService usuarioService;
    private OrdemServicoService ordemServicoService;
    private ClienteService clienteService; // Mantém para passar aos componentes
    private VeiculoService veiculoService; // Mantém para passar aos componentes
    private ItemEstoqueService itemEstoqueService;
    private ServicoService servicoService;
    private UsuarioCRUD usuarioCRUD;
    private Scanner scanner;
    private RelatorioService relatorioService;
    private ElevadorService elevadorService;

    // INSTÂNCIAS DOS NOVOS COMPONENTES VISUAIS PARA GERENCIAR CLIENTES E VEÍCULOS
    // Estes serão instanciados uma vez no construtor do MenuGerente
    private CompGerenciarCliente compGerenciarCliente;
    private CompGerenciarVeiculo compGerenciarVeiculo;

    /**
     * Construtor do MenuGerente.
     * Recebe todas as dependências necessárias para suas operações.
     */
    public MenuGerente(UsuarioCRUD usuarioCRUD, Scanner scanner, UsuarioService usuarioService,
                       OrdemServicoService ordemServicoService, ClienteService clienteService,
                       VeiculoService veiculoService, ItemEstoqueService itemEstoqueService,
                       ServicoService servicoService, RelatorioService relatorioService,
                       ElevadorService elevadorService) {
        this.usuarioCRUD = usuarioCRUD;
        this.scanner = scanner;
        this.usuarioService = usuarioService;
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.itemEstoqueService = itemEstoqueService;
        this.servicoService = servicoService;
        this.relatorioService = relatorioService;
        this.elevadorService = elevadorService;

        // Inicializa os componentes de gerenciamento de Cliente e Veículo
        // Eles recebem os services e o scanner que o MenuGerente já possui.
        this.compGerenciarCliente = new CompGerenciarCliente(this.clienteService, this.scanner);
        this.compGerenciarVeiculo = new CompGerenciarVeiculo(this.veiculoService, this.clienteService, this.scanner);
    }

    /**
     * Exibe o menu principal do Gerente e processa as opções escolhidas.
     */
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Menu do Gerente =====");
            System.out.println("1. Gerenciar Usuários");
            System.out.println("2. Gerenciar Estoque");
            System.out.println("3. Acessar Relatórios Financeiros");
            System.out.println("4. Gerenciar Ordens de Serviço");
            System.out.println("5. Gerenciar Clientes (Cadastro/Consulta)"); // Renomeado para refletir a nova responsabilidade
            System.out.println("0. Voltar ao Painel Principal");
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

    /**
     * Processa a opção escolhida pelo Gerente no menu.
     * @param opcao A opção numérica selecionada.
     */
    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                System.out.println("\n--- Abrindo Gerenciamento de Usuários ---");
                CompGerenciarUsuario compGerenciarUsuario = new CompGerenciarUsuario(this.usuarioCRUD, this.scanner);
                compGerenciarUsuario.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Gerente ---");
                break;
            case 2: // Gerenciar Estoque
                System.out.println("\n--- Abrindo Gerenciamento de Estoque ---");
                CompGerenciarEstoque compGerenciarEstoque = new CompGerenciarEstoque(this.itemEstoqueService, this.scanner);
                compGerenciarEstoque.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Gerente ---");
                break;
            case 3: // Acessar Relatórios Financeiros
                System.out.println("\n--- Acessando Relatórios ---");
                CompGerenciarRelatorio compGerarRelatorios = new CompGerenciarRelatorio(this.relatorioService, this.scanner);
                compGerarRelatorios.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Gerente ---");
                break;
            case 4: // Gerenciar Ordens de Serviço
                System.out.println("\n--- Abrindo Gerenciamento de Ordens de Serviço ---");
                CompGerenciarOS compGerenciarOS = new CompGerenciarOS(
                        this.ordemServicoService, this.clienteService, this.veiculoService, this.usuarioService,
                        this.servicoService, this.itemEstoqueService,
                        this.scanner, this.elevadorService, this.compGerenciarCliente, this.compGerenciarVeiculo
                );
                compGerenciarOS.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Gerente ---");
                break;
            case 5: // Gerenciar Clientes (agora delega para o CompGerenciarCliente)
                System.out.println("\n--- Abrindo Gerenciamento de Clientes ---");
                // compGerenciarCliente já está instanciado no construtor
                this.compGerenciarCliente.exibirMenuPrincipal(); // Você precisará criar este método no CompGerenciarCliente
                System.out.println("\n--- Retornando ao Menu do Gerente ---");
                break;
            case 0:
                System.out.println("Voltando ao Painel Principal.");
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                break;
        }
    }

    // --- MÉTODOS REMOVIDOS ---
    // Os métodos 'gerenciarClientes()', 'adicionarCliente()' e 'listarClientes()'
    // foram movidos (ou sua lógica transferida) para 'CompGerenciarCliente'.
}