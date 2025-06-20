/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import models.OrdemServico;
import observers.IObservadorOrdemServico;

/**
 *
 * @author marcos_miller
 */
/**
 * Esta classe simula o painel visual do mecânico e atua como um Observer concreto
 * para a OrdemServico.
 */
public class PainelMecanico implements IObservadorOrdemServico {

    // simula a UI
    private List<OrdemServico> ordensParaTrabalho;

    /**
     * Construtor do PainelMecanico.
     * Inicializa a lista de ordens que serão exibidas.
     */
    public PainelMecanico() {
        this.ordensParaTrabalho = new ArrayList<>();
        System.out.println("[LOG:PainelMecanico] Inicializado e pronto para monitorar Ordens de Serviço.");
    }

    /**
     * Este método é chamado automaticamente pela OrdemServico quando seu status é alterado.
     * Ele atualiza a "tela" do mecânico com base no novo status da OS.
     *
     * @param ordem O objeto OrdemServico que teve seu status alterado.
     */
    @Override
    public void notificarStatusOrdem(OrdemServico ordem) {
        System.out.println("\n[LOG:PainelMecanico] Notificação recebida para OS " + ordem.getCodigo() + " ---");
        System.out.println("    Status: " + ordem.getStatus());

        boolean isRelevanteParaMecanico = false;
        switch (ordem.getStatus()) {
            case EM_DIAGNOSTICO:
            case AGUARDANDO_LIBERACAO:
            case EM_EXECUCAO:
                isRelevanteParaMecanico = true;
                break;
            case AGUARDANDO_PAGAMENTO:
            case FINALIZADA:
            case CANCELADA:
                isRelevanteParaMecanico = false;
                break;
        }
        //
        // Remove a versão antiga da OS da lista (se existir, baseando-se no 'codigo')
        ordensParaTrabalho.removeIf(os -> Objects.equals(os.getCodigo(), ordem.getCodigo()));

        if (isRelevanteParaMecanico) {
            // Adiciona a versão atualizada da OS à lista do painel
            ordensParaTrabalho.add(ordem);
            System.out.println("    [LOG:PainelMecanico] OS " + ordem.getCodigo() + " ADICIONADA/ATUALIZADA na lista de 'Carros em Trabalho' (" + ordem.getStatus() + ").");
        } else {
            // Se a OS não é mais relevante, a linha 'removeIf' acima já a removeu.
            System.out.println("    [LOG:PainelMecanico] OS " + ordem.getCodigo() + " REMOVIDA da lista de 'Carros em Trabalho' (Status: " + ordem.getStatus() + ").");
        }
        exibirOrdensAtuais(); // Atualiza a exibição no console
    }

    /**
     * Método auxiliar para exibir o estado atual do painel do mecânico (simulação de UI).
     */
    public void exibirOrdensAtuais() {
                System.out.println("    -------------------------------------");

        System.out.println("    --- LISTA EXIBIDA NO NOSSO PAINEL MECÂNICO/UI: Ordens Atuais ---");
        if (ordensParaTrabalho.isEmpty()) {
            System.out.println("    Nenhuma ordem de serviço relevante no momento.");
        } else {
            for (OrdemServico os : ordensParaTrabalho) {
                System.out.println("    - OS: " + os.getCodigo() + ", Veículo: " + os.getVeiculo().getPlaca() + ", Status: " + os.getStatus());
            }
        }
        System.out.println("    -------------------------------------");
    }
}
