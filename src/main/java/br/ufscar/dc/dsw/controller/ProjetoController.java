package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.ProjetoDAO;
import br.ufscar.dc.dsw.dao.UsuarioDAO;
import br.ufscar.dc.dsw.domain.Projeto;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Mapeia todas as URLs que começam com /projetos
@WebServlet(urlPatterns = "/projetos/*")
public class ProjetoController extends HttpServlet {

    private static final long serialVersionUID = 1L;

      // DAOs para acesso a dados de projetos e usuários
    private ProjetoDAO projetoDAO;
    private UsuarioDAO usuarioDAO; 

    // Inicializa os DAOs quando o servlet é carregado
    @Override
    public void init() {
        projetoDAO = new ProjetoDAO();
        usuarioDAO = new UsuarioDAO();
    }

    // Todas as requisições POST são redirecionadas para GET
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
     // Método principal para tratamento de requisições
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Verifica se há usuário logado na sessão
        Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();

        if (usuarioLogado == null) {
             // Redireciona para login se não houver usuário autenticado
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        // Obtém a ação a partir do path da URL
        String action = request.getPathInfo();
        if (action == null) {
            action = "";
        }

        // Verifica se a ação requer privilégios de administrador
        boolean isAdminAction = action.equals("/cadastro") || action.equals("/insercao") ||
                action.equals("/edicao") || action.equals("/atualizacao") ||
                action.equals("/remocao");
         // Bloqueia acesso não autorizado para ações administrativas
        if (isAdminAction && !Role.ADMIN.equals(usuarioLogado.getTipo())) {
            erros.add("Acesso não autorizado!");
            erros.add("Esta funcionalidade é restrita a administradores.");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
            rd.forward(request, response);
            return;
        }

        // Verifica permissão para ações de TESTER ou ADMIN
        boolean isTesterOrAdminAction = action.equals("") || action.equals("/") || action.equals("/lista");
        if (isTesterOrAdminAction && !(Role.ADMIN.equals(usuarioLogado.getTipo()) || Role.TESTER.equals(usuarioLogado.getTipo()))) {
            erros.add("Acesso não autorizado!");
            erros.add("Você não tem permissão para visualizar esta página.");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
            rd.forward(request, response);
            return;
        }


        try {
            // Roteamento para diferentes operações
            switch (action) {
                case "/cadastro":
                    apresentaFormCadastro(request, response, usuarioLogado);
                    break;
                case "/insercao":
                    insere(request, response, usuarioLogado);
                    break;
                case "/remocao":
                    remove(request, response, usuarioLogado);
                    break;
                case "/edicao":
                    apresentaFormEdicao(request, response, usuarioLogado);
                    break;
                case "/atualizacao":
                    atualiza(request, response, usuarioLogado);
                    break;
                default: // Includes /lista, /, ""
                    lista(request, response, usuarioLogado);
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            // Tratamento de erros
            erros.add("Erro inesperado: " + e.getMessage());
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp"); 
            rd.forward(request, response);
           
        }
    }
     // Exibe a listagem de projetos com ordenação
    private void lista(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "nome"; // Coluna padrão
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "asc"; // Ordem ascendente como padrão
        }
          // Obtém projetos ordenados e prepara atributos para a view
        List<Projeto> listaProjetos = projetoDAO.getAll(sortBy, sortOrder);
        request.setAttribute("listaProjetos", listaProjetos);
        request.setAttribute("usuarioLogado", usuarioLogado); 
        request.setAttribute("currentSortBy", sortBy);
        request.setAttribute("currentSortOrder", sortOrder);
         // Encaminha para a página de listagem
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/projeto/lista.jsp");
        dispatcher.forward(request, response);
    }
    // Exibe formulário de cadastro de novo projeto
    private void apresentaFormCadastro(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        // Obtém todos os usuários para atribuição ao projeto
        List<Usuario> todosUsuarios = usuarioDAO.getAll(); 
        request.setAttribute("todosUsuarios", todosUsuarios);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/admin/projeto/formulario.jsp");
        dispatcher.forward(request, response);
    }
    
    // Exibe formulário de edição de projeto existente
    private void apresentaFormEdicao(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            // Lidar com erro usuario sem ID ou ID null
            response.sendRedirect(request.getContextPath() + "/projetos/lista");
            return;
        }
        // Carrega dados do projeto e usuários para o formulário
        int id = Integer.parseInt(idParam);
        Projeto projeto = projetoDAO.get(id);
        List<Usuario> todosUsuarios = usuarioDAO.getAll();

        request.setAttribute("projeto", projeto);
        request.setAttribute("todosUsuarios", todosUsuarios);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/admin/projeto/formulario.jsp");
        dispatcher.forward(request, response);
    }

     // Processa a criação de um novo projeto
    private void insere(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
         // Coleta parâmetros do formulário
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String[] usuariosSelecionadosIds = request.getParameterValues("usuariosSelecionados");

         // Converte IDs de usuários selecionados em objetos Usuario
        List<Usuario> usuariosDoProjeto = new ArrayList<>();
        if (usuariosSelecionadosIds != null) {
            for (String userIdStr : usuariosSelecionadosIds) {
                Usuario u = usuarioDAO.get(Long.parseLong(userIdStr));
                if (u != null) usuariosDoProjeto.add(u);
            }
        }

        Projeto projeto = new Projeto(nome, descricao, usuariosDoProjeto);
        projeto.setCriadoEm(LocalDateTime.now()); 

        projetoDAO.insert(projeto);
         // Redireciona para listagem após criação
        response.sendRedirect(request.getContextPath() + "/projetos/lista");
    }

     // Atualiza um projeto existente
    private void atualiza(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        // Coleta dados do formulário
        int id = Integer.parseInt(request.getParameter("id"));
        String nome = request.getParameter("nome");
        String descricao = request.getParameter("descricao");
        String[] usuariosSelecionadosIds = request.getParameterValues("usuariosSelecionados");

            // Processa usuários selecionados
        List<Usuario> usuariosDoProjeto = new ArrayList<>();
        if (usuariosSelecionadosIds != null) {
            for (String userIdStr : usuariosSelecionadosIds) {
                Usuario u = usuarioDAO.get(Long.parseLong(userIdStr));
                if (u != null) usuariosDoProjeto.add(u);
            }
        }

         // Mantém a data de criação original
        Projeto projetoExistente = projetoDAO.get(id);
        if (projetoExistente == null) {
            // Erro: projeto nao encontrado
            response.sendRedirect(request.getContextPath() + "/projetos/lista");
            return;
        }

        Projeto projeto = new Projeto(id, nome, descricao, projetoExistente.getCriadoEm(), usuariosDoProjeto);
        projetoDAO.update(projeto);
        response.sendRedirect(request.getContextPath() + "/projetos/lista");
    }

    // Remove um projeto existente
    private void remove(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        projetoDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/projetos/lista");
    }
}
