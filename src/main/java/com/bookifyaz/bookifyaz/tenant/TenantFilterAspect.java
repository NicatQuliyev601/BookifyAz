package com.bookifyaz.bookifyaz.tenant;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hibernate.Session;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantFilterAspect {

    @PersistenceContext
    private EntityManager entityManager;

    @Around("execution(* com.bookifyaz.bookifyaz.repository.*.*(..))")
    public Object applyTenantFilter(ProceedingJoinPoint pjp) throws Throwable {
        Integer tenantId = TenantContext.getCurrentTenantId();
        Session session = entityManager.unwrap(Session.class);

        if (tenantId != null) {
            session.enableFilter("tenantFilter").setParameter("tenantId", tenantId);
        }
        try {
            return pjp.proceed();
        } finally {
            if (tenantId != null) {
                session.disableFilter("tenantFilter");
            }
        }
    }
}
