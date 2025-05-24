package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.domain.Projeto;
import br.ufscar.dc.dsw.domain.Sessao;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.domain.enums.Role;
import br.ufscar.dc.dsw.domain.enums.SessionStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessaoDAO extends GenericDAO {

    // Dependencias de outros DAOS pra carregar relações
    private final UsuarioDAO usuarioDAO;
    private final EstrategiaDAO estrategiaDAO;
    private final ProjetoDAO projetoDAO;

    public SessaoDAO() {
        super();
        this.usuarioDAO = new UsuarioDAO();
        this.estrategiaDAO = new EstrategiaDAO();
        this.projetoDAO = new ProjetoDAO();
    }

    public void insert(Sessao sessao) {
        String sql = "INSERT INTO Sessao (titulo, descricao, testador_id, estrategia_id, projeto_id, status, criadoEm, inicioEm, finalizadoEm) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, sessao.getTitulo());
            ps.setString(2, sessao.getDescricao());
            ps.setLong(3, sessao.getTestador().getId());
            ps.setInt(4, sessao.getEstrategia().getId());
            ps.setInt(5, sessao.getProjeto().getId());
            ps.setString(6, sessao.getStatus().name());
            ps.setTimestamp(7, Timestamp.valueOf(sessao.getCriadoEm()));

            if (sessao.getInicioEm() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(sessao.getInicioEm()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }
            if (sessao.getFinalizadoEm() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(sessao.getFinalizadoEm()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                sessao.setId(rs.getInt(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error inserting sessao: " + e.getMessage(), e);
        }
    }

    public void update(Sessao sessao) {
        String sql = "UPDATE Sessao SET titulo = ?, descricao = ?, testador_id = ?, estrategia_id = ?, projeto_id = ?, " +
                "status = ?, criadoEm = ?, inicioEm = ?, finalizadoEm = ? WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sessao.getTitulo());
            ps.setString(2, sessao.getDescricao());
            ps.setLong(3, sessao.getTestador().getId());
            ps.setInt(4, sessao.getEstrategia().getId());
            ps.setInt(5, sessao.getProjeto().getId());
            ps.setString(6, sessao.getStatus().name());
            ps.setTimestamp(7, Timestamp.valueOf(sessao.getCriadoEm()));

            if (sessao.getInicioEm() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(sessao.getInicioEm()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }
            if (sessao.getFinalizadoEm() != null) {
                ps.setTimestamp(9, Timestamp.valueOf(sessao.getFinalizadoEm()));
            } else {
                ps.setNull(9, Types.TIMESTAMP);
            }
            ps.setInt(10, sessao.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error updating sessao: " + e.getMessage(), e);
        }
    }

    public Sessao get(int id) {
        Sessao sessao = null;
        String sql = "SELECT * FROM Sessao WHERE id = ?";

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                sessao = mapResultSetToSessao(rs);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching sessao by ID: " + e.getMessage(), e);
        }
        return sessao;
    }

    public List<Sessao> getAllByProjetoId(int projetoId, String sortBy, String sortOrder) {
        List<Sessao> listaSessoes = new ArrayList<>();
        String orderByClause = " ORDER BY s.criadoEm DESC"; 
        if (sortBy != null && !sortBy.isEmpty()) {
            String column = "s.criadoEm"; 
            if ("titulo".equalsIgnoreCase(sortBy)) {
                column = "s.titulo";
            } else if ("status".equalsIgnoreCase(sortBy)) {
                column = "s.status";
            } else if ("testador".equalsIgnoreCase(sortBy)) { 
                column = "u.nome";
            }
           
            String order = "ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder) ? sortOrder.toUpperCase() : "DESC";

            if ("u.nome".equals(column)) { 
                orderByClause = " ORDER BY " + column + " " + order;
            } else {
                orderByClause = " ORDER BY s." + column.substring(2) + " " + order; 
            }
        }

        
        String sql = "SELECT s.* FROM Sessao s ";
        if ("u.nome".equals(orderByClause.split(" ")[2])) { 
            sql += "JOIN Usuario u ON s.testador_id = u.id ";
        }
        sql += "WHERE s.projeto_id = ?" + orderByClause;


        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, projetoId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaSessoes.add(mapResultSetToSessao(rs));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching sessoes by projetoId: " + e.getMessage(), e);
        }
        return listaSessoes;
    }


    public List<Sessao> getAllByTestadorId(long testadorId, String sortBy, String sortOrder) {
        List<Sessao> listaSessoes = new ArrayList<>();
        String orderByClause = " ORDER BY criadoEm DESC"; // Default

        String sql = "SELECT * FROM Sessao WHERE testador_id = ?" + orderByClause;
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, testadorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaSessoes.add(mapResultSetToSessao(rs));
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching sessoes by testadorId: " + e.getMessage(), e);
        }
        return listaSessoes;
    }

    public void delete(int id) {
        String sql = "DELETE FROM Sessao WHERE id = ?";
        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting sessao: " + e.getMessage(), e);
        }
    }

    
    private Sessao mapResultSetToSessao(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String titulo = rs.getString("titulo");
        String descricaoSessao = rs.getString("descricao");
         // Carregamento de relacionamentos
        long testadorId = rs.getLong("testador_id");
        Usuario testador = usuarioDAO.get(testadorId); 

        int estrategiaId = rs.getInt("estrategia_id");
        Estrategia estrategia = estrategiaDAO.get(estrategiaId); 

        int projetoId = rs.getInt("projeto_id");
        Projeto projeto = projetoDAO.get(projetoId); 

          // Controle do ciclo de vida
        SessionStatus status = SessionStatus.valueOf(rs.getString("status"));
        LocalDateTime criadoEm = rs.getTimestamp("criadoEm").toLocalDateTime();

        Timestamp inicioEmTs = rs.getTimestamp("inicioEm");
        LocalDateTime inicioEm = (inicioEmTs != null) ? inicioEmTs.toLocalDateTime() : null;

        Timestamp finalizadoEmTs = rs.getTimestamp("finalizadoEm");
        LocalDateTime finalizadoEm = (finalizadoEmTs != null) ? finalizadoEmTs.toLocalDateTime() : null;

        return new Sessao(id, titulo, testador, estrategia, projeto, descricaoSessao,
                status, criadoEm, inicioEm, finalizadoEm);
    }
}
