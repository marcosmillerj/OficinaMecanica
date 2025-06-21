/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oficinamecanica; // Seu pacote principal, que contém a classe Main/Principal

import java.util.Scanner;
import models.Usuario;
import models.enums.TipoUsuario;
import repository.PontoRepository;
import repository.UsuarioCRUD;
import service.RegistroPontoService;
import util.AuthService;
import util.UserSession;
import view.MenuUsuario;
import view.ComponentePonto;
import view.PainelPrincipal;

/**
 * Ponto de entrada principal do Sistema de Gerenciamento da Oficina.
 * Responsável por iniciar o sistema, gerenciar o fluxo de login
 * e redirecionar para os painéis de usuário apropriados.
 *
 * @author barbo
 */
public class Principal {

    // Instâncias estáticas dos repositórios e scanner, acessíveis em toda a classe Main
    private static UsuarioCRUD usuarioCRUD = new UsuarioCRUD();
    private static PontoRepository pontoRepository = new PontoRepository();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Gerenciamento da Oficina...");

        // **Atenção:** Se esta é a primeira execução ou se você apagou os JSONs,
        // insira temporariamente o bloco de adição de usuários de teste AQUI.
        // Após a primeira execução e com os usuários salvos, REMOVA este bloco.

        AuthService authService = new AuthService(usuarioCRUD, scanner);
        
        // Tenta realizar o login do usuário
        Usuario usuarioLogado = authService.login();

        // Verifica se o login foi bem-sucedido
        if (usuarioLogado != null) {
            // Se o login for bem-sucedido, define o usuário na UserSession
            UserSession.getInstance().setLoggedInUser(usuarioLogado);
            System.out.println("\nLogin realizado com sucesso! Bem-vindo(a), " + usuarioLogado.getNome() + "!");
            
            // --- AQUI É ONDE O CONTROLE É PASSADO PARA O PainelPrincipal ---
            // Instancia o serviço de ponto e o PainelPrincipal, passando as dependências
            RegistroPontoService pontoService = new RegistroPontoService(pontoRepository);
            PainelPrincipal painelPrincipal = new PainelPrincipal(usuarioCRUD, pontoService, scanner);
            painelPrincipal.exibirPainel(); // Inicia o loop principal do painel
            
        } else {
            // Se o login falhar (usuário digitou '0' para sair), encerra o sistema
            System.out.println("Não foi possível realizar o login. Encerrando o sistema.");
        }

        // Fecha o scanner no final da execução do programa
        scanner.close();
    }

    // O método 'exibirPainelPorTipoUsuario()' ESTÁ REMOVIDO DESTA CLASSE.
    // Toda a lógica de roteamento e exibição de painéis específicos foi movida para PainelPrincipal.
}