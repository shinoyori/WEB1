package br.ufscar.dc.dsw.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Projeto {

    private Integer id;
    private String nome;
    private String descricao;
    private LocalDateTime criadoEm;
    private List<Usuario> usuarios;

    public Projeto() {
        this.usuarios = new ArrayList<>();
        this.criadoEm = LocalDateTime.now();
    }

    public Projeto(String nome, String descricao, List<Usuario> usuarios) {
        this.nome = nome;
        this.descricao = descricao;
        this.criadoEm = LocalDateTime.now();
        this.usuarios = usuarios != null ? usuarios : new ArrayList<>();
    }

    public Projeto(Integer id, String nome, String descricao, LocalDateTime criadoEm, List<Usuario> usuarios) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.criadoEm = criadoEm;
        this.usuarios = usuarios != null ? usuarios : new ArrayList<>();
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }
    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<Usuario> getUsuarios() {
        return usuarios;
    }
    public void setUsuarios(List<Usuario> usuarios) {
        this.usuarios = usuarios;
    }

    public void adicionarUsuario(Usuario usuario) {
        if (this.usuarios == null) {
            this.usuarios = new ArrayList<>();
        }
        this.usuarios.add(usuario);
    }
}
