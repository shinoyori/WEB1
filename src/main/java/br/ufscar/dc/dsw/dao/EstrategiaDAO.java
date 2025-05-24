package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.domain.Imagem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstrategiaDAO extends GenericDAO {

    public void insert(Estrategia estrategia) {
        String sqlEstrategia = "INSERT INTO Estrategia (nome, descricao, dicas) VALUES (?, ?, ?)";
        String sqlImagem = "INSERT INTO Imagem (estrategia_id, url, descricao) VALUES (?, ?, ?)";

        try (Connection conn = this.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

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
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            // In a real app, check if conn is null or closed before rollback
            // try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Error inserting estrategia: " + e.getMessage(), e);
        }
    }

    public List<Estrategia> getAll() {
        List<Estrategia> listaEstrategias = new ArrayList<>();
        // For simplicity, this getAll won't fetch all images for all strategies initially.
        // Images can be loaded when viewing a single strategy or on demand.
        // Or, fetch only the first/primary image if desired for the list view.
        String sql = "SELECT id, nome, descricao, dicas FROM Estrategia ORDER BY nome ASC";

        try (Connection conn = this.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nome = resultSet.getString("nome");
                String descricao = resultSet.getString("descricao");
                String dicas = resultSet.getString("dicas");

                // Create Estrategia with an empty list of images for now for the general list
                Estrategia estrategia = new Estrategia(id, nome, descricao, dicas, new ArrayList<>());
                // Optionally, you could load a primary image here if needed for the list view.
                listaEstrategias.add(estrategia);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all estrategias: " + e.getMessage(), e);
        }
        return listaEstrategias;
    }

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

    public void update(Estrategia estrategia) {
        String sqlEstrategia = "UPDATE Estrategia SET nome = ?, descricao = ?, dicas = ? WHERE id = ?";
        // For images, a common approach: delete all existing images for this strategy and re-insert current ones.
        // This simplifies handling of added/removed/updated images.
        String sqlDeleteImagens = "DELETE FROM Imagem WHERE estrategia_id = ?";
        String sqlInsertImagem = "INSERT INTO Imagem (estrategia_id, url, descricao) VALUES (?, ?, ?)";

        try (Connection conn = this.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Update Estrategia details
            try (PreparedStatement psEstrategia = conn.prepareStatement(sqlEstrategia)) {
                psEstrategia.setString(1, estrategia.getNome());
                psEstrategia.setString(2, estrategia.getDescricao());
                psEstrategia.setString(3, estrategia.getDicas());
                psEstrategia.setInt(4, estrategia.getId());
                psEstrategia.executeUpdate();
            }

            // Delete old images
            try (PreparedStatement psDeleteImagens = conn.prepareStatement(sqlDeleteImagens)) {
                psDeleteImagens.setInt(1, estrategia.getId());
                psDeleteImagens.executeUpdate();
            }

            // Insert new/current images
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
            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            // try { if (conn != null) conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            throw new RuntimeException("Error updating estrategia: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        // ON DELETE CASCADE for Imagem table will handle associated images
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