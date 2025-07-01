/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.time.LocalDateTime;

/**
 *
 * @author marcos_miller
 */
public class RegistroPonto {
    public static int proximoId = 1;

    private int id;
    private int idUsuario;
    private LocalDateTime dataHoraEntrada;
    private LocalDateTime dataHoraSaida;

    /**
     * Construtor para registrar uma nova entrada de ponto.
     * A data e hora de saída são inicialmente nulas.
     *
     * @param idUsuario O ID do usuário que está registrando a entrada.
     */
    public RegistroPonto(int idUsuario) {
        this.id = proximoId++;
        this.idUsuario = idUsuario;
        this.dataHoraEntrada = LocalDateTime.now();
        this.dataHoraSaida = null;
    }

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
     * @param dataHoraSaida A data e hora da saída.
     */
    public void setDataHoraSaida(LocalDateTime dataHoraSaida) {
        this.dataHoraSaida = dataHoraSaida;
    }

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
