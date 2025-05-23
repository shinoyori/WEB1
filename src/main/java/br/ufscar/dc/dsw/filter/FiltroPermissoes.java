package br.ufscar.dc.dsw.filter;

import br.ufscar.dc.dsw.domain.Usuario;

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
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        boolean recursoPublico = uri.contains("/login") ||
                uri.contains("/erro.jsp") ||
                uri.contains("/noAuth.jsp") ||
                uri.contains("/estrategias") ||
                uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png");

        if (recursoPublico) {
            chain.doFilter(req, res);
            return;
        }

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        if ("TESTADOR".equals(usuario.getTipo())) {
            if (uri.contains("/usuarios") || uri.contains("/projetos") || uri.contains("/admin")) {
                redirecionaNaoAutorizado(request, response);
                return;
            }
        }

        // ADMIN pode acessar tudo

        chain.doFilter(req, res);
    }

    private void redirecionaNaoAutorizado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        request.getSession().setAttribute("mensagens", new br.ufscar.dc.dsw.util.Erro("Você não tem permissão para acessar esta página."));
        response.sendRedirect(request.getContextPath() + "/noAuth.jsp");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException { }

    @Override
    public void destroy() { }
}
