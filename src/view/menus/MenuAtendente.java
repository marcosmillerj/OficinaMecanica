package view.menus;

import service.AgendamentoService;
import service.ClienteService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.PagamentoService;
import service.RegistroPontoService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import view.componentes.CompGerenciarAgendamento;
import view.componentes.CompGerenciarCliente;
import view.componentes.CompGerenciarEstoque;
import view.componentes.CompGerenciarOS;
import view.componentes.CompGerenciarVeiculo;
import view.componentes.CompProcessarPagamento;
import util.UserSession;

import java.util.InputMismatchException;
import java.util.Scanner;
import service.ElevadorService;

/**
 * Menu do Atendente.
 * Oferece funcionalidades específicas para o perfil de atendente.
 *
 * @author marcos_miller
 */
public class MenuAtendente {

    private final ClienteService clienteService;
    private final ItemEstoqueService itemEstoqueService;
    private final OrdemServicoService ordemServicoService;
    private final RegistroPontoService pontoService;
    private final ServicoService servicoService;
    private final UsuarioService usuarioService;
    private final VeiculoService veiculoService;
    private final AgendamentoService agendamentoService;
    private final PagamentoService pagamentoService;
    private final Scanner scanner;
    private final CompGerenciarCliente compGerenciarCliente;
    private final CompGerenciarVeiculo compGerenciarVeiculo;
    private final ElevadorService elevadorService; 

    /**
     * Construtor do MenuAtendente.
     * Recebe as dependências necessárias para suas operações.
     */
    public MenuAtendente(ClienteService clienteService, ItemEstoqueService itemEstoqueService,
                         OrdemServicoService ordemServicoService, RegistroPontoService pontoService,
                         ServicoService servicoService, UsuarioService usuarioService,
                         VeiculoService veiculoService, Scanner scanner,
                         AgendamentoService agendamentoService, PagamentoService pagamentoService,
                         ElevadorService elevadorService) { 
        this.clienteService = clienteService;
        this.itemEstoqueService = itemEstoqueService;
        this.ordemServicoService = ordemServicoService;
        this.pontoService = pontoService;
        this.servicoService = servicoService;
        this.usuarioService = usuarioService;
        this.veiculoService = veiculoService;
        this.scanner = scanner;
        this.agendamentoService = agendamentoService;
        this.pagamentoService = pagamentoService;
        this.elevadorService = elevadorService;

        if (UserSession.getInstance().getLoggedInUser() == null || UserSession.getInstance().getLoggedInUser().getTipo() != models.enums.TipoUsuario.ATENDENTE) {
            System.err.println("Erro: Acesso não autorizado ao Menu Atendente. Nenhum usuário logado ou tipo incorreto.");
            System.exit(1);
        }

        this.compGerenciarCliente = new CompGerenciarCliente(clienteService, scanner, ordemServicoService, usuarioService, veiculoService);
        this.compGerenciarVeiculo = new CompGerenciarVeiculo(this.veiculoService, this.clienteService, this.scanner);
    }

    /**
     * Exibe o menu principal do Atendente e processa as opções escolhidas.
     */
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Menu do Atendente =====");
            System.out.println("1. Gerenciar Clientes");
            System.out.println("2. Gerenciar Ordens de Serviço");
            System.out.println("3. Gerenciar Agendamentos");
            System.out.println("4. Consultar Estoque");
            System.out.println("5. Realizar Pagamento");
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
            case 1: // Gerenciar Clientes
                System.out.println("\n--- Abrindo Gerenciamento de Clientes ---");
                this.compGerenciarCliente.exibirMenuPrincipal();
                System.out.println("\n--- Retornando ao Menu do Atendente ---");
                break;
            case 2: // Gerenciar Ordens de Serviço
                System.out.println("\n--- Abrindo Gerenciamento de Ordens de Serviço ---");
                CompGerenciarOS compGerenciarOS = new CompGerenciarOS(
                        this.ordemServicoService, this.clienteService, this.veiculoService, this.usuarioService,
                        this.servicoService, this.itemEstoqueService,
                        this.scanner, this.elevadorService, this.compGerenciarCliente, this.compGerenciarVeiculo
                );
                compGerenciarOS.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Atendente ---");
                break;
            case 3: // Gerenciar Agendamentos
                System.out.println("\n--- Abrindo Gerenciamento de Agendamentos ---");
                CompGerenciarAgendamento compGerenciarAgendamento = new CompGerenciarAgendamento(
                        agendamentoService, clienteService, veiculoService, scanner, compGerenciarCliente, compGerenciarVeiculo
                );
                compGerenciarAgendamento.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Atendente ---");
                break;
            case 4: // Consultar Estoque
                System.out.println("\n--- Consultando Estoque ---");
                CompGerenciarEstoque compGerenciarEstoque = new CompGerenciarEstoque(itemEstoqueService, scanner);
                compGerenciarEstoque.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Atendente ---");
                break;
            case 5: // Realizar Pagamento
                System.out.println("\n--- Realizando Pagamento ---");
                CompProcessarPagamento compProcessarPagamento = new CompProcessarPagamento(
                        pagamentoService, ordemServicoService, scanner
                );
                compProcessarPagamento.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Atendente ---");
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