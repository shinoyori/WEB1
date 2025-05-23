package br.ufscar.dc.dsw.controller;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

import br.ufscar.dc.dsw.domain.enums.Role;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import br.ufscar.dc.dsw.dao.UsuarioDAO;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.util.Erro;

@WebServlet(urlPatterns = "/usuarios/*")
public class UsuarioController extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private UsuarioDAO dao;

    @Override
    public void init() {
        dao = new UsuarioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp"); // Redirecionamento aqui
            return;
        }

        String action = request.getPathInfo();
        if (action == null) {
            action = "";
        }

        //apenas ADMIN pode acessar
        boolean apenasAdmin = List.of("/cadastro", "/insercao", "/edicao", "/atualizacao", "/remocao").contains(action);

        //se não for ADMIN e tentar acessar algo restrito
        if (apenasAdmin && !usuario.getTipo().hasAccess(Role.ADMIN)) {
            erros.add("Acesso não autorizado!");
            erros.add("Apenas administradores podem acessar essa funcionalidade.");
            request.setAttribute("mensagens", erros);
            request.getRequestDispatcher("/noAuth.jsp").forward(request, response);
            return;
        }

        //se for GUEST, não tem permissão pra nada aqui
        if (!usuario.getTipo().hasAccess(Role.TESTER)) { // <-- Problema: GUEST não deveria acessar nem a lista?
            erros.add("Acesso não autorizado!");
            erros.add("Você precisa estar logado como testador ou administrador.");
            request.setAttribute("mensagens", erros);
            request.getRequestDispatcher("/noAuth.jsp").forward(request, response);
            return;
        }

        try {
            switch (action) {
                case "/cadastro":
                    apresentaFormCadastro(request, response);
                    break;
                case "/insercao":
                    insere(request, response);
                    break;
                case "/remocao":
                    remove(request, response);
                    break;
                case "/edicao":
                    apresentaFormEdicao(request, response);
                    break;
                case "/atualizacao":
                    atualize(request, response);
                    break;
                default: // /lista ou ""
                    lista(request, response);
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            throw new ServletException(e);
        }
    }

    private void lista(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Usuario> listaUsuarios = dao.getAll();
        request.setAttribute("listaUsuarios", listaUsuarios);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/usuario/lista.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormCadastro(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/usuario/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void apresentaFormEdicao(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Usuario usuario = dao.get(id);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/usuario/formulario.jsp");
        request.setAttribute("usuario", usuario);
        dispatcher.forward(request, response);
    }

    private void insere(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nome");
        String login = request.getParameter("login");
        String senha = request.getParameter("senha");
        Role tipo = Role.valueOf(request.getParameter("tipo"));

        Usuario usuario = new Usuario(nome, login, senha, tipo);
        dao.insert(usuario);

        response.sendRedirect("lista");
    }

    private void atualize(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        Long id = Long.parseLong(request.getParameter("id"));
        String nome = request.getParameter("nome");
        // String email = request.getParameter("email");
        String login = request.getParameter("login");
        String senha = request.getParameter("senha");
        Role tipo = Role.valueOf(request.getParameter("tipo"));

        Usuario usuario = new Usuario(id, nome, login, senha, tipo);
        dao.update(usuario);

        response.sendRedirect("lista");
    }

    private void remove(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Long id = Long.parseLong(request.getParameter("id"));
        Usuario usuario = new Usuario(id);
        dao.delete(usuario);

        response.sendRedirect("lista");
    }
}