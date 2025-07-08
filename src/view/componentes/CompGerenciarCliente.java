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
 * Inclui criar, listar, editar e excluir clientes, além de funcionalidades de seleção on-demand.
 *
 * @author marcos_miller
 */
public class CompGerenciarCliente {

    private final ClienteService clienteService;
    private final Scanner scanner;

    public CompGerenciarCliente(ClienteService clienteService, Scanner scanner) {
        this.clienteService = clienteService;
        this.scanner = scanner;
    }

    /**
     * Exibe o menu principal para o gerenciamento de clientes,
     * permitindo adicionar, listar, editar, excluir e ordenar clientes.
     */
    public void exibirMenuPrincipal() {
        int opcao;
        do {
            System.out.println("\n===== Gerenciar Clientes =====");
            System.out.println("1. Adicionar Novo Cliente");
            System.out.println("2. Listar Todos os Clientes (Sem Ordenação)");
            System.out.println("3. Listar Clientes por Nome (Ordenado)");
            System.out.println("4. Listar Clientes por Email (Ordenado)");
            System.out.println("5. Editar Cliente Existente");
            System.out.println("6. Excluir Cliente");
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
                case 5:
                    editarCliente();
                    break;
                case 6:
                    excluirCliente();
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
     *
     * @param comparator O Comparator a ser usado para ordenar, ou null para sem ordenação.
     */
    private void listarClientesInterno(Comparator<Cliente> comparator) {
        System.out.println("\n--- LISTA DE CLIENTES ---");
        List<Cliente> clientes = (comparator != null) ?
                                 clienteService.listarClientesOrdenados(comparator) :
                                 clienteService.listarTodosClientes();

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
        } else {
            System.out.println("ID    | Nome                   | Telefone         | Email");
            System.out.println("------------------------------------------------------------------");
            for (Cliente cliente : clientes) {
                System.out.printf("%-5d | %-22s | %-16s | %s%n",
                                  cliente.getId(), cliente.getNome(), cliente.getTelefone(), cliente.getEmail());
            }
            System.out.println("------------------------------------------------------------------");
        }
    }

    /**
     * Permite ao usuário editar os dados de um cliente existente.
     */
    private void editarCliente() {
        System.out.println("\n--- EDITAR CLIENTE ---");
        System.out.print("Digite o ID do cliente a ser editado: ");
        int idCliente = lerInteiroValido();

        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(idCliente);
        if (clienteOpt.isEmpty()) {
            System.out.println("Cliente com ID " + idCliente + " não encontrado.");
            return;
        }

        Cliente clienteAtual = clienteOpt.get();
        System.out.println("Cliente selecionado: " + clienteAtual.getNome() + " (ID: " + clienteAtual.getId() + ")");

        System.out.print("Novo nome (deixe em branco para manter '" + clienteAtual.getNome() + "'): ");
        String novoNome = scanner.nextLine();
        if (novoNome.isEmpty()) {
            novoNome = clienteAtual.getNome();
        }

        System.out.print("Novo telefone (deixe em branco para manter '" + clienteAtual.getTelefone() + "'): ");
        String novoTelefone = scanner.nextLine();
        if (novoTelefone.isEmpty()) {
            novoTelefone = clienteAtual.getTelefone();
        }

        System.out.print("Novo email (deixe em branco para manter '" + clienteAtual.getEmail() + "'): ");
        String novoEmail = scanner.nextLine();
        if (novoEmail.isEmpty()) {
            novoEmail = clienteAtual.getEmail();
        }

        try {
            clienteService.atualizarCliente(idCliente, novoNome, novoTelefone, novoEmail);
            System.out.println("Cliente atualizado com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao atualizar cliente: " + e.getMessage());
        }
    }

    /**
     * Permite ao usuário excluir um cliente existente.
     */
    private void excluirCliente() {
        System.out.println("\n--- EXCLUIR CLIENTE ---");
        System.out.print("Digite o ID do cliente a ser excluído: ");
        int idCliente = lerInteiroValido();

        Optional<Cliente> clienteOpt = clienteService.buscarClientePorId(idCliente);
        if (clienteOpt.isEmpty()) {
            System.out.println("Cliente com ID " + idCliente + " não encontrado.");
            return;
        }

        Cliente clienteParaExcluir = clienteOpt.get();
        System.out.println("Tem certeza que deseja excluir o cliente: " + clienteParaExcluir.getNome() + " (ID: " + clienteParaExcluir.getId() + ")? (S/N)");
        String confirmacao = scanner.nextLine().trim().toUpperCase();

        if (confirmacao.equals("S")) {
            try {
                clienteService.removerCliente(idCliente);
                System.out.println("Cliente excluído com sucesso!");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.err.println("Erro ao excluir cliente: " + e.getMessage());
            }
        } else {
            System.out.println("Exclusão de cliente cancelada.");
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
}