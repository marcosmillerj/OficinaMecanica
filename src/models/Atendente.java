/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;


/**
 *
 * @author marcos_miller
 */

//classe extends de Usuario
public class Atendente extends Usuario { 
    
    //construtor referenciando a classe pai com SUPER
    public Atendente(String nome, String cpf, String endereco, String email, String telefone, String senha){
        super(nome, cpf, endereco, email, telefone, senha);
    }
    
    //métodos principais da nossa classe
    
    //realizarAgendamento
    public String realizarAgendamento(Cliente cliente, Veiculo veiculo, String dataHora){
        // fazer uma validação de tipos no futuro
        
        //Agendamento agendamento = new Agendamento(cliente, veiculo, dataHora)
        
        return "Agendamento Criado:\n" +
            "Cliente: " + cliente.getNome() + "\n" +
            "Veículo: " + veiculo.getModelo() + "," + veiculo.getPlaca() + "\n" +
            "Data: " + dataHora;
        };
    
    
    //processarOrdemServico
    
    public String processarOrdemServico(){
        return null;
        //comentario pra teste do gitigore, PODE EXCLUIR ELE
    };
    
}
    

