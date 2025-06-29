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
 *
 * @author marcos_miller
 */
public class Agendamento {
    public static int proximoId = 1;

    private int id;
    private LocalDateTime dataHora;
    private int idCliente;
    private int idVeiculo;
    private BigDecimal valor;
    private StatusAgendamento status;
    
    /**
     * Construtor para criar um novo agendamento.
     * @param dataHora Data e hora do agendamento (não pode ser nula).
     * @param idCliente ID do cliente associado ao agendamento.
     * @param idVeiculo ID do veículo associado ao agendamento.
     * @param valor O valor do agendamento (taxa, estimado, etc.).
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
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * @param id ID do agendamento.
     * @param dataHora Data e hora do agendamento.
     * @param idCliente ID do cliente.
     * @param idVeiculo ID do veículo.
     * @param valor Valor do agendamento.
     * @param status Status do agendamento.
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
    public int getId() { return id; }
    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = Objects.requireNonNull(dataHora, "Data e hora não podem ser nulas."); }
    public int getIdCliente() { return idCliente; }
    public void setIdCliente(int idCliente) { this.idCliente = idCliente; }
    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }
    public BigDecimal getValor() { return valor; } // NOVO GETTER
    public void setValor(BigDecimal valor) { // NOVO SETTER
        this.valor = Objects.requireNonNull(valor, "Valor do agendamento não pode ser nulo.");
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do agendamento não pode ser negativo.");
        }
    }
    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = Objects.requireNonNull(status, "Status não pode ser nulo."); }


    // --- Métodos de Comportamento ---
    /**
     * Reagenda o agendamento para uma nova data e hora.
     * O agendamento não pode estar CANCELADO ou REALIZADO.
     * @param novaDataHora Nova data e hora para o agendamento.
     * @return true se o agendamento foi reagendado com sucesso, false caso contrário.
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
        this.status = StatusAgendamento.PENDENTE; // Reagendamento volta para PENDENTE
        System.out.println("Agendamento ID " + id + " reagendado para " + novaDataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + ".");
        return true;
    }
    
    /**
     * Cancela o agendamento. Retém 20% do valor.
     * @return O valor retido (20% do valor total do agendamento).
     */
    public BigDecimal cancelar(){
        if(status == StatusAgendamento.REALIZADO){
            System.out.println("Não é possível cancelar um agendamento já Realizado.");
            return BigDecimal.ZERO; // Retorna zero se não puder cancelar
        }
        this.status = StatusAgendamento.CANCELADO;
        BigDecimal valorRetido = this.valor.multiply(new BigDecimal("0.20")); // 20% do valor
        System.out.println("Agendamento ID " + id + " cancelado. Valor retido: R$ " + String.format("%.2f", valorRetido));
        return valorRetido;
    }
    
    /**
     * Confirma o agendamento.
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
     * Marca o agendamento como REALIZADO.
     * @return true se o status foi atualizado para REALIZADO, false caso contrário.
     */
    public boolean marcarComoRealizado() {
        if (status == StatusAgendamento.REALIZADO || status == StatusAgendamento.CANCELADO || status == StatusAgendamento.FALTOU) {
            System.out.println("Não é possível marcar como realizado um agendamento com status " + status.getDescricao() + ".");
            return false;
        }
        this.status = StatusAgendamento.REALIZADO;
        System.out.println("Agendamento ID " + id + " marcado como REALIZADO.");
        return true;
    }

    /**
     * Marca o agendamento como FALTOU (cliente não compareceu).
     * @return true se o status foi atualizado para FALTOU, false caso contrário.
     */
    public boolean marcarComoFaltou() {
        if (status == StatusAgendamento.REALIZADO || status == StatusAgendamento.CANCELADO) {
            System.out.println("Não é possível marcar como faltou um agendamento com status " + status.getDescricao() + ".");
            return false;
        }
        this.status = StatusAgendamento.FALTOU;
        System.out.println("Agendamento ID " + id + " marcado como FALTOU.");
        return true;
    }
    
    /**
     * Verifica se o agendamento está vencido (data/hora no passado e status PENDENTE ou CONFIRMADO).
     * @return true se o agendamento está vencido, false caso contrário.
     */
    public boolean estaVencido(){
        return LocalDateTime.now().isAfter(this.dataHora) &&
                (status == StatusAgendamento.PENDENTE || status == StatusAgendamento.CONFIRMADO);
    }
    
    @Override
    public String toString() {
        return "Agendamento{" +
               "ID=" + id +
               ", Data/Hora='" + dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + '\'' +
               ", ClienteID=" + idCliente +
               ", VeiculoID=" + idVeiculo +
               ", Valor=R$ " + String.format("%.2f", valor) + '\'' + // Formata o valor
               ", Status='" + status.getDescricao() + '\'' +
               '}';
    }

    // Adição de equals e hashCode para garantir que Agendamento possa ser comparado por ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agendamento that = (Agendamento) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
