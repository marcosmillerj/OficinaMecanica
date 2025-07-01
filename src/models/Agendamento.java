/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import models.enums.StatusAgendamento;

/**
 * Representa um agendamento de serviço dentro do sistema da oficina.
 * Um agendamento possui um identificador único, data e hora,
 * associação com um cliente e um veículo, um valor e um status que reflete seu ciclo de vida.
 * A classe gerencia o estado e as operações básicas de um agendamento, como reagendar,
 * cancelar e confirmar.
 *
 * @author marcos_miller
 */
public class Agendamento {
    /**
     * Campo estático que armazena o próximo ID disponível para um novo agendamento.
     * Garante que cada nova instância de Agendamento receba um ID único.
     */
    public static int proximoId = 1;

    private int id;
    private LocalDateTime dataHora;
    private int idCliente;
    private int idVeiculo;
    private BigDecimal valor;
    private StatusAgendamento status;
    
    /**
     * Construtor para criar uma nova instância de agendamento.
     * Atribui um ID automaticamente e inicializa o status como {@link StatusAgendamento#PENDENTE}.
     * Valida que a data/hora e o valor não sejam nulos e que o valor não seja negativo.
     *
     * @param dataHora Data e hora do agendamento. Não pode ser nula.
     * @param idCliente O identificador único do cliente associado a este agendamento.
     * @param idVeiculo O identificador único do veículo associado a este agendamento.
     * @param valor O valor monetário estimado ou taxa do agendamento. Não pode ser nulo ou negativo.
     * @throws NullPointerException se `dataHora` ou `valor` forem nulos.
     * @throws IllegalArgumentException se `valor` for negativo.
     */
    public Agendamento(LocalDateTime dataHora, int idCliente, int idVeiculo, BigDecimal valor){
        this.id = proximoId++;
        this.dataHora = Objects.requireNonNull(dataHora, "Data e hora do agendamento não podem ser nulas.");
        this.idCliente = idCliente;
        this.idVeiculo = idVeiculo;
        this.valor = Objects.requireNonNull(valor, "Valor do agendamento não pode ser nulo.");
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do agendamento não pode ser negativo.");
        }
        this.status = StatusAgendamento.PENDENTE;
    }

    /**
     * Construtor utilizado principalmente por bibliotecas de serialização/desserialização (como Gson)
     * para reconstruir um objeto `Agendamento` a partir de dados persistidos.
     * Permite a atribuição explícita de todos os atributos, incluindo o ID e o status.
     *
     * @param id O identificador único do agendamento.
     * @param dataHora A data e hora do agendamento.
     * @param idCliente O identificador do cliente associado.
     * @param idVeiculo O identificador do veículo associado.
     * @param valor O valor monetário do agendamento.
     * @param status O status atual do agendamento (PENDENTE, CONFIRMADO, CANCELADO, REALIZADO, FALTOU).
     */
    public Agendamento(int id, LocalDateTime dataHora, int idCliente, int idVeiculo, BigDecimal valor, StatusAgendamento status) { // Construtor para Gson
        this.id = id;
        this.dataHora = dataHora;
        this.idCliente = idCliente;
        this.idVeiculo = idVeiculo;
        this.valor = valor;
        this.status = status;
    }

    // --- Getters e Setters ---
    // Os getters e setters são autoexplicativos e não necessitam de JavaDoc adicional, conforme solicitado.
    public int getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = Objects.requireNonNull(dataHora, "Data e hora não podem ser nulas."); }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do agendamento não pode ser nulo.");
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do agendamento não pode ser negativo.");
        }
    }
    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = Objects.requireNonNull(status, "Status não pode ser nulo."); }


    /**
     * Tenta reagendar o agendamento para uma nova data e hora.
     * O reagendamento só é possível se o status atual do agendamento não for
     * {@link StatusAgendamento#CANCELADO}, {@link StatusAgendamento#REALIZADO} ou {@link StatusAgendamento#FALTOU}.
     * Após o sucesso, o status do agendamento é revertido para {@link StatusAgendamento#PENDENTE}.
     *
     * @param novaDataHora A nova data e hora para o agendamento. Não pode ser nula ou uma data/hora passada.
     * @return `true` se o agendamento foi reagendado com sucesso, `false` caso contrário.
     * @throws NullPointerException se `novaDataHora` for nula.
     */
    public boolean reagendar(LocalDateTime novaDataHora){
        Objects.requireNonNull(novaDataHora, "Nova data e hora não podem ser nulas para reagendamento.");
        if(status == StatusAgendamento.CANCELADO || status == StatusAgendamento.REALIZADO || status == StatusAgendamento.FALTOU){
            System.out.println("Não é possível reagendar um agendamento com status " + status.getDescricao() + ".");
            return false;
        }
        if (novaDataHora.isBefore(LocalDateTime.now())) {
            System.out.println("Não é possível reagendar para uma data/hora no passado.");
            return false;
        }
        this.dataHora = novaDataHora;
        this.status = StatusAgendamento.PENDENTE;
        System.out.println("Agendamento ID " + id + " reagendado para " + novaDataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ".");
        return true;
    }
    

    /**
     * Cancela o agendamento, alterando seu status para {@link StatusAgendamento#CANCELADO}.
     * Não é possível cancelar um agendamento que já foi {@link StatusAgendamento#REALIZADO}.
     * Ao cancelar, um valor retido (20% do valor original do agendamento) é calculado e retornado.
     *
     * @return O valor retido do agendamento (20% do valor total), ou {@link BigDecimal#ZERO} se o cancelamento não for possível.
     */
    public BigDecimal cancelar(){
        if(status == StatusAgendamento.REALIZADO){
            System.out.println("Não é possível cancelar um agendamento já Realizado.");
            return BigDecimal.ZERO;
        }
        this.status = StatusAgendamento.CANCELADO;
        BigDecimal valorRetido = this.valor.multiply(new BigDecimal("0.20"));
        System.out.println("Agendamento ID " + id + " cancelado. Valor retido: R$ " + String.format("%.2f", valorRetido));
        return valorRetido;
    }
    
    /**
     * Confirma o agendamento, alterando seu status para {@link StatusAgendamento#CONFIRMADO}.
     * Um agendamento não pode ser confirmado se já estiver {@link StatusAgendamento#REALIZADO} ou {@link StatusAgendamento#CANCELADO}.
     */
    public void confirmar(){
        if(status == StatusAgendamento.REALIZADO || status == StatusAgendamento.CANCELADO){
            System.out.println("Não é possível confirmar um agendamento já " + status.getDescricao() + ".");
            return;
        }
        this.status = StatusAgendamento.CONFIRMADO;
        System.out.println("Agendamento ID " + id + " confirmado.");
    }

    
    /**
     * Retorna uma representação em String do objeto Agendamento,
     * incluindo seu ID, data/hora formatada, IDs de cliente e veículo, valor e status.
     *
     * @return Uma String formatada com os detalhes do agendamento.
     */
    @Override
    public String toString() {
        return "Agendamento{" +
                "ID=" + id +
                ", Data/Hora='" + dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + '\'' +
                ", ClienteID=" + idCliente +
                ", VeiculoID=" + idVeiculo +
                ", Valor=R$ " + String.format("%.2f", valor) + '\'' +
                ", Status='" + status.getDescricao() + '\'' +
                '}';
    }

    /**
     * Compara este objeto Agendamento com o objeto especificado para verificar igualdade.
     * Dois agendamentos são considerados iguais se possuírem o mesmo ID.
     *
     * @param o O objeto a ser comparado com este agendamento.
     * @return `true` se o objeto especificado for igual a este agendamento, `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agendamento that = (Agendamento) o;
        return id == that.id;
    }

    /**
     * Retorna um valor de código hash para o objeto Agendamento.
     * O código hash é baseado exclusivamente no ID do agendamento, garantindo consistência
     * com o método `equals`.
     *
     * @return Um valor de código hash para este objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}