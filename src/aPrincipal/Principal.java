/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package aPrincipal;

import java.util.Scanner;
import util.UserSession;
import models.Usuario;
import models.enums.TipoUsuario;
import repository.*;
import service.*;
import view.PainelPrincipal;
import view.componentes.CompGerenciarUsuario;
import view.componentes.CompPonto;

/**
 * Ponto de entrada principal do Sistema de Gerenciamento da Oficina.
 * Responsável por iniciar o sistema, gerenciar o fluxo de login
 * e redirecionar para os painéis de usuário apropriados.
 *
 * @author barbo
 */
public class Principal {

    // Instâncias estáticas de TODOS os Repositórios que o sistema utilizará
    private static UsuarioCRUD usuarioCRUD = new UsuarioCRUD();
    private static PontoRepository pontoRepository = new PontoRepository();
    private static OrdemServicoRepository ordemServicoRepository = new OrdemServicoRepository();
    private static ClienteRepository clienteRepository = new ClienteRepository();
    private static VeiculoRepository veiculoRepository = new VeiculoRepository();
    private static ItemEstoqueRepository itemEstoqueRepository = new ItemEstoqueRepository();
    private static ServicoRepository servicoRepository = new ServicoRepository();
    private static AgendamentoRepository agendamentoRepository = new AgendamentoRepository();
    private static PagamentoRepository pagamentoRepository = new PagamentoRepository();
    private static RelatorioRepository relatorioRepository = new RelatorioRepository();
    private static ElevadorRepository elevadorRepository = ElevadorRepository.getInstance();
    private static Scanner scanner = new Scanner(System.in);

    // Declaração de todas as instâncias de Service (serão inicializadas no main)
    private static UsuarioService usuarioService;
    private static ClienteService clienteService;
    private static VeiculoService veiculoService;
    private static RegistroPontoService pontoService;
    private static ItemEstoqueService itemEstoqueService;
    private static ServicoService servicoService;
    private static ElevadorService elevadorService; // NOVO ATRIBUTO PARA ELEVADORSERVICE!
    private static AgendamentoService agendamentoService;
    private static PagamentoService pagamentoService;
    private static OrdemServicoService ordemServicoService;
    private static RelatorioService relatorioService;

    /**
     * O método principal que inicia a execução do sistema.
     * Gerencia o processo de login do usuário, inicializa todos os serviços
     * necessários e exibe o painel principal da aplicação com base no usuário logado.
     *
     * @param args Argumentos de linha de comando (não utilizados nesta aplicação).
     */
    public static void main(String[] args) {
        System.out.println("==============================================");
        //
        //PrincipalTestes.rodarTodasDemonstracoes(scanner);
        //
        System.out.println("==============================================");
        System.out.println("Iniciando Sistema de Gerenciamento da Oficina...");

        AuthService authService = new AuthService(usuarioCRUD, scanner);
        
        // Tenta realizar o login do usuário
        Usuario usuarioLogado = authService.login();

        if (usuarioLogado != null) {
            UserSession.getInstance().setLoggedInUser(usuarioLogado);
            System.out.println("\nLogin realizado com sucesso! Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            
            // --- CRIAÇÃO DOS SERVIÇOS DE NEGÓCIO PRINCIPAIS (APÓS O LOGIN) ---
            // A ordem de inicialização é CRÍTICA aqui! (Dependências devem ser inicializadas ANTES de quem as usa)
            usuarioService = new UsuarioService(usuarioCRUD);
            clienteService = new ClienteService(clienteRepository, veiculoRepository);
            veiculoService = new VeiculoService(veiculoRepository, clienteRepository);
            pontoService = new RegistroPontoService(pontoRepository);
            itemEstoqueService = new ItemEstoqueService(itemEstoqueRepository);
            servicoService = new ServicoService(servicoRepository, itemEstoqueRepository);
            elevadorService = new ElevadorService(elevadorRepository, veiculoRepository); // <<< INICIALIZA AQUI!

            // OrdemServicoService depende de ElevadorService agora
            ordemServicoService = new OrdemServicoService(
                ordemServicoRepository, usuarioCRUD, clienteService, veiculoService, servicoService, elevadorService // <<< PASSANDO ELEVADORSERVICE!
            );
            
            // PagamentoService depende de OrdemServicoService
            pagamentoService = new PagamentoService(pagamentoRepository, ordemServicoService); 
            
            // AgendamentoService depende de ClienteService, VeiculoService
            agendamentoService = new AgendamentoService(agendamentoRepository, clienteRepository, veiculoRepository);
            
            // RelatorioService depende de OrdemServicoService, ServicoService, etc.
            relatorioService = new RelatorioService(
                ordemServicoRepository, itemEstoqueRepository, pagamentoRepository, agendamentoRepository,
                clienteRepository, veiculoRepository, usuarioCRUD, servicoService, relatorioRepository,
                ordemServicoService 
            );

            // --- INSTANCIA E INICIA O PAINEL PRINCIPAL ---
            // Passa TODOS os Services e o Scanner para o PainelPrincipal
            PainelPrincipal painelPrincipal = new PainelPrincipal(
                usuarioCRUD,        // Para MenuGerente (compatibilidade)
                pontoService,       // Para ComponentePonto
                scanner,            // Para toda a View
                ordemServicoService, // Para CompGerenciarOS
                clienteService,     // Para CompGerenciarOS e futuros menus de Cliente
                veiculoService,     // Para CompGerenciarOS e futuros menus de Veiculo
                usuarioService,     // Para MenuGerente, CompGerenciarOS
                itemEstoqueService, // Para CompGerenciarEstoque, CompGerenciarOS
                servicoService,     // Para CompGerenciarOS, CompGerenciarServico
                elevadorService,    // <<< AGORA PASSANDO ELEVADORSERVICE!
                agendamentoService, // Para MenuAtendente (Agendamento)
                pagamentoService,   // Para MenuAtendente (Pagamento)
                relatorioService    // Para CompGerarRelatorios
            );
            painelPrincipal.exibirPainel();
            
        } else {
            System.out.println("Não foi possível realizar o login. Encerrando o sistema.");
        }

        scanner.close(); // Fecha o scanner no final do programa
    }
}