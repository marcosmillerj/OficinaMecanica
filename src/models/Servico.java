/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.util.Objects;
import models.enums.SetorServico;

/**
 * Representa um **serviço** que pode ser realizado na oficina,
 * como um reparo ou manutenção. Cada serviço tem um ID único,
 * um custo de mão de obra, um setor de atuação, pode envolver
 * o uso de uma peça específica e ter observações adicionais.
 * Também indica se o serviço requer prioridade (como um elevador de alinhamento).
 *
 * @author camila_barbosa
 */
public class Servico {
    /**
     * Contador estático que gera **IDs únicos** para cada nova instância de Serviço.
     * Garante que cada serviço receba um identificador exclusivo ao ser criado.
     */
    public static int proximoId = 1;

    private int id;
    private BigDecimal precoMaoDeObra;
    private SetorServico setor;
    private String codigoPeca;
    private int quantidadePeca;
    private boolean requerPrioridade;
    private String observacoes;

    /**
     * Construtor principal para criar uma nova instância de **Serviço**.
     * Atribui um ID único automaticamente e realiza validações para garantir
     * que os dados essenciais não sejam nulos ou negativos.
     *
     * @param precoMaoDeObra O preço da mão de obra para a execução deste serviço. Não pode ser nulo ou negativo.
     * @param setor O {@link SetorServico} ao qual este serviço pertence. Não pode ser nulo.
     * @param codigoPeca O código do item de estoque (peça) associado a este serviço. Pode ser nulo ou vazio se não houver peça.
     * @param quantidadePeca A quantidade da peça utilizada para este serviço. Será 0 se não houver peça.
     * @param requerPrioridade Indica se este serviço possui um requisito de prioridade para recursos (e.g., elevador de alinhamento).
     * @param observacoes Uma descrição detalhada ou notas específicas para este serviço. Pode ser nulo ou vazio.
     * @throws NullPointerException se `precoMaoDeObra` ou `setor` forem nulos.
     * @throws IllegalArgumentException se `precoMaoDeObra` for negativo.
     */
    public Servico(BigDecimal precoMaoDeObra, SetorServico setor, String codigoPeca,
                   int quantidadePeca, boolean requerPrioridade, String observacoes) {
        this.id = proximoId++;
        setPrecoMaoDeObra(precoMaoDeObra); // Usa o setter para aplicar validação
        this.setor = Objects.requireNonNull(setor, "Setor do serviço não pode ser nulo.");
        this.codigoPeca = (codigoPeca != null && !codigoPeca.isEmpty()) ? codigoPeca : null;
        this.quantidadePeca = quantidadePeca;
        this.requerPrioridade = requerPrioridade;
        this.observacoes = (observacoes != null && !observacoes.isEmpty()) ? observacoes : null;
    }

    /**
     * Construtor utilizado por bibliotecas de desserialização (e.g., Gson)
     * para reconstruir um objeto `Servico` a partir de dados persistidos.
     * Permite a atribuição explícita de todos os atributos, incluindo o ID.
     *
     * @param id O identificador único da instância do serviço.
     * @param precoMaoDeObra O preço da mão de obra para este serviço.
     * @param setor O setor ao qual este serviço pertence.
     * @param codigoPeca O código da peça de estoque associada (pode ser nulo).
     * @param quantidadePeca A quantidade da peça utilizada.
     * @param requerPrioridade Indica se este serviço requer prioridade.
     * @param observacoes Observações específicas para este serviço (pode ser nulo).
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

    /**
     * Retorna o preço de mão de obra deste serviço.
     * Este método é um alias para `getPrecoMaoDeObra()` e serve para clareza
     * em contextos onde "preço" se refere especificamente à mão de obra.
     *
     * @return O preço de mão de obra do serviço em `BigDecimal`.
     */
    public BigDecimal getPreco() { return precoMaoDeObra; }

    /**
     * Retorna uma representação em String formatada do objeto Serviço,
     * incluindo seu ID, descrição (observações), preço de mão de obra, setor,
     * informações da peça (se aplicável) e se requer prioridade.
     *
     * @return Uma String formatada com os detalhes do serviço.
     */
    @Override
    public String toString() {
        String pecaInfo = (codigoPeca != null) ? "| Peça: " + codigoPeca + " (Qtd: " + quantidadePeca + ")" : "";
        String prioridadeInfo = requerPrioridade ? "| REQUER PRIORIDADE" : "";
        String obsInfo = (observacoes != null) ? " | Obs: " + observacoes : ""; // Esta linha está duplicada na sua versão original. Manterei apenas uma.

        return String.format("Serviço [ID:%d | Descrição: %s | M.O.: R$ %.2f | Setor: %s %s%s]",
                id,
                (observacoes != null ? observacoes : "Sem descrição"),
                precoMaoDeObra,
                setor.getDescricao(),
                pecaInfo,
                prioridadeInfo,
                obsInfo // Removi o obsInfo que estava solto, usei apenas este aqui dentro da String.format
        );
    }
    
    /**
     * Compara este objeto Serviço com o objeto especificado para verificar igualdade.
     * Dois serviços são considerados iguais se possuírem o **mesmo ID**.
     *
     * @param o O objeto a ser comparado com este serviço.
     * @return `true` se o objeto especificado for igual a este serviço, `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Servico servico = (Servico) o;
        return id == servico.id;
    }

    /**
     * Retorna um valor de código hash para o objeto Serviço.
     * O código hash é baseado exclusivamente no **ID do serviço**, garantindo consistência
     * com o método `equals` (contrato `hashCode()/equals()`).
     *
     * @return Um valor de código hash para este objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}