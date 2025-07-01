/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.Optional;
import java.util.Scanner;
import models.Usuario;
import repository.UsuarioCRUD;

/**
 *
 * @author marcos_miller
 */
public class AuthService {

    private UsuarioCRUD usuarioCRUD;
    private Scanner scanner;

    /**
     * Construtor do AuthService.
     *
     * @param usuarioCRUD Instância do UsuarioCRUD para buscar e validar usuários.
     * @param scanner     Instância do Scanner para leitura de entrada do usuário.
     */
    public AuthService(UsuarioCRUD usuarioCRUD, Scanner scanner) {
        this.usuarioCRUD = usuarioCRUD;
        this.scanner = scanner;
    }

    /**
     * Realiza o processo de login, solicitando email e senha.
     * Continua pedindo credenciais até que o login seja bem-sucedido ou o usuário escolha sair.
     *
     * @return O objeto Usuario autenticado, se o login for bem-sucedido; caso contrário, retorna null.
     */
    public Usuario login() {
        while (true) {
            System.out.println("\n--- Tela de Login ---");
            System.out.println("Digite suas credenciais ou '0' para sair.");
            System.out.print("Email: ");
            String email = scanner.nextLine();

            if (email.equals("0")) {
                System.out.println("Saindo da tela de login.");
                return null;
            }

            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            Optional<Usuario> usuarioEncontrado = usuarioCRUD.buscarUsuarioPorEmail(email);

            if (usuarioEncontrado.isPresent()) {
                Usuario usuario = usuarioEncontrado.get();
                if (usuario.fazerLogin(email, senha)) {
                    System.out.println("Credenciais verificadas. Prosseguindo...");
                    return usuario; 
                } else {
                    System.out.println("Senha incorreta. Tente novamente.");
                }
            } else {
                System.out.println("Email não cadastrado. Tente novamente.");
            }
        }
    }
}