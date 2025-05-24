package br.ufscar.dc.dsw.domain;

import br.ufscar.dc.dsw.domain.enums.SessionStatus;

import java.time.LocalDateTime;

public class Sessao {

    private Integer id;
    private String titulo;
    private Usuario testador;
    private Estrategia estrategia;
    private Projeto projeto;
    private String descricao;
    private SessionStatus status;
    private LocalDateTime criadoEm;
    private LocalDateTime inicioEm;
    private LocalDateTime finalizadoEm;

    public Sessao() {
        this.criadoEm = LocalDateTime.now();
        this.status = SessionStatus.CRIADA;
    }

    public Sessao(String titulo, Usuario testador, Estrategia estrategia, Projeto projeto, String descricao, SessionStatus status, LocalDateTime criadoEm, LocalDateTime inicioEm, LocalDateTime finalizadoEm) {}

    public Sessao(String titulo, Usuario testador, Estrategia estrategia, String descricao,
                  SessionStatus status, LocalDateTime criadoEm, LocalDateTime inicioEm, LocalDateTime finalizadoEm) {
        this.titulo = titulo;
        this.testador = testador;
        this.estrategia = estrategia;
        this.descricao = descricao;
        this.status = status;
        this.projeto = projeto;
        this.criadoEm = criadoEm;
        this.inicioEm = inicioEm;
        this.finalizadoEm = finalizadoEm;
    }

    public Sessao(Integer id, String titulo, Usuario testador, Estrategia estrategia, Projeto projeto, String descricao,
                  SessionStatus status, LocalDateTime criadoEm, LocalDateTime inicioEm, LocalDateTime finalizadoEm) {
        this(titulo, testador, estrategia, projeto, descricao, status, criadoEm, inicioEm, finalizadoEm);
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Usuario getTestador() {
        return testador;
    }
    public void setTestador(Usuario testador) {
        this.testador = testador;
    }

    public Estrategia getEstrategia() {
        return estrategia;
    }
    public void setEstrategia(Estrategia estrategia) {
        this.estrategia = estrategia;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public SessionStatus getStatus() {
        return status;
    }
    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getInicioEm() {
        return inicioEm;
    }
    public void setInicioEm(LocalDateTime inicioEm) {
        this.inicioEm = inicioEm;
    }

    public LocalDateTime getFinalizadoEm() {
        return finalizadoEm;
    }
    public void setFinalizadoEm(LocalDateTime finalizadoEm) {
        this.finalizadoEm = finalizadoEm;
    }

    public Projeto getProjeto() {
        return projeto;
    }

    public void setProjeto(Projeto projeto) {
        this.projeto = projeto;
    }
}
