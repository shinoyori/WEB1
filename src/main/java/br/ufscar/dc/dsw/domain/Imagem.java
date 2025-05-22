package br.ufscar.dc.dsw.domain;

public class Imagem {

    private Integer id;
    private String url;
    private String descricao;

    public Imagem() {}

    public Imagem(String url, String descricao) {
        this.url = url;
        this.descricao = descricao;
    }

    public Imagem(Integer id, String url, String descricao) {
        this.id = id;
        this.url = url;
        this.descricao = descricao;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
