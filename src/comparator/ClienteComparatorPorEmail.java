/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comparator;

import java.util.Comparator;
import models.Cliente;

/**
 * Implementa a interface {@link java.util.Comparator} para comparar objetos {@link models.Cliente}
 * com base no atributo **email**.
 * Esta classe define uma estratégia de ordenação específica, permitindo que listas de clientes
 * sejam ordenadas por seus endereços de e-mail de forma flexível e reutilizável.
 *
 * @author marcos_miller
 */
public class ClienteComparatorPorEmail implements Comparator<Cliente> {

    /**
     * Compara dois objetos {@link models.Cliente} pelo atributo **email**.
     * A comparação é realizada de forma **case-insensitive**, ignorando diferenças entre letras maiúsculas e minúsculas.
     *
     * @param c1 O primeiro objeto {@link models.Cliente} a ser comparado.
     * @param c2 O segundo objeto {@link models.Cliente} a ser comparado.
     * @return Um valor inteiro que indica a ordem relativa dos clientes:
     * <ul>
     * <li>Um valor negativo se o email de {@code c1} for lexicograficamente menor que o email de {@code c2}.</li>
     * <li>Zero se os emails de {@code c1} e {@code c2} forem lexicograficamente iguais.</li>
     * <li>Um valor positivo se o email de {@code c1} for lexicograficamente maior que o email de {@code c2}.</li>
     * </ul>
     */
    @Override
    public int compare(Cliente c1, Cliente c2) {
        // Compara os clientes pelo atributo 'email' de forma case-insensitive
        // Útil para ordenar por um identificador secundário.
        return c1.getEmail().compareToIgnoreCase(c2.getEmail());
    }
}