/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.Scanner;
import models.RegistroPonto;
import models.Usuario;
import service.RegistroPontoService;
import util.UserSession;

/**
 *
 * @author marcos_miller
 */
public class ComponentePonto {

    private RegistroPontoService pontoService; // Dependência do serviço de ponto
    private Scanner scanner;                   // Scanner injetado para entrada do usuário

    // Formatador para exibir a data e hora do ponto
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd/MM");

    /**
     * Construtor do ComponentePonto.
     * @param pontoService O serviço de negócio para operações de ponto.
     * @param scanner O scanner para ler a entrada do usuário.
     */
    public ComponentePonto(RegistroPontoService pontoService, Scanner scanner) {
        this.pontoService = pontoService;
        this.scanner = scanner;
    }

    /**
     * Exibe o status atual do ponto para o usuário logado e as opções de registro.
     * @return A opção numérica escolhida pelo usuário (8 para entrada, 9 para saída, ou outra para ignorar).
     */
    public int exibirStatusEPedirAcao() {
        Usuario usuarioLogado = UserSession.getInstance().getLoggedInUser(); // Obtém o usuário logado da sessão

        if (usuarioLogado == null) {
            System.err.println("Erro: Nenhum usuário logado na sessão para exibir o ponto.");
            return -1; // Retorna um valor inválido
        }

        System.out.println("\n--- Status do Ponto de " + usuarioLogado.getNome() + " ---");

        Optional<RegistroPonto> pontoAbertoOpt = pontoService.obterStatusPontoAtual(usuarioLogado);
        String statusEntrada = "Não Registrado";
        String statusSaida = "Não Registrado";

        if (pontoAbertoOpt.isPresent()) {
            RegistroPonto pontoAberto = pontoAbertoOpt.get();
            statusEntrada = pontoAberto.getDataHoraEntrada().format(FORMATTER);
            if (pontoAberto.getDataHoraSaida() != null) {
                statusSaida = pontoAberto.getDataHoraSaida().format(FORMATTER);
            }
        }

        System.out.println("Entrada: " + statusEntrada);
        System.out.println("Saída: " + statusSaida);
        System.out.println("---------------------------");

        System.out.println("Aperte 8 para Assinar a Entrada");
        System.out.println("Aperte 9 para Assinar a Saída");
        System.out.println("(Outra tecla para ignorar)");
        System.out.print("Escolha uma opção de ponto: ");

        try {
            int opcaoPonto = scanner.nextInt();
            scanner.nextLine(); // Consome a nova linha
            return opcaoPonto;
        } catch (java.util.InputMismatchException e) {
            scanner.nextLine(); // Limpa o buffer em caso de entrada inválida
            return -1; // Retorna um valor que indica que a opção não é de ponto
        }
    }

    /**
     * Processa a ação de ponto escolhida pelo usuário.
     * @param opcao A opção numérica (8 ou 9).
     * @param usuarioLogado O usuário logado para quem a ação de ponto será registrada.
     */
    public void processarAcaoPonto(int opcao, Usuario usuarioLogado) {
        try {
            if (opcao == 8) {
                pontoService.registrarEntrada(usuarioLogado);
            } else if (opcao == 9) {
                pontoService.registrarSaida(usuarioLogado);
            }
        } catch (IllegalStateException e) {
            System.err.println("Erro ao registrar ponto: " + e.getMessage());
        }
    }
}
