/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers;

import models.OrdemServico;

/**
 *
 * @author marcos_miller
 */
/**
 * Esta interface define o "contrato" para qualquer classe que queira "assinar"
 * (observar) um objeto OrdemServico, especificamente sobre mudanças de status.
 * Todas as classes que implementam esta interface concordam em ter um método
 * que será chamado quando o status da Ordem de Serviço for alterado.
 */
public interface IObservadorOrdemServico {
    /**
     * Método chamado pelo Subject (OrdemServico) para enviar a notícia aos assinantes.
     * Quando o status de uma Ordem de Serviço é alterado, este método é invocado
     * em todos os assinantes registrados.
     *
     * @param ordem O objeto OrdemServico que teve seu status alterado.
     */
    void notificarStatusOrdem(OrdemServico aThis);

}
