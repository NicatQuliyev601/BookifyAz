package com.bookifyaz.bookifyaz.tenant;

import com.bookifyaz.bookifyaz.repository.TenantRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TenantResolutionFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantResolutionFilter.class);

    private final TenantRepository tenantRepository;

    public TenantResolutionFilter(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String slug = resolveSlug(request);
            if (slug != null) {
                tenantRepository.findBySlug(slug)
                        .ifPresentOrElse(
                                t -> {
                                    TenantContext.setCurrentTenantId(t.getId());
                                    log.info("[Tenant] slug={} → tenant_id={}", slug, t.getId());
                                },
                                () -> log.info("[Tenant] slug='{}' tapılmadı", slug)
                        );
            } else {
                log.info("[Tenant] slug yoxdur, tenant təyin edilmədi");
            }
            filterChain.doFilter(request, response);
        } finally {
            TenantContext.clear();
        }
    }

    // Local dev: X-Tenant-Slug header; Production: subdomain
    private String resolveSlug(HttpServletRequest request) {
        String header = request.getHeader("X-Tenant-Slug");
        if (header != null && !header.isBlank()) {
            return header;
        }
        String host = request.getServerName();
        if (host == null || host.equals("localhost") || !host.contains(".")) {
            return null;
        }
        return host.split("\\.")[0];
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/auth/");
    }
}
