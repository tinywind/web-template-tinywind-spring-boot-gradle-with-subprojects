package org.tinywind.server.web.filter;

import org.tinywind.server.web.request.CachedHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Predicate;

public class CachedHttpServletRequestFilter extends OncePerRequestFilter {

    private final Predicate<HttpServletRequest> excluded;

    public CachedHttpServletRequestFilter(Predicate<HttpServletRequest> excluded) {
        this.excluded = excluded;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        final String contentType = request.getContentType();
        final boolean multipart = contentType != null && contentType.regionMatches(true, 0, "multipart/", 0, 10);
        if (!excluded.test(request) && !multipart) {
            CachedHttpServletRequest cachedBodyHttpServletRequest = new CachedHttpServletRequest(request);
            filterChain.doFilter(cachedBodyHttpServletRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}