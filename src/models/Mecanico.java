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
    private List<Servico> servicosQualificados;

    public Mecanico(String nome, String cpf, String endereco, String email,
                    String telefone, TipoUsuario tipo, String senha, String especialidade) {
        super(nome, cpf, endereco, email, telefone, tipo, senha);
        this.especialidade = Objects.requireNonNull(especialidade, "Especialidade não pode ser nula.");
        this.disponivel = true;
        this.servicosQualificados = new ArrayList<>();
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