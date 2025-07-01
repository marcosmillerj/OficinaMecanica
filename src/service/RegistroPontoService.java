/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import models.RegistroPonto;
import models.Usuario;
import repository.PontoRepository;

/**
 *
 * @author marcos_miller
 */
public class RegistroPontoService {

    private PontoRepository pontoRepository;

    /**
     * Construtor do RegistroPontoService.
     * @param pontoRepository Instância do PontoRepository para gerenciar a persistência dos registros de ponto.
     */
    public RegistroPontoService(PontoRepository pontoRepository) {
        this.pontoRepository = pontoRepository;
    }

    /**
     * Registra a entrada de ponto para um usuário.
     * @param usuario O usuário que está batendo o ponto.
     * @return O RegistroPonto criado, ou null se já houver uma entrada aberta.
     * @throws IllegalStateException Se o usuário já tiver uma entrada de ponto em aberto.
     */
    public RegistroPonto registrarEntrada(Usuario usuario) throws IllegalStateException {
        Optional<RegistroPonto> pontoAberto = pontoRepository.buscarUltimoPontoAbertoPorUsuario(usuario.getId());
        if (pontoAberto.isPresent()) {
            throw new IllegalStateException("Erro: Usuário já possui uma entrada de ponto em aberto.");
        }

        RegistroPonto novoRegistro = new RegistroPonto(usuario.getId());

        pontoRepository.adicionarRegistro(novoRegistro);
        System.out.println("Ponto de entrada registrado para " + usuario.getNome() + " às " + novoRegistro.getDataHoraEntrada().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM")));
        return novoRegistro;
    }

    /**
     * Registra a saída de ponto para um usuário.
     * @param usuario O usuário que está batendo o ponto.
     * @return O RegistroPonto atualizado com a saída, ou null se não houver entrada em aberto.
     * @throws IllegalStateException Se o usuário não tiver uma entrada de ponto em aberto.
     */
    public RegistroPonto registrarSaida(Usuario usuario) throws IllegalStateException {
        Optional<RegistroPonto> pontoAberto = pontoRepository.buscarUltimoPontoAbertoPorUsuario(usuario.getId());
        if (pontoAberto.isEmpty()) {
            throw new IllegalStateException("Erro: Não há registro de entrada de ponto em aberto para este usuário.");
        }

        RegistroPonto registroParaAtualizar = pontoAberto.get();
        registroParaAtualizar.setDataHoraSaida(LocalDateTime.now());
        pontoRepository.atualizarRegistro(registroParaAtualizar);
        System.out.println("Ponto de saída registrado para " + usuario.getNome() + " às " + registroParaAtualizar.getDataHoraSaida().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm dd/MM")));
        return registroParaAtualizar;
    }

    /**
     * Obtém o status atual do ponto de um usuário (entrada em aberto ou não).
     * @param usuario O usuário para verificar o status.
     * @return Um Optional contendo o RegistroPonto se houver uma entrada em aberto, ou um Optional vazio.
     */
    public Optional<RegistroPonto> obterStatusPontoAtual(Usuario usuario) {
        return pontoRepository.buscarUltimoPontoAbertoPorUsuario(usuario.getId());
    }
}
