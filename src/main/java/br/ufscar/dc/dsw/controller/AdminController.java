package br.ufscar.dc.dsw.controller;

import java.io.IOException;
import java.io.Serial;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException; //sempre verificar que isso aqui foi importado corretamente!!!
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import br.ufscar.dc.dsw.dao.EstrategiaDAO;
import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.domain.Imagem;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.domain.enums.Role;
import br.ufscar.dc.dsw.util.Erro;

@WebServlet(urlPatterns = "/admin/*")
public class AdminController extends HttpServlet {

    @Serial
    private static final long serialVersionUID = 1L;

    private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        estrategiaDAO = new EstrategiaDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();

        if (usuario == null || !usuario.getTipo().hasAccess(Role.ADMIN)) {
            erros.add("Acesso não autorizado!");
            erros.add("Apenas Papel [ADMIN] tem acesso a esta área administrativa.");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
            rd.forward(request, response);
            return;
        }

        String action = request.getPathInfo();
        if (action == null) {
            action = "";
        }

        try {
            switch (action) {
                case "/estrategias/cadastro":
                    apresentaFormCadastroEstrategia(request, response);
                    break;
                case "/estrategias/insercao":
                    insereEstrategia(request, response);
                    break;
                case "/estrategias/remocao":
                    removeEstrategia(request, response);
                    break;
                case "/estrategias/edicao":
                    apresentaFormEdicaoEstrategia(request, response);
                    break;
                case "/estrategias/atualizacao":
                    atualizaEstrategia(request, response);
                    break;
                case "/":
                case "":
                    RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/admin/index.jsp");
                    dispatcher.forward(request, response);
                    break;
                default:
                    erros.add("Funcionalidade administrativa não implementada: " + action);
                    request.setAttribute("mensagens", erros);
                    RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
                    rd.forward(request, response);
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            e.printStackTrace();
            erros.add("Erro inesperado ao processar a requisição: " + e.getMessage());
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
        }
    }

    //métodos para estratégias

    private void apresentaFormCadastroEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/admin/estrategia/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void insereEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String dicas = request.getParameter("dicas");

        List<Imagem> imagens = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String url = request.getParameter("imageUrl" + i);
            String desc = request.getParameter("imageDesc" + i);
            if (url != null && !url.trim().isEmpty()) {
                imagens.add(new Imagem(url.trim(), desc != null ? desc.trim() : ""));
            }
        }

        Estrategia estrategia = new Estrategia(nome, descricao, dicas, imagens);
        estrategiaDAO.insert(estrategia);

        response.sendRedirect(request.getContextPath() + "/estrategias/lista");
    }

    private void apresentaFormEdicaoEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Erro erros = new Erro();
        String idParam = request.getParameter("id");
        int id = -1;

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                erros.add("ID da estratégia inválido para edição.");
            }
        } else {
            erros.add("ID da estratégia não fornecido para edição.");
        }

        if (erros.isExisteErros()) {
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
            return;
        }

        Estrategia estrategia = estrategiaDAO.get(id);
        if (estrategia == null) {
            erros.add("Estratégia não encontrada para edição.");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
            return;
        }

        request.setAttribute("estrategia", estrategia);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/admin/estrategia/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void atualizaEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        int id = Integer.parseInt(request.getParameter("id"));
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String dicas = request.getParameter("dicas");

        List<Imagem> imagens = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            String url = request.getParameter("imageUrl" + i);
            String desc = request.getParameter("imageDesc" + i);
            if (url != null && !url.trim().isEmpty()) {
                imagens.add(new Imagem(url.trim(), desc != null ? desc.trim() : ""));
            }
        }

        Estrategia estrategia = new Estrategia(id, nome, descricao, dicas, imagens);
        estrategiaDAO.update(estrategia);

        response.sendRedirect(request.getContextPath() + "/estrategias/lista");
    }

    private void removeEstrategia(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException { //foi adicionado ServletException aqui pq o metodo forward já está sendo usado!!!
        Erro erros = new Erro();
        String idParam = request.getParameter("id");
        int id = -1;

        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                erros.add("ID da estratégia inválido para remoção.");
            }
        } else {
            erros.add("ID da estratégia não fornecido para remoção.");
        }

        if (erros.isExisteErros()) {
            request.setAttribute("mensagens", erros);
            // try-catch removed as this method now declares ServletException
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
            return;
        }

        estrategiaDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/estrategias/lista");
    }
}
