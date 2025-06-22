/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import view.componentes.CompPonto;
import view.menus.MenuGerente;
import java.util.Scanner;
import models.Usuario;
import repository.UsuarioCRUD;
import service.ClienteService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.RegistroPontoService;
import service.UsuarioService;
import service.VeiculoService;
import util.UserSession;

/**
 *
 * @author marcos_miller
 */
public class PainelPrincipal {

    private Usuario usuarioLogado;
    private UsuarioCRUD usuarioCRUD;
    private RegistroPontoService pontoService;
    private OrdemServicoService ordemServicoService;
    private ClienteService clienteService;
    private VeiculoService veiculoService;
    private UsuarioService usuarioService;
    private ItemEstoqueService itemEstoqueService;
    private Scanner scanner;

    private CompPonto componentePonto;
    // Futuramente: ComponenteOrdemServicoEspecializada componenteOSEspecializada;

    /**
     * Construtor do PainelPrincipal.
     * Recebe todas as dependências necessárias para suas operações.
     * @param usuarioCRUD O CRUD de usuários.
     * @param pontoService O serviço de negócio para o registro de ponto.
     * @param scanner O scanner para entrada do usuário.
     * @param ordemServicoService O serviço de ordens de serviço.
     * @param clienteService O serviço de clientes.
     * @param veiculoService O serviço de veículos.
     * @param usuarioService O serviço de usuários.
     * @param itemEstoqueService O serviço de itens de estoque. // NOVO PARÂMETRO DOC
     */
    public PainelPrincipal(UsuarioCRUD usuarioCRUD, RegistroPontoService pontoService, Scanner scanner,
                           OrdemServicoService ordemServicoService, ClienteService clienteService,
                           VeiculoService veiculoService, UsuarioService usuarioService,
                           ItemEstoqueService itemEstoqueService) { // NOVO PARÂMETRO!
        this.usuarioCRUD = usuarioCRUD;
        this.pontoService = pontoService;
        this.scanner = scanner;
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.usuarioService = usuarioService;
        this.itemEstoqueService = itemEstoqueService; // Inicializa ItemEstoqueService
        
        this.usuarioLogado = UserSession.getInstance().getLoggedInUser();
        this.componentePonto = new CompPonto(pontoService, scanner);
        
        if (this.usuarioLogado == null) {
            System.err.println("Erro: Tentativa de exibir PainelPrincipal sem usuário logado. Encerrando.");
            System.exit(1);
        }
    }

    public void exibirPainel() {
        int opcao;
        do {
            System.out.println("\n=============================================");
            System.out.println("--- PAINEL PRINCIPAL: " + usuarioLogado.getTipo().getDescricao().toUpperCase() + " ---");
            System.out.println("Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            System.out.println("---------------------------------------------");

            // 1. Exibir e Processar o Componente de Ponto
            int opcaoPonto = componentePonto.exibirStatusEPedirAcao();
            if (opcaoPonto == 8 || opcaoPonto == 9) {
                componentePonto.processarAcaoPonto(opcaoPonto, usuarioLogado);
            } else if (opcaoPonto != -1) {
                System.out.println("Opção de ponto não reconhecida. Prosseguindo para o menu principal...");
            }
            System.out.println("---------------------------------------------");

            // 2. Exibir a Lista de O.S. Especializadas
            exibirOrdensDeServicoEspecializadas();
            System.out.println("---------------------------------------------");

            // 3. Exibir o Menu de Opções Específico para o Tipo de Usuário
            exibirMenuOpcoesPorTipo();

            System.out.print("Escolha uma opção do menu principal ou '0' para sair: ");
            try {
                opcao = scanner.nextInt();
                scanner.nextLine();
            } catch (java.util.InputMismatchException e) {
                System.err.println("Entrada inválida para a opção do menu. Por favor, digite um número.");
                scanner.nextLine();
                opcao = -1;
            }

            // 4. Processar a Opção Escolhida no Menu Principal
            processarOpcaoMenu(opcao);

        } while (opcao != 0);
        
        UserSession.getInstance().logout();
        System.out.println("Saindo do Painel " + usuarioLogado.getTipo().getDescricao() + ". Até mais!");
    }

    private void exibirOrdensDeServicoEspecializadas() {
        System.out.println("\n[ORDENS DE SERVIÇO ATRIBUÍDAS / EM ABERTO]");
        System.out.println("Nenhuma Ordem de Serviço exibida ainda (Lógica a ser implementada futuramente).");
    }

    private void exibirMenuOpcoesPorTipo() {
        System.out.println("\n[MENU DE OPÇÕES]");
        switch (usuarioLogado.getTipo()) {
            case ATENDENTE:
                System.out.println("1. Gerenciar Agendamentos");
                System.out.println("2. Gerar Nova Ordem de Serviço");
                System.out.println("3. Processar Pagamento");
                break;
            case MECANICO:
                System.out.println("1. Visualizar Minhas Ordens de Serviço");
                System.out.println("2. Registrar Diagnóstico");
                System.out.println("3. Registrar Execução de Serviço");
                break;
            case GERENTE:
                System.out.println("1. Gerenciar Usuários");
                System.out.println("2. Gerenciar Estoque");
                System.out.println("3. Acessar Relatórios Financeiros");
                System.out.println("4. Gerenciar Ordens de Serviço");
                break;
            default:
                System.out.println("Nenhuma opção disponível para este tipo de usuário.");
                break;
        }
        System.out.println("0. Sair do Painel");
    }

    private void processarOpcaoMenu(int opcao) {
        if (opcao == 0) {
            return;
        }

        switch (usuarioLogado.getTipo()) {
            case ATENDENTE:
                // Chamaria o MenuAtendente real
                System.out.println("Funcionalidade de Gerenciamento para Atendente ainda não implementada.");
                break;
            case MECANICO:
                // Chamaria o MenuMecanico real
                System.out.println("Funcionalidade de Gerenciamento para Mecânico ainda não implementada.");
                break;
            case GERENTE:
                MenuGerente menuGerente = new MenuGerente(
                    usuarioCRUD, scanner, usuarioService, ordemServicoService, clienteService, veiculoService,
                    itemEstoqueService // NOVO PARÂMETRO!
                );
                menuGerente.exibirMenu();
                break;
            default:
                System.out.println("Opção inválida para este tipo de usuário.");
                break;
        }
    }
    // Remova os métodos processarMenuAtendente e processarMenuMecanico se ainda estiverem aqui.
}