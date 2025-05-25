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

    // Default constructor
    public Sessao() {
        this.criadoEm = LocalDateTime.now();
        this.status = SessionStatus.CRIADA; // Default status
    }

    // This is the main constructor for setting all fields except ID.
    // The DAO's 10-arg constructor will call this one.
    // Ensure parameter name for 'descricao' matches what the 10-arg constructor passes.
    public Sessao(String titulo, Usuario testador, Estrategia estrategia, Projeto projeto, String descricao,
                  SessionStatus status, LocalDateTime criadoEm, LocalDateTime inicioEm, LocalDateTime finalizadoEm) {
        this.titulo = titulo;
        this.testador = testador;
        this.estrategia = estrategia;
        this.projeto = projeto;
        this.descricao = descricao;
        this.status = status;
        this.criadoEm = (criadoEm != null) ? criadoEm : LocalDateTime.now(); // Ensure criadoEm is set
        this.inicioEm = inicioEm;
        this.finalizadoEm = finalizadoEm;
        if (this.status == null) { // Default status if not provided
            this.status = SessionStatus.CRIADA;
        }
    }

    // This is the constructor your DAO calls (10 arguments)
    public Sessao(Integer id, String titulo, Usuario testador, Estrategia estrategia, Projeto projeto, String descricaoSessao, // DAO uses 'descricaoSessao'
                  SessionStatus status, LocalDateTime criadoEm, LocalDateTime inicioEm, LocalDateTime finalizadoEm) {
        // Call the 9-argument constructor above, passing 'descricaoSessao' for the 'descricao' parameter
        this(titulo, testador, estrategia, projeto, descricaoSessao, status, criadoEm, inicioEm, finalizadoEm);
        this.id = id;
    }

    // Remove or fix any other constructors that cause duplication.
    // For instance, the 8-argument constructor you had:
    // public Sessao(String titulo, Usuario testador, Estrategia estrategia, String descricao,
    //               SessionStatus status, LocalDateTime criadoEm, LocalDateTime inicioEm, LocalDateTime finalizadoEm) { ... }
    // This one is different because it's missing 'Projeto projeto'. If you need it, ensure it's correct.
    // If it was intended to be one of the conflicting ones, it needs to be addressed.

    // Getters and Setters
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