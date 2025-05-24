package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.ProjetoDAO;
import br.ufscar.dc.dsw.dao.UsuarioDAO;
import br.ufscar.dc.dsw.domain.Projeto;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.domain.enums.Role;
import br.ufscar.dc.dsw.util.Erro;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = "/projetos/*")
public class ProjetoController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private ProjetoDAO projetoDAO;
    private UsuarioDAO usuarioDAO; // Needed for assigning users to projects

    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();

        if (usuarioLogado == null) {
            // No user logged in, redirect to login or home
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getPathInfo();
        if (action == null) {
            action = "";
        }

        // Admin-only actions
        boolean isAdminAction = action.equals("/cadastro") || action.equals("/insercao") ||
                action.equals("/edicao") || action.equals("/atualizacao") ||
                action.equals("/remocao");

        if (isAdminAction && !Role.ADMIN.equals(usuarioLogado.getTipo())) {
            erros.add("Acesso não autorizado!");
            erros.add("Esta funcionalidade é restrita a administradores.");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
            rd.forward(request, response);
            return;
        }

        // Actions accessible by TESTER or ADMIN (e.g., listing)
        boolean isTesterOrAdminAction = action.equals("") || action.equals("/") || action.equals("/lista");
        if (isTesterOrAdminAction && !(Role.ADMIN.equals(usuarioLogado.getTipo()) || Role.TESTER.equals(usuarioLogado.getTipo()))) {
            erros.add("Acesso não autorizado!");
            erros.add("Você não tem permissão para visualizar esta página.");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
            rd.forward(request, response);
            return;
        }


        try {
            switch (action) {
                case "/cadastro":
                    apresentaFormCadastro(request, response, usuarioLogado);
                    break;
                case "/insercao":
                    insere(request, response, usuarioLogado);
                    break;
                case "/remocao":
                    remove(request, response, usuarioLogado);
                    break;
                case "/edicao":
                    apresentaFormEdicao(request, response, usuarioLogado);
                    break;
                case "/atualizacao":
                    atualiza(request, response, usuarioLogado);
                    break;
                default: // Includes /lista, /, ""
                    lista(request, response, usuarioLogado);
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            // Log error e.printStackTrace();
            erros.add("Erro inesperado: " + e.getMessage());
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp"); // General error page
            rd.forward(request, response);
            // throw new ServletException(e); // Or rethrow
        }
    }

    private void lista(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "nome"; // Default sort column
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "asc"; // Default sort order
        }

        List<Projeto> listaProjetos = projetoDAO.getAll(sortBy, sortOrder);
        request.setAttribute("listaProjetos", listaProjetos);
        request.setAttribute("usuarioLogado", usuarioLogado); // Pass user for conditional rendering in JSP
        request.setAttribute("currentSortBy", sortBy);
        request.setAttribute("currentSortOrder", sortOrder);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/projeto/lista.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormCadastro(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        List<Usuario> todosUsuarios = usuarioDAO.getAll(); // For user assignment
        request.setAttribute("todosUsuarios", todosUsuarios);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/admin/projeto/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormEdicao(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            // Handle error: ID is required
            response.sendRedirect(request.getContextPath() + "/projetos/lista");
            return;
        }
        int id = Integer.parseInt(idParam);
        Projeto projeto = projetoDAO.get(id);
        List<Usuario> todosUsuarios = usuarioDAO.getAll();

        request.setAttribute("projeto", projeto);
        request.setAttribute("todosUsuarios", todosUsuarios);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/admin/projeto/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void insere(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String[] usuariosSelecionadosIds = request.getParameterValues("usuariosSelecionados");

        List<Usuario> usuariosDoProjeto = new ArrayList<>();
        if (usuariosSelecionadosIds != null) {
            for (String userIdStr : usuariosSelecionadosIds) {
                // Fetch full Usuario object or just use IDs if DAO handles it
                Usuario u = usuarioDAO.get(Long.parseLong(userIdStr));
                if (u != null) usuariosDoProjeto.add(u);
            }
        }

        Projeto projeto = new Projeto(nome, descricao, usuariosDoProjeto);
        projeto.setCriadoEm(LocalDateTime.now()); // DAO also sets this, but can be set here too.

        projetoDAO.insert(projeto);
        response.sendRedirect(request.getContextPath() + "/projetos/lista");
    }

    private void atualiza(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        int id = Integer.parseInt(request.getParameter("id"));
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String[] usuariosSelecionadosIds = request.getParameterValues("usuariosSelecionados");

        List<Usuario> usuariosDoProjeto = new ArrayList<>();
        if (usuariosSelecionadosIds != null) {
            for (String userIdStr : usuariosSelecionadosIds) {
                Usuario u = usuarioDAO.get(Long.parseLong(userIdStr));
                if (u != null) usuariosDoProjeto.add(u);
            }
        }

        // Fetch original criadoEm time to preserve it
        Projeto projetoExistente = projetoDAO.get(id);
        if (projetoExistente == null) {
            // Handle error - project not found
            response.sendRedirect(request.getContextPath() + "/projetos/lista");
            return;
        }

        Projeto projeto = new Projeto(id, nome, descricao, projetoExistente.getCriadoEm(), usuariosDoProjeto);
        projetoDAO.update(projeto);
        response.sendRedirect(request.getContextPath() + "/projetos/lista");
    }

    private void remove(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        projetoDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/projetos/lista");
    }
}