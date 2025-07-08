/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view.componentes;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.Usuario;
import models.enums.TipoUsuario;
import repository.UsuarioCRUD;
import service.UsuarioService;

/**
 *
 * @author marcos_miller
 */
public class CompGerenciarUsuario {

    private UsuarioService usuarioService;
    private Scanner scanner;

    /**
     * Construtor do MenuUsuario.
     * @param usuarioCRUD Instância do UsuarioCRUD, que será usada para inicializar o UsuarioService.
     */
    public CompGerenciarUsuario(UsuarioCRUD usuarioCRUD, Scanner scanner) {
        this.usuarioService = new UsuarioService(usuarioCRUD);
        this.scanner = scanner;
        System.out.println("Menu de Gerenciamento de Usuários iniciado.");
    }
    
    // Método principal para exibir o menu e gerenciar as opções
    public void exibirMenu() {
        int opcao;
        do {
            System.out.println("\n--- Gerenciar Usuários ---");
            System.out.println("1. Adicionar Novo Usuário");
            System.out.println("2. Listar Todos os Usuários");
            System.out.println("3. Atualizar Usuário por CPF");
            System.out.println("4. Remover Usuário por CPF");
            System.out.println("0. Voltar ao Menu Principal");
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
                    adicionarUsuario();
                    break;
                case 2:
                    listarUsuarios();
                    break;
                case 3:
                    atualizarUsuario();
                    break;
                case 4:
                    removerUsuario();
                    break;
                case 0:
                    System.out.println("Saindo do Gerenciamento de Usuários.");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        } while (opcao != 0);
    }

