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
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) {
        this.valor = Objects.requireNonNull(valor, "Valor do agendamento não pode ser nulo.");
        if (valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor do agendamento não pode ser negativo.");
        }
    }
    public StatusAgendamento getStatus() { return status; }
    public void setStatus(StatusAgendamento status) { this.status = Objects.requireNonNull(status, "Status não pode ser nulo."); }


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
    
    public void confirmar(){
        if(status == StatusAgendamento.REALIZADO || status == StatusAgendamento.CANCELADO){
            System.out.println("Não é possível confirmar um agendamento já " + status.getDescricao() + ".");
            return;
        }
        this.status = StatusAgendamento.CONFIRMADO;
        System.out.println("Agendamento ID " + id + " confirmado.");
    }

    
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
