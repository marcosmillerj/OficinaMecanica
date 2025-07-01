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
 * Representa um **relatório** gerado pelo sistema da oficina.
 * Um relatório possui um ID único, um tipo específico, a data e hora de sua geração,
 * um título, o conteúdo textual gerado e o ID do usuário responsável por sua criação.
 * Esta classe é {@link java.io.Serializable} para permitir sua persistência.
 *
 * @author marcos_miller
 */
public class Relatorio implements Serializable {

    /**
     * O `serialVersionUID` é um identificador de versão universal para uma classe Serializable.
     * Utilizado para garantir que a versão da classe durante a desserialização seja a mesma
     * da versão utilizada durante a serialização, evitando {@link java.io.InvalidClassException}.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Contador estático que gera **IDs únicos** para cada nova instância de Relatorio.
     * Garante que cada relatório receba um identificador exclusivo ao ser criado.
     */
    public static int proximoId = 1;

    private int id;
    private TipoRelatorio tipo;
    private LocalDateTime dataGeracao;
    private String titulo;
    private String conteudo;
    private int usuarioGeradorId;

    /**
     * Construtor padrão vazio para a classe Relatorio.
     * Utilizado por frameworks de serialização/desserialização que exigem um construtor sem argumentos.
     */
    public Relatorio() {
    }

    /**
     * Construtor principal para criar uma nova instância de **Relatorio**.
     * Atribui um ID único automaticamente e realiza validações para garantir
     * que os parâmetros essenciais (tipo, data de geração, título, conteúdo) não sejam nulos.
     *
     * @param tipo O {@link TipoRelatorio} que especifica a categoria do relatório. Não pode ser nulo.
     * @param dataGeracao A data e hora em que o relatório foi gerado. Não pode ser nula.
     * @param titulo O título descritivo do relatório. Não pode ser nulo.
     * @param conteudo O conteúdo textual detalhado do relatório. Não pode ser nulo.
     * @param usuarioGeradorId O identificador único do usuário que gerou este relatório.
     * @throws NullPointerException se `tipo`, `dataGeracao`, `titulo` ou `conteudo` forem nulos.
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
     * Construtor utilizado por bibliotecas de desserialização (e.g., Gson)
     * para reconstruir um objeto `Relatorio` a partir de dados persistidos.
     * Permite a atribuição explícita de todos os atributos, incluindo o ID.
     *
     * @param id O identificador único do relatório.
     * @param tipo O {@link TipoRelatorio} do relatório.
     * @param dataGeracao A data e hora de geração do relatório.
     * @param titulo O título do relatório.
     * @param conteudo O conteúdo textual do relatório.
     * @param usuarioGeradorId O identificador do usuário que gerou o relatório.
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

    // Getters e Setters (sem JavaDoc, conforme solicitado)
    public int getId() { return id; }
    public TipoRelatorio getTipo() { return tipo; }
    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public String getTitulo() { return titulo; }
    public String getConteudo() { return conteudo; }
    public int getUsuarioGeradorId() { return usuarioGeradorId; }

    public void setId(int id) { this.id = id; }
    public void setTipo(TipoRelatorio tipo) { this.tipo = tipo; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
    public void setUsuarioGeradorId(int usuarioGeradorId) { this.usuarioGeradorId = usuarioGeradorId; }

    /**
     * Retorna uma representação em String do objeto Relatorio,
     * incluindo seu ID, tipo (descrição), data de geração formatada e título.
     *
     * @return Uma String formatada com os detalhes essenciais do relatório.
     */
    @Override
    public String toString() {
        return "Relatorio{" +
               "id=" + id +
               ", tipo=" + (tipo != null ? tipo.getDescricao() : "N/A") +
               ", dataGeracao=" + (dataGeracao != null ? dataGeracao.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A") +
               ", titulo='" + titulo + '\'' +
               '}';
    }

    /**
     * Compara este objeto Relatorio com o objeto especificado para verificar igualdade.
     * Dois relatórios são considerados iguais se possuírem o **mesmo ID**.
     *
     * @param o O objeto a ser comparado com este relatório.
     * @return `true` se o objeto especificado for igual a este relatório, `false` caso contrário.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Relatorio relatorio = (Relatorio) o;
        return id == relatorio.id;
    }

    /**
     * Retorna um valor de código hash para o objeto Relatorio.
     * O código hash é baseado exclusivamente no **ID do relatório**, garantindo consistência
     * com o método `equals` (contrato `hashCode()/equals()`).
     *
     * @return Um valor de código hash para este objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}