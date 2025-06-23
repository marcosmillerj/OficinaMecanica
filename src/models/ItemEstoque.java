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
    public static int proximoId = 1;

    private int id;
    private String codigo; // NOVO ATRIBUTO: Código real da peça (Ex: "VELA-NGK-BP6ES")
    private String nome;
    private int quantidade;
    private BigDecimal precoUnitario;

    /**
     * Construtor para criar um novo ItemEstoque.
     * @param codigo O código identificador da peça (não pode ser nulo).
     * @param nome O nome do item (não pode ser nulo).
     * @param quantidade A quantidade em estoque (não pode ser negativa).
     * @param precoUnitario O preço unitário do item (não pode ser nulo ou negativo).
     */
    public ItemEstoque(String codigo, String nome, int quantidade, BigDecimal precoUnitario) { // NOVO PARÂMETRO 'codigo'
        this.id = proximoId++;
        this.codigo = Objects.requireNonNull(codigo, "Código do item não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome do item não pode ser nulo.");
        setQuantidade(quantidade); // Usa setter para validação
        setPrecoUnitario(precoUnitario); // Usa setter para validação
    }

    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * @param id ID do item.
     * @param codigo Código do item.
     * @param nome Nome do item.
     * @param quantidade Quantidade em estoque.
     * @param precoUnitario Preço unitário do item.
     */
    public ItemEstoque(int id, String codigo, String nome, int quantidade, BigDecimal precoUnitario) { // NOVO PARÂMETRO 'codigo'
        this.id = id;
        this.codigo = Objects.requireNonNull(codigo, "Código do item não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome do item não pode ser nulo.");
        setQuantidade(quantidade);
        setPrecoUnitario(precoUnitario);
    }

    // --- Getters e Setters ---
    public int getId() { return id; }

    public String getCodigo() { return codigo; } // NOVO GETTER
    public void setCodigo(String codigo) { this.codigo = Objects.requireNonNull(codigo, "Código do item não pode ser nulo."); } // NOVO SETTER

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = Objects.requireNonNull(nome, "Nome do item não pode ser nulo."); }

    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) {
        if (quantidade < 0) { throw new IllegalArgumentException("Quantidade não pode ser negativa."); }
        this.quantidade = quantidade;
    }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) {
        Objects.requireNonNull(precoUnitario, "Preço unitário não pode ser nulo.");
        if (precoUnitario.compareTo(BigDecimal.ZERO) < 0) { throw new IllegalArgumentException("Preço unitário não pode ser negativo."); }
        this.precoUnitario = precoUnitario;
    }

    @Override
    public String toString() {
        return String.format("ItemEstoque {ID: %d | Código: %s | Nome: %s | Quantidade: %d | Preço Unitário: R$ %.2f}",
                id, codigo, nome, quantidade, precoUnitario);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEstoque that = (ItemEstoque) o;
        return id == that.id; // Ou: return codigo.equalsIgnoreCase(that.codigo); se Código for sua chave única
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Ou Objects.hash(codigo.toLowerCase());
    }
}