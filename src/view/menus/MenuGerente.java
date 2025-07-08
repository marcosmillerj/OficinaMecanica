/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.menus;

import comparator.ClienteComparatorPorEmail;
import comparator.ClienteComparatorPorNome;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import models.Cliente;
import repository.UsuarioCRUD;
import service.ClienteService;
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.RelatorioService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import view.componentes.CompGerenciarEstoque;
import view.componentes.CompGerenciarOS;
import view.componentes.CompGerenciarRelatorio;
import view.componentes.CompGerenciarUsuario;

/**
 *
 * @author marcos_miller
 */
public class MenuGerente {

    private UsuarioService usuarioService;
    private OrdemServicoService ordemServicoService;
    private ClienteService clienteService;
    private VeiculoService veiculoService;
    private ItemEstoqueService itemEstoqueService;
    private ServicoService servicoService;
    private UsuarioCRUD usuarioCRUD;
    private Scanner scanner;
    private RelatorioService relatorioService;
    private ElevadorService elevadorService;

    /**
     * Construtor do MenuGerente.
     * Recebe todas as dependências necessárias para suas operações.
     *
     * @param usuarioCRUD O CRUD de usuários.
     * @param scanner O scanner para entrada do usuário.
     * @param usuarioService O serviço de negócio para usuários.
     * @param ordemServicoService O serviço de ordens de serviço.
     * @param clienteService O serviço de clientes.
     * @param veiculoService O serviço de veículos.
     * @param itemEstoqueService O serviço de itens de estoque.
     * @param servicoService O serviço de serviços.
     * // REMOVIDO: @param elevadorService O serviço de elevadores.
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
            System.out.println("5. Gerenciar Clientes");
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
                    this.scanner, this.elevadorService
                );
                compGerenciarOS.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Gerente ---");
                break;
            case 5: // Gerenciar Clientes (TESTE COMPARATOR)
                gerenciarClientes();
                break;
            case 0:
                System.out.println("Voltando ao Painel Principal.");
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                break;
        }
    }

    // --- MÉTODOS PARA GERENCIAR CLIENTES E DEMONSTRAR COMPARATOR (MANTIDOS) ---
    private void gerenciarClientes() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Clientes =====");
            System.out.println("1. Adicionar Novo Cliente");
            System.out.println("2. Listar Todos os Clientes (Sem Ordenação)");
            System.out.println("3. Listar Clientes por Nome (Ordenado)");
            System.out.println("4. Listar Clientes por Email (Ordenado)");
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
                case 1: adicionarCliente(); break;
                case 2: listarClientes(null); break;
                case 3: listarClientes(new ClienteComparatorPorNome()); break;
                case 4: listarClientes(new ClienteComparatorPorEmail()); break;
                case 0: System.out.println("Saindo do Gerenciamento de Clientes."); break;
                default: System.out.println("Opção inválida. Tente novamente."); break;
            }
        } while (opcao != 0);
    }

    private void adicionarCliente() {
        System.out.println("\n--- ADICIONAR NOVO CLIENTE ---");
        System.out.print("Nome: "); String nome = scanner.nextLine();
        System.out.print("Telefone: "); String telefone = scanner.nextLine();
        System.out.print("Email: "); String email = scanner.nextLine();

        try {
            clienteService.adicionarCliente(nome, telefone, email);
            System.out.println("Cliente adicionado com sucesso!");
        } catch (IllegalStateException e) {
            System.err.println("Erro ao adicionar cliente: " + e.getMessage());
        }
    }

    private void listarClientes(Comparator<Cliente> comparator) {
        System.out.println("\n--- LISTA DE CLIENTES ---");
        List<Cliente> clientes;
        if (comparator != null) {
            clientes = clienteService.listarClientesOrdenados(comparator);
            System.out.println("Lista ordenada.");
        } else {
            clientes = clienteService.listarTodosClientes();
            System.out.println("Lista sem ordenação específica.");
        }

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            clientes.forEach(System.out::println);
        }
    }
}