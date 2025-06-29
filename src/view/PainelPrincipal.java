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
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.RegistroPontoService;
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
     * @param elevadorService O serviço de elevadores. // NOVO PARÂMETRO DOC
     */
    public PainelPrincipal(UsuarioCRUD usuarioCRUD, RegistroPontoService pontoService, Scanner scanner,
                           OrdemServicoService ordemServicoService, ClienteService clienteService,
                           VeiculoService veiculoService, UsuarioService usuarioService,
                           ItemEstoqueService itemEstoqueService, ServicoService servicoService,
                           ElevadorService elevadorService) { // NOVO PARÂMETRO!
        this.usuarioCRUD = usuarioCRUD;
        this.pontoService = pontoService;
        this.scanner = scanner;
        this.ordemServicoService = ordemServicoService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.usuarioService = usuarioService;
        this.itemEstoqueService = itemEstoqueService;
        this.servicoService = servicoService;
        this.elevadorService = elevadorService; // Inicializa ElevadorService
        
        this.usuarioLogado = util.UserSession.getInstance().getLoggedInUser();
        this.compPonto = new CompPonto(pontoService, scanner);
        
        if (this.usuarioLogado == null) {
            System.err.println("Erro: Tentativa de exibir PainelPrincipal sem usuario logado. Encerrando.");
            System.exit(1);
        }
    }

    /**
     * Inicia o fluxo de exibição do painel principal para o usuário logado.
     * Após exibir os componentes iniciais, direciona para o menu de funcionalidade do usuário.
     */
    public void exibirPainel() {
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
        exibirMenuOSEspecializadas(); // Este método chamará o CompOSEspecializada.exibirMenu()
        System.out.println("---------------------------------------------");

        // 3. DIRECIONAR PARA O MENU DE FUNCIONALIDADES DO USUÁRIO
        // Cada MenuXyz terá seu próprio loop do-while interno
        switch (usuarioLogado.getTipo()) {
            case ATENDENTE:
                System.out.println("\n--- ABRINDO MENU DO ATENDENTE ---");
                // Instancia e exibe o MenuAtendente
                MenuAtendente menuAtendente = new MenuAtendente(
                    clienteService, itemEstoqueService, ordemServicoService, pontoService,
                    servicoService, usuarioService, veiculoService, scanner // JÁ EXISTENTE
                );
                menuAtendente.exibirMenu(); // MenuAtendente tem seu próprio do-while
                break;
            case MECANICO:
                System.out.println("\n--- ABRINDO MENU DO MECÂNICO ---");
                // Instancia e exibe o MenuMecanico
                MenuMecanico menuMecanico = new MenuMecanico(
                    itemEstoqueService, ordemServicoService, servicoService, usuarioService, scanner,
                    elevadorService // NOVO PARÂMETRO!
                );
                menuMecanico.exibirMenu(); // MenuMecanico tem seu próprio do-while
                break;
            case GERENTE:
                System.out.println("\n--- ABRINDO MENU DO GERENTE ---");
                // Instancia e exibe o MenuGerente
                MenuGerente menuGerente = new MenuGerente(
                    usuarioCRUD, scanner, usuarioService, ordemServicoService, clienteService, veiculoService,
                    itemEstoqueService, servicoService
                );
                menuGerente.exibirMenu(); // MenuGerente tem seu próprio do-while
                break;
            default:
                System.out.println("Tipo de usuario nao reconhecido ou sem menu especifico. Encerrando.");
                break;
        }

        // Após o menu do usuario encerrar (opção 0 dentro de MenuXyz), o controle volta para cá.
        UserSession.getInstance().logout(); 
        System.out.println("Saindo do Painel " + usuarioLogado.getTipo().getDescricao() + ". Até mais!");
    }

    private void exibirMenuOSEspecializadas() {
        CompOSEspecializada compOSEspecializada = new CompOSEspecializada(
            ordemServicoService, usuarioService, clienteService, veiculoService,
            itemEstoqueService, servicoService, scanner
        );
        compOSEspecializada.exibirMenu();
    }
}