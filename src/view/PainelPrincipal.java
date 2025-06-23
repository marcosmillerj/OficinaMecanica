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
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import util.UserSession;
import view.componentes.CompOSEspecializada;

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
    private ServicoService servicoService; // NOVO ATRIBUTO!
    private Scanner scanner;

    private CompPonto compPonto;

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
     * @param itemEstoqueService O serviço de itens de estoque.
     * @param servicoService O serviço de serviços. // NOVO PARÂMETRO DOC
     */

    public PainelPrincipal(UsuarioCRUD usuarioCRUD, RegistroPontoService pontoService, Scanner scanner,
                           OrdemServicoService ordemServicoService, ClienteService clienteService,
                           VeiculoService veiculoService, UsuarioService usuarioService,
                           ItemEstoqueService itemEstoqueService, ServicoService servicoService) {
        this.usuarioCRUD = usuarioCRUD;
        this.pontoService = pontoService;
        this.scanner = scanner;
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.usuarioService = usuarioService;
        this.itemEstoqueService = itemEstoqueService;
        this.servicoService = servicoService; // Inicializa ServicoService AQUI
        
        this.usuarioLogado = UserSession.getInstance().getLoggedInUser();
        this.compPonto = new CompPonto(pontoService, scanner);
        
        if (this.usuarioLogado == null) {
            System.err.println("Erro: Tentativa de exibir PainelPrincipal sem usuário logado. Encerrando.");
            System.exit(1);
        }
    }

    /**
     * Inicia e exibe o loop principal do painel.
     * Gerencia a interação com o usuário através dos componentes e menus específicos.
     */
    public void exibirPainel() {
        int opcao;
        do {
            System.out.println("\n=============================================");
            System.out.println("--- PAINEL PRINCIPAL: " + usuarioLogado.getTipo().getDescricao().toUpperCase() + " ---");
            System.out.println("Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            System.out.println("---------------------------------------------");

            // 1. Exibir e Processar o Componente de Ponto
            int opcaoPonto = compPonto.exibirStatusEPedirAcao();
            if (opcaoPonto == 8 || opcaoPonto == 9) {
                compPonto.processarAcaoPonto(opcaoPonto, usuarioLogado);
            } else if (opcaoPonto != -1) {
                System.out.println("Opção de ponto não reconhecida. Prosseguindo para o menu principal...");
            }
            System.out.println("---------------------------------------------");

            // 2. Exibir a Lista de O.S. Especializadas
            exibirMenuOSEspecializadas(); // <<< AGORA VAI CHAMAR O COMPONENTE REAL!
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

    // --- MÉTODO PARA EXIBIR O MENU DO COMPONENTE DE OS ESPECIALIZADA ---
    private void exibirMenuOSEspecializadas() {
        CompOSEspecializada compOSEspecializada = new CompOSEspecializada(
            ordemServicoService, usuarioService, clienteService, veiculoService,
            itemEstoqueService, servicoService, scanner 
        );
        compOSEspecializada.exibirMenu();
        System.out.println("---------------------------------------------");
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
                // Aqui você chamaria o MenuAtendente real (futuramente)
                System.out.println("Funcionalidade de Gerenciamento para Atendente ainda não implementada.");
                break;
            case MECANICO:
                // Se a opção 1 for para 'Registrar Diagnóstico', ou 2 para 'Executar Serviço'
                // Aqui você chamaria os serviços para Mecânico.
                System.out.println("Funcionalidade de Gerenciamento para Mecânico ainda não implementada.");
                break;
            case GERENTE:
                // Instancia o MenuGerente, passando TODAS as dependências que ele pode precisar
                MenuGerente menuGerente = new MenuGerente(
                    usuarioCRUD, scanner, usuarioService, ordemServicoService, clienteService, veiculoService,
                    itemEstoqueService, servicoService // PASSANDO SERVICOSERVICE AQUI!
                );
                menuGerente.exibirMenu();
                break;
            default:
                System.out.println("Opção inválida para este tipo de usuário.");
                break;
        }
    }
}