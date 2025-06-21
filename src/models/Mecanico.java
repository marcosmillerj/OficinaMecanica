/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import models.enums.StatusOrdem;
import models.enums.TipoUsuario;

/**
 * Classe que representa um mecânico da oficina.
 * @author barbo
 */
public class Mecanico extends Usuario {
    private String especialidade;
    private boolean disponivel;
    private List<Servico> servicosQualificados;
/**
     * Construtor da classe Mecanico.
     *
     * @param nome Nome completo do mecânico
     * @param cpf CPF do mecânico (11 dígitos)
     * @param endereco Endereço completo
     * @param email Email válido
     * @param telefone Telefone para contato
     * @param senha Senha de acesso ao sistema
     * @param especialidade Área de especialização do mecânico (ex: "Motor", "Freios")
     */
    public Mecanico(String nome, String cpf, String endereco, String email, 
                   String telefone, TipoUsuario tipo, String senha, String especialidade) {
        super(nome, cpf, endereco, email, telefone, tipo, senha);
        this.especialidade = Objects.requireNonNull(especialidade, "Especialidade não pode ser nula");
        this.disponivel = true;
        this.servicosQualificados = new ArrayList<>();
    }

    // Getters e Setters
    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    /**
     * Adiciona uma qualificação que o mecanico pode realizar
     * @param servico Serviço a ser adicionado nas qualificações
     */
     public void adicionarQualificacao(Servico servico) {
        Objects.requireNonNull(servico, "Serviço não pode ser nulo");
        if (!servicosQualificados.contains(servico)) {
            servicosQualificados.add(servico);
        }
    }
     /**
      * Verifica se o mecânico está qualificado para executar um serviço.
      * @param servico Serviço que será verificado
      * @return true se o mecânico é qualificado, se não, retorna false.
      */

    public boolean podeExecutarServico(Servico servico) {
        return servicosQualificados.contains(servico);
    }
    /**
     * Inicia um diagnóstico técnico e gera uma nova ordem de serviço
     * @param veiculo veículo que será diagnosticado
     * @param cliente cliente proprietário do veículo
     * @return Irá retornar uma nova ordem de serviço 
     */
    public OrdemServico realizarDiagnostico(Veiculo veiculo, Cliente cliente) {
    if (!this.disponivel) {
        throw new IllegalStateException("Mecânico não está disponível para diagnóstico");
    }

    //Cria lista de serviços inicial com diagnóstico padrão
    List<Servico> servicosIniciais = new ArrayList<>();
    Servico diagnostico = new Servico(
        "DIAG", 
        "Diagnóstico Inicial",
        50.0, 
        "Diagnóstico", 
        null  // Nenhuma peça associada ao diagnóstico
    );
    servicosIniciais.add(diagnostico);

    //Calcula preço total inicial (apenas o diagnóstico)
    double precoTotalInicial = servicosIniciais.stream() //alterar para orçamento
            .mapToDouble(Servico::getPreco)
            .sum();

    OrdemServico os = new OrdemServico(
        gerarCodigoOS(),           
        LocalDateTime.now(),
        precoTotalInicial,         
        veiculo,                  
        cliente,                   
        this,                      
        StatusOrdem.EM_DIAGNOSTICO,
        servicosIniciais           
    );

    this.disponivel = false;
    return os;
}
    /**
     * Executa um serviço em uma ordem de serviço que já existe
     * @param ordem Ordem de serviço a ser executada
     * @param servico Serviço que será realizado
     */
    public void executarServico(OrdemServico ordem, Servico servico) {
        if (!this.disponivel) {
            throw new IllegalStateException("Mecânico não disponível");
        }
        if (!podeExecutarServico(servico)) {
            throw new IllegalStateException("Mecânico não qualificado para este serviço");
        }
        if (!ordem.getStatus().equals(StatusOrdem.AGUARDANDO_LIBERACAO)) {
            throw new IllegalStateException("Ordem não está pronta para execução");
        }

        ordem.adicionarServico(servico);
        ordem.setStatus(StatusOrdem.EM_EXECUCAO);
        this.disponivel = false;
    }

    /**
     * Gera um código único para cada os
     * @return retorna código no formato "OS-{timestamp}-{3primeirosDigitosCPF}"
     */
    private String gerarCodigoOS() {
        return "OS-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) + 
               "-" + this.getCpf().substring(0, 3);
    }

    @Override
    public String toString() {
        return String.format("Mecânico [%s] - Especialidade: %s - %s - %d qualificações",
            getNome(),
            especialidade,
            disponivel ? "Disponível" : "Ocupado",
            servicosQualificados.size());
    }
}