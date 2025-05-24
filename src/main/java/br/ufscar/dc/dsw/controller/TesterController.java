package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.ProjetoDAO;
import br.ufscar.dc.dsw.dao.SessaoDAO; // Optional: if showing tester's sessions on dashboard
import br.ufscar.dc.dsw.domain.Projeto;
import br.ufscar.dc.dsw.domain.Sessao;   // Optional
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
import java.util.List;

@WebServlet(urlPatterns = "/tester/*")
public class TesterController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private ProjetoDAO projetoDAO;
    private SessaoDAO sessaoDAO; // Optional: for listing tester's own sessions

    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
        sessaoDAO = new SessaoDAO(); // Initialize if used
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

        if (usuarioLogado == null || !Role.TESTER.equals(usuarioLogado.getTipo())) {
            erros.add("Acesso não autorizado!");
            erros.add("Esta área é restrita a usuários com o papel TESTER.");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
            rd.forward(request, response);
            return;
        }

        String action = request.getPathInfo();
        if (action == null) {
            action = "/dashboard"; // Default to dashboard
        }

        try {
            switch (action) {
                case "/dashboard":
                    showDashboard(request, response, usuarioLogado);
                    break;
                default:
                    erros.add("Funcionalidade de Testador não implementada: " + action);
                    request.setAttribute("mensagens", erros);
                    RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
                    rd.forward(request, response);
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            e.printStackTrace();
            erros.add("Erro inesperado: " + e.getMessage());
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response, Usuario testadorLogado)
            throws ServletException, IOException {

        List<Projeto> projetosDoTester = projetoDAO.getProjetosByUsuarioId(testadorLogado.getId());
        request.setAttribute("projetosDoTester", projetosDoTester);

        // Optional: Fetch and display current tester's active/recent sessions
        // List<Sessao> sessoesDoTester = sessaoDAO.getAllByTestadorId(testadorLogado.getId(), "criadoEm", "desc");
        // request.setAttribute("sessoesDoTester", sessoesDoTester);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/tester/dashboard.jsp");
        dispatcher.forward(request, response);
    }
}