/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comparator;

import java.util.Comparator;
import models.Cliente;

/**
 * Implementa a interface {@link java.util.Comparator} para comparar objetos {@link models.Cliente}
 * com base no seu atributo de **nome**.
 * Esta classe define uma estratégia de ordenação específica, permitindo que listas de clientes
 * sejam ordenadas alfabeticamente por seus nomes de forma flexível e reutilizável.
 *
 * @author marcos_miller
 */
public class ClienteComparatorPorNome implements Comparator<Cliente> {

    /**
     * Compara dois objetos {@link models.Cliente} pelo seu atributo de **nome**.
     * A comparação é realizada de forma **insensível a maiúsculas e minúsculas**,
     * desconsiderando diferenças entre letras maiúsculas e minúsculas.
     *
     * @param c1 O primeiro objeto {@link models.Cliente} a ser comparado.
     * @param c2 O segundo objeto {@link models.Cliente} a ser comparado.
     * @return Um valor inteiro que indica a ordem relativa dos clientes:
     * <ul>
     * <li>Um valor negativo se o nome de {@code c1} for lexicograficamente menor que o nome de {@code c2}.</li>
     * <li>Zero se os nomes de {@code c1} e {@code c2} forem lexicograficamente iguais.</li>
     * <li>Um valor positivo se o nome de {@code c1} for lexicograficamente maior que o nome de {@code c2}.</li>
     * </ul>
     */
    @Override
    public int compare(Cliente c1, Cliente c2) {
        // Compara os clientes pelo atributo 'nome' de forma insensível a maiúsculas e minúsculas.
        // Retorna:
        // - um número negativo se c1.nome vem antes de c2.nome
        // - zero se os nomes são iguais
        // - um número positivo se c1.nome vem depois de c2.nome
        return c1.getNome().compareToIgnoreCase(c2.getNome());
    }
}