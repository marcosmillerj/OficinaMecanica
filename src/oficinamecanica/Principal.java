/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oficinamecanica;

import java.util.Scanner;
import models.Usuario;
import models.enums.TipoUsuario;
import repository.ClienteRepository;
import repository.OrdemServicoRepository;
import repository.PontoRepository;
import repository.UsuarioCRUD;
import repository.VeiculoRepository;
import service.ClienteService;
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

    private static UsuarioCRUD usuarioCRUD = new UsuarioCRUD();
    private static PontoRepository pontoRepository = new PontoRepository();
    private static OrdemServicoRepository ordemServicoRepository = new OrdemServicoRepository();
    private static ClienteRepository clienteRepository = new ClienteRepository();
    private static VeiculoRepository veiculoRepository = new VeiculoRepository();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Gerenciamento da Oficina...");

        AuthService authService = new AuthService(usuarioCRUD, scanner);
        Usuario usuarioLogado = authService.login();

        if (usuarioLogado != null) {
            UserSession.getInstance().setLoggedInUser(usuarioLogado);
            System.out.println("\nLogin realizado com sucesso! Bem-vindo(a), " + usuarioLogado.getNome() + "!");

            // Instanciar os serviços de negócio principais
            UsuarioService usuarioService = new UsuarioService(usuarioCRUD);
            ClienteService clienteService = new ClienteService(clienteRepository, veiculoRepository);
            VeiculoService veiculoService = new VeiculoService(veiculoRepository, clienteRepository);
            RegistroPontoService pontoService = new RegistroPontoService(pontoRepository);
            OrdemServicoService ordemServicoService = new OrdemServicoService(
                ordemServicoRepository, usuarioCRUD, clienteService, veiculoService
            );

            // Instanciar e exibir o PainelPrincipal
            PainelPrincipal painelPrincipal = new PainelPrincipal(
                usuarioCRUD, pontoService, scanner, ordemServicoService, clienteService, veiculoService, usuarioService
            );
            painelPrincipal.exibirPainel();

        } else {
            System.out.println("Não foi possível realizar o login. Encerrando o sistema.");
        }
        scanner.close();
    }
}