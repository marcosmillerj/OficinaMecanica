/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.menus;

import java.util.InputMismatchException;
import java.util.Scanner;
import models.Usuario;
import service.ClienteService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.RegistroPontoService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import view.componentes.CompGerenciarEstoque;

/**
 *
 * @author marcos_miller
 */
public class MenuAtendente {

    private ClienteService clienteService;
    private ItemEstoqueService itemEstoqueService;
    private OrdemServicoService ordemServicoService;
    private RegistroPontoService pontoService;
    private ServicoService servicoService;
    private UsuarioService usuarioService;
    private VeiculoService veiculoService;
    private Scanner scanner;
    private Usuario atendenteLogado;

    /**
     * Construtor do MenuAtendente.
     * Recebe as dependências necessárias para suas operações.
     * @param clienteService O serviço de clientes.
     * @param itemEstoqueService O serviço de itens de estoque.
     * @param ordemServicoService O serviço de ordens de serviço.
     * @param pontoService O serviço de registro de ponto (se necessário aqui).
     * @param servicoService O serviço de serviços.
     * @param usuarioService O serviço de usuários.
     * @param veiculoService O serviço de veículos.
     * @param scanner O scanner para entrada do usuário.
     */
    public MenuAtendente(ClienteService clienteService, ItemEstoqueService itemEstoqueService,
                         OrdemServicoService ordemServicoService, RegistroPontoService pontoService,
                         ServicoService servicoService, UsuarioService usuarioService,
                         VeiculoService veiculoService, Scanner scanner) {
        this.clienteService = clienteService;
        this.itemEstoqueService = itemEstoqueService;
        this.ordemServicoService = ordemServicoService;
        this.pontoService = pontoService;
        this.servicoService = servicoService;
        this.usuarioService = usuarioService;
        this.veiculoService = veiculoService;
        this.scanner = scanner;
        this.atendenteLogado = util.UserSession.getInstance().getLoggedInUser();

        if (this.atendenteLogado == null || this.atendenteLogado.getTipo() != models.enums.TipoUsuario.ATENDENTE) {
            System.err.println("Erro: Acesso não autorizado ao Menu Atendente. Encerrando.");
            System.exit(1);
        }
    }

    /**
     * Exibe o menu principal do Atendente e processa as opções escolhidas.
     */
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Menu do Atendente =====");
            System.out.println("1. Consultar Estoque");
            System.out.println("2. Criar Agendamento (Ainda não implementado)");
            System.out.println("3. Realizar Pagamento (Ainda não implementado)");
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
     * Processa a opção escolhida pelo Atendente no menu.
     */
    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                System.out.println("\n--- Consultando Estoque ---");
                CompGerenciarEstoque compGerenciarEstoque = new CompGerenciarEstoque(itemEstoqueService, scanner);
                compGerenciarEstoque.exibirMenu(); // Abre o menu de estoque, o atendente usará listar/buscar
                System.out.println("\n--- Retornando ao Menu do Atendente ---");
                break;
            case 2:
                System.out.println("Funcionalidade 'Criar Agendamento' ainda não implementada.");
                // Futuramente: chamar um CompGerenciarAgendamento.exibirMenuCriar();
                break;
            case 3:
                System.out.println("Funcionalidade 'Realizar Pagamento' ainda não implementada.");
                // Futuramente: chamar um CompProcessarPagamento.exibirMenu();
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
