/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 * Define os serviços que são realizados na oficina. 
 * @author camila_barbosa
 */
public class Servico {

    private String codigo;
    private String descricao;
    private Double preco;
    private String categoria;
    private ItemEstoque peca; // Associação com ItemEstoque

    public Servico(String codigo, String descricao, Double preco, String categoria, ItemEstoque peca) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = preco;
        this.categoria = categoria;
        this.peca = peca;
    }

    // Getters e Setters
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void atualizarDescricao(String novaDescricao) {
        this.descricao = novaDescricao;
    }

    public Double getPreco() {
        return preco;
    }

    public void setPreco(Double preco) {
        this.preco = preco;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public ItemEstoque getPeca() {
        return peca;
    }

    public void setPeca(ItemEstoque peca) {
        this.peca = peca;
    }

    @Override
    public String toString() {
        return String.format("%s: %s - R$ %.2f %s",
                codigo,
                descricao,
                preco,
                peca != null ? "| Usa: " + peca.getNome() + " (ID:" + peca.getId() + ")" : ""
        );
    }
}
