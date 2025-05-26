//pacote onde essa classe está localizada
package br.ufscar.dc.dsw.dao;

//importa a classe Estrategia do pacote domain
import br.ufscar.dc.dsw.domain.Estrategia;
//importa a classe Imagem do pacote domain
import br.ufscar.dc.dsw.domain.Imagem;

//importa classes necessárias para trabalhar com banco de dados
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//declara a classe EstrategiaDAO que herda de GenericDAO
public class EstrategiaDAO extends GenericDAO {

    //função que insere uma nova estrategia e suas imagens
    public void insert(Estrategia estrategia) {
        //sql para inserir dados na tabela Estrategia
        String sqlEstrategia = "INSERT INTO Estrategia (nome, descricao, dicas) VALUES (?, ?, ?)";
        //sql para inserir dados na tabela Imagem associando à estratégia
        String sqlImagem = "INSERT INTO Imagem (estrategia_id, url, descricao) VALUES (?, ?, ?)";

        //obtém conexão com o banco de dados
        try (Connection conn = this.getConnection()) {
            conn.setAutoCommit(false); //desativa o commit automático para permitir transações

            //prepara o comando para inserir a estratégia e recuperar a chave gerada
            try (PreparedStatement psEstrategia = conn.prepareStatement(sqlEstrategia, Statement.RETURN_GENERATED_KEYS)) {
                psEstrategia.setString(1, estrategia.getNome()); //define o primeiro parâmetro: nome
                psEstrategia.setString(2, estrategia.getDescricao()); //define o segundo parâmetro: descrição
                psEstrategia.setString(3, estrategia.getDicas()); //define o terceiro parâmetro: dicas
                psEstrategia.executeUpdate(); //executa o insert

                //obtém a chave primária gerada automaticamente
                ResultSet rs = psEstrategia.getGeneratedKeys();
                if (rs.next()) {
                    estrategia.setId(rs.getInt(1)); //define o id da estratégia com o valor gerado
                } else {
                    conn.rollback(); //desfaz a transação em caso de falha
                    throw new SQLException("Creating estrategia failed, no ID obtained."); //lança exceção
                }
            }

            //caso existam imagens associadas, insere cada uma
            if (estrategia.getImagens() != null && !estrategia.getImagens().isEmpty()) {
                try (PreparedStatement psImagem = conn.prepareStatement(sqlImagem)) {
                    for (Imagem imagem : estrategia.getImagens()) {
                        psImagem.setInt(1, estrategia.getId()); //define o id da estratégia
                        psImagem.setString(2, imagem.getUrl()); //define a url da imagem
                        psImagem.setString(3, imagem.getDescricao()); //define a descrição da imagem
                        psImagem.addBatch(); //adiciona à batch de execução
                    }
                    psImagem.executeBatch(); //executa todas as inserções em lote
                }
            }
            conn.commit(); //confirma a transação
        } catch (SQLException e) {
            //para um aplicativo real é preciso verificar se a conexão (conn) não está nula ou fechada antes de fazer rollback
            //então: { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Error inserting estrategia: " + e.getMessage(), e); //relança a exceção como runtime
        }
    }

