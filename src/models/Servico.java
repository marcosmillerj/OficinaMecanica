/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.util.Objects;
import models.enums.SetorServico;

/**
 *
 * @author camila_barbosa
 */
public class Servico {
    public static int proximoId = 1;

    private int id;
    private String codigo;
    private String descricao;
    private BigDecimal preco;
    private SetorServico setor; // NOVO ATRIBUTO: O setor a que este serviço pertence
    private int idItemEstoquePeca; // Referência por ID para ItemEstoque (se houver peça)
    private boolean requerElevadorAlinhamento; // NOVO ATRIBUTO: Indica se este serviço requer um elevador de alinhamento

    /**
     * Construtor principal para criar um novo Serviço.
     * @param codigo O código único do serviço.
     * @param descricao A descrição detalhada do serviço.
     * @param preco O preço do serviço.
     * @param setor O setor a que este serviço pertence.
     * @param idItemEstoquePeca O ID da peça de estoque associada a este serviço (0 se não houver peça).
     * @param requerElevadorAlinhamento true se este serviço exigir um elevador de alinhamento, false caso contrário.
     */
    public Servico(String codigo, String descricao, BigDecimal preco, SetorServico setor, int idItemEstoquePeca, boolean requerElevadorAlinhamento) {
        this.id = proximoId++;
        this.codigo = Objects.requireNonNull(codigo, "Código do serviço não pode ser nulo.");
        this.descricao = Objects.requireNonNull(descricao, "Descrição do serviço não pode ser nula.");
        setPreco(preco); // Usa o setter para validação de preço
        this.setor = Objects.requireNonNull(setor, "Setor do serviço não pode ser nulo."); // Valida setor
        this.idItemEstoquePeca = idItemEstoquePeca;
        this.requerElevadorAlinhamento = requerElevadorAlinhamento; // Inicializa o novo atributo
    }

    /**
     * Construtor para Serviço sem peça associada e que não requer elevador de alinhamento (padrão).
     * @param codigo O código único do serviço.
     * @param descricao A descrição detalhada do serviço.
     * @param preco O preço do serviço.
     * @param setor O setor a que este serviço pertence.
     */
    public Servico(String codigo, String descricao, BigDecimal preco, SetorServico setor) {
        this(codigo, descricao, preco, setor, 0, false); // Chama o construtor completo com defaults
    }
    
    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * @param id ID do serviço.
     * @param codigo Código do serviço.
     * @param descricao Descrição do serviço.
     * @param preco Preço do serviço.
     * @param setor Setor do serviço.
     * @param idItemEstoquePeca ID da peça de estoque associada.
     * @param requerElevadorAlinhamento Indica se o serviço requer elevador de alinhamento.
     */
    public Servico(int id, String codigo, String descricao, BigDecimal preco, SetorServico setor, int idItemEstoquePeca, boolean requerElevadorAlinhamento) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = preco;
        this.setor = setor;
        this.idItemEstoquePeca = idItemEstoquePeca;
        this.requerElevadorAlinhamento = requerElevadorAlinhamento;
    }

    // --- Getters e Setters ---
    public int getId() { return id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = Objects.requireNonNull(codigo, "Código do serviço não pode ser nulo."); }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = Objects.requireNonNull(descricao, "Descrição do serviço não pode ser nula."); }

    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) {
        Objects.requireNonNull(preco, "Preço do serviço não pode ser nulo.");
        if (preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço do serviço não pode ser negativo.");
        }
        this.preco = preco;
    }

    public SetorServico getSetor() { return setor; } // Getter para o Setor
    public void setSetor(SetorServico setor) { this.setor = Objects.requireNonNull(setor, "Setor do serviço não pode ser nulo."); } // Setter para o Setor

    public int getIdItemEstoquePeca() { return idItemEstoquePeca; }
    public void setIdItemEstoquePeca(int idItemEstoquePeca) { this.idItemEstoquePeca = idItemEstoquePeca; }

    public boolean requerElevadorAlinhamento() { return requerElevadorAlinhamento; } // Getter para o novo atributo
    public void setRequerElevadorAlinhamento(boolean requerElevadorAlinhamento) { this.requerElevadorAlinhamento = requerElevadorAlinhamento; } // Setter para o novo atributo

    @Override
    public String toString() {
        return String.format("Serviço [ID: %d | Código: %s | Descrição: %s | Preço: R$ %.2f | Setor: %s %s %s]",
                id,
                codigo,
                descricao,
                preco,
                setor.getDescricao(), // Usando a descrição do SetorServico
                (idItemEstoquePeca != 0) ? "| Peça ID: " + idItemEstoquePeca : "",
                requerElevadorAlinhamento ? "| REQUER ALINHAMENTO" : "" // Indica se requer elevador de alinhamento
        );
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servico servico = (Servico) o;
        return id == servico.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}