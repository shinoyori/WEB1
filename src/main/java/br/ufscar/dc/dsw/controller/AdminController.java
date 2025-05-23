package br.ufscar.dc.dsw.controller;

import java.io.IOException;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.domain.enums.Role; // Importar a enum Role
import br.ufscar.dc.dsw.util.Erro;

@WebServlet(urlPatterns = "/admin/*")
public class AdminController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();

        //verifica se o usuário logado tem permissão de ADMIN
        if (!usuario.getTipo().hasAccess(Role.ADMIN)) { // Correção: Usar a enum Role para comparação
            erros.add("Acesso não autorizado!");
            erros.add("Apenas Papel [ADMIN] tem acesso a esta área administrativa.");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp"); // Encaminha para noAuth.jsp
            rd.forward(request, response);
            return;
        }

        //se chegou aqui, o usuário é ADMIN e está autorizado para a área /admin/
        String action = request.getPathInfo();
        if (action == null || action.equals("/") || action.isEmpty()) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/admin/index.jsp"); //
            dispatcher.forward(request, response);
        } else {
            //adicionar um switch-case aqui para lidar com
            //ações como /admin/usuarios
            //ou /admin/projetos etc.
            erros.add("Funcionalidade administrativa não implementada: " + action);
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp"); //
            rd.forward(request, response);
        }
    }
}