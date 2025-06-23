/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models.enums;

/**
 *
 * @author marcos_miller
 */
public enum SetorServico {
    MOTOR("Servicos relacionadas a MOTOR"),
    SUSPENSAO("Servicos relacionados a SUSPENSÃO"),
    ACABAMENTO("Servicos relacionados a ACABAMENTO"),
    ELETRICA("Servicos relacionados a Elétrica"),
    FREIOS("Freios"),
    PNEUS_RODAS("Pneus e Rodas (Inclui Alinhamento/Balanceamento)"),
    DIAGNOSTICO("Orçamento"),
    OUTROS("Outros Serviços");

    private final String descricao;

    private SetorServico(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
