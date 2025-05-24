package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.domain.Imagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstrategiaDAO extends GenericDAO {
    // metodo para inserir uma nova estrategia e suas imagens
    public void insert(Estrategia estrategia) {
        String sqlEstrategia = "INSERT INTO Estrategia (nome, descricao, dicas) VALUES (?, ?, ?)";
        String sqlImagem = "INSERT INTO Imagem (estrategia_id, url, descricao) VALUES (?, ?, ?)";

        try (Connection conn = this.getConnection()) {
            conn.setAutoCommit(false); // Começa

            try (PreparedStatement psEstrategia = conn.prepareStatement(sqlEstrategia, Statement.RETURN_GENERATED_KEYS)) {
                psEstrategia.setString(1, estrategia.getNome());
                psEstrategia.setString(2, estrategia.getDescricao());
                psEstrategia.setString(3, estrategia.getDicas());
                psEstrategia.executeUpdate();

                ResultSet rs = psEstrategia.getGeneratedKeys();
                if (rs.next()) {
                    estrategia.setId(rs.getInt(1));
                } else {
                    conn.rollback();
                    throw new SQLException("Creating estrategia failed, no ID obtained.");
                }
            }
             // Insere as imagens associadas (se houver)
            if (estrategia.getImagens() != null && !estrategia.getImagens().isEmpty()) {
                try (PreparedStatement psImagem = conn.prepareStatement(sqlImagem)) {
                    for (Imagem imagem : estrategia.getImagens()) {
                        psImagem.setInt(1, estrategia.getId());
                        psImagem.setString(2, imagem.getUrl());
                        psImagem.setString(3, imagem.getDescricao());
                        psImagem.addBatch();
                    }
                    psImagem.executeBatch();
                }
            }
            conn.commit(); // Confirma a transação
        } catch (SQLException e) {
            // In a real app, check if conn is null or closed before rollback
            // try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Error inserting estrategia: " + e.getMessage(), e);
        }
    }

    public List<Estrategia> getAll() {
        // Método para obter todas as estratégias (sem carregar imagens por padrão)
        List<Estrategia> listaEstrategias = new ArrayList<>();
    
        String sql = "SELECT id, nome, descricao, dicas FROM Estrategia ORDER BY nome ASC";

        try (Connection conn = this.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String descricao = resultSet.getString("descricao");
                String dicas = resultSet.getString("dicas");

                // Cria estratégia com lista vazia de imagens para otimização
                Estrategia estrategia = new Estrategia(id, nome, descricao, dicas, new ArrayList<>());
                listaEstrategias.add(estrategia);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all estrategias: " + e.getMessage(), e);
        }
        return listaEstrategias;
    }
    // Método para obter uma estratégia específica com todas suas imagens
    public Estrategia get(int id) {
        Estrategia estrategia = null;
        String sqlEstrategia = "SELECT * FROM Estrategia WHERE id = ?";
        String sqlImagens = "SELECT id as img_id, url, descricao as img_descricao FROM Imagem WHERE estrategia_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement psEstrategia = conn.prepareStatement(sqlEstrategia)) {

            psEstrategia.setInt(1, id);
            try (ResultSet rsEstrategia = psEstrategia.executeQuery()) {
                if (rsEstrategia.next()) {
                    String nome = rsEstrategia.getString("nome");
                    String descricao = rsEstrategia.getString("descricao");
                    String dicas = rsEstrategia.getString("dicas");
                    List<Imagem> imagens = new ArrayList<>();

                    try (PreparedStatement psImagens = conn.prepareStatement(sqlImagens)) {
                        psImagens.setInt(1, id);
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
                    estrategia = new Estrategia(id, nome, descricao, dicas, imagens);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching estrategia by ID: " + e.getMessage(), e);
        }
        return estrategia;
    }

    // Método para atualizar uma estratégia e suas imagens
    public void update(Estrategia estrategia) {
        String sqlEstrategia = "UPDATE Estrategia SET nome = ?, descricao = ?, dicas = ? WHERE id = ?";
    
        String sqlDeleteImagens = "DELETE FROM Imagem WHERE estrategia_id = ?";
        String sqlInsertImagem = "INSERT INTO Imagem (estrategia_id, url, descricao) VALUES (?, ?, ?)";

        try (Connection conn = this.getConnection()) {
            conn.setAutoCommit(false); // Começa a transaçãoo

             // Atualiza dados da estratégia
            try (PreparedStatement psEstrategia = conn.prepareStatement(sqlEstrategia)) {
                psEstrategia.setString(1, estrategia.getNome());
                psEstrategia.setString(2, estrategia.getDescricao());
                psEstrategia.setString(3, estrategia.getDicas());
                psEstrategia.setInt(4, estrategia.getId());
                psEstrategia.executeUpdate();
            }

             // Remove imagens antigas
            try (PreparedStatement psDeleteImagens = conn.prepareStatement(sqlDeleteImagens)) {
                psDeleteImagens.setInt(1, estrategia.getId());
                psDeleteImagens.executeUpdate();
            }

            // Insere novas imagens
            if (estrategia.getImagens() != null && !estrategia.getImagens().isEmpty()) {
                try (PreparedStatement psInsertImagem = conn.prepareStatement(sqlInsertImagem)) {
                    for (Imagem imagem : estrategia.getImagens()) {
                        psInsertImagem.setInt(1, estrategia.getId());
                        psInsertImagem.setString(2, imagem.getUrl());
                        psInsertImagem.setString(3, imagem.getDescricao());
                        psInsertImagem.addBatch();
                    }
                    psInsertImagem.executeBatch();
                }
            }
            conn.commit(); // confirma transação
        } catch (SQLException e) {
        
            throw new RuntimeException("Error updating estrategia: " + e.getMessage(), e);
        }
    }

    // Método para excluir uma estratégia (usa Cascade para imagens)
    public void delete(int id) {
        String sql = "DELETE FROM Estrategia WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting estrategia: " + e.getMessage(), e);
        }
    }
}
