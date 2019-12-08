package main;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HeaderManageFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // Header pre ochranu proti ClickJacking utokom
        response.addHeader("X-Frame-Options", "DENY");
        // Header for  web browser's XSS filter enable
        response.addHeader("X-XSS-Protection", "1");
        // Anti-MIME-Sniffing header X-Content-Type-Options
        // Ochrana proti mime sniffing
        response.addHeader("X-Content-Type-Options", "nosniff");
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
