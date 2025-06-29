/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oficinamecanica;

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

    // Instâncias estáticas de TODOS os Repositórios
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
    // private static ElevadorRepository elevadorRepository = ElevadorRepository.getInstance(); 
    private static Scanner scanner = new Scanner(System.in);

    // Declaração de todas as instâncias de Service
    private static UsuarioService usuarioService;
    private static ClienteService clienteService;
    private static VeiculoService veiculoService;
    private static RegistroPontoService pontoService;
    private static ItemEstoqueService itemEstoqueService;
    private static ServicoService servicoService;
    private static AgendamentoService agendamentoService;
    private static PagamentoService pagamentoService;
    private static OrdemServicoService ordemServicoService;
    private static RelatorioService relatorioService;


    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Gerenciamento da Oficina...");

        AuthService authService = new AuthService(usuarioCRUD, scanner);
        
        // Tenta realizar o login do usuário
        Usuario usuarioLogado = authService.login();

        if (usuarioLogado != null) {
            UserSession.getInstance().setLoggedInUser(usuarioLogado);
            System.out.println("\nLogin realizado com sucesso! Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            
            // --- CRIAÇÃO DOS SERVIÇOS DE NEGÓCIO PRINCIPAIS (APÓS O LOGIN) ---
            // A ordem de inicialização é IMPORTANTISSIMA.
            usuarioService = new UsuarioService(usuarioCRUD);
            clienteService = new ClienteService(clienteRepository, veiculoRepository);
            veiculoService = new VeiculoService(veiculoRepository, clienteRepository);
            pontoService = new RegistroPontoService(pontoRepository);
            itemEstoqueService = new ItemEstoqueService(itemEstoqueRepository);
            servicoService = new ServicoService(servicoRepository, itemEstoqueRepository);
            // elevadorService = new ElevadorService(elevadorRepository, veiculoRepository);
            ordemServicoService = new OrdemServicoService(
                ordemServicoRepository, usuarioCRUD, clienteService, veiculoService, servicoService
            );
            pagamentoService = new PagamentoService(pagamentoRepository, ordemServicoService);
            agendamentoService = new AgendamentoService(agendamentoRepository, clienteRepository, veiculoRepository);
            relatorioService = new RelatorioService(
                ordemServicoRepository, itemEstoqueRepository, pagamentoRepository, agendamentoRepository,
                clienteRepository, veiculoRepository, usuarioCRUD, servicoService, relatorioRepository,
                ordemServicoService
            );

            // --- INSTANCIA E INICIA O PAINEL PRINCIPAL ---
            PainelPrincipal painelPrincipal = new PainelPrincipal(
                usuarioCRUD, pontoService, scanner, ordemServicoService, clienteService, veiculoService, usuarioService,
                itemEstoqueService, servicoService,
                null,
                agendamentoService, pagamentoService, relatorioService
            );
            painelPrincipal.exibirPainel();
            
        } else {
            System.out.println("Não foi possível realizar o login. Encerrando o sistema.");
        }

        scanner.close();
    }
}