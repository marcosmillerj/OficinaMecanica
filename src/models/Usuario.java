/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package models;

import java.time.LocalDateTime;
import models.enums.TipoUsuario;

/**
 * Superclasse que serve como base para os usuários do sistema
 * @author barbo
 */
public class Usuario {
    public static int proximoId = 1;

    private int id;
    private String nome;
    private String cpf;
    private String endereco;
    private String email;
    private String telefone;
    private TipoUsuario tipo; 
    private String senha;

    /**
     * Construtor para criar uma nova instância de Usuário.
     * Atribui um ID único e inicializa os dados básicos do usuário.
     * @param nome O nome completo do usuário.
     * @param cpf O CPF do usuário (será validado no setter).
     * @param endereco O endereço completo do usuário.
     * @param email O endereço de email do usuário.
     * @param telefone O número de telefone do usuário.
     * @param tipo O tipo (perfil) do usuário (Gerente, Atendente, Mecanico).
     * @param senha A senha do usuário (não armazenar em texto puro em produção).
     */
    public Usuario(String nome, String cpf, String endereco, String email, String telefone, TipoUsuario tipo, String senha) {
        this.id = proximoId++; 
        this.nome = nome;
        setCpf(cpf);
        this.endereco = endereco;
        this.email = email;
        this.telefone = telefone;
        this.tipo = tipo; 
        this.senha = senha;
    }

    // --- Métodos Getters ---

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }
    
    public TipoUsuario getTipo(){ 
        return tipo;
    }
    

    // --- Métodos Setters ---

    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Define o CPF do usuário, aplicando validação para garantir que tenha 11 dígitos numéricos.
     * @param cpf O CPF a ser definido.
     * @throws IllegalArgumentException Se o CPF for nulo ou não contiver exatamente 11 dígitos.
     */
    public void setCpf(String cpf) {
        if (cpf != null && cpf.matches("\\d{11}")) {
            this.cpf = cpf;
        } else {
            throw new IllegalArgumentException("CPF inválido: Deve conter exatamente 11 dígitos numéricos.");
        }
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
    
    public void setTipo(TipoUsuario tipo){ 
        this.tipo = tipo;
    }

    // --- Métodos de Comportamento e Utilitários ---

    public String getCpfPseudoanonimizado() {
        if (cpf != null && cpf.length() == 11) {
            return "***." + cpf.substring(3, 6) + ".***-" + cpf.substring(9);
        } else {
            return "CPF inválido ou não formatado";
        }
    }


    public boolean fazerLogin(String emailDigitado, String senhaDigitada) {
        return this.email.equals(emailDigitado) && this.senha.equals(senhaDigitada);
    }

    @Override
    public String toString() {
        return "Usuario{"
                + "id=" + id +
                ", nome='" + nome + '\'' +
                ", cpf='" + getCpfPseudoanonimizado() + '\'' +
                ", endereco='" + endereco + '\'' +
                ", email='" + email + '\'' +
                ", telefone='" + telefone + '\'' +
                ", tipo='" + tipo.getDescricao() + '\'' + 
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}

