package org.balancetonrappeur.config;

import jakarta.servlet.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HexFormat;

@Component
public class AdminAuthFilter implements Filter {

    public static final String COOKIE_NAME    = "btr_admin";
    private static final int   SESSION_SECONDS = 8 * 3600;

    private final String adminPassword;

    public AdminAuthFilter(@Value("${btr.admin.password:}") String adminPassword) {
        this.adminPassword = adminPassword;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        var request  = (HttpServletRequest) req;
        var response = (HttpServletResponse) res;
        var path     = request.getRequestURI();

        if (!path.startsWith("/admin") || path.equals("/admin/login")) {
            chain.doFilter(req, res);
            return;
        }

        if (isAuthenticated(request)) {
            chain.doFilter(req, res);
            return;
        }

        response.sendRedirect("/admin/login");
    }

    /** Vérifie que le mot de passe soumis correspond au mot de passe configuré. */
    public boolean checkPassword(String submitted) {
        return !adminPassword.isBlank() && hash(submitted).equals(hash(adminPassword));
    }

    /** Crée le cookie d'authentification admin. */
    public Cookie createAuthCookie(HttpServletRequest request) {
        var cookie = new Cookie(COOKIE_NAME, hash(adminPassword));
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(SESSION_SECONDS);
        if (isHttps(request)) cookie.setSecure(true);
        return cookie;
    }

    /** Crée un cookie vide pour invalider la session. */
    public Cookie createLogoutCookie() {
        var cookie = new Cookie(COOKIE_NAME, "");
        cookie.setMaxAge(0);
        cookie.setPath("/");
        return cookie;
    }

    private boolean isHttps(HttpServletRequest request) {
        String proto = request.getHeader("X-Forwarded-Proto");
        return "https".equalsIgnoreCase(proto) || request.isSecure();
    }

    private boolean isAuthenticated(HttpServletRequest request) {
        if (request.getCookies() == null) return false;
        return Arrays.stream(request.getCookies())
                .filter(c -> COOKIE_NAME.equals(c.getName()))
                .anyMatch(c -> c.getValue().equals(hash(adminPassword)));
    }

    private String hash(String value) {
        try {
            var digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 non disponible", e);
        }
    }
}
