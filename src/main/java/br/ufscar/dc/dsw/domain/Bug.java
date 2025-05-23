package br.ufscar.dc.dsw.domain;

import br.ufscar.dc.dsw.domain.enums.Severity;

import java.time.LocalDateTime;

public class Bug {

    private Integer id;
    private Sessao sessao;
    private Severity severidade;
    private String descricao;
    private LocalDateTime timestamp;

    public Bug() {
        this.timestamp = LocalDateTime.now();
    }

    public Bug(Sessao sessao, Severity severidade, String descricao) {
        this.sessao = sessao;
        this.severidade = severidade;
        this.descricao = descricao;
        this.timestamp = LocalDateTime.now();
    }

    public Bug(Integer id, Sessao sessao, Severity severidade, String descricao, LocalDateTime timestamp) {
        this.id = id;
        this.sessao = sessao;
        this.severidade = severidade;
        this.descricao = descricao;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Sessao getSessao() {
        return sessao;
    }
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
    }

    public Severity getSeveridade() {
        return severidade;
    }
    public void setSeveridade(Severity severidade) {
        this.severidade = severidade;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
