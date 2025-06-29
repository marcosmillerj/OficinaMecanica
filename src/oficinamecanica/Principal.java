/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oficinamecanica; // Seu pacote principal, que contém a classe Main/Principal

import java.util.Scanner;
import models.Usuario;
import models.enums.TipoUsuario;
import repository.ClienteRepository;
import repository.ElevadorRepository;
import repository.ItemEstoqueRepository;
import repository.OrdemServicoRepository;
import repository.PontoRepository;
import repository.ServicoRepository;
import repository.UsuarioCRUD;
import repository.VeiculoRepository;
import service.ClienteService;
import service.ElevadorService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
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
    private static ElevadorRepository elevadorRepository = ElevadorRepository.getInstance(); // Instância Singleton
    private static Scanner scanner = new Scanner(System.in); // Scanner compartilhado

    // Declaração de todas as instâncias de Service (serão inicializadas no main)
    private static UsuarioService usuarioService;
    private static ClienteService clienteService;
    private static VeiculoService veiculoService;
    private static RegistroPontoService pontoService;
    private static ItemEstoqueService itemEstoqueService;
    private static ServicoService servicoService;
    private static ElevadorService elevadorService; // NOVO ATRIBUTO!
    private static OrdemServicoService ordemServicoService;

    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Gerenciamento da Oficina...");
        AuthService authService = new AuthService(usuarioCRUD, scanner);
        
        // Tenta realizar o login do usuário
        Usuario usuarioLogado = authService.login();

        if (usuarioLogado != null) {
            UserSession.getInstance().setLoggedInUser(usuarioLogado);
            System.out.println("\nLogin realizado com sucesso! Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            
            // --- CRIAÇÃO DOS SERVIÇOS DE NEGÓCIO PRINCIPAIS (APÓS O LOGIN) ---
            // Eles dependem dos repositórios que já foram instanciados estaticamente.
            // A ordem de inicialização importa para dependências.
            usuarioService = new UsuarioService(usuarioCRUD);
            clienteService = new ClienteService(clienteRepository, veiculoRepository);
            veiculoService = new VeiculoService(veiculoRepository, clienteRepository);
            pontoService = new RegistroPontoService(pontoRepository);
            itemEstoqueService = new ItemEstoqueService(itemEstoqueRepository);
            servicoService = new ServicoService(servicoRepository, itemEstoqueRepository);
            elevadorService = new ElevadorService(elevadorRepository, veiculoRepository);
            ordemServicoService = new OrdemServicoService(
                ordemServicoRepository, usuarioCRUD, clienteService, veiculoService, servicoService, elevadorService, scanner
            );

            // --- INSTANCIA E INICIA O PAINEL PRINCIPAL ---
            // Passa TODOS os Services e o Scanner para o PainelPrincipal
            PainelPrincipal painelPrincipal = new PainelPrincipal(
                usuarioCRUD,          // Para MenuGerente (compatibilidade)
                pontoService,         // Para ComponentePonto
                scanner,              // Para toda a View
                ordemServicoService,  // Para CompGerenciarOS
                clienteService,       // Para CompGerenciarOS e futuros menus de Cliente
                veiculoService,       // Para CompGerenciarOS e futuros menus de Veiculo
                usuarioService,       // Para MenuGerente, CompGerenciarOS
                itemEstoqueService,   // Para CompGerenciarEstoque, CompGerenciarOS
                servicoService,       // Para CompGerenciarOS, CompGerenciarServico
                elevadorService       // NOVO PARÂMETRO!
            );
            painelPrincipal.exibirPainel();
            
        } else {
            System.out.println("Não foi possível realizar o login. Encerrando o sistema.");
        }

        scanner.close(); // Fecha o scanner no final do programa
    }
}