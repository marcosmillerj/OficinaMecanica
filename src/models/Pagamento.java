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
 * Representa um **registro de pagamento** efetuado no sistema da oficina.
 * Cada pagamento possui um ID único, data e hora da transação, o valor pago,
 * o tipo de pagamento e uma associação opcional com uma Ordem de Serviço.
 *
 * @author marcos_miller
 */

public class Pagamento {

    /**
     * Contador estático que gera **IDs únicos** para cada nova instância de Pagamento.
     * Garante que cada pagamento receba um identificador exclusivo ao ser criado.
     */
    public static int proximoId = 1;

    private int id;
    private LocalDateTime dataHora;
    private BigDecimal valor;
    private TipoPagamento tipo;
    private Optional<Integer> idOrdemServico;
    
    /**
     * Construtor principal para criar uma nova instância de **Pagamento**.
     * Atribui um ID único automaticamente e inicializa os campos `dataHora`, `valor`
     * e `tipo` como nulos/zero, esperando que sejam definidos posteriormente
     * através do método `finalizar`.
     *
     * @param idOrdemServico Um `Optional<Integer>` representando o ID da Ordem de Serviço
     * à qual este pagamento se refere. Use `Optional.empty()` se
     * o pagamento não estiver vinculado a uma OS específica. Não pode ser nulo.
     * @throws NullPointerException se `idOrdemServico` for nulo.
     */
    public Pagamento(Optional<Integer> idOrdemServico) {
        this.id = proximoId++;
        this.idOrdemServico = Objects.requireNonNull(idOrdemServico, "ID da Ordem de Serviço não pode ser nulo (use Optional.empty()).");
        this.dataHora = null;
        this.valor = BigDecimal.ZERO;
        this.tipo = null;
    }

    /**
     * Construtor utilizado por bibliotecas de desserialização (e.g., Gson)
     * para reconstruir um objeto `Pagamento` a partir de dados persistidos.
     * Permite a atribuição explícita de todos os atributos, incluindo o ID,
     * e garante que `idOrdemServico` seja sempre um `Optional` válido (nunca `null`).
     *
     * @param id O identificador único do pagamento.
     * @param dataHora A data e hora em que o pagamento foi finalizado.
     * @param valor O valor total do pagamento.
     * @param tipo O {@link TipoPagamento} utilizado para a transação.
     * @param idOrdemServico Um `Optional<Integer>` contendo o ID da Ordem de Serviço
     * associada, ou `Optional.empty()` se não houver.
     */
    public Pagamento(int id, LocalDateTime dataHora, BigDecimal valor, TipoPagamento tipo, Optional<Integer> idOrdemServico) {
        this.id = id;
        this.dataHora = dataHora;
        this.valor = valor;
        this.tipo = tipo;
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

    public Optional<Integer> getIdOrdemServico() { 
        return Objects.requireNonNullElse(idOrdemServico, Optional.empty());
    }
    public void setIdOrdemServico(Optional<Integer> idOrdemServico) {
        this.idOrdemServico = Objects.requireNonNull(idOrdemServico, "ID da Ordem de Serviço não pode ser nulo (use Optional.empty()).");
    }

    /**
     * Finaliza o processo de pagamento, registrando o valor final, a data/hora atual
     * e o tipo de pagamento realizado.
     *
     * @param valorFinal O valor total final pago.
     * @param tipoFinal O {@link TipoPagamento} selecionado para esta transação.
     * @return Uma mensagem de confirmação formatada contendo os detalhes do pagamento finalizado.
     * @throws NullPointerException se `valorFinal` ou `tipoFinal` forem nulos.
     * @throws IllegalArgumentException se `valorFinal` for negativo.
     */
    public String finalizar(BigDecimal valorFinal, TipoPagamento tipoFinal) {
        this.setValor(valorFinal); // Utiliza o setter para aplicar validações
        this.dataHora = LocalDateTime.now(); // Define a data/hora atual da finalização
        this.setTipo(tipoFinal); // Utiliza o setter para aplicar validações

        String mensagem = "Pagamento de R$" + String.format("%.2f", valorFinal) +
                          " finalizado via " + tipoFinal.getPagamento() +
                          " em " + this.dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) +
                          ". ID: " + this.id +
                          (this.getIdOrdemServico().isPresent() ? ", OS: " + this.getIdOrdemServico().get() : "");

        System.out.println(mensagem);
        return mensagem;
    }

    /**
     * Compara este objeto Pagamento com o objeto especificado para verificar igualdade.
     * Dois pagamentos são considerados iguais se possuírem o **mesmo ID**.
     *
     * @param o O objeto a ser comparado com este pagamento.
     * @return `true` se o objeto especificado for igual a este pagamento, `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pagamento pagamento = (Pagamento) o;
        return id == pagamento.id;
    }

    /**
     * Retorna um valor de código hash para o objeto Pagamento.
     * O código hash é baseado exclusivamente no **ID do pagamento**, garantindo consistência
     * com o método `equals` (contrato `hashCode()/equals()`).
     *
     * @return Um valor de código hash para este objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}