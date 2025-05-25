package br.ufscar.dc.dsw.controller;

import java.io.IOException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import br.ufscar.dc.dsw.dao.UsuarioDAO;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.domain.enums.Role;
import br.ufscar.dc.dsw.util.Erro;

@WebServlet(name = "Index", urlPatterns = { "/home", "/logout.jsp", "/login"})
public class IndexController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());
        if (path.isEmpty()) {
            path = "/";
        }

        //logout
        if (path.equals("/logout.jsp")) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/home.jsp");
            return;
        }

        //login
        if (request.getParameter("bOK") != null) {
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
                        //direcionamento por Role
                        if (usuario.getTipo() == Role.ADMIN) {
                            response.sendRedirect(request.getContextPath() + "/admin/");
                        } else if (usuario.getTipo() == Role.TESTER) {
                            response.sendRedirect(request.getContextPath() + "/tester/dashboard");
                        } else {
                            response.sendRedirect(request.getContextPath() + "/home.jsp");
                        }
                        return;
                    } else {
                        erros.add("Senha inválida!");
                    }
                } else {
                    erros.add("Usuário não encontrado!");
                }
            }
            request.setAttribute("mensagens", erros); // Mantenha as mensagens de erro
        } else {
            //se não é um POST de login, não é logout, não é um login já efetuado
            Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
            if (usuarioLogado != null) {
                if (usuarioLogado.getTipo() == Role.ADMIN) {
                    response.sendRedirect(request.getContextPath() + "/admin/");
                } else if (usuarioLogado.getTipo() == Role.TESTER) {
                    response.sendRedirect(request.getContextPath() + "/tester/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/home.jsp");
                }
                return;
            }
        }

        request.getSession().invalidate();

        String URL = "/home.jsp";

        RequestDispatcher rd = request.getRequestDispatcher(URL);
        rd.forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}