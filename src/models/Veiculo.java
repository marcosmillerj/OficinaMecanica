/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package models;

import java.util.Objects;
import models.enums.StatusOrdem;

/**
 *Classe que representa veículo
 * @author camila_barbosa
 */
public class Veiculo {
    public static int proximoId = 1; 

    private int id;
    private String placa;
    private String modelo;
    private String cor;
    private int idCliente;

    /**
     * Construtor para criar um novo veículo.
     * @param placa A placa do veículo (não pode ser nula).
     * @param modelo O modelo do veículo (não pode ser nulo).
     * @param cor A cor do veículo (não pode ser nula).
     * @param idCliente O ID do cliente proprietário do veículo.
     */
    public Veiculo (String placa, String modelo, String cor, int idCliente){ 
        this.id = proximoId++;
        this.placa = Objects.requireNonNull(placa, "Placa não pode ser nula.");
        this.modelo = Objects.requireNonNull(modelo, "Modelo não pode ser nulo.");
        this.cor = Objects.requireNonNull(cor, "Cor não pode ser nula.");
        this.idCliente = idCliente;
    }

    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * @param id ID do veículo.
     * @param placa Placa do veículo.
     * @param modelo Modelo do veículo.
     * @param cor Cor do veículo.
     * @param idCliente ID do cliente proprietário.
     */
    public Veiculo(int id, String placa, String modelo, String cor, int idCliente) { 
        this.id = id;
        this.placa = Objects.requireNonNull(placa, "Placa não pode ser nula.");
        this.modelo = Objects.requireNonNull(modelo, "Modelo não pode ser nulo.");
        this.cor = Objects.requireNonNull(cor, "Cor não pode ser nula.");
        this.idCliente = idCliente;
    }

    // --- Getters e Setters ---
    public int getId() {
        return id;
    }
    
    public String getPlaca(){
        return placa;
    }
    public void setPlaca(String placa){
        this.placa = Objects.requireNonNull(placa, "Placa não pode ser nula.");
    }
    
    public String getModelo(){
        return modelo;
    }
    public void setModelo(String modelo){
        this.modelo = Objects.requireNonNull(modelo, "Modelo não pode ser nulo.");
    }
    public String getCor(){
        return cor;
    }
    public void setCor(String cor){
        this.cor = Objects.requireNonNull(cor, "Cor não pode ser nula.");
    }
    
    public int getIdCliente(){
        return idCliente;
    }
    
    public void setIdCliente(int idCliente){
        this.idCliente = idCliente;
    }
    
    @Override
    public String toString() {
        return "Veiculo {" +
                "ID=" + id +
                ", placa='" + placa + '\'' +
                ", modelo='" + modelo + '\'' +
                ", cor='" + cor + '\'' +
                ", clienteID=" + idCliente + 
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return id == veiculo.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}