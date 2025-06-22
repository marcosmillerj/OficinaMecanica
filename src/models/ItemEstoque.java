/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 *
 * @author marcos_miller
 */

public class ItemEstoque {
    public static int proximoId = 1; // Contador estático para gerar IDs únicos

    private int id;
    private String nome;
    private int quantidade;
    private BigDecimal precoUnitario; // ALTERADO: De double para BigDecimal

    /**
     * Construtor para criar um novo ItemEstoque.
     * @param nome O nome do item (não pode ser nulo).
     * @param quantidade A quantidade em estoque (não pode ser negativa).
     * @param precoUnitario O preço unitário do item (não pode ser nulo ou negativo).
     */
    public ItemEstoque(String nome, int quantidade, BigDecimal precoUnitario) { // ALTERADO: Recebe BigDecimal
        this.id = proximoId++;
        this.nome = Objects.requireNonNull(nome, "Nome do item não pode ser nulo.");
        setQuantidade(quantidade); // Usa setter para validação
        setPrecoUnitario(precoUnitario); // Usa setter para validação
    }

    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * @param id ID do item.
     * @param nome Nome do item.
     * @param quantidade Quantidade em estoque.
     * @param precoUnitario Preço unitário do item.
     */
    public ItemEstoque(int id, String nome, int quantidade, BigDecimal precoUnitario) { // ALTERADO: Recebe BigDecimal
        this.id = id;
        this.nome = Objects.requireNonNull(nome, "Nome do item não pode ser nulo.");
        setQuantidade(quantidade);
        setPrecoUnitario(precoUnitario);
    }

    // --- Getters e Setters ---
    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = Objects.requireNonNull(nome, "Nome do item não pode ser nulo.");
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa.");
        }
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() { // ALTERADO: Retorna BigDecimal
        return precoUnitario;
    }

    public void setPrecoUnitario(BigDecimal precoUnitario) { // ALTERADO: Recebe BigDecimal
        Objects.requireNonNull(precoUnitario, "Preço unitário não pode ser nulo.");
        if (precoUnitario.compareTo(BigDecimal.ZERO) < 0) { // Comparação com BigDecimal.ZERO
            throw new IllegalArgumentException("Preço unitário não pode ser negativo.");
        }
        this.precoUnitario = precoUnitario;
    }

    @Override
    public String toString() {
        return String.format("ItemEstoque {ID: %d | Nome: %s | Quantidade: %d | Preço Unitário: R$ %.2f}",
                id, nome, quantidade, precoUnitario); // BigDecimal já se formata bem
    }

    // Adição de equals e hashCode para garantir que itens sejam comparados por ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEstoque that = (ItemEstoque) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
