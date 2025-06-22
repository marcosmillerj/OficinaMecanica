/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oficinamecanica; // Seu pacote principal, que contém a classe Main/Principal

import java.util.Scanner;
import models.Usuario;
import models.enums.TipoUsuario;
import repository.ClienteRepository;
import repository.ItemEstoqueRepository;
import repository.OrdemServicoRepository;
import repository.PontoRepository;
import repository.UsuarioCRUD;
import repository.VeiculoRepository;
import service.ClienteService;
import service.ItemEstoqueService;
import service.OrdemServicoService;
import service.RegistroPontoService;
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
    private static Scanner scanner = new Scanner(System.in);
    private static UsuarioService usuarioService;
    private static ClienteService clienteService;
    private static VeiculoService veiculoService;
    private static RegistroPontoService pontoService;
    private static ItemEstoqueService itemEstoqueService;
    private static OrdemServicoService ordemServicoService;

    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Gerenciamento da Oficina...");

        // --- INICIALIZAÇÃO DOS SERVIÇOS ---
        // Eles dependem dos repositórios que já foram instanciados estaticamente.
        // Ordem de inicialização importa para dependências (ex: ClienteService precisa de VeiculoRepository)
        usuarioService = new UsuarioService(usuarioCRUD);
        clienteService = new ClienteService(clienteRepository, veiculoRepository);
        veiculoService = new VeiculoService(veiculoRepository, clienteRepository);
        pontoService = new RegistroPontoService(pontoRepository);
        itemEstoqueService = new ItemEstoqueService(itemEstoqueRepository);
        ordemServicoService = new OrdemServicoService(
            ordemServicoRepository, usuarioCRUD, clienteService, veiculoService
        );
        // --- FIM DA INICIALIZAÇÃO DOS SERVIÇOS ---

        // Instancia o serviço de autenticação, passando as dependências
        AuthService authService = new AuthService(usuarioCRUD, scanner);
        
        // Tenta realizar o login do usuário
        Usuario usuarioLogado = authService.login();

        // Verifica se o login foi bem-sucedido
        if (usuarioLogado != null) {
            // Se o login for bem-sucedido, define o usuário na UserSession
            UserSession.getInstance().setLoggedInUser(usuarioLogado);
            System.out.println("\nLogin realizado com sucesso! Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            
            // --- AQUI É ONDE O CONTROLE É PASSADO PARA O PainelPrincipal ---
            // Passa TODOS os Services e o Scanner para o PainelPrincipal
            PainelPrincipal painelPrincipal = new PainelPrincipal(
                usuarioCRUD,            // Para compatibilidade em MenuGerente
                pontoService,           // Para CompPonto
                scanner,                // Para toda a View
                ordemServicoService,    // Para CompGerenciarOS
                clienteService,         // Para CompGerenciarOS e futuros menus de Cliente
                veiculoService,         // Para CompGerenciarOS e futuros menus de Veiculo
                usuarioService,         // Para MenuGerente, CompGerenciarOS
                itemEstoqueService      // Para CompGerenciarEstoque
            );
            painelPrincipal.exibirPainel(); // Inicia o loop principal do painel
            
        } else {
            // Se o login falhar (usuário digitou '0' para sair), encerra o sistema
            System.out.println("Não foi possível realizar o login. Encerrando o sistema.");
        }

        // Fecha o scanner no final da execução do programa
        scanner.close();
    }
}