    // Método para adicionar um novo usuário (agora chamando o Service e com TipoUsuario)
    private void adicionarUsuario() {
        System.out.println("\n--- Adicionar Novo Usuário ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF (11 dígitos, apenas números): ");
        String cpf = scanner.nextLine();
        System.out.print("Endereço: ");
        String endereco = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();

        // Seleção do Tipo de Usuário (usando o enum TipoUsuario)
        System.out.println("Selecione o Tipo de Usuário:");
        TipoUsuario[] tipos = TipoUsuario.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + ". " + tipos[i].getDescricao());
        }
        System.out.print("Opção do Tipo: ");
        int tipoOpcao = 0;
        try {
            tipoOpcao = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Erro: Entrada inválida para o tipo. Abortando adição.");
            scanner.nextLine(); 
            return;
        }

        TipoUsuario tipoSelecionado = null;
        try {
            // Converte a opção numérica para o valor do enum
            tipoSelecionado = tipos[tipoOpcao - 1]; // ordinal() é base 0, opções são base 1
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Erro: Opção de tipo inválida. Abortando adição.");
            return;
        }

        try {
            // A view agora passa os dados brutos para o service
            Usuario novoUsuario = usuarioService.adicionarUsuario(nome, cpf, endereco, email, telefone, senha, tipoSelecionado);
            System.out.println("Usuário '" + novoUsuario.getNome() + "' (ID: " + novoUsuario.getId() + ") adicionado com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            // A view trata as exceções que vêm do service (ou do model, via service)
            System.err.println("Erro ao adicionar usuário: " + e.getMessage());
        }
    }
    
    // Método para listar todos os usuários (chama o Service)
    private void listarUsuarios() {
        System.out.println("\n--- Lista de Usuários ---");
        List<Usuario> usuarios = usuarioService.listarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("Nenhum usuário cadastrado.");
        } else {
            for (Usuario u : usuarios) {
                System.out.println(u.toString());
            }
        }
    }

    // Método para atualizar um usuário (agora busca por CPF e chama o Service)
    private void atualizarUsuario() {
        System.out.println("\n--- Atualizar Usuário ---");
        System.out.print("Digite o CPF do usuário a ser atualizado: ");
        String cpfBusca = scanner.nextLine();

        Optional<Usuario> usuarioExistenteOpt = usuarioService.buscarUsuarioPorCpf(cpfBusca);
        if (usuarioExistenteOpt.isEmpty()) {
            System.out.println("Usuário com CPF " + cpfBusca + " não encontrado.");
            return;
        }
        Usuario usuarioExistente = usuarioExistenteOpt.get();

        System.out.println("Usuário encontrado: " + usuarioExistente.getNome() + " (Tipo: " + usuarioExistente.getTipo().getDescricao() + ")");
        System.out.println("Deixe em branco para manter o valor atual.");

        System.out.print("Novo Nome (" + usuarioExistente.getNome() + "): ");
        String novoNome = scanner.nextLine();
        if (novoNome.isEmpty()) { novoNome = usuarioExistente.getNome(); }

        System.out.print("Novo Endereço (" + usuarioExistente.getEndereco() + "): ");
        String novoEndereco = scanner.nextLine();
        if (novoEndereco.isEmpty()) { novoEndereco = usuarioExistente.getEndereco(); }

        System.out.print("Novo Email (" + usuarioExistente.getEmail() + "): ");
        String novoEmail = scanner.nextLine();
        if (novoEmail.isEmpty()) { novoEmail = usuarioExistente.getEmail(); }

        System.out.print("Novo Telefone (" + usuarioExistente.getTelefone() + "): ");
        String novoTelefone = scanner.nextLine();
        if (novoTelefone.isEmpty()) { novoTelefone = usuarioExistente.getTelefone(); }

        // Seleção do Novo Tipo de Usuário
        System.out.println("Selecione o Novo Tipo de Usuário (Atual: " + usuarioExistente.getTipo().getDescricao() + "):");
        TipoUsuario[] tipos = TipoUsuario.values();
        for (int i = 0; i < tipos.length; i++) {
            System.out.println((i + 1) + ". " + tipos[i].getDescricao());
        }
        System.out.print("Opção do Novo Tipo: ");
        int novoTipoOpcao = 0;
        try {
            novoTipoOpcao = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Erro: Entrada inválida para o tipo. Mantendo tipo atual.");
            scanner.nextLine();
            return;
        }
        // Valida se a opção é válida e, se não for, mantém o tipo existente
        TipoUsuario novoTipoSelecionado = null;
        try {
            novoTipoSelecionado = tipos[novoTipoOpcao - 1];
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Erro: Opção de tipo inválida. Mantendo tipo atual: " + usuarioExistente.getTipo().getDescricao());
            novoTipoSelecionado = usuarioExistente.getTipo(); // Mantém o tipo antigo
        }


        try {
            // Passa o ID do usuário (que obtivemos após a busca por CPF) para o Service
            // O Service é que usará o ID para encontrar o objeto na lista do CRUD
            boolean sucesso = usuarioService.atualizarUsuario(usuarioExistente.getId(), novoNome, novoEndereco, novoEmail, novoTelefone, novoTipoSelecionado);
            if (sucesso) {
                System.out.println("Usuário atualizado com sucesso!");
            } else {
                System.out.println("Falha ao atualizar usuário. (Verifique o log de erros do Service)");
            }
        } catch (IllegalStateException e) {
            System.err.println("Erro ao atualizar usuário: " + e.getMessage()); 
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de formato ao atualizar usuário: " + e.getMessage());
        }
    }

    // Método para remover um usuário (agora busca por CPF e chama o Service)
    private void removerUsuario() {
        System.out.println("\n--- Remover Usuário ---");
        System.out.print("Digite o CPF do usuário a ser removido: ");
        String cpfBusca = scanner.nextLine();

        Optional<Usuario> usuarioParaRemoverOpt = usuarioService.buscarUsuarioPorCpf(cpfBusca);
        if (usuarioParaRemoverOpt.isEmpty()) {
            System.out.println("Usuário com CPF " + cpfBusca + " não encontrado.");
            return;
        }

        // Se encontrou o usuário, usa o ID dele para remover
        boolean sucesso = usuarioService.removerUsuario(usuarioParaRemoverOpt.get().getId());
        if (sucesso) {
            System.out.println("Usuário removido com sucesso!");
        } else {
            System.out.println("Falha ao remover usuário. (Verifique o log de erros do Service)");
        }
    }
}