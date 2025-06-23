/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import models.enums.SetorServico;
import models.enums.StatusOrdem;
import models.enums.TipoUsuario;

/**
 * Classe que representa um mecânico da oficina.
 * @author barbo
 */
public class Mecanico extends Usuario {
    private String especialidade;
    private boolean disponivel;
    private List<Servico> servicosQualificados; // Lista de serviços para os quais o mecânico é qualificado

    public Mecanico(String nome, String cpf, String endereco, String email,
                    String telefone, TipoUsuario tipo, String senha, String especialidade) {
        super(nome, cpf, endereco, email, telefone, tipo, senha);
        this.especialidade = Objects.requireNonNull(especialidade, "Especialidade não pode ser nula.");
        this.disponivel = true;
        this.servicosQualificados = new ArrayList<>();
    }

    // ... (Getters e Setters) ...

    /**
     * Adiciona uma qualificação a este mecânico.
     * @param servico O Serviço a ser adicionado nas qualificações.
     */
    public void adicionarQualificacao(Servico servico) {
        Objects.requireNonNull(servico, "Serviço não pode ser nulo para qualificação.");
        if (!servicosQualificados.contains(servico)) {
            servicosQualificados.add(servico);
            // ALTERADO: servico.getDescricao() para servico.getObservacoes()
            System.out.println("Mecânico " + getNome() + " qualificado para: " + servico.getObservacoes());
        } else {
            System.out.println("Mecânico " + getNome() + " já qualificado para: " + servico.getObservacoes());
        }
    }

    /**
     * Verifica se o mecânico está qualificado para executar um determinado serviço.
     * @param servico O Serviço que será verificado.
     * @return true se o mecânico é qualificado, false caso contrário.
     */
    public boolean podeExecutarServico(Servico servico) {
        Objects.requireNonNull(servico, "Serviço não pode ser nulo para verificação de qualificação.");
        return servicosQualificados.contains(servico);
    }

    /**
     * Inicia um diagnóstico técnico para um veículo e gera uma nova ordem de serviço.
     * @param veiculo O veículo que será diagnosticado.
     * @param cliente O cliente proprietário do veículo.
     * @return A nova OrdemServico criada.
     * @throws IllegalStateException Se o mecânico não estiver disponível.
     * @throws NullPointerException Se veículo ou cliente forem nulos.
     */
    public OrdemServico realizarDiagnostico(Veiculo veiculo, Cliente cliente) {
        Objects.requireNonNull(veiculo, "Veículo não pode ser nulo para diagnóstico.");
        Objects.requireNonNull(cliente, "Cliente não pode ser nulo para diagnóstico.");

        if (!this.disponivel) {
            throw new IllegalStateException("Mecânico " + getNome() + " não está disponível para diagnóstico.");
        }

        List<Servico> servicosIniciais = new ArrayList<>();
        // ALTERADO: Construtor de Servico agora é (precoMaoDeObra, setor, codigoPeca, quantidadePeca, requerPrioridade, observacoes)
        Servico diagnostico = new Servico(
            new BigDecimal("50.00"), // Preço Mão de Obra
            SetorServico.DIAGNOSTICO, // Setor
            null, // Código da peça (diagnóstico não usa peça)
            0, // Quantidade de peça
            false, // Requer prioridade (diagnóstico não requer elevador especial)
            "Diagnóstico Inicial do Veículo" // Observações (descrição do serviço)
        );
        servicosIniciais.add(diagnostico);

        OrdemServico os = new OrdemServico(
            gerarCodigoOS(),
            LocalDateTime.now(),
            veiculo.getId(),
            cliente.getId(),
            this.getId(),
            StatusOrdem.EM_DIAGNOSTICO
        );
        
        for (Servico s : servicosIniciais) {
            os.adicionarServico(s);
        }

        this.disponivel = false;
        System.out.println("Mecânico " + getNome() + " iniciou diagnóstico para veículo " + veiculo.getPlaca() + ". OS: " + os.getCodigo());
        return os;
    }

    /**
     * Executa um serviço em uma ordem de serviço existente.
     * @param ordem Ordem de serviço a ser executada.
     * @param servico Serviço que será realizado.
     */
    public void executarServico(OrdemServico ordem, Servico servico) {
        Objects.requireNonNull(ordem, "Ordem de Serviço não pode ser nula.");
        Objects.requireNonNull(servico, "Serviço não pode ser nulo.");

        if (!this.disponivel) {
            throw new IllegalStateException("Mecânico " + getNome() + " não está disponível para execução de serviço.");
        }
        if (!podeExecutarServico(servico)) {
            // ALTERADO: servico.getDescricao() para servico.getObservacoes()
            throw new IllegalStateException("Mecânico " + getNome() + " não qualificado para este serviço: " + servico.getObservacoes());
        }
        if (!ordem.getStatus().equals(StatusOrdem.AGUARDANDO_LIBERACAO) &&
            !ordem.getStatus().equals(StatusOrdem.EM_DIAGNOSTICO)) { 
            throw new IllegalStateException("Ordem de Serviço " + ordem.getCodigo() + " não está pronta para execução. Status atual: " + ordem.getStatus().getDescricao());
        }

        ordem.adicionarServico(servico);
        ordem.alterarStatus(StatusOrdem.EM_EXECUCAO);
        this.disponivel = false;
        // ALTERADO: servico.getDescricao() para servico.getObservacoes()
        System.out.println("Mecânico " + getNome() + " iniciou execução do serviço '" + servico.getObservacoes() + "' na OS " + ordem.getCodigo());
    }

    /**
     * Gera um código único para cada Ordem de Serviço.
     * @return Retorna código no formato "OS-{timestamp}-{3primeirosDigitosCPF}".
     */
    private String gerarCodigoOS() {
        String cpfParte = (this.getCpf() != null && this.getCpf().length() >= 3) ? this.getCpf().substring(0, 3) : "XXX";
        return "OS-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) +
               "-" + cpfParte;
    }

    @Override
    public String toString() {
        return String.format("Mecânico [ID: %d | Nome: %s] - Especialidade: %s - %s - %d qualificações",
            getId(),
            getNome(),
            especialidade,
            disponivel ? "Disponível" : "Ocupado",
            servicosQualificados.size());
    }
}