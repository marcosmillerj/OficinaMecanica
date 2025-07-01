/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package models;

import java.util.Objects;
import models.enums.StatusOrdem; // A importação de StatusOrdem não é utilizada nesta classe, mas será mantida como está no original.

/**
 * Representa um **veículo** na oficina, contendo suas informações básicas
 * e uma associação com o cliente proprietário.
 * Cada veículo possui um ID único, placa, modelo, cor e o identificador do seu dono.
 *
 * @author camila_barbosa
 */
public class Veiculo {
    /**
     * Contador estático que gera **IDs únicos** para cada nova instância de Veiculo.
     * Garante que cada veículo receba um identificador exclusivo ao ser criado.
     */
    public static int proximoId = 1; 

    private int id;
    private String placa;
    private String modelo;
    private String cor;
    private int idCliente;

    /**
     * Construtor para criar uma nova instância de **Veiculo**.
     * Atribui um ID único automaticamente e inicializa os dados essenciais do veículo.
     * Valida que os parâmetros de string (placa, modelo, cor) não sejam nulos.
     *
     * @param placa A placa do veículo. Não pode ser nula.
     * @param modelo O modelo do veículo (ex: "Fiat Palio", "VW Gol"). Não pode ser nulo.
     * @param cor A cor predominante do veículo. Não pode ser nula.
     * @param idCliente O identificador único do cliente proprietário deste veículo.
     * @throws NullPointerException se `placa`, `modelo` ou `cor` forem nulos.
     */
    public Veiculo (String placa, String modelo, String cor, int idCliente){ 
        this.id = proximoId++;
        this.placa = Objects.requireNonNull(placa, "Placa não pode ser nula.");
        this.modelo = Objects.requireNonNull(modelo, "Modelo não pode ser nulo.");
        this.cor = Objects.requireNonNull(cor, "Cor não pode ser nula.");
        this.idCliente = idCliente;
    }

    /**
     * Construtor utilizado por bibliotecas de desserialização (e.g., Gson)
     * para reconstruir um objeto `Veiculo` a partir de dados persistidos.
     * Permite a atribuição explícita de todos os atributos, incluindo o ID.
     *
     * @param id O identificador único do veículo.
     * @param placa A placa do veículo.
     * @param modelo O modelo do veículo.
     * @param cor A cor do veículo.
     * @param idCliente O identificador do cliente proprietário.
     * @throws NullPointerException se `placa`, `modelo` ou `cor` forem nulos.
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
    
    /**
     * Retorna uma representação em String do objeto Veiculo,
     * incluindo seu ID, placa, modelo, cor e o ID do cliente proprietário.
     *
     * @return Uma String formatada com os detalhes do veículo.
     */
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

    /**
     * Compara este objeto Veiculo com o objeto especificado para verificar igualdade.
     * Dois veículos são considerados iguais se possuírem o **mesmo ID**.
     *
     * @param o O objeto a ser comparado com este veículo.
     * @return `true` se o objeto especificado for igual a este veículo, `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Veiculo veiculo = (Veiculo) o;
        return id == veiculo.id;
    }

    /**
     * Retorna um valor de código hash para o objeto Veiculo.
     * O código hash é baseado exclusivamente no **ID do veículo**, garantindo consistência
     * com o método `equals` (contrato `hashCode()/equals()`).
     *
     * @return Um valor de código hash para este objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}