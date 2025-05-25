package br.ufscar.dc.dsw.filter;

import br.ufscar.dc.dsw.domain.Usuario;
import br.ufscar.dc.dsw.domain.enums.Role;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class FiltroPermissoes implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String uri = request.getRequestURI();
        String contextPath = request.getContextPath();
        String path = uri.substring(contextPath.length());


        boolean recursoPublico = path.equals("/") ||
        path.equals("/home") ||
                path.equals("/login") ||
                path.equals("/login.jsp") ||
                path.equals("/home.jsp") ||
                path.equals("/erro.jsp") ||
                path.equals("/noAuth.jsp") ||
                path.startsWith("/estrategias") ||
                uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") ||
                uri.endsWith(".ico") || uri.endsWith(".jpg") || uri.endsWith(".jpeg") || uri.endsWith(".gif");


        if (recursoPublico) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if (usuario.getTipo() == Role.ADMIN) {
            chain.doFilter(req, res);
            return;
        }
        if (usuario.getTipo() == Role.TESTER) {
            if (uri.contains("/admin") || uri.contains("/usuarios/cadastro") ||
                    uri.contains("/usuarios/insercao") || uri.contains("/usuarios/edicao") ||
                    uri.contains("/usuarios/atualizacao") || uri.contains("/usuarios/remocao")) {

                redirecionaNaoAutorizado(request, response);
                return;
            }
            chain.doFilter(req, res);
            return;
        }
        if (usuario.getTipo() == Role.GUEST) {
            redirecionaNaoAutorizado(request, response);
            return;
        }
        chain.doFilter(req, res);
    }

    private void redirecionaNaoAutorizado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.setAttribute("mensagens", new br.ufscar.dc.dsw.util.Erro("Você não tem permissão para acessar esta página."));
        try {
            request.getRequestDispatcher("/noAuth.jsp").forward(request, response);
        } catch (ServletException e) {
            throw new IOException("Erro ao encaminhar para noAuth.jsp", e);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }
}