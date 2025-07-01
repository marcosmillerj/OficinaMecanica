/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Importar para usar no toString formatado

/**
 * Representa um **registro de ponto** de um usuário no sistema.
 * Armazena a data e hora de entrada e, opcionalmente, a data e hora de saída,
 * permitindo o controle de jornada de trabalho.
 *
 * @author marcos_miller
 */
public class RegistroPonto {
    /**
     * Contador estático que gera **IDs únicos** para cada novo registro de ponto.
     * Garante que cada registro receba um identificador exclusivo ao ser criado.
     */
    public static int proximoId = 1;

    private int id;
    private int idUsuario;
    private LocalDateTime dataHoraEntrada;
    private LocalDateTime dataHoraSaida;

    /**
     * Construtor para criar um novo registro de **entrada de ponto**.
     * O ID é gerado automaticamente, a data e hora de entrada são definidas
     * como o momento atual, e a data e hora de saída são inicialmente `null`.
     *
     * @param idUsuario O identificador único do usuário que está registrando a entrada de ponto.
     */
    public RegistroPonto(int idUsuario) {
        this.id = proximoId++;
        this.idUsuario = idUsuario;
        this.dataHoraEntrada = LocalDateTime.now();
        this.dataHoraSaida = null; // Inicialmente nula, será preenchida ao registrar a saída.
    }

    /**
     * Construtor utilizado por bibliotecas de desserialização (e.g., Gson)
     * para reconstruir um objeto `RegistroPonto` a partir de dados persistidos.
     * Permite a atribuição explícita de todos os atributos, incluindo o ID
     * e ambas as datas/horas (entrada e saída).
     *
     * @param id O identificador único do registro de ponto.
     * @param idUsuario O identificador do usuário associado a este registro.
     * @param dataHoraEntrada A data e hora de entrada registradas.
     * @param dataHoraSaida A data e hora de saída registradas, ou `null` se ainda não houver saída.
     */
    public RegistroPonto(int id, int idUsuario, LocalDateTime dataHoraEntrada, LocalDateTime dataHoraSaida) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.dataHoraEntrada = dataHoraEntrada;
        this.dataHoraSaida = dataHoraSaida;
    }


    // --- Getters ---
    public int getId() {
        return id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public LocalDateTime getDataHoraEntrada() {
        return dataHoraEntrada;
    }

    public LocalDateTime getDataHoraSaida() {
        return dataHoraSaida;
    }


    /**
     * Define a data e hora de saída para este registro de ponto.
     * Este método é utilizado quando o usuário registra sua saída.
     *
     * @param dataHoraSaida A data e hora exata da saída a ser registrada.
     */
    public void setDataHoraSaida(LocalDateTime dataHoraSaida) {
        this.dataHoraSaida = dataHoraSaida;
    }

    /**
     * Retorna uma representação em String do objeto RegistroPonto,
     * incluindo seu ID, o ID do usuário, a data e hora de entrada formatada,
     * e a data e hora de saída formatada (ou "Não Registrado" se ainda não houver).
     *
     * @return Uma String formatada com os detalhes do registro de ponto.
     */
    @Override
    public String toString() {
        String saida = (dataHoraSaida == null) ? "Não Registrado" : dataHoraSaida.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM"));
        return "RegistroPonto{" +
                "ID=" + id +
                ", UsuárioID=" + idUsuario +
                ", Entrada=" + dataHoraEntrada.format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM")) +
                ", Saída=" + saida +
                '}';
    }
}