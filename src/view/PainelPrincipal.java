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
import service.AgendamentoService;
import service.ClienteService;
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.PagamentoService;
import service.RegistroPontoService;
import service.RelatorioService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import util.UserSession;
import view.componentes.CompGerenciarEstoque;
import view.componentes.CompOSEspecializada;
import view.menus.MenuAtendente;
import view.menus.MenuMecanico;

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
    private ServicoService servicoService;
    private ElevadorService elevadorService; 
    private AgendamentoService agendamentoService;
    private PagamentoService pagamentoService;
    private RelatorioService relatorioService;
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
     * @param servicoService O serviço de serviços.
     * // REMOVIDO: @param elevadorService O serviço de elevadores.
     * @param agendamentoService O serviço de agendamentos.
     * @param pagamentoService O serviço de pagamentos.
     */
    public PainelPrincipal(UsuarioCRUD usuarioCRUD, RegistroPontoService pontoService, Scanner scanner,
                           OrdemServicoService ordemServicoService, ClienteService clienteService,
                           VeiculoService veiculoService, UsuarioService usuarioService,
                           ItemEstoqueService itemEstoqueService, ServicoService servicoService,
                           ElevadorService elevadorService, 
                           AgendamentoService agendamentoService, PagamentoService pagamentoService,
                           RelatorioService relatorioService) {
        this.usuarioCRUD = usuarioCRUD;
        this.pontoService = pontoService;
        this.scanner = scanner;
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.usuarioService = usuarioService;
        this.itemEstoqueService = itemEstoqueService;
        this.servicoService = servicoService;
        this.elevadorService = elevadorService; 
        this.agendamentoService = agendamentoService;
        this.pagamentoService = pagamentoService;
        this.relatorioService = relatorioService;
        this.usuarioLogado = util.UserSession.getInstance().getLoggedInUser();
        this.compPonto = new CompPonto(pontoService, scanner);
        
        if (this.usuarioLogado == null) {
            System.err.println("Erro: Tentativa de exibir PainelPrincipal sem usuario logado. Encerrando.");
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
            int opcaoPonto = compPonto.exibirStatusEPedirAcao();
            if (opcaoPonto == 8 || opcaoPonto == 9) {
                compPonto.processarAcaoPonto(opcaoPonto, usuarioLogado);
            } else if (opcaoPonto != -1) {
                System.out.println("Opção de ponto não reconhecida. Prosseguindo...");
            }
            System.out.println("---------------------------------------------");

            // 2. Exibir a Lista de O.S. Especializadas
            // IMPORTANTE: Se CompOSEspecializada ainda precisar de ElevadorService,
            // e o OrdemServicoService NÃO MAIS tiver ElevadorService, precisaremos decidir.
            // Por agora, vamos ASSUMIR que CompOSEspecializada e OrdemServicoService não o usam mais.
            exibirMenuOSEspecializadas(); 
            System.out.println("---------------------------------------------");

            // 3. DIRECIONAR PARA O MENU DE FUNCIONALIDADES DO USUÁRIO
            switch (usuarioLogado.getTipo()) {
                case ATENDENTE:
                    System.out.println("\n--- ABRINDO MENU DO ATENDENTE ---");
                    MenuAtendente menuAtendente = new MenuAtendente(
                        clienteService, itemEstoqueService, ordemServicoService, pontoService,
                        servicoService, usuarioService, veiculoService, scanner,
                        agendamentoService, pagamentoService
                    );
                    menuAtendente.exibirMenu();
                    break;
                case MECANICO:
                    System.out.println("\n--- ABRINDO MENU DO MECÂNICO ---");
                    // MenuMecanico agora SEM ElevadorService
                    MenuMecanico menuMecanico = new MenuMecanico(
                        itemEstoqueService, ordemServicoService, servicoService, usuarioService, scanner,
                        elevadorService
                    );
                    menuMecanico.exibirMenu();
                    break;
                case GERENTE:
                    System.out.println("\n--- ABRINDO MENU DO GERENTE ---");
                    // MenuGerente agora SEM ElevadorService
                    MenuGerente menuGerente = new MenuGerente(
                        usuarioCRUD, scanner, usuarioService, ordemServicoService, clienteService, veiculoService,
                        itemEstoqueService, servicoService, relatorioService
                        // REMOVIDO: elevadorService
                    );
                    menuGerente.exibirMenu();
                    break;
                default:
                    System.out.println("Tipo de usuario nao reconhecido ou sem menu especifico. Encerrando.");
                    break;
            }

            UserSession.getInstance().logout(); 
            System.out.println("Saindo do Painel " + usuarioLogado.getTipo().getDescricao() + ". Até mais!");
        } while (false); // Loop externo do PainelPrincipal - ajustado no último turno para ser um loop
                         // Se o PainelPrincipal tiver um loop do-while, esta linha é `while (opcao != 0);` no final do método.
                         // Pelo que está no código atual, ele TEM um loop do-while.
        // A sua versão atual do PainelPrincipal tinha um do-while no exibirPainel().
        // Não vamos mexer no loop.
    }

    // Método que chama o CompOSEspecializada, agora SEM ElevadorService
    private void exibirMenuOSEspecializadas() {
        CompOSEspecializada compOSEspecializada = new CompOSEspecializada(
            ordemServicoService, usuarioService, clienteService, veiculoService,
            itemEstoqueService, servicoService, elevadorService, scanner
        );
        compOSEspecializada.exibirMenu();
    }
}