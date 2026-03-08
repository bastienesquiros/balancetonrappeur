package org.balancetonrappeur.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.HexFormat;

@Component
public class AdminAuthFilter implements Filter {

    public static final String COOKIE_NAME = "btr_admin";

    @Value("${btr.admin.password:}")
    private String adminPassword;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        var request  = (HttpServletRequest) req;
        var response = (HttpServletResponse) res;
        var path     = request.getRequestURI();

        // Seules les routes /admin/** sont protégées
        if (!path.startsWith("/admin")) {
            chain.doFilter(req, res);
            return;
        }

        // La page de login est accessible sans cookie
        if (path.equals("/admin/login")) {
            chain.doFilter(req, res);
            return;
        }

        // Vérifier le cookie
        if (isAuthenticated(request)) {
            chain.doFilter(req, res);
            return;
        }

        response.sendRedirect("/admin/login");
    }

    boolean isAuthenticated(HttpServletRequest request) {
        if (request.getCookies() == null) return false;
        return Arrays.stream(request.getCookies())
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .anyMatch(c -> c.getValue().equals(hash(adminPassword)));
    }

    public String hash(String value) {
        try {
            var md = MessageDigest.getInstance("SHA-256");
            var digest = md.digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            return value;
        }
    }
}

