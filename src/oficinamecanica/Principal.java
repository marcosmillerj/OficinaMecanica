/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oficinamecanica; 

import models.*; 
import models.enums.TipoPagamento; 
import observers.IObservavelOrdemServico;  

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import view.PainelMecanico;


/**
 * 
 * O objetivo é demonstrar o fluxo de notificação da OrdemServico para PainelMecanico.
 *
 * @author barbo
 */
public class Principal {

    public static void main(String[] args) {

        System.out.println("====================================================================");
        System.out.println("                     INÍCIO DO TESTE: PADRÃO OBSERVER                     ");
        System.out.println("        (Demonstrando Múltiplas OrdemServico -> PainelMecanico como Assinante)     ");
        System.out.println("====================================================================");

        System.out.println("\n[ETAPA 1] Configuração Inicial de Objetos:");

        Cliente clienteA = new Cliente("Ana Costa", "11987654321", "ana.costa@email.com");
        Cliente clienteB = new Cliente("Bruno Silva", "22987654321", "bruno.silva@email.com");
        Veiculo veiculoA = new Veiculo("DEF5678", "Ford Ka", "Vermelho", clienteA, null); 
        Veiculo veiculoB = new Veiculo("GHI9012", "VW Gol", "Branco", clienteB, null);
        Mecanico mecanicoChefe = new Mecanico("Carlos Chefe", "11122233344", "Rua A", "carlos@oficina.com", "987654321", "senha123", "Geral"); 
        
        
        OrdemServico osCarroA = new OrdemServico(
            "OS-001-A", new Date(), 0.0, veiculoA, clienteA, mecanicoChefe, StatusOrdem.AGUARDANDO_PAGAMENTO, new ArrayList<Servico>()
        );
        OrdemServico osCarroB = new OrdemServico(
            "OS-002-B", new Date(), 0.0, veiculoB, clienteB, mecanicoChefe, StatusOrdem.FINALIZADA, new ArrayList<Servico>()
        );
        System.out.println("  -> Publicador 1 (OrdemServico) criado: " + osCarroA.getCodigo() + " (Status Inicial: " + osCarroA.getStatus() + ")");
        System.out.println("  -> Publicador 2 (OrdemServico) criado: " + osCarroB.getCodigo() + " (Status Inicial: " + osCarroB.getStatus() + ")");
        
        System.out.println("\n--------------------------------------------------------------------");

        System.out.println("\n[ETAPA 2] Conexão: Publicadores e Assinante");
        System.out.println("  -> Assinante (PainelMecanico) criado.");
        PainelMecanico painelMecanico = new PainelMecanico();

        System.out.println("  -> PainelMecanico se registrando na OrdemServico " + osCarroA.getCodigo() + "...");
        osCarroA.adicionarObservador(painelMecanico);
        System.out.println("  -> PainelMecanico se registrando na OrdemServico " + osCarroB.getCodigo() + "...");
        osCarroB.adicionarObservador(painelMecanico);
        
        System.out.println("\n--------------------------------------------------------------------");

        System.out.println("\n[ETAPA 3] Cenários de Teste: Alterações de Status e Reações do Assinante");
        System.out.println("\n--- CENÁRIO 3.1: Ordem A -> 'EM_DIAGNÓSTICO' (RELEVANTE) ---");
        System.out.println("  [LOGMAIN:AÇÃO DA OS] Alterando status da OS " + osCarroA.getCodigo() + " para EM_DIAGNOSTICO...");
        osCarroA.alterarStatus(StatusOrdem.EM_DIAGNOSTICO);
        
        System.out.println("\n--- FIM DO CENÁRIO 3.1 ---");
        System.out.println("--------------------------------------------------------------------");

        System.out.println("\n--- CENÁRIO 3.2: Ordem B -> 'EM_EXECUÇÃO' (RELEVANTE) ---");
        System.out.println("  [LOGMAIN:AÇÃO DA OS] Alterando status da OS " + osCarroB.getCodigo() + " para EM_EXECUCAO...");
        osCarroB.alterarStatus(StatusOrdem.EM_EXECUCAO);
        
        System.out.println("\n--- FIM DO CENÁRIO 3.2 ---");
        System.out.println("--------------------------------------------------------------------");

        System.out.println("\n--- CENÁRIO 3.3: Ordem A -> 'AGUARDANDO_PAGAMENTO' (NÃO relevante) ---");
        System.out.println("  [LOGMAIN:AÇÃO DA OS] Alterando status da OS " + osCarroA.getCodigo() + " para AGUARDANDO_PAGAMENTO...");
        osCarroA.alterarStatus(StatusOrdem.AGUARDANDO_PAGAMENTO);
        
        System.out.println("\n--- FIM DO CENÁRIO 3.3 ---");
        System.out.println("--------------------------------------------------------------------");

        System.out.println("\n[ETAPA 4] Desconexão e Verificação Final");
        System.out.println("\n--- CENÁRIO 3.4: Removendo Assinante de TODAS as Ordens ---");
        System.out.println("  [AÇÃO] Removendo PainelMecanico como assinante da OS " + osCarroA.getCodigo() + "...");
        osCarroA.removerObservador(painelMecanico);
        System.out.println("  [AÇÃO] Removendo PainelMecanico como assinante da OS " + osCarroB.getCodigo() + "...");
        osCarroB.removerObservador(painelMecanico);

        System.out.println("  [LOGMAIN:AÇÃO DA OS] Alterando status da OS " + osCarroB.getCodigo() + " para FINALIZADA (PainelMecanico NÃO deve reagir)...");
        osCarroB.alterarStatus(StatusOrdem.FINALIZADA);
        
        System.out.println("\n--- FIM DO CENÁRIO 3.4 ---");
        System.out.println("====================================================================");
        System.out.println("                         FIM DO TESTE: PADRÃO OBSERVER                          ");
        System.out.println("====================================================================");
    }
}