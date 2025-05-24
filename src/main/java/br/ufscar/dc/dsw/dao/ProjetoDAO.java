package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.Projeto;
import br.ufscar.dc.dsw.domain.Usuario;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ProjetoDAO extends GenericDAO {

    public void insert(Projeto projeto) {
        String sqlProjeto = "INSERT INTO Projeto (nome, descricao, criadoEm) VALUES (?, ?, ?)";
        String sqlProjetoUsuario = "INSERT INTO Projeto_Usuario (projeto_id, usuario_id) VALUES (?, ?)";

        try (Connection conn = this.getConnection();
             PreparedStatement statementProjeto = conn.prepareStatement(sqlProjeto, Statement.RETURN_GENERATED_KEYS)) {

            conn.setAutoCommit(false); // Começa transação

            statementProjeto.setString(1, projeto.getNome());
            statementProjeto.setString(2, projeto.getDescricao());
            statementProjeto.setTimestamp(3, Timestamp.valueOf(projeto.getCriadoEm()));
            statementProjeto.executeUpdate();

             // Recupera o ID gerado automaticamente
            ResultSet rs = statementProjeto.getGeneratedKeys();
            if (rs.next()) {
                projeto.setId(rs.getInt(1));
            } else {
                conn.rollback(); // Rollback se o ID do projeto nao for gerado
                throw new SQLException("Creating project failed, no ID obtained.");
            }

            // Associa usuários ao projeto na tabela Projeto_Usuario
            if (projeto.getUsuarios() != null && !projeto.getUsuarios().isEmpty()) {
                try (PreparedStatement statementProjetoUsuario = conn.prepareStatement(sqlProjetoUsuario)) {
                    for (Usuario usuario : projeto.getUsuarios()) {
                        statementProjetoUsuario.setInt(1, projeto.getId());
                        statementProjetoUsuario.setLong(2, usuario.getId());
                        statementProjetoUsuario.addBatch();
                    }
                    statementProjetoUsuario.executeBatch();
                }
            }
            conn.commit(); // Confirma transação
        } catch (SQLException e) {
 
            throw new RuntimeException("Error inserting project: " + e.getMessage(), e);
        }
    }
    
    // Método para obter todos projetos ordenados
    public List<Projeto> getAll(String sortBy, String sortOrder) {
        List<Projeto> listaProjetos = new ArrayList<>();
    
        String orderByClause = " ORDER BY p.nome ASC"; // Ordenação padrão
         // Valida e constrói cláusula ORDER BY dinâmica
        if (sortBy != null && !sortBy.isEmpty()) {
            String column = "p.nome"; 
            if ("nome".equalsIgnoreCase(sortBy)) {
                column = "p.nome";
            } else if ("criadoEm".equalsIgnoreCase(sortBy)) {
                column = "p.criadoEm";
            }
             // Valida direção da ordenação
            String order = "ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder) ? sortOrder.toUpperCase() : "ASC";
            orderByClause = " ORDER BY " + column + " " + order;
        }

        String sql = "SELECT p.id as projeto_id, p.nome as projeto_nome, p.descricao as projeto_descricao, p.criadoEm as projeto_criadoEm " +
                "FROM Projeto p" + orderByClause;

        try (Connection conn = this.getConnection();
             Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            // Processa cada registro retornado
            while (resultSet.next()) {
                int id = resultSet.getInt("projeto_id");
                String nome = resultSet.getString("projeto_nome");
                String descricao = resultSet.getString("projeto_descricao");
                LocalDateTime criadoEm = resultSet.getTimestamp("projeto_criadoEm").toLocalDateTime();

                 // Cria projeto sem lista de usuários para otimização
                Projeto projeto = new Projeto(id, nome, descricao, criadoEm, new ArrayList<>()); 
                listaProjetos.add(projeto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all projects: " + e.getMessage(), e);
        }
        return listaProjetos;
    }

    public Projeto get(int id) {
        Projeto projeto = null;
        String sqlProjeto = "SELECT * FROM Projeto WHERE id = ?";
        String sqlUsuarios = "SELECT u.id, u.nome, u.login, u.senha, u.role " +
                "FROM Usuario u JOIN Projeto_Usuario pu ON u.id = pu.usuario_id " +
                "WHERE pu.projeto_id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement statementProjeto = conn.prepareStatement(sqlProjeto)) {

            statementProjeto.setInt(1, id);
            try (ResultSet rsProjeto = statementProjeto.executeQuery()) {
                if (rsProjeto.next()) {
                    String nome = rsProjeto.getString("nome");
                    String descricao = rsProjeto.getString("descricao");
                    LocalDateTime criadoEm = rsProjeto.getTimestamp("criadoEm").toLocalDateTime();
                    List<Usuario> usuarios = new ArrayList<>();

                    try (PreparedStatement statementUsuarios = conn.prepareStatement(sqlUsuarios)) {
                        statementUsuarios.setInt(1, id);
                        try (ResultSet rsUsuarios = statementUsuarios.executeQuery()) {
                            while (rsUsuarios.next()) {
                                usuarios.add(new Usuario(
                                        rsUsuarios.getLong("id"),
                                        rsUsuarios.getString("nome"),
                                        rsUsuarios.getString("login"),
                                        rsUsuarios.getString("senha"), 
                                        br.ufscar.dc.dsw.domain.enums.Role.valueOf(rsUsuarios.getString("role"))
                                ));
                            }
                        }
                    }
                    projeto = new Projeto(id, nome, descricao, criadoEm, usuarios);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching project by ID: " + e.getMessage(), e);
        }
        return projeto;
    }

    public void update(Projeto projeto) {
        String sqlProjeto = "UPDATE Projeto SET nome = ?, descricao = ? WHERE id = ?";
        String sqlDeleteUsuarios = "DELETE FROM Projeto_Usuario WHERE projeto_id = ?";
        String sqlInsertUsuario = "INSERT INTO Projeto_Usuario (projeto_id, usuario_id) VALUES (?, ?)";

        try (Connection conn = this.getConnection()) {
            conn.setAutoCommit(false); // Começa transação

            try (PreparedStatement psProjeto = conn.prepareStatement(sqlProjeto)) {
                psProjeto.setString(1, projeto.getNome());
                psProjeto.setString(2, projeto.getDescricao());
                psProjeto.setInt(3, projeto.getId());
                psProjeto.executeUpdate();
            }

            try (PreparedStatement psDeleteUsuarios = conn.prepareStatement(sqlDeleteUsuarios)) {
                psDeleteUsuarios.setInt(1, projeto.getId());
                psDeleteUsuarios.executeUpdate();
            }

            if (projeto.getUsuarios() != null && !projeto.getUsuarios().isEmpty()) {
                try (PreparedStatement psInsertUsuario = conn.prepareStatement(sqlInsertUsuario)) {
                    for (Usuario usuario : projeto.getUsuarios()) {
                        psInsertUsuario.setInt(1, projeto.getId());
                        psInsertUsuario.setLong(2, usuario.getId());
                        psInsertUsuario.addBatch();
                    }
                    psInsertUsuario.executeBatch();
                }
            }
            conn.commit(); // Confirma transação
        } catch (SQLException e) {
            throw new RuntimeException("Error updating project: " + e.getMessage(), e);
        }
    }

    // Método para excluir um projeto
    public void delete(int id) {
        String sql = "DELETE FROM Projeto WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement statement = conn.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting project: " + e.getMessage(), e);
        }
    }

    // Método para obter projetos associados a um usuário específico
    public List<Projeto> getProjetosByUsuarioId(Long usuarioId) {
        List<Projeto> listaProjetos = new ArrayList<>();
        String sql = "SELECT p.id, p.nome, p.descricao, p.criadoEm " +
                "FROM Projeto p " +
                "JOIN Projeto_Usuario pu ON p.id = pu.projeto_id " +
                "WHERE pu.usuario_id = ? " +
                "ORDER BY p.nome ASC";

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, usuarioId);
            ResultSet rs = ps.executeQuery();

             // Processa cada projeto do usuário
            while (rs.next()) {
                int id = rs.getInt("id");
                String nome = rs.getString("nome");
                String descricao = rs.getString("descricao");
                LocalDateTime criadoEm = rs.getTimestamp("criadoEm").toLocalDateTime();
                Projeto projeto = new Projeto(id, nome, descricao, criadoEm, new ArrayList<>()); 
                listaProjetos.add(projeto);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching projetos by usuarioId: " + e.getMessage(), e);
        }
        return listaProjetos;
    }
}
