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
 * Representa um **mecânico** da oficina, estendendo a classe base {@link models.Usuario}.
 * Além das informações de usuário, um mecânico possui uma especialidade, um status de disponibilidade
 * e uma lista de serviços para os quais está qualificado.
 *
 * @author barbo
 */
public class Mecanico extends Usuario {
    private String especialidade;
    private boolean disponivel;
    private List<Servico> servicosQualificados;

    /**
     * Construtor para criar uma nova instância de **Mecânico**.
     * Inicializa os atributos herdados da classe {@link Usuario} e os atributos específicos de `Mecanico`.
     * O mecânico é criado como disponível por padrão e com uma lista vazia de serviços qualificados.
     *
     * @param nome Nome completo do mecânico.
     * @param cpf CPF do mecânico.
     * @param endereco Endereço completo do mecânico.
     * @param email Endereço de e-mail do mecânico.
     * @param telefone Telefone de contato do mecânico.
     * @param tipo O tipo de usuário, que deve ser {@link models.enums.TipoUsuario#MECANICO} para esta classe.
     * @param senha Senha de acesso do mecânico ao sistema.
     * @param especialidade A área de especialização do mecânico (ex: "Motor", "Suspensão"). Não pode ser nula.
     * @throws NullPointerException se `especialidade` for nula.
     */
    public Mecanico(String nome, String cpf, String endereco, String email,
                    String telefone, TipoUsuario tipo, String senha, String especialidade) {
        super(nome, cpf, endereco, email, telefone, tipo, senha);
        this.especialidade = Objects.requireNonNull(especialidade, "Especialidade não pode ser nula.");
        this.disponivel = true;
        this.servicosQualificados = new ArrayList<>();
    }

    /**
     * Retorna uma representação em String do objeto Mecânico,
     * incluindo seu ID, nome, especialidade, status de disponibilidade
     * e o número de serviços para os quais está qualificado.
     *
     * @return Uma String formatada com os detalhes do mecânico.
     */
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