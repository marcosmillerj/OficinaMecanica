/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.menus;

import java.util.InputMismatchException;
import java.util.Scanner;
import models.Usuario;
import service.ClienteService;
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import util.UserSession;
import view.componentes.CompGerenciarCliente;
import view.componentes.CompGerenciarEstoque;
import view.componentes.CompGerenciarOS;
import view.componentes.CompGerenciarRelatorio;
import view.componentes.CompGerenciarVeiculo;
import view.componentes.CompOSEspecializada;

/**
 *
 * @author marcos_miller
 */
public class MenuMecanico {

    private ItemEstoqueService itemEstoqueService;
    private OrdemServicoService ordemServicoService;
    private ServicoService servicoService; 
    private UsuarioService usuarioService;
    private Scanner scanner;
    private Usuario mecanicoLogado;
    private ElevadorService elevadorService;
    private ClienteService clienteService;
    private VeiculoService veiculoService;
    private CompGerenciarCliente compGerenciarCliente;
    private CompGerenciarVeiculo compGerenciarVeiculo;



    /**
     * Construtor do MenuMecanico.
     * Recebe as dependências necessárias para suas operações.
     * @param itemEstoqueService O serviço de itens de estoque.
     * @param ordemServicoService O serviço de ordens de serviço.
     * @param servicoService O serviço de serviços.
     * @param usuarioService O serviço de usuários.
     * @param scanner O scanner para entrada do usuário.
     */
    public MenuMecanico(ItemEstoqueService itemEstoqueService, OrdemServicoService ordemServicoService,
                         ServicoService servicoService, UsuarioService usuarioService, Scanner scanner,
                         ElevadorService elevadorService, ClienteService clienteService,
                         VeiculoService veiculoService) {
        this.itemEstoqueService = itemEstoqueService;
        this.ordemServicoService = ordemServicoService;
        this.servicoService = servicoService;
        this.usuarioService = usuarioService;
        this.scanner = scanner;
        this.mecanicoLogado = UserSession.getInstance().getLoggedInUser();
        this.elevadorService = elevadorService;
        this.clienteService = clienteService;
        this.veiculoService = veiculoService;
        this.compGerenciarCliente = new CompGerenciarCliente(clienteService, scanner, ordemServicoService, usuarioService, veiculoService);
        this.compGerenciarVeiculo = new CompGerenciarVeiculo(this.veiculoService, this.clienteService, this.scanner);
        
        if (this.mecanicoLogado == null || this.mecanicoLogado.getTipo() != models.enums.TipoUsuario.MECANICO) {
            System.err.println("Erro: Acesso não autorizado ao Menu Mecânico.");
            System.exit(1);
        }
    }

    /**
     * Exibe o menu principal do Mecânico e processa as opções escolhidas.
     */
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Menu do Mecânico =====");
            System.out.println("1. Consultar Estoque");
            System.out.println("2. Informar Falta/Acabando Peça");
            System.out.println("3. Gerenciar Ordens de Serviço");
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
     * Processa a opção escolhida pelo Mecânico no menu.
     */
    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                System.out.println("\n--- Consultando Estoque ---");
                CompGerenciarEstoque compGerenciarEstoque = new CompGerenciarEstoque(itemEstoqueService, scanner);
                compGerenciarEstoque.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Mecânico ---");
                break;
            case 2:
                informarFaltaOuAcabandoPeca();
                break;
            case 3:
                System.out.println("\n--- Abrindo Gerenciamento de Ordens de Serviço ---");
                CompGerenciarOS compGerenciarOS = new CompGerenciarOS(
                        this.ordemServicoService, this.clienteService, this.veiculoService, this.usuarioService,
                        this.servicoService, this.itemEstoqueService,
                        this.scanner, this.elevadorService, this.compGerenciarCliente, this.compGerenciarVeiculo
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

    /**
     * Permite ao mecânico informar que uma peça está faltando ou acabando.
     * Esta informação simula uma notificação para o gerente POR HORA.
     */
    private void informarFaltaOuAcabandoPeca() {
        System.out.println("\n--- INFORMAR FALTA/ACABANDO PEÇA ---");
        System.out.print("Digite o código da peça: ");
        String codigoPeca = scanner.nextLine();
        System.out.print("Mensagem (Ex: 'Precisa repor urgência'): ");
        String mensagem = scanner.nextLine();

        try {
            // Por agora, vamos simular a notificação com uma mensagem para o console
            itemEstoqueService.informarFaltaDeItem(codigoPeca, mensagem); // Chamada hipotética
            System.out.println("Notificação de falta de peça enviada para o gerente (simulado).");
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao informar falta de peça: " + e.getMessage());
        }
    }
}