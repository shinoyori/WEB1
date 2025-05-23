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
        HttpSession session = request.getSession(false);
        Usuario usuario = (session != null) ? (Usuario) session.getAttribute("usuarioLogado") : null;

        // Lista de recursos públicos que não exigem autenticação
        boolean recursoPublico = uri.contains("/login") || // O controller /login agora trata o login.jsp e o POST do login
                uri.contains("/erro.jsp") ||
                uri.contains("/noAuth.jsp") ||
                uri.contains("/estrategias") ||
                uri.endsWith(".css") || uri.endsWith(".js") || uri.endsWith(".png") ||
                uri.equals(request.getContextPath() + "/") || // URL raiz
                uri.equals(request.getContextPath() + "/index.jsp") || // index.jsp (se usado como redirect ou link)
                uri.equals(request.getContextPath() + "/home.jsp"); // A nova home page pública


        if (recursoPublico) {
            chain.doFilter(req, res);
            return;
        }

        //se o usuário não está logado e o recurso não é público, redireciona para a home page ou login
        if (usuario == null) {
            // Pode redirecionar para a home page pública ou para o login
            response.sendRedirect(request.getContextPath() + "/login.jsp"); // Redireciona para o login.jsp caso não logado
            return;
        }

        //ADMIN: tem acesso a tudo
        if (usuario.getTipo() == Role.ADMIN) {
            chain.doFilter(req, res); // ADMIN sempre pode acessar tudo
            return;
        }

        //TESTER: Não pode acessar áreas de ADMIN
        if (usuario.getTipo() == Role.TESTER) {
            if (uri.contains("/admin") || uri.contains("/usuarios/cadastro") ||
                    uri.contains("/usuarios/insercao") || uri.contains("/usuarios/edicao") ||
                    uri.contains("/usuarios/atualizacao") || uri.contains("/usuarios/remocao")) {

                redirecionaNaoAutorizado(request, response);
                return;
            }
            chain.doFilter(req, res); // TESTER pode acessar o restante
            return;
        }

        //GUEST: Apenas pode acessar recursos públicos
        //se chegou aqui, é GUEST e o recurso não é público, então bloqueia
        if (usuario.getTipo() == Role.GUEST) {
            redirecionaNaoAutorizado(request, response); // GUEST não pode acessar nada além dos recursos públicos
            return;
        }
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