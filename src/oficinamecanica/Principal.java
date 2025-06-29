/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oficinamecanica; // Seu pacote principal, que contém a classe Main/Principal

import java.util.Scanner;
import models.Usuario;
import models.enums.TipoUsuario;
import repository.AgendamentoRepository;
import repository.ClienteRepository;
import repository.ElevadorRepository;
import repository.ItemEstoqueRepository;
import repository.OrdemServicoRepository;
import repository.PagamentoRepository;
import repository.PontoRepository;
import repository.ServicoRepository;
import repository.UsuarioCRUD;
import repository.VeiculoRepository;
import service.AgendamentoService;
import service.ClienteService;
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.PagamentoService;
import service.RegistroPontoService;
import service.ServicoService;
import service.UsuarioService;
import service.VeiculoService;
import util.AuthService;
import util.UserSession;
import view.componentes.CompGerenciarUsuario;
import view.PainelPrincipal;
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
    private static Scanner scanner = new Scanner(System.in); // Scanner compartilhado

    // Declaração de todas as instâncias de Service (serão inicializadas no main)
    private static UsuarioService usuarioService;
    private static ClienteService clienteService;
    private static VeiculoService veiculoService;
    private static RegistroPontoService pontoService;
    private static ItemEstoqueService itemEstoqueService;
    private static ServicoService servicoService;
    private static AgendamentoService agendamentoService;
    private static PagamentoService pagamentoService;
    private static OrdemServicoService ordemServicoService; // Declarado aqui


    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Gerenciamento da Oficina...");

        AuthService authService = new AuthService(usuarioCRUD, scanner);
        Usuario usuarioLogado = authService.login();

        if (usuarioLogado != null) {
            UserSession.getInstance().setLoggedInUser(usuarioLogado);
            System.out.println("\nLogin realizado com sucesso! Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            
            // --- CRIAÇÃO DOS SERVIÇOS DE NEGÓCIO PRINCIPAIS (APÓS O LOGIN) ---
            // A ordem de inicialização importa para dependências.
            usuarioService = new UsuarioService(usuarioCRUD);
            clienteService = new ClienteService(clienteRepository, veiculoRepository);
            veiculoService = new VeiculoService(veiculoRepository, clienteRepository);
            pontoService = new RegistroPontoService(pontoRepository);
            itemEstoqueService = new ItemEstoqueService(itemEstoqueRepository);
            servicoService = new ServicoService(servicoRepository, itemEstoqueRepository);
            // elevadorService = new ElevadorService(elevadorRepository, veiculoRepository); // Não inicializa aqui por enquanto
            
            // ATENÇÃO: Ordem de inicialização corrigida!
            ordemServicoService = new OrdemServicoService( // << INICIALIZA AGORA!
                ordemServicoRepository, usuarioCRUD, clienteService, veiculoService, servicoService
            );
            pagamentoService = new PagamentoService(pagamentoRepository, ordemServicoService); // << AGORA ordemServicoService NÃO É NULO!
            agendamentoService = new AgendamentoService(agendamentoRepository, clienteRepository, veiculoRepository); 

            // --- INSTANCIA E INICIA O PAINEL PRINCIPAL ---
            PainelPrincipal painelPrincipal = new PainelPrincipal(
                usuarioCRUD, pontoService, scanner, ordemServicoService, clienteService, veiculoService, usuarioService,
                itemEstoqueService, servicoService,
                null, // << ElevadorService está nulo aqui no PainelPrincipal (conforme sua decisão atual de removê-lo)
                agendamentoService, pagamentoService
            );
            painelPrincipal.exibirPainel();
            
        } else {
            System.out.println("Não foi possível realizar o login. Encerrando o sistema.");
        }

        scanner.close(); // Fecha o scanner no final do programa
    }
}