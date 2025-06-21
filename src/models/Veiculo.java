/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package models;

import models.enums.StatusOrdem;

/**
 *Classe que representa veículo
 * @author camila_barbosa
 */
public class Veiculo {
    private String placa;
    private String modelo;
    private String cor;
    private Cliente cliente;
    private StatusOrdem status;
    
    //construtor 
    public Veiculo (String placa, String modelo, String cor, Cliente cliente, StatusOrdem status){
       this.placa=placa;
       this.modelo=modelo;
       this.cor=cor;
       this.cliente=cliente;
       this.status=status;
    }
    
    //getters e setters
    public String getPlaca(){
        return placa;
    }
    public void setPlaca(String placa){
        this.placa=placa;
    }
    
    public String getModelo(){
        return modelo;
    }
    public void setModelo(String modelo){
        this.modelo=modelo;
    }
    public String getCor(){
        return cor;
    }
    public void setCor(String cor){
        this.cor=cor;
    }
    public Cliente getCliente(){
        return cliente;
    }
    public void setCliente(Cliente cliente){
        this.cliente=cliente;
    }
    public StatusOrdem getStatus(){
        return status;
    }
    public void setStatus(StatusOrdem status){
        this.status=status;
    }
    
    //métodos
    /**
     * Verifica o status do veículo
     * @return 
     */
    public StatusOrdem verificarStatus(){
        return this.status;
    }
    
    @Override
    public String toString() {
    return "Veiculo {" +
           "placa='" + placa + '\'' +
           ", modelo='" + modelo + '\'' +
           ", cor='" + cor + '\'' +
           ", cliente=" + cliente +
           ", status=" + status +
           '}';
}

}
