package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.EstrategiaDAO;
import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.util.Erro; 

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
    private EstrategiaDAO estrategiaDAO; // Objeto DAO para acesso aos dados das estratégias

    // cria instância do DAO
    @Override
    public void init() {
        estrategiaDAO = new EstrategiaDAO();
    }
    // Trata requisicoes POST repassando para GET 
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
    // Metodo principal para tratamento de requisições GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getPathInfo();
        if (action == null) {
            action = "/";  //ação padrão se não houver path 
        }

        try {
            switch (action) {
                case "/detalhes":
                    mostraDetalhes(request, response);
                    break;
                case "/": // caminho raiz
                case "/lista": // caminho explicito pra listagem
                    listaPublica(request, response);
                    break;
                default:
                    // Trata caminhos desconhecidos redirecionando para listagem
                    listaPublica(request, response);
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
        List<Estrategia> listaEstrategias = estrategiaDAO.getAll();   // Obtém todas as estratégias do banco de dados
         // Define atributo para a view e redireciona para página de listagem
        request.setAttribute("listaEstrategias", listaEstrategias);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/publico/estrategia/lista.jsp");
        dispatcher.forward(request, response);
    }

    private void mostraDetalhes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Erro erros = new Erro();
        String idParam = request.getParameter("id");
        int id = -1;
        
        // Validação do parâmetro ID
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
            // Lida com erros, exibe a listagem com mensagens
            listaPublica(request, response); 
            return;
        }
         // Busca estratégia no banco de dados
        Estrategia estrategia = estrategiaDAO.get(id);
        // Verifica se estratégia foi encontrada
        if (estrategia == null) {
            erros.add("Estratégia não encontrada.");
            request.setAttribute("mensagens", erros);
            listaPublica(request, response); 
            return;
        }
        // Define atributo para a view e redireciona para página de detalhes
        request.setAttribute("estrategia", estrategia);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/publico/estrategia/detalhes.jsp");
        dispatcher.forward(request, response);
    }
}
