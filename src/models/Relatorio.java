/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import models.enums.TipoRelatorio;

/**
 *
 * @author marcos_miller
 */
public class Relatorio implements Serializable {

    private static final long serialVersionUID = 1L;

    public static int proximoId = 1;

    private int id;
    private TipoRelatorio tipo;
    private LocalDateTime dataGeracao;
    private String titulo;
    private String conteudo;
    private int usuarioGeradorId;

    public Relatorio() {
    }

    /**
     * CONSTRUTOR PRINCIPAL: Para criar um novo relatório (gerando ID automaticamente).
     * @param tipo Tipo do relatório.
     * @param dataGeracao Data e hora de geração do relatório.
     * @param titulo Título do relatório.
     * @param conteudo Conteúdo textual do relatório.
     * @param usuarioGeradorId ID do usuário que gerou o relatório.
     */
    public Relatorio(TipoRelatorio tipo, LocalDateTime dataGeracao,
                     String titulo, String conteudo, int usuarioGeradorId) {
        this.id = proximoId++;
        this.tipo = Objects.requireNonNull(tipo, "O tipo do relatório não pode ser nulo.");
        this.dataGeracao = Objects.requireNonNull(dataGeracao, "A data de geração não pode ser nula.");
        this.titulo = Objects.requireNonNull(titulo, "O título do relatório não pode ser nulo.");
        this.conteudo = Objects.requireNonNull(conteudo, "O conteúdo do relatório não pode ser nulo.");
        this.usuarioGeradorId = usuarioGeradorId;
    }

    /**
     * Construtor para uso pelo Gson ao desserializar (reconstruir o objeto do JSON).
     * @param id ID do relatório.
     * @param tipo Tipo do relatório.
     * @param dataGeracao Data e hora de geração do relatório.
     * @param titulo Título do relatório.
     * @param conteudo Conteúdo textual do relatório.
     * @param usuarioGeradorId ID do usuário que gerou o relatório.
     */
    public Relatorio(int id, TipoRelatorio tipo, LocalDateTime dataGeracao,
                     String titulo, String conteudo, int usuarioGeradorId) {
        this.id = id;
        this.tipo = tipo;
        this.dataGeracao = dataGeracao;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.usuarioGeradorId = usuarioGeradorId;
    }

    // Getters
    public int getId() { return id; }
    public TipoRelatorio getTipo() { return tipo; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public String getTitulo() { return titulo; }
    public String getConteudo() { return conteudo; }
    public int getUsuarioGeradorId() { return usuarioGeradorId; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTipo(TipoRelatorio tipo) { this.tipo = tipo; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public void setUsuarioGeradorId(int usuarioGeradorId) { this.usuarioGeradorId = usuarioGeradorId; }

    @Override
    public String toString() {
        return "Relatorio{" +
               "id=" + id +
               ", tipo=" + (tipo != null ? tipo.getDescricao() : "N/A") +
               ", dataGeracao=" + (dataGeracao != null ? dataGeracao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A") +
               ", titulo='" + titulo + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relatorio relatorio = (Relatorio) o;
        return id == relatorio.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
