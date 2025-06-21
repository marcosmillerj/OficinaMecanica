/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.util.InputMismatchException;
import java.util.Scanner;
import repository.UsuarioCRUD;
import service.UsuarioService;

/**
 *
 * @author marcos_miller
 */
public class MenuGerente {

    private UsuarioService usuarioService;
    private Scanner scanner;
    private UsuarioCRUD usuarioCRUD;

    /**
     * Construtor do MenuGerente.
     * Recebe as dependências necessárias para suas operações.
     * @param usuarioCRUD O CRUD de usuários.
     * @param scanner O scanner para entrada do usuário.
     */
    public MenuGerente(UsuarioCRUD usuarioCRUD, Scanner scanner) {
        this.usuarioCRUD = usuarioCRUD;
        this.usuarioService = new UsuarioService(usuarioCRUD); 
        this.scanner = scanner;
    }

    /**
     * Exibe o menu principal do Gerente e processa as opções escolhidas.
     */
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Menu do Gerente =====");
            System.out.println("1. Gerenciar Usuários");
            System.out.println("2. Gerenciar Estoque (Ainda não implementado)");
            System.out.println("3. Acessar Relatórios Financeiros (Ainda não implementado)");
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
     * Processa a opção escolhida pelo Gerente no menu.
     * @param opcao A opção numérica selecionada.
     */
    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1:
                System.out.println("\n--- Abrindo Gerenciamento de Usuários ---");
                // Agora 'this.usuarioCRUD' é acessível, pois é um atributo da classe
                MenuUsuario menuUsuario = new MenuUsuario(this.usuarioCRUD, this.scanner); 
                menuUsuario.exibirMenu();
                System.out.println("\n--- Retornando ao Menu do Gerente ---");
                break;
            case 2:
                System.out.println("Funcionalidade 'Gerenciar Estoque' ainda não implementada.");
                break;
            case 3:
                System.out.println("Funcionalidade 'Acessar Relatórios Financeiros' ainda não implementada.");
                break;
            case 0:
                System.out.println("Voltando ao Painel Principal.");
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                break;
        }
    }
}