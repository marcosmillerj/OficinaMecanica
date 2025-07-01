/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa um **cliente** da oficina, contendo suas informações básicas
 * e uma lista dos identificadores dos veículos que possui.
 * Cada cliente possui um ID único, nome, telefone, e-mail e uma associação com seus veículos.
 *
 * @author barbo
 */
public class Cliente {
    /**
     * Contador estático para gerar **IDs únicos** para cada nova instância de Cliente.
     * Garante que cada cliente receba um identificador exclusivo ao ser criado.
     */
    public static int proximoId = 1;

    private int id; // ID único do cliente
    private String nome;
    private String telefone;
    private String email;
    private final List<Integer> idVeiculos; // Lista de IDs de veículos (int), e 'final'

    /**
     * Construtor para criar uma nova instância de **Cliente**.
     * Atribui um ID único automaticamente e inicializa a lista de IDs de veículos vazia.
     * Valida que os parâmetros essenciais (nome, telefone, email) não sejam nulos.
     *
     * @param nome Nome completo do cliente. Não pode ser nulo.
     * @param telefone Telefone de contato do cliente. Não pode ser nulo.
     * @param email Email do cliente. Não pode ser nulo.
     * @throws NullPointerException se `nome`, `telefone` ou `email` forem nulos.
     */
    public Cliente(String nome, String telefone, String email) {
        this.id = proximoId++;
        this.nome = Objects.requireNonNull(nome, "Nome do cliente não pode ser nulo.");
        this.telefone = Objects.requireNonNull(telefone, "Telefone do cliente não pode ser nulo.");
        this.email = Objects.requireNonNull(email, "Email do cliente não pode ser nulo.");
        this.idVeiculos = new ArrayList<>(); // Inicializa a lista de IDs de veículos vazia
    }

    /**
     * Construtor para uso por bibliotecas de desserialização (e.g., Gson)
     * para reconstruir um objeto `Cliente` a partir de dados persistidos.
     * Permite a atribuição explícita de todos os atributos, incluindo o ID
     * e a lista de IDs de veículos previamente associados.
     *
     * @param id O identificador único do cliente.
     * @param nome O nome completo do cliente.
     * @param telefone O telefone de contato do cliente.
     * @param email O e-mail do cliente.
     * @param idVeiculos Uma lista de identificadores de veículos associados ao cliente.
     * @throws NullPointerException se `nome`, `telefone` ou `email` forem nulos.
     */
    public Cliente(int id, String nome, String telefone, String email, List<Integer> idVeiculos) {
        this.id = id;
        this.nome = Objects.requireNonNull(nome, "Nome do cliente não pode ser nulo."); // Validações também no construtor do Gson para consistência
        this.telefone = Objects.requireNonNull(telefone, "Telefone do cliente não pode ser nulo.");
        this.email = Objects.requireNonNull(email, "Email do cliente não pode ser nulo.");
        // Garante que a lista de veículos seja uma nova instância para evitar problemas de referência direta do Gson.
        this.idVeiculos = (idVeiculos != null) ? new ArrayList<>(idVeiculos) : new ArrayList<>();
    }

 
    public int getId() {
        return id;
    }
    
    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, "Nome do cliente não pode ser nulo.");
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = Objects.requireNonNull(telefone, "Telefone do cliente não pode ser nulo.");
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = Objects.requireNonNull(email, "Email do cliente não pode ser nulo.");
    }

    /**
     * Retorna uma **cópia** da lista de identificadores de veículos associados a este cliente.
     * O retorno de uma cópia garante o **encapsulamento**, protegendo a lista interna
     * contra modificações externas diretas.
     *
     * @return Uma nova `List<Integer>` contendo os IDs de todos os veículos do cliente.
     */
    public List<Integer> getIdVeiculos() { // ALTERADO: Retorna List<Integer>
        return new ArrayList<>(idVeiculos); // Retorna uma cópia para encapsulamento
    }

    /**
     * Adiciona o identificador de um veículo à lista de IDs de veículos associados a este cliente.
     * A adição ocorre apenas se o ID do veículo ainda não estiver presente na lista,
     * prevenindo duplicatas.
     *
     * @param idVeiculo O identificador único do veículo a ser adicionado.
     */
    public void adicionarIdVeiculo(int idVeiculo) { // ALTERADO: Recebe int (ID)
        if (!idVeiculos.contains(idVeiculo)) { // Verifica se já não contém o ID
            idVeiculos.add(idVeiculo);
            System.out.println("Cliente '" + nome + "' associado ao Veículo ID: " + idVeiculo);
        } else {
            System.out.println("Veículo ID " + idVeiculo + " já associado ao cliente " + nome + ".");
        }
    }

    /**
     * Remove o identificador de um veículo da lista de IDs de veículos associados a este cliente.
     *
     * @param idVeiculo O identificador único do veículo a ser removido.
     * @return `true` se o ID do veículo foi encontrado e removido com sucesso, `false` caso contrário.
     */
    public boolean removerIdVeiculo(int idVeiculo) { // ALTERADO: Recebe int (ID)
        boolean removido = idVeiculos.remove(Integer.valueOf(idVeiculo)); // Usa Integer.valueOf para remover por valor
        if (removido) {
            System.out.println("Veículo ID " + idVeiculo + " removido da lista do cliente " + nome + ".");
        }
        return removido;
    }
    
    /**
     * Retorna uma representação em String do objeto Cliente,
     * incluindo seu ID, nome, telefone, e-mail e a quantidade de veículos associados.
     *
     * @return Uma String formatada com os detalhes do cliente.
     */
    @Override
    public String toString() {
        return "Cliente{"
                + "id=" + id +
                ", nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", qtdVeiculos=" + idVeiculos.size() + // Exibe a quantidade de IDs de veículos
                '}';
    }

    /**
     * Compara este objeto Cliente com o objeto especificado para verificar igualdade.
     * Dois clientes são considerados iguais se possuírem o **mesmo ID**.
     *
     * @param o O objeto a ser comparado com este cliente.
     * @return `true` se o objeto especificado for igual a este cliente, `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id == cliente.id;
    }

    /**
     * Retorna um valor de código hash para o objeto Cliente.
     * O código hash é baseado exclusivamente no **ID do cliente**, garantindo consistência
     * com o método `equals` (contrato `hashCode()/equals()`).
     *
     * @return Um valor de código hash para este objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}