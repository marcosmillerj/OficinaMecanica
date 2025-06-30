/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import models.enums.TipoPagamento;
import observers.ObservadorPagamento;

/**
 *
 * @author marcos_miller
 */

/**
 * Representa um pagamento realizado na oficina.
 * Agora atua como um 'Subject' no padrão Observer, notificando interessados ao ser finalizado.
 */
public class Pagamento {

    public static int proximoId = 1;

    private int id;
    private LocalDateTime dataHora;
    private BigDecimal valor;
    private TipoPagamento tipo;
    private Optional<Integer> idOrdemServico; // Referência por ID para OrdemServico (Optional)
    
    /**
     * Construtor principal para criar um novo pagamento.
     * @param idOrdemServico O ID da OrdemDeServiço à qual este pagamento se refere (Optional.empty() se não houver OS).
     */
    public Pagamento(Optional<Integer> idOrdemServico) {
        this.id = proximoId++;
        this.idOrdemServico = Objects.requireNonNull(idOrdemServico, "ID da Ordem de Serviço não pode ser nulo (use Optional.empty()).");
        this.dataHora = null; // Definido ao finalizar
        this.valor = BigDecimal.ZERO; // Definido ao finalizar
        this.tipo = null; // Definido ao finalizar
    }

    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * Garante que `idOrdemServico` sempre seja um `Optional` (nunca `null`).
     * @param id ID do pagamento.
     * @param dataHora Data e hora da finalização do pagamento.
     * @param valor Valor final do pagamento.
     * @param tipo Tipo de pagamento.
     * @param idOrdemServico ID da Ordem de Serviço associada.
     */
    public Pagamento(int id, LocalDateTime dataHora, BigDecimal valor, TipoPagamento tipo, Optional<Integer> idOrdemServico) {
        this.id = id;
        this.dataHora = dataHora;
        this.valor = valor;
        this.tipo = tipo;
        // <<< CORREÇÃO AQUI: Garante que idOrdemServico não seja null, mesmo que o Gson passe null
        this.idOrdemServico = Objects.requireNonNullElse(idOrdemServico, Optional.empty()); 
    }


    // --- Getters e Setters ---
    public int getId() { return id; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) {
        this.valor = Objects.requireNonNull(valor, "Valor não pode ser nulo.");
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor não pode ser negativo.");
        }
    }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = Objects.requireNonNull(dataHora, "Data e hora não podem ser nulas."); }

    public TipoPagamento getTipo() { return tipo; }
    public void setTipo(TipoPagamento tipo) { this.tipo = Objects.requireNonNull(tipo, "Tipo de pagamento não pode ser nulo."); }

    /**
     * Retorna o Optional<Integer> do ID da Ordem de Serviço.
     * GARANTE QUE NUNCA RETORNE NULL, SEMPRE UM OPTIONAL (VAZIO OU COM VALOR).
     * @return Optional<Integer> do ID da OS, ou Optional.empty() se não houver OS.
     */
    public Optional<Integer> getIdOrdemServico() { 
        return Objects.requireNonNullElse(idOrdemServico, Optional.empty()); // <<< CORREÇÃO AQUI!
    }
    public void setIdOrdemServico(Optional<Integer> idOrdemServico) {
        this.idOrdemServico = Objects.requireNonNull(idOrdemServico, "ID da Ordem de Serviço não pode ser nulo (use Optional.empty()).");
    }

    /**
     * Finaliza o pagamento, definindo seu valor, data/hora e tipo.
     * @param valorFinal O valor final pago.
     * @param tipoFinal O tipo de pagamento.
     * @return Uma mensagem de confirmação da finalização.
     */
    public String finalizar(BigDecimal valorFinal, TipoPagamento tipoFinal) {
        this.setValor(valorFinal);
        this.dataHora = LocalDateTime.now();
        this.setTipo(tipoFinal);

        String mensagem = "Pagamento de R$" + String.format("%.2f", valorFinal) +
                          " finalizado via " + tipoFinal.getPagamento() +
                          " em " + this.dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                          ". ID: " + this.id +
                          (this.getIdOrdemServico().isPresent() ? ", OS: " + this.getIdOrdemServico().get() : ""); // Usa o getter defensivo

        System.out.println(mensagem);
        return mensagem;
    }

    /**
     * Exibe os detalhes completos do pagamento.
     */
    public void exibirDetalhes() {
        System.out.println("--- Detalhes do Pagamento ---");
        System.out.println("ID: " + this.id);
        System.out.println("Valor: R$" + String.format("%.2f", this.valor));
        System.out.println("Data/Hora: " + (this.dataHora != null ? this.dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) : "N/A"));
        System.out.println("Forma: " + (this.tipo != null ? this.tipo.getPagamento() : "N/A"));
        System.out.println("OS Relacionada: " + (this.getIdOrdemServico().isPresent() ? this.getIdOrdemServico().get() : "N/A")); // Usa o getter defensivo
        System.out.println("-----------------------------");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return id == pagamento.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}