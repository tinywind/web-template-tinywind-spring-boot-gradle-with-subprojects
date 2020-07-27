package org.tinywind.server.web.filter;

import org.tinywind.server.web.request.CachedHttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Predicate;

public class CachedHttpServletRequestFilter extends OncePerRequestFilter {

    private final Predicate<HttpServletRequest> excluded;

    public CachedHttpServletRequestFilter(Predicate<HttpServletRequest> excluded) {
        this.excluded = excluded;
    }

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
        if (!excluded.test(request)) {
            CachedHttpServletRequest cachedBodyHttpServletRequest = new CachedHttpServletRequest(request);
            filterChain.doFilter(cachedBodyHttpServletRequest, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}