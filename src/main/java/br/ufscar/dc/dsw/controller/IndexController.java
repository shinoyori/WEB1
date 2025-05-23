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

@WebServlet(name = "Index", urlPatterns = { "/", "/index.jsp", "/logout.jsp", "/login" }) // Adicionado "/login" para centralizar o POST do login
public class IndexController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //se o POST veio do formulário de login (bOK não é null), processa o login
        if (request.getParameter("bOK") != null) {
            processLogin(request, response);
        } else {
            //outros POSTs para a raiz (se houver) podem ser tratados aqui ou rejeitados
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Método POST não permitido para este caminho sem parâmetros de login.");
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();

        //verifica se a URL é para logout
        if (uri.endsWith("/logout.jsp")) { // Ou if (request.getServletPath().equals("/logout.jsp"))
            logout(request, response);
            return;
        }

        //verifica se a URL é explicitamente para a página de login
        if (uri.endsWith("/login") || uri.endsWith("/login.jsp")) {
            // Se o usuário já estiver logado, redireciona para a página apropriada
            Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
            if (usuarioLogado != null) {
                redirecionaPorRole(request, response, usuarioLogado);
                return;
            }
            //se não estiver logado e explicitamente pediu login, encaminha para a página de login
            RequestDispatcher rd = request.getRequestDispatcher("/login.jsp");
            rd.forward(request, response);
            return;
        }

        //para qualquer outra requisição (ex: "/"), trata como a home page pública
        Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
        request.setAttribute("usuarioLogado", usuarioLogado); // Passa o usuarioLogado para a JSP para exibir o nome
        RequestDispatcher rd = request.getRequestDispatcher("/home.jsp"); // Encaminha para a nova home page pública
        rd.forward(request, response);
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
            response.sendRedirect(request.getContextPath() + "/usuarios/");
        } else { // GUEST
            response.sendRedirect(request.getContextPath() + "/home.jsp");
        }
    }
}