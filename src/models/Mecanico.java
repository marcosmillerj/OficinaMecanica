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

    /**
     * Construtor da classe Mecanico.
     * @param nome Nome completo do mecânico.
     * @param cpf CPF do mecânico (11 dígitos).
     * @param endereco Endereço completo.
     * @param email Email válido.
     * @param telefone Telefone para contato.
     * @param tipo O tipo de usuário (deve ser TipoUsuario.MECANICO).
     * @param senha Senha de acesso ao sistema.
     * @param especialidade Área de especialização do mecânico (ex: "Motor", "Freios").
     */
    public Mecanico(String nome, String cpf, String endereco, String email,
                    String telefone, TipoUsuario tipo, String senha, String especialidade) {
        // Chama o construtor da superclasse Usuario
        super(nome, cpf, endereco, email, telefone, tipo, senha); 
        this.especialidade = Objects.requireNonNull(especialidade, "Especialidade não pode ser nula.");
        this.disponivel = true; // Mecânico inicia disponível por padrão
        this.servicosQualificados = new ArrayList<>(); // Inicializa a lista de qualificações vazia
    }

    // --- Getters e Setters ---
    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = Objects.requireNonNull(especialidade, "Especialidade não pode ser nula.");
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public void setDisponivel(boolean disponivel) {
        this.disponivel = disponivel;
    }

    /**
     * Adiciona uma qualificação a este mecânico, indicando um tipo de serviço que ele pode realizar.
     * @param servico O Serviço a ser adicionado nas qualificações.
     */
    public void adicionarQualificacao(Servico servico) {
        Objects.requireNonNull(servico, "Serviço não pode ser nulo para qualificação.");
        if (!servicosQualificados.contains(servico)) { // Usa o equals/hashCode de Servico
            servicosQualificados.add(servico);
            System.out.println("Mecânico " + getNome() + " qualificado para: " + servico.getDescricao());
        } else {
            System.out.println("Mecânico " + getNome() + " já qualificado para: " + servico.getDescricao());
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
     * A OS é criada com status EM_DIAGNOSTICO e o mecânico fica indisponível.
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
        // CUIDADO AQUI: O construtor de Servico espera (codigo, descricao, preco, setor, idItemEstoquePeca, requerElevadorAlinhamento)
        Servico diagnostico = new Servico(
            "DIAG-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmm")), // Código único
            "Diagnóstico Inicial do Veículo",
            new BigDecimal("50.00"), // Preço como BigDecimal
            SetorServico.DIAGNOSTICO, // SetorServico.DIAGNOSTICO
            0, // idItemEstoquePeca (0 para 'sem peça' ou seu valor padrão)
            false // requerElevadorAlinhamento (diagnóstico não requer elevador especial)
        );
        servicosIniciais.add(diagnostico);

        OrdemServico os = new OrdemServico(
            gerarCodigoOS(),
            LocalDateTime.now(),
            veiculo.getId(), // ID do Veículo
            cliente.getId(), // ID do Cliente
            this.getId(), // ID deste Mecânico
            StatusOrdem.EM_DIAGNOSTICO // Status inicial da OS
        );
        
        // Adiciona os serviços iniciais à OS (ela vai calcular o total internamente)
        for (Servico s : servicosIniciais) {
            os.adicionarServico(s);
        }

        this.disponivel = false; // Mecânico fica ocupado com a OS de diagnóstico
        System.out.println("Mecânico " + getNome() + " iniciou diagnóstico para veículo " + veiculo.getPlaca() + ". OS: " + os.getCodigo());
        return os;
    }

    /**
     * Executa um serviço em uma ordem de serviço existente.
     * O status da OS é atualizado e o mecânico fica indisponível.
     * @param ordem Ordem de serviço a ser executada.
     * @param servico Serviço que será realizado.
     * @throws IllegalStateException Se o mecânico não estiver disponível, não qualificado ou a OS não estiver no status correto.
     * @throws NullPointerException Se ordem ou serviço forem nulos.
     */
    public void executarServico(OrdemServico ordem, Servico servico) {
        Objects.requireNonNull(ordem, "Ordem de Serviço não pode ser nula.");
        Objects.requireNonNull(servico, "Serviço não pode ser nulo.");

        if (!this.disponivel) {
            throw new IllegalStateException("Mecânico " + getNome() + " não está disponível para execução de serviço.");
        }
        if (!podeExecutarServico(servico)) {
            throw new IllegalStateException("Mecânico " + getNome() + " não qualificado para este serviço: " + servico.getDescricao());
        }
        // Validações de transição de status para execução (pode vir de AGUARDANDO_LIBERACAO ou EM_DIAGNOSTICO)
        if (!ordem.getStatus().equals(StatusOrdem.AGUARDANDO_LIBERACAO) &&
            !ordem.getStatus().equals(StatusOrdem.EM_DIAGNOSTICO)) { 
            throw new IllegalStateException("Ordem de Serviço " + ordem.getCodigo() + " não está pronta para execução. Status atual: " + ordem.getStatus().getDescricao());
        }

        ordem.adicionarServico(servico); // Adiciona o serviço à OS (e recalcula o total)
        ordem.alterarStatus(StatusOrdem.EM_EXECUCAO); // Altera o status da OS (e notifica observadores)
        this.disponivel = false; // Mecânico fica ocupado
        System.out.println("Mecânico " + getNome() + " iniciou execução do serviço '" + servico.getDescricao() + "' na OS " + ordem.getCodigo());
    }

    /**
     * Gera um código único para cada Ordem de Serviço.
     * @return Retorna código no formato "OS-{timestamp}-{3primeirosDigitosCPF}".
     */
    private String gerarCodigoOS() {
        // Obtém os 3 primeiros dígitos do CPF do mecânico para o código da OS
        String cpfParte = (this.getCpf() != null && this.getCpf().length() >= 3) ? this.getCpf().substring(0, 3) : "XXX";
        return "OS-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmm")) +
               "-" + cpfParte;
    }

    @Override
    public String toString() {
        return String.format("Mecânico [ID: %d | Nome: %s] - Especialidade: %s - %s - %d qualificações",
            getId(), // ID herdado de Usuario
            getNome(), // Nome herdado de Usuario
            especialidade,
            disponivel ? "Disponível" : "Ocupado",
            servicosQualificados.size());
    }
}