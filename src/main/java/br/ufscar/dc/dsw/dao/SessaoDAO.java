package br.ufscar.dc.dsw.dao;

import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.domain.Projeto;
import br.ufscar.dc.dsw.domain.Sessao;
import br.ufscar.dc.dsw.domain.Usuario;
// Role enum is not directly used in this DAO, but SessionStatus is.
import br.ufscar.dc.dsw.domain.enums.SessionStatus;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessaoDAO extends GenericDAO {

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
            // Ensure sessao.getTestador(), getEstrategia(), getProjeto() are not null before calling getId()
            // This check should ideally happen in the controller or service layer before calling DAO.
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
            // It's good practice to close ResultSet, though try-with-resources handles PreparedStatement and Connection
            if (rs != null) rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching sessao by ID: " + e.getMessage(), e);
        }
        return sessao;
    }

    public List<Sessao> getAllByProjetoId(int projetoId, String sortBy, String sortOrder) {
        List<Sessao> listaSessoes = new ArrayList<>();

        String sqlSortByColumn = "s.criadoEm"; // Default sort column with alias
        boolean needsUserJoin = false;

        if (sortBy != null && !sortBy.isEmpty()) {
            if ("titulo".equalsIgnoreCase(sortBy)) {
                sqlSortByColumn = "s.titulo";
            } else if ("status".equalsIgnoreCase(sortBy)) {
                sqlSortByColumn = "s.status";
            } else if ("testador".equalsIgnoreCase(sortBy)) {
                sqlSortByColumn = "u.nome"; // User table alias 'u', column 'nome'
                needsUserJoin = true;
            }
            // Add other valid columns from Sessao table if needed, e.g., s.inicioEm
        }

        String sqlSortOrder = ("ASC".equalsIgnoreCase(sortOrder)) ? "ASC" : "DESC"; // Default to DESC if not ASC
        String orderByClause = " ORDER BY " + sqlSortByColumn + " " + sqlSortOrder;

        String sql = "SELECT s.* FROM Sessao s ";
        if (needsUserJoin) { // Join Usuario table if sorting by tester's name
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
            if (rs != null) rs.close();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching sessoes by projetoId: " + e.getMessage(), e);
        }
        return listaSessoes;
    }

    public List<Sessao> getAllByTestadorId(long testadorId, String sortBy, String sortOrder) {
        List<Sessao> listaSessoes = new ArrayList<>();

        // Corrected: Implement dynamic sorting if intended, or remove parameters
        String sqlSortByColumn = "criadoEm"; // Default sort column (no alias needed if only Sessao in FROM)
        // Assuming sortBy here refers to columns directly on the Sessao table for simplicity
        // If joining is needed for sorting (e.g. by project name), add join logic similar to getAllByProjetoId
        if (sortBy != null && !sortBy.isEmpty()) {
            if ("titulo".equalsIgnoreCase(sortBy)) {
                sqlSortByColumn = "titulo";
            } else if ("status".equalsIgnoreCase(sortBy)) {
                sqlSortByColumn = "status";
            }
            // Add other valid columns from Sessao table
        }
        String sqlSortOrder = ("ASC".equalsIgnoreCase(sortOrder)) ? "ASC" : "DESC";
        String orderByClause = " ORDER BY " + sqlSortByColumn + " " + sqlSortOrder;

        String sql = "SELECT * FROM Sessao WHERE testador_id = ?" + orderByClause;

        try (Connection conn = this.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, testadorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                listaSessoes.add(mapResultSetToSessao(rs));
            }
            if (rs != null) rs.close();
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
        System.out.println("[SessaoDAO.mapResultSetToSessao] Mapping new session..."); // DEBUG
        Integer id = rs.getInt("id");
        System.out.println("  Raw id from DB: " + id); // DEBUG

        String titulo = rs.getString("titulo");
        System.out.println("  Raw titulo from DB: " + titulo); // DEBUG

        String descricaoSessao = rs.getString("descricao");
        // System.out.println("  Raw descricao from DB: " + descricaoSessao); // DEBUG (optional)

        long testadorId = rs.getLong("testador_id");
        System.out.println("  Raw testador_id from DB: " + testadorId); // DEBUG
        Usuario testador = usuarioDAO.get(testadorId);
        if (testador == null) {
            System.out.println("  !! usuarioDAO.get(" + testadorId + ") returned NULL"); // DEBUG
        } else {
            System.out.println("  ++ usuarioDAO.get(" + testadorId + ") returned User: " + testador.getNome()); // DEBUG
        }

        int estrategiaId = rs.getInt("estrategia_id");
        System.out.println("  Raw estrategia_id from DB: " + estrategiaId); // DEBUG
        Estrategia estrategia = estrategiaDAO.get(estrategiaId);
        if (estrategia == null) {
            System.out.println("  !! estrategiaDAO.get(" + estrategiaId + ") returned NULL"); // DEBUG
        } else {
            System.out.println("  ++ estrategiaDAO.get(" + estrategiaId + ") returned Estrategia: " + estrategia.getNome()); // DEBUG
        }

        int projetoId = rs.getInt("projeto_id");
        // System.out.println("  Raw projeto_id from DB: " + projetoId); // DEBUG (optional, seems less likely to be the issue for blank fields)
        Projeto projeto = projetoDAO.get(projetoId);
        // if (projeto == null) System.out.println("  !! projetoDAO.get(" + projetoId + ") returned NULL"); // DEBUG

        String statusString = rs.getString("status");
        System.out.println("  Raw status string from DB: '" + statusString + "'"); // DEBUG
        SessionStatus status = null; // Initialize to null
        try {
            if (statusString != null) { // Check if string itself is null
                status = SessionStatus.valueOf(statusString.trim().toUpperCase()); // Trim and convert to uppercase for safety
            } else {
                System.out.println("  !! Status string from DB is NULL, though schema says NOT NULL. Check DB data for session ID " + id); // DEBUG
            }
        } catch (IllegalArgumentException e) {
            System.out.println("  !! FAILED to convert status string '" + statusString + "' to SessionStatus enum for session ID " + id + ". Error: " + e.getMessage()); // DEBUG
            // Decide how to handle this - throw error, use default, or leave status as null (current behavior)
        }
        System.out.println("  Final SessionStatus object: " + status); // DEBUG

        LocalDateTime criadoEm = rs.getTimestamp("criadoEm").toLocalDateTime();
        // System.out.println("  Raw criadoEm from DB: " + criadoEm); // DEBUG

        Timestamp inicioEmTs = rs.getTimestamp("inicioEm");
        LocalDateTime inicioEm = (inicioEmTs != null) ? inicioEmTs.toLocalDateTime() : null;

        Timestamp finalizadoEmTs = rs.getTimestamp("finalizadoEm");
        LocalDateTime finalizadoEm = (finalizadoEmTs != null) ? finalizadoEmTs.toLocalDateTime() : null;

        System.out.println("[SessaoDAO.mapResultSetToSessao] Finished mapping session ID: " + id + ". Title: " + titulo + ", Status: " + status); // DEBUG
        return new Sessao(id, titulo, testador, estrategia, projeto, descricaoSessao,
                status, criadoEm, inicioEm, finalizadoEm);
    }
}