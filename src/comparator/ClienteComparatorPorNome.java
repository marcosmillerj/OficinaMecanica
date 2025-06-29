/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package comparator;

import java.util.Comparator;
import models.Cliente;

/**
 *
 * @author marcos_miller
 */
public class ClienteComparatorPorNome implements Comparator<Cliente> {

    @Override
    public int compare(Cliente c1, Cliente c2) {
        // Compara os clientes pelo atributo 'nome' de forma case-insensitive
        // Retorna:
        // - um número negativo se c1.nome vem antes de c2.nome
        // - zero se os nomes são iguais
        // - um número positivo se c1.nome vem depois de c2.nome
        return c1.getNome().compareToIgnoreCase(c2.getNome());
    }
}