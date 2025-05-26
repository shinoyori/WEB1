package br.ufscar.dc.dsw.domain; //define o pacote ao qual esta classe pertence

import java.util.List; //importa a interface List para manipulação de listas genéricas
import java.util.ArrayList; //importa a implementação ArrayList da interface List

public class Estrategia { //declara a classe pública Estrategia

    private Integer id; //atributo que armazena o identificador único da estratégia
    private String nome; //atributo que armazena o nome da estratégia
    private String descricao; //atributo que armazena a descrição da estratégia
    private String dicas; //atributo que armazena as dicas da estratégia
    private List<Imagem> imagens; //atributo que armazena a lista de imagens associadas à estratégia

    public Estrategia() { //construtor padrão sem parâmetros
        this.imagens = new ArrayList<>(); //inicializa a lista de imagens como uma lista vazia
    }

    public Estrategia(String nome, String descricao, String dicas, List<Imagem> imagens) { //construtor com todos os atributos exceto o id
        this.nome = nome; //atribui o nome passado como parâmetro ao atributo da classe
        this.descricao = descricao; //atribui a descrição passada ao atributo da classe
        this.dicas = dicas; //atribui as dicas passadas ao atributo da classe
        this.imagens = imagens != null ? imagens : new ArrayList<>(); //atribui a lista de imagens ou inicializa uma nova vazia se for nula
    }

    public Estrategia(Integer id, String nome, String descricao, String dicas, List<Imagem> imagens) { //construtor com todos os atributos, incluindo o id
        this.id = id; //atribui o id passado ao atributo da classe
        this.nome = nome; //atribui o nome passado ao atributo da classe
        this.descricao = descricao; //atribui a descrição passada ao atributo da classe
        this.dicas = dicas; //atribui as dicas passadas ao atributo da classe
        this.imagens = imagens != null ? imagens : new ArrayList<>(); //atribui a lista de imagens ou inicializa uma nova vazia se for nula
    }

    public Integer getId() { //método getter para o atributo id
        return id; //retorna o valor atual do id
    }

    public void setId(Integer id) { //método setter para o atributo id
        this.id = id; //define um novo valor para o id
    }

    public String getNome() { //método getter para o atributo nome
        return nome; //retorna o valor atual do nome
    }

    public void setNome(String nome) { //método setter para o atributo nome
        this.nome = nome; //define um novo valor para o nome
    }

    public String getDescricao() { //método getter para o atributo descricao
        return descricao; //retorna o valor atual da descrição
    }

    public void setDescricao(String descricao) { //método setter para o atributo descricao
        this.descricao = descricao; //define um novo valor para a descrição
    }

    public String getDicas() { //método getter para o atributo dicas
        return dicas; //retorna o valor atual das dicas
    }

    public void setDicas(String dicas) { //método setter para o atributo dicas
        this.dicas = dicas; //define um novo valor para as dicas
    }

    public List<Imagem> getImagens() { //método getter para o atributo imagens
        return imagens; //retorna a lista de imagens associadas à estratégia
    }

    public void setImagens(List<Imagem> imagens) { //método setter para o atributo imagens
        this.imagens = imagens; //define uma nova lista de imagens para a estratégia
    }

    public void adicionarImagem(Imagem imagem) { //método que adiciona uma imagem à lista de imagens
        if (this.imagens == null) { //verifica se a lista de imagens ainda não foi inicializada
            this.imagens = new ArrayList<>(); //inicializa a lista de imagens como uma nova lista vazia
        }
        this.imagens.add(imagem); //adiciona a imagem passada à lista de imagens
    }
}
