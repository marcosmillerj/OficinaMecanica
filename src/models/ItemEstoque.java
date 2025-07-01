/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Representa um **item no estoque** da oficina, como peças, suprimentos ou produtos.
 * Cada item possui um identificador único, um código, um nome, a quantidade disponível
 * e seu preço unitário. A classe garante a integridade dos dados, como quantidades e preços não negativos.
 *
 * @author marcos_miller
 */
public class ItemEstoque {
    /**
     * Contador estático que gera **IDs únicos** para cada nova instância de ItemEstoque.
     * Garante que cada item receba um identificador exclusivo ao ser criado.
     */
    public static int proximoId = 1;

    private int id;
    private String codigo;
    private String nome;
    private int quantidade;
    private BigDecimal precoUnitario;

    /**
     * Construtor para criar uma nova instância de **ItemEstoque**.
     * Atribui um ID único automaticamente e realiza validações para garantir
     * que os dados essenciais não sejam nulos ou negativos.
     *
     * @param codigo O código identificador exclusivo da peça ou item. Não pode ser nulo.
     * @param nome O nome descritivo do item. Não pode ser nulo.
     * @param quantidade A quantidade atual do item disponível em estoque. Não pode ser negativa.
     * @param precoUnitario O preço de venda unitário do item. Não pode ser nulo ou negativo.
     * @throws NullPointerException se `codigo`, `nome` ou `precoUnitario` forem nulos.
     * @throws IllegalArgumentException se `quantidade` ou `precoUnitario` forem negativos.
     */
    public ItemEstoque(String codigo, String nome, int quantidade, BigDecimal precoUnitario) {
        this.id = proximoId++;
        this.codigo = Objects.requireNonNull(codigo, "Código do item não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome do item não pode ser nulo.");
        setQuantidade(quantidade); // Usa o setter para aplicar validação
        setPrecoUnitario(precoUnitario); // Usa o setter para aplicar validação
    }

    /**
     * Construtor utilizado por bibliotecas de desserialização (e.g., Gson)
     * para reconstruir um objeto `ItemEstoque` a partir de dados persistidos.
     * Permite a atribuição explícita de todos os atributos, incluindo o ID,
     * e aplica as mesmas validações dos setters para garantir a consistência dos dados.
     *
     * @param id O identificador único do item.
     * @param codigo O código identificador da peça.
     * @param nome O nome do item.
     * @param quantidade A quantidade em estoque.
     * @param precoUnitario O preço unitário do item.
     * @throws NullPointerException se `codigo`, `nome` ou `precoUnitario` forem nulos.
     * @throws IllegalArgumentException se `quantidade` ou `precoUnitario` forem negativos.
     */
    public ItemEstoque(int id, String codigo, String nome, int quantidade, BigDecimal precoUnitario) {
        this.id = id;
        this.codigo = Objects.requireNonNull(codigo, "Código do item não pode ser nulo.");
        this.nome = Objects.requireNonNull(nome, "Nome do item não pode ser nulo.");
        setQuantidade(quantidade); // Usa o setter para aplicar validação
        setPrecoUnitario(precoUnitario); // Usa o setter para aplicar validação
    }

    // --- Getters e Setters ---
    public int getId() { return id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = Objects.requireNonNull(codigo, "Código do item não pode ser nulo."); }

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

    /**
     * Retorna uma representação em String formatada do objeto ItemEstoque,
     * incluindo seu ID, código, nome, quantidade e preço unitário.
     *
     * @return Uma String formatada com os detalhes do item de estoque.
     */
    @Override
    public String toString() {
        return String.format("ItemEstoque {ID: %d | Código: %s | Nome: %s | Quantidade: %d | Preço Unitário: R$ %.2f}",
                id, codigo, nome, quantidade, precoUnitario);
    }

    /**
     * Compara este objeto ItemEstoque com o objeto especificado para verificar igualdade.
     * Dois itens de estoque são considerados iguais se possuírem o **mesmo ID**.
     *
     * @param o O objeto a ser comparado com este item de estoque.
     * @return `true` se o objeto especificado for igual a este item de estoque, `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemEstoque that = (ItemEstoque) o;
        return id == that.id;
    }

    /**
     * Retorna um valor de código hash para o objeto ItemEstoque.
     * O código hash é baseado exclusivamente no **ID do item**, garantindo consistência
     * com o método `equals` (contrato `hashCode()/equals()`).
     *
     * @return Um valor de código hash para este objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}