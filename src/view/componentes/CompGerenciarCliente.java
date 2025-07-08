package view.componentes;

import comparator.ClienteComparatorPorEmail;
import comparator.ClienteComparatorPorNome;
import java.util.Comparator;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.Cliente;
import service.ClienteService;

/**
 * Componente visual responsável por gerenciar operações de Cliente.
 * Isso inclui solicitar clientes existentes, cadastrar novos, listar e ordenar.
 *
 * @author marcos_miller
 */
public class CompGerenciarCliente {

    private ClienteService clienteService;
    private Scanner scanner;

    public CompGerenciarCliente(ClienteService clienteService, Scanner scanner) {
        this.clienteService = clienteService;
        this.scanner = scanner;
    }

    /**
     * Exibe o menu principal para o gerenciamento de clientes,
     * permitindo adicionar, listar e ordenar clientes.
     */
    public void exibirMenuPrincipal() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Clientes =====");
            System.out.println("1. Adicionar Novo Cliente");
            System.out.println("2. Listar Todos os Clientes (Sem Ordenação)");
            System.out.println("3. Listar Clientes por Nome (Ordenado)");
            System.out.println("4. Listar Clientes por Email (Ordenado)");
            System.out.println("0. Voltar ao Menu Anterior");
            System.out.print("Escolha uma opção: ");

            try {
                opcao = scanner.nextInt();
                scanner.nextLine(); // Consome a quebra de linha
            } catch (InputMismatchException e) {
                System.err.println("Entrada inválida. Por favor, digite um número.");
                scanner.nextLine(); // Consome a entrada inválida
                opcao = -1; // Garante que o loop continue
            }

            switch (opcao) {
                case 1:
                    adicionarNovoClienteInterno();
                    break;
                case 2:
                    listarClientesInterno(null);
                    break;
                case 3:
                    listarClientesInterno(new ClienteComparatorPorNome());
                    break;
                case 4:
                    listarClientesInterno(new ClienteComparatorPorEmail());
                    break;
                case 0:
                    System.out.println("Saindo do Gerenciamento de Clientes.");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        } while (opcao != 0);
    }

    /**
     * Solicita ao usuário os dados para cadastrar um novo cliente.
     */
    private void adicionarNovoClienteInterno() {
        System.out.println("\n--- ADICIONAR NOVO CLIENTE ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();

        try {
            clienteService.adicionarCliente(nome, telefone, email);
            System.out.println("Cliente adicionado com sucesso!");
        } catch (IllegalStateException e) {
            System.err.println("Erro ao adicionar cliente: " + e.getMessage());
        }
    }

    /**
     * Lista todos os clientes, opcionalmente utilizando um Comparator para ordenação.
     * @param comparator O Comparator a ser usado para ordenar, ou null para sem ordenação.
     */
    private void listarClientesInterno(Comparator<Cliente> comparator) {
        System.out.println("\n--- LISTA DE CLIENTES ---");
        List<Cliente> clientes;
        if (comparator != null) {
            clientes = clienteService.listarClientesOrdenados(comparator);
            System.out.println("Lista ordenada.");
        } else {
            clientes = clienteService.listarTodosClientes();
            System.out.println("Lista sem ordenação específica.");
        }

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            clientes.forEach(System.out::println);
        }
    }

    /**
     * Solicita ao usuário que forneça um email para buscar um cliente existente.
     * Este método é público pois pode ser usado por outros componentes
     * que precisam selecionar um cliente (e.g., CompGerenciarOS, CompGerenciarAgendamento).
     *
     * @return Um Optional contendo o Cliente encontrado, ou Optional.empty() se não encontrado.
     */
    public Optional<Cliente> solicitarClienteExistente() {
        System.out.print("Digite o Email do Cliente: ");
        String email = scanner.nextLine();
        Optional<Cliente> clienteOpt = clienteService.buscarClientePorEmail(email);
        if (clienteOpt.isEmpty()) {
            System.out.println("Cliente com email '" + email + "' não encontrado.");
        }
        return clienteOpt;
    }

    /**
     * Solicita ao usuário os dados para cadastrar um novo cliente.
     * Este método é público pois pode ser usado por outros componentes
     * que precisam cadastrar um cliente (e.g., CompGerenciarOS, CompGerenciarAgendamento).
     *
     * @return Um Optional contendo o Cliente recém-cadastrado, ou Optional.empty() em caso de falha.
     */
    public Optional<Cliente> solicitarDadosNovoCliente() {
        System.out.println("--- NOVO CADASTRO DE CLIENTE ---");
        System.out.print("Nome do Cliente: ");
        String nome = scanner.nextLine();
        System.out.print("Telefone do Cliente: ");
        String telefone = scanner.nextLine();
        System.out.print("Email do Cliente: ");
        String email = scanner.nextLine();

        try {
            Cliente novoCliente = clienteService.adicionarCliente(nome, telefone, email);
            System.out.println("Cliente '" + novoCliente.getNome() + "' cadastrado com sucesso!");
            return Optional.of(novoCliente);
        } catch (IllegalStateException e) {
            System.err.println("Erro ao cadastrar cliente: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Guia o usuário pelo processo de seleção ou criação de um cliente.
     * Este é o método chave para a funcionalidade "on-demand" em outros componentes.
     * Ele é público para que CompGerenciarOS e CompGerenciarAgendamento possam usá-lo.
     *
     * @return Um Optional contendo o Cliente selecionado/criado, ou Optional.empty() se o processo for abortado.
     */
    public Optional<Cliente> selecionarOuCriarCliente() {
        System.out.print("Cliente já cadastrado no sistema? (S/N/C - 'C' para Cancelar): ");
        String respCliente = scanner.nextLine().trim().toUpperCase();

        if (respCliente.equals("S")) {
            return solicitarClienteExistente();
        } else if (respCliente.equals("N")) {
            return solicitarDadosNovoCliente();
        } else if (respCliente.equals("C")) {
            System.out.println("Operação de seleção/criação de cliente cancelada.");
            return Optional.empty();
        } else {
            System.out.println("Resposta inválida. Operação de seleção/criação de cliente abortada.");
            return Optional.empty();
        }
    }
}