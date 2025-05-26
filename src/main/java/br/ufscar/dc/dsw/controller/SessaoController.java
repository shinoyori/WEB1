package br.ufscar.dc.dsw.controller;

import br.ufscar.dc.dsw.dao.EstrategiaDAO;
import br.ufscar.dc.dsw.dao.ProjetoDAO;
import br.ufscar.dc.dsw.dao.SessaoDAO;
import br.ufscar.dc.dsw.domain.Estrategia;
import br.ufscar.dc.dsw.domain.Projeto;
import br.ufscar.dc.dsw.domain.Sessao;
import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.domain.enums.Role;
import br.ufscar.dc.dsw.domain.enums.SessionStatus;
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
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.HashMap;

@WebServlet(urlPatterns = "/sessoes/*")
public class SessaoController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private SessaoDAO sessaoDAO;
    private ProjetoDAO projetoDAO;
    private EstrategiaDAO estrategiaDAO;

    @Override
    public void init() {
        sessaoDAO = new SessaoDAO();
        projetoDAO = new ProjetoDAO();
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

        Usuario usuarioLogado = (Usuario) request.getSession().getAttribute("usuarioLogado");
        Erro erros = new Erro();

        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        String action = request.getPathInfo();
        if (action == null) {
            action = "/";
        }

        try {
            switch (action) {
                case "/cadastro": // R7: Cadastro de sessões de teste (requer login de testador)
                    if (Role.TESTER.equals(usuarioLogado.getTipo())) {
                        apresentaFormCadastro(request, response, usuarioLogado);
                    } else {
                        acessoNegado(request, response, "Apenas TESTERs podem cadastrar sessões.");
                    }
                    break;
                case "/insercao": // R7
                    if (Role.TESTER.equals(usuarioLogado.getTipo())) {
                        insereSessao(request, response, usuarioLogado);
                    } else {
                        acessoNegado(request, response, "Apenas TESTERs podem inserir sessões.");
                    }
                    break;
                case "/listaPorProjeto": // R9: Listagem de sessões de teste de um projeto
                    listaPorProjeto(request, response, usuarioLogado);
                    break;
                case "/atualizarStatus": // R8: Gerenciamento do ciclo de vida
                    atualizaStatusSessao(request, response, usuarioLogado);
                    break;
                case "/detalhes":
                    mostraDetalhesSessao(request, response, usuarioLogado);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/");
                    break;
            }
        } catch (RuntimeException | IOException | ServletException e) {
            e.printStackTrace(); // Log error
            erros.add("Erro inesperado ao processar sessões: " + e.getMessage());
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
        }
    }

    private void apresentaFormCadastro(HttpServletRequest request, HttpServletResponse response, Usuario testadorLogado)
            throws ServletException, IOException {
        Erro erros = new Erro();
        String projetoIdParam = request.getParameter("projetoId");
        int projetoId = -1;

        if (projetoIdParam != null && !projetoIdParam.isEmpty()) {
            try {
                projetoId = Integer.parseInt(projetoIdParam);
            } catch (NumberFormatException e) {
                erros.add("ID do projeto inválido.");
            }
        } else {
            erros.add("ID do projeto é obrigatório para criar uma sessão.");
        }

        if (erros.isExisteErros()) {
            request.setAttribute("mensagens", erros);
            response.sendRedirect(request.getContextPath() + "/projetos/lista"); // Assuming you have a project list
            return;
        }

        Projeto projeto = projetoDAO.get(projetoId);
        List<Estrategia> listaEstrategias = estrategiaDAO.getAll();

        if (projeto == null) {
            erros.add("Projeto não encontrado.");
            request.setAttribute("mensagens", erros);
            response.sendRedirect(request.getContextPath() + "/projetos/lista");
            return;
        }

        request.setAttribute("projeto", projeto);
        request.setAttribute("listaEstrategias", listaEstrategias);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/sessao/formulario.jsp");
        dispatcher.forward(request, response);
    }

    private void insereSessao(HttpServletRequest request, HttpServletResponse response, Usuario testadorLogado)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        Erro erros = new Erro();

        String titulo = request.getParameter("titulo");
        String descricao = request.getParameter("descricao");
        String estrategiaIdParam = request.getParameter("estrategiaId");
        String projetoIdParam = request.getParameter("projetoId");

        int estrategiaId = -1, projetoId = -1;

        if (titulo == null || titulo.trim().isEmpty()) erros.add("Título é obrigatório.");
        if (estrategiaIdParam == null || estrategiaIdParam.isEmpty()) {
            erros.add("Estratégia é obrigatória.");
        } else {
            try { estrategiaId = Integer.parseInt(estrategiaIdParam); } catch (NumberFormatException e) { erros.add("ID da Estratégia inválido."); }
        }
        if (projetoIdParam == null || projetoIdParam.isEmpty()) {
            erros.add("ID do Projeto é obrigatório.");
        } else {
            try { projetoId = Integer.parseInt(projetoIdParam); } catch (NumberFormatException e) { erros.add("ID do Projeto inválido."); }
        }

        if (erros.isExisteErros()){
            request.setAttribute("mensagens", erros);
            if (projetoId > 0) request.setAttribute("projeto", projetoDAO.get(projetoId));
            request.setAttribute("listaEstrategias", estrategiaDAO.getAll());
            request.setAttribute("sessaoFormData", request.getParameterMap());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/sessao/formulario.jsp");
            dispatcher.forward(request, response);
            return;
        }

        Estrategia estrategia = estrategiaDAO.get(estrategiaId);
        Projeto projeto = projetoDAO.get(projetoId);

        if (estrategia == null || projeto == null) {
            erros.add("Estratégia ou Projeto inválido.");
            request.setAttribute("mensagens", erros);
            if (projetoId > 0) request.setAttribute("projeto", projetoDAO.get(projetoId));
            request.setAttribute("listaEstrategias", estrategiaDAO.getAll());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/sessao/formulario.jsp");
            dispatcher.forward(request, response);
            return;
        }

        Sessao sessao = new Sessao();
        sessao.setTitulo(titulo);
        sessao.setDescricao(descricao);
        sessao.setTestador(testadorLogado);
        sessao.setEstrategia(estrategia);
        sessao.setProjeto(projeto);
        sessao.setStatus(SessionStatus.CRIADA);
        sessao.setCriadoEm(LocalDateTime.now());

        sessaoDAO.insert(sessao);
        response.sendRedirect(request.getContextPath() + "/sessoes/listaPorProjeto?projetoId=" + projeto.getId());
    }

    private void listaPorProjeto(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        System.out.println("[SessaoController] Entering listaPorProjeto method..."); // DEBUG
        Erro erros = new Erro();
        String projetoIdParam = request.getParameter("projetoId");
        int projetoId = -1;

        if (projetoIdParam != null && !projetoIdParam.isEmpty()) {
            try {
                projetoId = Integer.parseInt(projetoIdParam);
                System.out.println("[SessaoController] projetoId: " + projetoId); // DEBUG
            } catch (NumberFormatException e) {
                erros.add("ID do projeto inválido.");
            }
        } else {
            erros.add("ID do projeto é obrigatório para listar sessões.");
        }

        if (erros.isExisteErros()) {
            request.setAttribute("mensagens", erros);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/projetos/lista");
            dispatcher.forward(request, response);
            return;
        }

        Projeto projeto = projetoDAO.get(projetoId);
        if (projeto == null) {
            erros.add("Projeto não encontrado.");
            System.out.println("[SessaoController] Projeto with ID " + projetoId + " not found."); // DEBUG
            request.setAttribute("mensagens", erros);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/projetos/lista");
            dispatcher.forward(request, response);
            return;
        }
        System.out.println("[SessaoController] Fetched Projeto: " + projeto.getNome()); // DEBUG

        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        if (sortBy == null || sortBy.trim().isEmpty()) sortBy = "criadoEm";
        if (sortOrder == null || sortOrder.trim().isEmpty()) sortOrder = "desc";
        System.out.println("[SessaoController] Sorting by: " + sortBy + ", Order: " + sortOrder); // DEBUG

        List<Sessao> listaSessoesOriginal = sessaoDAO.getAllByProjetoId(projetoId, sortBy, sortOrder);
        System.out.println("[SessaoController] Number of sessions found: " + listaSessoesOriginal.size()); // DEBUG

        List<Map<String, Object>> listaSessoesView = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Sessao sessao : listaSessoesOriginal) {
            System.out.println("  [SessaoController] Processing session ID: " + sessao.getId()); // DEBUG
            Map<String, Object> sessaoViewMap = new HashMap<>();
            sessaoViewMap.put("id", sessao.getId());

            String titulo = sessao.getTitulo();
            sessaoViewMap.put("titulo", titulo);
            System.out.println("    > Titulo: " + titulo); // DEBUG

            Usuario testador = sessao.getTestador();
            sessaoViewMap.put("testador", testador);
            if (testador != null) {
                System.out.println("    > Testador ID: " + testador.getId() + ", Nome: " + testador.getNome()); // DEBUG
            } else {
                System.out.println("    > Testador: NULL"); // DEBUG
            }

            Estrategia estrategia = sessao.getEstrategia();
            sessaoViewMap.put("estrategia", estrategia);
            if (estrategia != null) {
                System.out.println("    > Estrategia ID: " + estrategia.getId() + ", Nome: " + estrategia.getNome()); // DEBUG
            } else {
                System.out.println("    > Estrategia: NULL"); // DEBUG
            }

            SessionStatus status = sessao.getStatus();
            sessaoViewMap.put("status", status);
            System.out.println("    > Status: " + (status != null ? status.name() : "NULL")); // DEBUG


            if (sessao.getCriadoEm() != null) {
                sessaoViewMap.put("criadoEmFormatado", sessao.getCriadoEm().format(formatter));
            } else {
                sessaoViewMap.put("criadoEmFormatado", "");
            }
            if (sessao.getInicioEm() != null) {
                sessaoViewMap.put("inicioEmFormatado", sessao.getInicioEm().format(formatter));
            } else {
                sessaoViewMap.put("inicioEmFormatado", "");
            }
            if (sessao.getFinalizadoEm() != null) {
                sessaoViewMap.put("finalizadoEmFormatado", sessao.getFinalizadoEm().format(formatter));
            } else {
                sessaoViewMap.put("finalizadoEmFormatado", "");
            }
            listaSessoesView.add(sessaoViewMap);
        }

        System.out.println("[SessaoController] listaSessoesView prepared with " + listaSessoesView.size() + " items."); // DEBUG
        request.setAttribute("projeto", projeto);
        request.setAttribute("listaSessoesView", listaSessoesView);
        request.setAttribute("usuarioLogado", usuarioLogado);
        request.setAttribute("currentSortBy", sortBy);
        request.setAttribute("currentSortOrder", sortOrder);

        System.out.println("[SessaoController] Forwarding to /logado/sessao/lista.jsp"); // DEBUG
        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/sessao/lista.jsp");
        dispatcher.forward(request, response);
    }

    private void atualizaStatusSessao(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        Erro erros = new Erro();
        String sessaoIdParam = request.getParameter("sessaoId");
        String novoStatusParam = request.getParameter("novoStatus");
        int sessaoId = -1;

        if (sessaoIdParam != null) try { sessaoId = Integer.parseInt(sessaoIdParam); } catch (NumberFormatException e) { erros.add("ID da Sessão inválido.");}
        else erros.add("ID da Sessão não fornecido.");

        SessionStatus novoStatus = null;
        if (novoStatusParam != null) {
            try { novoStatus = SessionStatus.valueOf(novoStatusParam.toUpperCase()); } catch (IllegalArgumentException e) { erros.add("Status novo inválido.");}
        } else {
            erros.add("Novo status não fornecido.");
        }

        String projetoIdRedirect = request.getParameter("projetoId");

        if (erros.isExisteErros()) {
            request.setAttribute("mensagens", erros);
            // Redirect back or show error
            if (projetoIdRedirect != null && !projetoIdRedirect.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/sessoes/listaPorProjeto?projetoId=" + projetoIdRedirect);
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
            return;
        }

        Sessao sessao = sessaoDAO.get(sessaoId);

        if (sessao == null) {
            erros.add("Sessão não encontrada.");
        } else {
            // R8: Apenas testador e admin conseguem alterar status
            if (!Role.ADMIN.equals(usuarioLogado.getTipo()) && sessao.getTestador().getId() != usuarioLogado.getId()) {
                erros.add("Você não tem permissão para alterar o status desta sessão.");
            } else {
                boolean isValidTransition = false;
                switch (sessao.getStatus()) {
                    case CRIADA:
                        if (novoStatus == SessionStatus.EM_ANDAMENTO || novoStatus == SessionStatus.CANCELADA) isValidTransition = true;
                        break;
                    case EM_ANDAMENTO:
                        if (novoStatus == SessionStatus.FINALIZADA || novoStatus == SessionStatus.CANCELADA) isValidTransition = true;
                        break;
                    case FINALIZADA:
                    case CANCELADA:
                        erros.add("Sessão já está " + sessao.getStatus().name().toLowerCase() + " e não pode ser alterada.");
                        break;
                }

                if (!isValidTransition && !erros.isExisteErros()) {
                    erros.add("Transição de status inválida de " + sessao.getStatus() + " para " + novoStatus + ".");
                }

                if (!erros.isExisteErros()) {
                    sessao.setStatus(novoStatus);
                    if (novoStatus == SessionStatus.EM_ANDAMENTO && sessao.getInicioEm() == null) {
                        sessao.setInicioEm(LocalDateTime.now());
                    } else if (novoStatus == SessionStatus.FINALIZADA || novoStatus == SessionStatus.CANCELADA) {
                        sessao.setFinalizadoEm(LocalDateTime.now());
                        if (sessao.getInicioEm() == null) sessao.setInicioEm(sessao.getCriadoEm());
                    }
                    sessaoDAO.update(sessao);
                }
            }
        }

        if(erros.isExisteErros()){
            request.setAttribute("mensagens", erros);
        }

        if (sessao != null && sessao.getProjeto() != null) {
            response.sendRedirect(request.getContextPath() + "/sessoes/listaPorProjeto?projetoId=" + sessao.getProjeto().getId());
        } else if (projetoIdRedirect != null && !projetoIdRedirect.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/sessoes/listaPorProjeto?projetoId=" + projetoIdRedirect);
        }
        else {
            response.sendRedirect(request.getContextPath() + "/");
        }
    }


    private void mostraDetalhesSessao(HttpServletRequest request, HttpServletResponse response, Usuario usuarioLogado)
            throws ServletException, IOException {
        Erro erros = new Erro();
        String idParam = request.getParameter("id");
        int id = -1;

        if (idParam != null && !idParam.isEmpty()) {
            try {
                id = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                erros.add("ID da sessão inválido.");
            }
        } else {
            erros.add("ID da sessão não fornecido.");
        }

        if (erros.isExisteErros()) {
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
            return;
        }

        Sessao sessao = sessaoDAO.get(id);

        if (sessao == null) {
            erros.add("Sessão não encontrada.");
            request.setAttribute("mensagens", erros);
            RequestDispatcher rd = request.getRequestDispatcher("/erro.jsp");
            rd.forward(request, response);
            return;
        }

        boolean canView = false;
        if (usuarioLogado != null) {
            if (Role.ADMIN.equals(usuarioLogado.getTipo())) {
                canView = true;
            } else if (Role.TESTER.equals(usuarioLogado.getTipo())) {
                if (sessao.getTestador() != null && sessao.getTestador().getId().equals(usuarioLogado.getId())) {
                    canView = true;
                } else {
                }
            }
        }


        if (!canView) {
            acessoNegado(request, response, "Você não tem permissão para ver detalhes desta sessão.");
            return;
        }

        // Pre-format dates for the view
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String criadoEmFormatado = (sessao.getCriadoEm() != null) ? sessao.getCriadoEm().format(formatter) : "";
        String inicioEmFormatado = (sessao.getInicioEm() != null) ? sessao.getInicioEm().format(formatter) : "";
        String finalizadoEmFormatado = (sessao.getFinalizadoEm() != null) ? sessao.getFinalizadoEm().format(formatter) : "";

        request.setAttribute("sessao", sessao);
        request.setAttribute("criadoEmFormatado", criadoEmFormatado);
        request.setAttribute("inicioEmFormatado", inicioEmFormatado);
        request.setAttribute("finalizadoEmFormatado", finalizadoEmFormatado);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/logado/sessao/detalhes.jsp");
        dispatcher.forward(request, response);
    }

    private void acessoNegado(HttpServletRequest request, HttpServletResponse response, String mensagem) throws ServletException, IOException {
        Erro erros = new Erro();
        erros.add("Acesso não autorizado!");
        if (mensagem != null && !mensagem.isEmpty()) {
            erros.add(mensagem);
        }
        request.setAttribute("mensagens", erros);
        RequestDispatcher rd = request.getRequestDispatcher("/noAuth.jsp");
        rd.forward(request, response);
    }
}