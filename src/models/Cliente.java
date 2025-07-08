/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * representa um cliente da oficina
 *
 * @author barbo
 */
public class Cliente {
    public static int proximoId = 1;

    private int id;
    private String nome;
    private String telefone;
    private String email;
    private final List<Integer> idVeiculos;

    /**
     * Construtor para criar um novo cliente.
     * @param nome Nome completo do cliente (não pode ser nulo).
     * @param telefone Telefone de contato do cliente (não pode ser nulo).
     * @param email Email do cliente (não pode ser nulo).
     */
    public Cliente(String nome, String telefone, String email) {
        this.id = proximoId++;
        this.nome = Objects.requireNonNull(nome, "Nome do cliente não pode ser nulo.");
        this.telefone = Objects.requireNonNull(telefone, "Telefone do cliente não pode ser nulo.");
        this.email = Objects.requireNonNull(email, "Email do cliente não pode ser nulo.");
        this.idVeiculos = new ArrayList<>();
    }

    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * @param id ID do cliente.
     * @param nome Nome do cliente.
     * @param telefone Telefone do cliente.
     * @param email Email do cliente.
     * @param idVeiculos Lista de IDs de veículos associados ao cliente.
     */
    public Cliente(int id, String nome, String telefone, String email, List<Integer> idVeiculos) {
        this.id = id;
        this.nome = Objects.requireNonNull(nome, "Nome do cliente não pode ser nulo.");
        this.telefone = Objects.requireNonNull(telefone, "Telefone do cliente não pode ser nulo.");
        this.email = Objects.requireNonNull(email, "Email do cliente não pode ser nulo.");
        this.idVeiculos = (idVeiculos != null) ? new ArrayList<>(idVeiculos) : new ArrayList<>();
    }

    // --- Getters e Setters ---
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
     * Retorna uma cópia da lista de IDs de veículos do cliente.
     * @return Uma nova lista contendo os IDs de todos os veículos do cliente.
     */
    public List<Integer> getIdVeiculos() {
        return new ArrayList<>(idVeiculos);
    }

    /**
     * Adiciona o ID de um veículo à lista de IDs de veículos do cliente.
     * @param idVeiculo O ID do veículo a ser adicionado.
     */
    public void adicionarIdVeiculo(int idVeiculo) {
        if (!idVeiculos.contains(idVeiculo)) {
            idVeiculos.add(idVeiculo);
            System.out.println("Cliente '" + nome + "' associado ao Veículo ID: " + idVeiculo);
        } else {
            System.out.println("Veículo ID " + idVeiculo + " já associado ao cliente " + nome + ".");
        }
    }

    /**
     * Remove o ID de um veículo da lista de IDs de veículos do cliente.
     * @param idVeiculo O ID do veículo a ser removido.
     * @return true se o ID do veículo foi encontrado e removido, false caso contrário.
     */
    public boolean removerIdVeiculo(int idVeiculo) { 
        boolean removido = idVeiculos.remove(Integer.valueOf(idVeiculo));
        if (removido) {
            System.out.println("Veículo ID " + idVeiculo + " removido da lista do cliente " + nome + ".");
        }
        return removido;
    }
    

    @Override
    public String toString() {
        return "Cliente{"
                + "id=" + id +
                ", nome='" + nome + '\'' +
                ", telefone='" + telefone + '\'' +
                ", email='" + email + '\'' +
                ", qtdVeiculos=" + idVeiculos.size() + 
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return id == cliente.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}