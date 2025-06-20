/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package observers;

/**
 *
 * @author marcos_miller
 */
/**
 * Esta interface define o contrato para classes que podem ser "observadas"
 * (atuar como Publicadores) sobre mudanças de estado da OrdemServico.
 * Ela especifica como os assinantes podem ser adicionados, removidos e notificados.
 */
public interface IObservavelOrdemServico {
    /**
     * Adiciona um observador à lista.
     * @param obs O observador a ser adicionado.
     */
    void adicionarObservador(IObservadorOrdemServico obs);

    /**
     * Remove um observador da lista.
     * @param obs O observador a ser removido.
     */
    void removerObservador(IObservadorOrdemServico obs);

    /**
     * Notifica todos os observadores registrados sobre uma mudança de estado.
     */
    void notificarObservadores();
}
