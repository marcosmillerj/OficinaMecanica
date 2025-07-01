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
    private BigDecimal precoMaoDeObra;
    private SetorServico setor;
    private String codigoPeca;
    private int quantidadePeca;
    private boolean requerPrioridade;
    private String observacoes;

    /**
     * Construtor principal para criar uma instância de Serviço para uma OS.
     * @param precoMaoDeObra O preço da mão de obra para este serviço.
     * @param setor O setor a que este serviço pertence.
     * @param codigoPeca O código da peça de estoque associada (nulo/vazio se não houver).
     * @param quantidadePeca A quantidade da peça usada (0 se não houver peça).
     * @param requerPrioridade Indica se este serviço requer prioridade de elevador (alinhamento).
     * @param observacoes Observações/descrição detalhada para este serviço.
     */
    public Servico(BigDecimal precoMaoDeObra, SetorServico setor, String codigoPeca,
                   int quantidadePeca, boolean requerPrioridade, String observacoes) {
        this.id = proximoId++;
        setPrecoMaoDeObra(precoMaoDeObra);
        this.setor = Objects.requireNonNull(setor, "Setor do serviço não pode ser nulo.");
        this.codigoPeca = (codigoPeca != null && !codigoPeca.isEmpty()) ? codigoPeca : null;
        this.quantidadePeca = quantidadePeca;
        this.requerPrioridade = requerPrioridade;
        this.observacoes = (observacoes != null && !observacoes.isEmpty()) ? observacoes : null;
    }

    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * @param id ID da instância do serviço.
     * @param precoMaoDeObra Preço da mão de obra.
     * @param setor Setor do serviço.
     * @param codigoPeca Código da peça de estoque associada.
     * @param quantidadePeca Quantidade da peça usada.
     * @param requerPrioridade Indica se o serviço requer prioridade de elevador.
     * @param observacoes Observações específicas.
     */
    public Servico(int id, BigDecimal precoMaoDeObra, SetorServico setor, String codigoPeca,
                   int quantidadePeca, boolean requerPrioridade, String observacoes) {
        this.id = id;
        this.precoMaoDeObra = precoMaoDeObra;
        this.setor = setor;
        this.codigoPeca = codigoPeca;
        this.quantidadePeca = quantidadePeca;
        this.requerPrioridade = requerPrioridade;
        this.observacoes = observacoes;
    }

    // --- Getters e Setters ---
    public int getId() { return id; }

    public BigDecimal getPrecoMaoDeObra() { return precoMaoDeObra; }
    public void setPrecoMaoDeObra(BigDecimal precoMaoDeObra) {
        Objects.requireNonNull(precoMaoDeObra, "Preço de mão de obra não pode ser nulo.");
        if (precoMaoDeObra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço de mão de obra não pode ser negativo.");
        }
        this.precoMaoDeObra = precoMaoDeObra;
    }

    public SetorServico getSetor() { return setor; }
    public void setSetor(SetorServico setor) { this.setor = Objects.requireNonNull(setor, "Setor do serviço não pode ser nulo."); }

    public String getCodigoPeca() { return codigoPeca; }
    public void setCodigoPeca(String codigoPeca) { this.codigoPeca = (codigoPeca != null && !codigoPeca.isEmpty()) ? codigoPeca : null; }

    public int getQuantidadePeca() { return quantidadePeca; }
    public void setQuantidadePeca(int quantidadePeca) { this.quantidadePeca = quantidadePeca; }

    public boolean requerPrioridade() { return requerPrioridade; }
    public void setRequerPrioridade(boolean requerPrioridade) { this.requerPrioridade = requerPrioridade; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = (observacoes != null && !observacoes.isEmpty()) ? observacoes : null; }

    public BigDecimal getPreco() { return precoMaoDeObra; }

    @Override
    public String toString() {
        String pecaInfo = (codigoPeca != null) ? "| Peça: " + codigoPeca + " (Qtd: " + quantidadePeca + ")" : "";
        String prioridadeInfo = requerPrioridade ? "| REQUER PRIORIDADE" : "";
        String obsInfo = (observacoes != null) ? " | Obs: " + observacoes : "";

        return String.format("Serviço [ID:%d | Descrição: %s | M.O.: R$ %.2f | Setor: %s %s%s]",
                id,
                (observacoes != null ? observacoes : "Sem descrição"),
                precoMaoDeObra,
                setor.getDescricao(),
                pecaInfo,
                prioridadeInfo,
                obsInfo
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