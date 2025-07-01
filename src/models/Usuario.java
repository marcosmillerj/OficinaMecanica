/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package models;

import java.time.LocalDateTime;
import java.util.Objects; // Importe Objects para usar Objects.hash() no hashCode
import models.enums.TipoUsuario;

/**
 * Superclasse abstrata que serve como base para todos os **usuários** do sistema da oficina.
 * Define atributos comuns como ID, nome, CPF, endereço, e-mail, telefone, tipo de usuário e senha.
 * Cada usuário possui um ID único gerado automaticamente e um tipo que define suas permissões e funções.
 *
 * @author barbo
 */
public class Usuario {
    /**
     * Contador estático que gera **IDs únicos** para cada nova instância de Usuário.
     * Garante que cada usuário receba um identificador exclusivo ao ser criado.
     */
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
     * Construtor para criar uma nova instância de **Usuário**.
     * Atribui um ID único automaticamente e inicializa os dados básicos do usuário.
     * A validação do CPF é realizada no método `setCpf`.
     *
     * @param nome O nome completo do usuário.
     * @param cpf O CPF do usuário. Deve conter exatamente 11 dígitos numéricos e não ser nulo.
     * @param endereco O endereço completo do usuário.
     * @param email O endereço de e-mail do usuário.
     * @param telefone O número de telefone do usuário.
     * @param tipo O {@link TipoUsuario} (perfil) do usuário (e.g., Gerente, Atendente, Mecanico).
     * @param senha A senha do usuário. **Importante:** Em um sistema de produção, senhas nunca devem ser armazenadas em texto puro.
     * @throws IllegalArgumentException se o CPF for inválido (nulo ou não contiver 11 dígitos numéricos).
     */
    public Usuario(String nome, String cpf, String endereco, String email, String telefone, TipoUsuario tipo, String senha) {
        this.id = proximoId++; 
        this.nome = nome;
        setCpf(cpf); // Define o CPF usando o setter para aplicar a validação
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
     * Se o CPF fornecido for nulo ou não contiver exatamente 11 dígitos, uma exceção será lançada.
     *
     * @param cpf O CPF a ser definido para o usuário.
     * @throws IllegalArgumentException Se o CPF for nulo ou não contiver exatamente 11 dígitos numéricos.
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

    /**
     * Retorna uma versão pseudoanonimizada do CPF do usuário.
     * Este método oculta parte do CPF para fins de exibição,
     * mantendo apenas os três primeiros dígitos, três dígitos do meio
     * e os dois últimos dígitos visíveis, além dos separadores.
     *
     * @return Uma String contendo o CPF pseudoanonimizado, ou uma mensagem
     * de "CPF inválido ou não formatado" se o CPF não estiver no formato esperado.
     */
    public String getCpfPseudoanonimizado() {
        if (cpf != null && cpf.length() == 11) {
            // Exemplo: 123.456.789-01 -> ***.456.***-01
            return "***." + cpf.substring(3, 6) + ".***-" + cpf.substring(9);
        } else {
            return "CPF inválido ou não formatado";
        }
    }

    /**
     * Tenta autenticar o usuário comparando o e-mail e a senha fornecidos
     * com os dados cadastrados neste objeto de usuário.
     *
     * @param emailDigitado O e-mail informado pelo usuário no momento do login.
     * @param senhaDigitada A senha informada pelo usuário no momento do login.
     * @return `true` se o `emailDigitado` e a `senhaDigitada` corresponderem
     * aos do usuário; `false` caso contrário.
     */
    public boolean fazerLogin(String emailDigitado, String senhaDigitada) {
        return this.email.equals(emailDigitado) && this.senha.equals(senhaDigitada);
    }

    /**
     * Retorna uma representação em String do objeto Usuário,
     * incluindo seu ID, nome, CPF pseudoanonimizado, endereço, e-mail, telefone
     * e o tipo de usuário.
     *
     * @return Uma String formatada com os detalhes do usuário.
     */
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

    /**
     * Compara este objeto Usuário com o objeto especificado para verificar igualdade.
     * Dois usuários são considerados iguais se possuírem o **mesmo ID**.
     *
     * @param o O objeto a ser comparado com este usuário.
     * @return `true` se o objeto especificado for igual a este usuário, `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    /**
     * Retorna um valor de código hash para o objeto Usuário.
     * O código hash é baseado exclusivamente no **ID do usuário**, garantindo consistência
     * com o método `equals` (contrato `hashCode()/equals()`).
     *
     * @return Um valor de código hash para este objeto.
     */
    @Override
    public int hashCode() {
        return id; // Retorna o ID como hashCode, conforme a implementação original.
                   // Para ser mais robusto, poderia usar Objects.hash(id);
    }
}