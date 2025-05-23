package br.ufscar.dc.dsw.domain;

import br.ufscar.dc.dsw.domain.enums.Role;

public class Usuario {

    private Long id;
    private String nome;
    private String login;
    private String senha;
    private Role tipo;

    public Usuario(Long id) {
        this.id = id;
    }

    public Usuario(String nome, String login, String senha, Role tipo) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    public Usuario(Long id, String nome, String login, String senha, Role tipo) {
        this.id = id;
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLogin() {
        return login;
    }
    public void setLogin(String login) {
        this.login = login;
    }

    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public Role getTipo() {
        return tipo;
    }
    public void setTipo(Role tipo) {
        this.tipo = tipo;
    }
}
