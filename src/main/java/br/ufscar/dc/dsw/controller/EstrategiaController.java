package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.EstrategiaDAO;
import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.util.Erro; // Assuming you have this utility for error messages

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/estrategias/*")
public class EstrategiaController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() {
        estrategiaDAO = new EstrategiaDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getPathInfo();
        if (action == null) {
            action = "/"; // Default to listing
        }

        try {
            switch (action) {
                case "/detalhes":
                    mostraDetalhes(request, response);
                    break;
                case "/": // Root path for /estrategias/
                case "/lista": // Explicit path for listing
                    listaPublica(request, response);
                    break;
                default:
                    // Optionally, handle unknown paths (e.g., show 404 or redirect to list)
                    listaPublica(request, response); // Default to list for unknown actions
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            // Log error e.printStackTrace();
            Erro erros = new Erro("Erro inesperado ao processar estratégias: " + e.getMessage());
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
        }
    }

    private void listaPublica(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Estrategia> listaEstrategias = estrategiaDAO.getAll(); // DAO fetches all strategies
        request.setAttribute("listaEstrategias", listaEstrategias);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/publico/estrategia/lista.jsp");
        dispatcher.forward(request, response);
    }

    private void mostraDetalhes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Erro erros = new Erro();
        String idParam = request.getParameter("id");
        int id = -1;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                erros.add("ID da estratégia inválido.");
            }
        } else {
            erros.add("ID da estratégia não fornecido.");
        }

        if (erros.isExisteErros()) {
            request.setAttribute("mensagens", erros);
            // Forward to an error display on the list page or a general error page
            listaPublica(request, response); // Or redirect to an error page
            return;
        }

        Estrategia estrategia = estrategiaDAO.get(id); // DAO fetches strategy by ID with images
        if (estrategia == null) {
            erros.add("Estratégia não encontrada.");
            request.setAttribute("mensagens", erros);
            listaPublica(request, response); // Or redirect to an error page
            return;
        }

        request.setAttribute("estrategia", estrategia);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/publico/estrategia/detalhes.jsp");
        dispatcher.forward(request, response);
    }
}