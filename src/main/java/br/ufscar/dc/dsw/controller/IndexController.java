package br.ufscar.dc.dsw.controller;

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.ufscar.dc.dsw.dao.UsuarioDAO;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.domain.enums.Role;
import br.ufscar.dc.dsw.util.Erro;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "Index", urlPatterns = { "/home", "/logout.jsp", "/login" })
public class IndexController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getParameter("bOK") != null) {
            processLogin(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Método POST não permitido para este caminho sem parâmetros de login.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();

        String path = uri.substring(contextPath.length());
        if (path.isEmpty()) {
            path = "/";
        }

        if (path.equals("/")) {
            Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
            request.setAttribute("usuarioLogado", usuarioLogado);
            RequestDispatcher rd = request.getRequestDispatcher("/home.jsp");
            rd.forward(request, response);
            return;
        }

        if (path.equals("/logout.jsp")) {
            logout(request, response);
            return;
        }

        if (path.equals("/login") || path.equals("/login.jsp")) {
            Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
            if (usuarioLogado != null) {
                redirecionaPorRole(request, response, usuarioLogado);
                return;
            }
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND, "Página não encontrada.");
    }

    private void processLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Erro erros = new Erro();

        String login = request.getParameter("login");
        String senha = request.getParameter("senha");

        if (login == null || login.isEmpty()) {
            erros.add("Login não informado!");
        }
        if (senha == null || senha.isEmpty()) {
            erros.add("Senha não informada!");
        }

        if (!erros.isExisteErros()) {
            UsuarioDAO dao = new UsuarioDAO();
            Usuario usuario = dao.getbyLogin(login);

            if (usuario != null) {
                if (usuario.getSenha().equals(senha)) {
                    request.getSession().setAttribute("usuarioLogado", usuario);
                    redirecionaPorRole(request, response, usuario);
                    return;
                } else {
                    erros.add("Senha inválida!");
                }
            } else {
                erros.add("Usuário não encontrado!");
            }
        }

        request.setAttribute("mensagens", erros);
        RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
        rd.forward(request, response);
    }

    private void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/home.jsp");
    }

    private void redirecionaPorRole(HttpServletRequest request, HttpServletResponse response, Usuario usuario) throws IOException {
        if (usuario.getTipo() == Role.ADMIN) {
            response.sendRedirect(request.getContextPath() + "/admin/");
        } else if (usuario.getTipo() == Role.TESTER) {
            response.sendRedirect(request.getContextPath() + "/tester/dashboard");
        } else {
            response.sendRedirect(request.getContextPath() + "/home.jsp");
        }
    }
}