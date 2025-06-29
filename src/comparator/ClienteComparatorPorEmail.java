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
public class ClienteComparatorPorEmail implements Comparator<Cliente> {

    @Override
    public int compare(Cliente c1, Cliente c2) {
        // Compara os clientes pelo atributo 'email' de forma case-insensitive
        // Útil para ordenar por um identificador secundário.
        return c1.getEmail().compareToIgnoreCase(c2.getEmail());
    }
}