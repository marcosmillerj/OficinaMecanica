/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.componentes;

/**
 *
 * @author marcos_miller
 */

import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.ItemEstoque;
import service.ItemEstoqueService;

public class CompGerenciarEstoque {

    private ItemEstoqueService itemEstoqueService;
    private Scanner scanner;

    public CompGerenciarEstoque(ItemEstoqueService itemEstoqueService, Scanner scanner) {
        this.itemEstoqueService = itemEstoqueService;
        this.scanner = scanner;
    }

    /**
     * Exibe o menu de opções para gerenciamento de Estoque.
     */
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Estoque =====");
            System.out.println("1. Adicionar Novo Item");
            System.out.println("2. Listar Todos os Itens");
            System.out.println("3. Atualizar Item (Nome, Quantidade, Preço)");
            System.out.println("4. Dar Baixa em Item (Remover Quantidade)");
            System.out.println("5. Adicionar Quantidade em Item (Reestoque)");
            System.out.println("6. Remover Item");
            System.out.println("0. Voltar ao Menu Anterior");
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

    private void processarOpcao(int opcao) {
        switch (opcao) {
            case 1: adicionarItem(); break;
            case 2: listarItens(); break;
            case 3: atualizarItem(); break;
            case 4: darBaixaEmItem(); break;
            case 5: adicionarQuantidadeEmItem(); break;
            case 6: removerItem(); break;
            case 0: System.out.println("Saindo do Gerenciamento de Estoque."); break;
            default: System.out.println("Opção inválida. Tente novamente."); break;
        }
    }

    private void adicionarItem() {
        System.out.println("\n--- Adicionar Novo Item ao Estoque ---");
        System.out.print("Código do Item (Ex: VELA-001): "); // NOVO: Solicitar o código
        String codigo = scanner.nextLine();
        System.out.print("Nome do Item: "); String nome = scanner.nextLine();
        System.out.print("Quantidade Inicial: "); int quantidade = lerInteiroValido();
        System.out.print("Preço Unitário (Ex: 12.50): "); BigDecimal precoUnitario = lerBigDecimalValido();

        try {
            // AGORA PASSA O CÓDIGO!
            ItemEstoque novoItem = itemEstoqueService.adicionarItem(codigo, nome, quantidade, precoUnitario);
            System.out.println("Item '" + novoItem.getNome() + "' adicionado com sucesso ao estoque!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Erro ao adicionar item: " + e.getMessage());
        }
    }

    private void listarItens() {
        System.out.println("\n--- LISTA DE ITENS EM ESTOQUE ---");
        List<ItemEstoque> itens = itemEstoqueService.listarTodosItens();
        if (itens.isEmpty()) {
            System.out.println("Nenhum item cadastrado no estoque.");
        } else {
            itens.forEach(System.out::println);
        }
    }

    private void atualizarItem() {
        System.out.println("\n--- Atualizar Item do Estoque ---");
        System.out.print("Digite o ID do item a ser atualizado: "); int id = lerInteiroValido();

        Optional<ItemEstoque> itemOpt = itemEstoqueService.buscarItemPorId(id);
        if (itemOpt.isEmpty()) {
            System.out.println("Item com ID " + id + " não encontrado.");
            return;
        }
        ItemEstoque itemExistente = itemOpt.get();
        System.out.println("Item encontrado: " + itemExistente.getNome() + " (Código: " + itemExistente.getCodigo() + ")"); // Exibir código

        System.out.println("Deixe em branco para manter o valor atual.");

        System.out.print("Novo Código (" + itemExistente.getCodigo() + "): "); // NOVO: Pedir novo código
        String novoCodigo = scanner.nextLine();
        if (novoCodigo.isEmpty()) { novoCodigo = itemExistente.getCodigo(); }

        System.out.print("Novo Nome (" + itemExistente.getNome() + "): "); String novoNome = scanner.nextLine();
        if (novoNome.isEmpty()) { novoNome = itemExistente.getNome(); }

        System.out.print("Nova Quantidade (" + itemExistente.getQuantidade() + "): ");
        String qtdStr = scanner.nextLine();
        int novaQuantidade = qtdStr.isEmpty() ? itemExistente.getQuantidade() : Integer.parseInt(qtdStr);

        System.out.print("Novo Preço Unitário (" + itemExistente.getPrecoUnitario() + "): ");
        String precoStr = scanner.nextLine();
        BigDecimal novoPreco = precoStr.isEmpty() ? itemExistente.getPrecoUnitario() : new BigDecimal(precoStr);

        try {
            // AGORA PASSA O NOVO CÓDIGO!
            boolean sucesso = itemEstoqueService.atualizarItem(id, novoCodigo, novoNome, novaQuantidade, novoPreco);
            if (sucesso) { System.out.println("Item atualizado com sucesso!"); }
            else { System.out.println("Falha ao atualizar item."); }
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.err.println("Erro ao atualizar item: " + e.getMessage());
        }
    }

    private void darBaixaEmItem() {
        System.out.println("\n--- Dar Baixa em Item do Estoque ---");
        System.out.print("Digite o ID do item para dar baixa: "); int id = lerInteiroValido();
        System.out.print("Quantidade a dar baixa: "); int quantidadeBaixa = lerInteiroValido();

        try {
            boolean sucesso = itemEstoqueService.darBaixaEstoque(id, quantidadeBaixa);
            if (sucesso) { System.out.println("Baixa realizada com sucesso!"); }
            else { System.out.println("Item não encontrado para dar baixa."); }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao dar baixa: " + e.getMessage());
        }
    }

    private void adicionarQuantidadeEmItem() {
        System.out.println("\n--- Adicionar Quantidade em Item do Estoque ---");
        System.out.print("Digite o ID do item para adicionar quantidade: "); int id = lerInteiroValido();
        System.out.print("Quantidade a adicionar: "); int quantidadeAdicao = lerInteiroValido();

        try {
            boolean sucesso = itemEstoqueService.adicionarEstoque(id, quantidadeAdicao);
            if (sucesso) { System.out.println("Quantidade adicionada com sucesso!"); }
            else { System.out.println("Item não encontrado para adicionar quantidade."); }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao adicionar quantidade: " + e.getMessage());
        }
    }

    private void removerItem() {
        System.out.println("\n--- Remover Item do Estoque ---");
        System.out.print("Digite o ID do item a ser removido: "); int id = lerInteiroValido();

        try {
            boolean sucesso = itemEstoqueService.removerItem(id);
            if (sucesso) { System.out.println("Item removido com sucesso!"); }
            else { System.out.println("Item não encontrado para remoção."); }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao remover item: " + e.getMessage());
        }
    }

    // --- Métodos Auxiliares de Leitura de Input ---
    private int lerInteiroValido() {
        while (true) {
            try {
                int valor = scanner.nextInt();
                scanner.nextLine();
                return valor;
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, digite um número inteiro.");
                scanner.nextLine();
            }
        }
    }

    private BigDecimal lerBigDecimalValido() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return new BigDecimal(input);
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, digite um número decimal válido (ex: 12.50).");
            }
        }
    }
}