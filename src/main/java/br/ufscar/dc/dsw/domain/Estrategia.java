package br.ufscar.dc.dsw.domain;

import java.util.List;
import java.util.ArrayList;

public class Estrategia {

    private Integer id;
    private String nome;
    private String descricao;
    private String dicas;
    private List<Imagem> imagens;

    public Estrategia() {
        this.imagens = new ArrayList<>();
    }

    public Estrategia(String nome, String descricao, String dicas, List<Imagem> imagens) {
        this.nome = nome;
        this.descricao = descricao;
        this.dicas = dicas;
        this.imagens = imagens != null ? imagens : new ArrayList<>();
    }

    public Estrategia(Integer id, String nome, String descricao, String dicas, List<Imagem> imagens) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.dicas = dicas;
        this.imagens = imagens != null ? imagens : new ArrayList<>();
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

    public String getDicas() {
        return dicas;
    }
    public void setDicas(String dicas) {
        this.dicas = dicas;
    }

    public List<Imagem> getImagens() {
        return imagens;
    }
    public void setImagens(List<Imagem> imagens) {
        this.imagens = imagens;
    }

    public void adicionarImagem(Imagem imagem) {
        if (this.imagens == null) {
            this.imagens = new ArrayList<>();
        }
        this.imagens.add(imagem);
    }
}
