// define o pacote onde está localizado o controller
package br.ufscar.dc.dsw.controller;

// importa a classe DAO responsável por acessar os dados de estratégias
import br.ufscar.dc.dsw.dao.EstrategiaDAO;
// importa a classe que representa a entidade Estrategia
import br.ufscar.dc.dsw.domain.Estrategia;
// importa a classe utilitária para tratar erros
import br.ufscar.dc.dsw.util.Erro;

// importa classes do servlet para manipulação de requisições e respostas
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

// define a URL que será mapeada para esse servlet: /estrategias/*
@WebServlet(urlPatterns = "/estrategias/*")
public class EstrategiaController extends HttpServlet {

    // define uma constante para a versão da classe, usada em serialização
    private static final long serialVersionUID = 1L;

    // objeto DAO usado para acessar os dados de estratégias no banco
    private EstrategiaDAO estrategiaDAO;

    // método chamado ao iniciar o servlet (uma vez só), cria a instância do DAO
    @Override
    public void init() {
        estrategiaDAO = new EstrategiaDAO();
    }

    // redireciona qualquer requisição POST para o método doGet (boa prática para simplificar controle)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // método principal que trata requisições do tipo GET
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // obtém o path da URL (ex: /detalhes, /lista, etc.)
        String action = request.getPathInfo();

        // define "/" como ação padrão caso nenhum path seja fornecido
        if (action == null) {
            action = "/";
        }

        try {
            // verifica qual ação foi requisitada e chama o método correspondente
            switch (action) {
                case "/detalhes":
                    mostraDetalhes(request, response); // exibe detalhes de uma estratégia
                    break;
                case "/": // caminho raiz
                case "/lista": // listagem explícita
                    listaPublica(request, response); // exibe lista pública de estratégias
                    break;
                default:
                    // para qualquer outro path, redireciona para a listagem
                    listaPublica(request, response);
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            // captura exceções e encaminha para página de erro
            Erro erros = new Erro("Erro inesperado ao processar estratégias: " + e.getMessage());
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
        }
    }

    // método que busca todas as estratégias e encaminha para a view de listagem
    private void listaPublica(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // obtém todas as estratégias cadastradas no banco de dados
        List<Estrategia> listaEstrategias = estrategiaDAO.getAll();

        // define atributo no request com a lista de estratégias
        request.setAttribute("listaEstrategias", listaEstrategias);

        // encaminha a requisição para a página JSP responsável por exibir a lista
        RequestDispatcher dispatcher = request.getRequestDispatcher("/publico/estrategia/lista.jsp");
        dispatcher.forward(request, response);
    }

    // método que exibe os detalhes de uma estratégia específica
    private void mostraDetalhes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // cria objeto para armazenar possíveis mensagens de erro
        Erro erros = new Erro();

        // recupera o parâmetro 'id' da requisição
        String idParam = request.getParameter("id");
        int id = -1;

        // valida se o id foi fornecido e se é um número válido
        if (idParam != null && !idParam.isEmpty()) {
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                erros.add("ID da estratégia inválido.");
            }
        } else {
            erros.add("ID da estratégia não fornecido.");
        }

        // se houver erros de validação, redireciona de volta para a lista com mensagens de erro
        if (erros.isExisteErros()) {
            request.setAttribute("mensagens", erros);
            listaPublica(request, response); 
            return;
        }

        // busca a estratégia com o id informado no banco de dados
        Estrategia estrategia = estrategiaDAO.get(id);

        // se a estratégia não for encontrada, exibe erro e redireciona para a lista
        if (estrategia == null) {
            erros.add("Estratégia não encontrada.");
            request.setAttribute("mensagens", erros);
            listaPublica(request, response); 
            return;
        }

        // se a estratégia for encontrada, define como atributo no request
        request.setAttribute("estrategia", estrategia);

        // encaminha para a página JSP que exibe os detalhes da estratégia
        RequestDispatcher dispatcher = request.getRequestDispatcher("/publico/estrategia/detalhes.jsp");
        dispatcher.forward(request, response);
    }
}
