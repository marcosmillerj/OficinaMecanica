/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.menus;

import java.util.InputMismatchException;
import java.util.Scanner;
import repository.UsuarioCRUD;
import service.ClienteService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import view.componentes.CompGerenciarEstoque;
import view.componentes.CompGerenciarOS;
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
    private ItemEstoqueService itemEstoqueService; // ATRIBUTO JÁ EXISTENTE
    private ServicoService servicoService;         // NOVO ATRIBUTO! (precisa ser adicionado)
    private UsuarioCRUD usuarioCRUD; // MANTIDO: Atributo para compatibilidade com o construtor do CompGerenciarUsuario no case 1
    private Scanner scanner;

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
     * @param servicoService O serviço de serviços. // NOVO PARÂMETRO DOC!
     */
    public MenuGerente(UsuarioCRUD usuarioCRUD, Scanner scanner, UsuarioService usuarioService,
                       OrdemServicoService ordemServicoService, ClienteService clienteService,
                       VeiculoService veiculoService, ItemEstoqueService itemEstoqueService, // JÁ EXISTENTE
                       ServicoService servicoService) { // NOVO PARÂMETRO!
        this.usuarioCRUD = usuarioCRUD;
        this.scanner = scanner;
        this.usuarioService = usuarioService;
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.itemEstoqueService = itemEstoqueService;
        this.servicoService = servicoService; // INICIALIZA O NOVO ATRIBUTO!
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
            System.out.println("3. Acessar Relatórios Financeiros (Ainda não implementado)");
            System.out.println("4. Gerenciar Ordens de Serviço");
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
            case 3:
                System.out.println("Funcionalidade 'Acessar Relatórios Financeiros' ainda não implementada.");
                break;
            case 4: // Gerenciar Ordens de Serviço
                System.out.println("\n--- Abrindo Gerenciamento de Ordens de Serviço ---");
                // *** AJUSTE AQUI: PASSAR ServicoService e ItemEstoqueService ***
                CompGerenciarOS compGerenciarOS = new CompGerenciarOS(
                    this.ordemServicoService, this.clienteService, this.veiculoService, this.usuarioService,
                    this.servicoService,       // NOVO PARÂMETRO!
                    this.itemEstoqueService,   // NOVO PARÂMETRO!
                    this.scanner
                );
                compGerenciarOS.exibirMenu();
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
}