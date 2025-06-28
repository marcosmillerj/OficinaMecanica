/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package view.menus;

import java.util.InputMismatchException;
import java.util.Scanner;
import models.Usuario;
import models.enums.TipoUsuario;
import util.UserSession;
import view.componentes.CompGerenciarOS; // Manter este, se for um componente de UI/Fluxo

/**
 *
 * @author camila_barbosa
 */
public class MenuMecanico {
    private final CompGerenciarOS compGerenciarOS;
    private final Scanner scanner;

    public MenuMecanico(Scanner scanner, CompGerenciarOS compGerenciarOS){
        this.scanner = scanner;
        this.compGerenciarOS=compGerenciarOS;
    }

    public void exibirMenu() {
        Usuario mecanico = UserSession.getInstance().getLoggedInUser();
        if (mecanico == null || mecanico.getTipo() != TipoUsuario.MECANICO) {
            System.out.println("Acesso permitido apenas para mecânicos.");
            return;
        }

        int opcao;
        do {
            System.out.println("\n=== MENU MECÂNICO ===");
            System.out.println("1. Atualizar Status de OS");
            System.out.println("0. Voltar ao Painel Principal"); // Alterado para clareza
            System.out.print("Opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Consumir a quebra de linha
            } catch (InputMismatchException e) {
                System.out.println("Entrada inválida. Digite um número.");
                scanner.nextLine(); // Limpar o buffer do scanner
                opcao = -1; // Valor inválido para continuar o loop
            }

            processarOpcao(opcao);

        } while (opcao != 0);
    }

    private void processarOpcao(int opcao) {
        try {
            switch (opcao) {
                case 1:
                    compGerenciarOS.atualizarStatusOrdemServico();
                    break;
                case 0:
                    System.out.println("Retornando ao Painel Principal...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }
        } catch (Exception e) { // Captura exceções gerais para melhor UX
            System.err.println("Ocorreu um erro: " + e.getMessage());
            // Opcional: logar a exceção completa para debug
            // e.printStackTrace();
        }
    }
}