    //função que recupera todas as estratégias e não carrega as imagens por padrão
    public List<Estrategia> getAll() {
        List<Estrategia> listaEstrategias = new ArrayList<>(); //cria lista para armazenar resultados

        //consulta SQL para recuperar todas as estratégias ordenadas por nome
        String sql = "SELECT id, nome, descricao, dicas FROM Estrategia ORDER BY nome ASC";

        //tenta obter conexão, criar comando e executar a consulta
        try (Connection conn = this.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            //itera sobre os resultados
            while (resultSet.next()) {
                int id = resultSet.getInt("id"); //obtém o id da estratégia
                String nome = resultSet.getString("nome"); //obtém o nome
                String descricao = resultSet.getString("descricao"); //obtém a descrição
                String dicas = resultSet.getString("dicas"); //obtém as dicas

                //cria objeto estratégia com lista de imagens vazia para otimizar desempenho
                Estrategia estrategia = new Estrategia(id, nome, descricao, dicas, new ArrayList<>());
                listaEstrategias.add(estrategia); //adiciona à lista
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all estrategias: " + e.getMessage(), e); //relança exceção
        }
        return listaEstrategias; //retorna a lista com todas as estratégias
    }

    //função que recupera uma estratégia específica com todas as suas imagens
    public Estrategia get(int id) {
        Estrategia estrategia = null; //inicializa a variável
        String sqlEstrategia = "SELECT * FROM Estrategia WHERE id = ?"; //sql para obter estratégia
        String sqlImagens = "SELECT id as img_id, url, descricao as img_descricao FROM Imagem WHERE estrategia_id = ?"; //sql para obter imagens da estratégia

        //obtém conexão e prepara o comando
        try (Connection conn = this.getConnection();
             PreparedStatement psEstrategia = conn.prepareStatement(sqlEstrategia)) {

            psEstrategia.setInt(1, id); //define o id no comando
            try (ResultSet rsEstrategia = psEstrategia.executeQuery()) {
                if (rsEstrategia.next()) {
                    String nome = rsEstrategia.getString("nome"); //pega o nome
                    String descricao = rsEstrategia.getString("descricao"); //pega a descrição
                    String dicas = rsEstrategia.getString("dicas"); //pega as dicas
                    List<Imagem> imagens = new ArrayList<>(); //lista para imagens

                    //executa a consulta de imagens associadas
                    try (PreparedStatement psImagens = conn.prepareStatement(sqlImagens)) {
                        psImagens.setInt(1, id); //define o id da estratégia
                        try (ResultSet rsImagens = psImagens.executeQuery()) {
                            while (rsImagens.next()) {
                                imagens.add(new Imagem(
                                        rsImagens.getInt("img_id"),
                                        rsImagens.getString("url"),
                                        rsImagens.getString("img_descricao")
                                ));
                            }
                        }
                    }
                    estrategia = new Estrategia(id, nome, descricao, dicas, imagens); //cria a estratégia completa
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching estrategia by ID: " + e.getMessage(), e); //relança exceção
        }
        return estrategia; //retorna a estratégia encontrada
    }

    //função que atualiza uma estrategia e as suas imagens
    public void update(Estrategia estrategia) {
        //sql para atualizar a estratégia
        String sqlEstrategia = "UPDATE Estrategia SET nome = ?, descricao = ?, dicas = ? WHERE id = ?";
        //sql para apagar imagens antigas associadas à estratégia
        String sqlDeleteImagens = "DELETE FROM Imagem WHERE estrategia_id = ?";
        //sql para inserir novas imagens
        String sqlInsertImagem = "INSERT INTO Imagem (estrategia_id, url, descricao) VALUES (?, ?, ?)";

        try (Connection conn = this.getConnection()) {
            conn.setAutoCommit(false); //começa a transação

            //atualiza os dados da estratégia
            try (PreparedStatement psEstrategia = conn.prepareStatement(sqlEstrategia)) {
                psEstrategia.setString(1, estrategia.getNome());
                psEstrategia.setString(2, estrategia.getDescricao());
                psEstrategia.setString(3, estrategia.getDicas());
                psEstrategia.setInt(4, estrategia.getId());
                psEstrategia.executeUpdate();
            }

            //remove as imagens antigas associadas à estratégia
            try (PreparedStatement psDeleteImagens = conn.prepareStatement(sqlDeleteImagens)) {
                psDeleteImagens.setInt(1, estrategia.getId());
                psDeleteImagens.executeUpdate();
            }

            //insere as novas imagens associadas à estratégia
            if (estrategia.getImagens() != null && !estrategia.getImagens().isEmpty()) {
                try (PreparedStatement psInsertImagem = conn.prepareStatement(sqlInsertImagem)) {
                    for (Imagem imagem : estrategia.getImagens()) {
                        psInsertImagem.setInt(1, estrategia.getId());
                        psInsertImagem.setString(2, imagem.getUrl());
                        psInsertImagem.setString(3, imagem.getDescricao());
                        psInsertImagem.addBatch(); //adiciona à batch
                    }
                    psInsertImagem.executeBatch(); //executa todas de uma vez
                }
            }
            conn.commit(); //confirma a transação
        } catch (SQLException e) {
            throw new RuntimeException("Error updating estrategia: " + e.getMessage(), e); //relança exceção
        }
    }

    //função que remove uma estratégia utilizando cascade para as imagens
    public void delete(int id) {
        //sql para deletar a estratégia com base no id
        String sql = "DELETE FROM Estrategia WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id); //define o id da estratégia a ser deletada
            statement.executeUpdate(); //executa a remoção
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting estrategia: " + e.getMessage(), e); //relança exceção
        }
    }
}

