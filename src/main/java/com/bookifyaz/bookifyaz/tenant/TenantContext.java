package com.bookifyaz.bookifyaz.tenant;

public class TenantContext {

    private static final ThreadLocal<Integer> currentTenantId = new ThreadLocal<>();

    public static void setCurrentTenantId(Integer tenantId) {
        currentTenantId.set(tenantId);
    }

    public static Integer getCurrentTenantId() {
        return currentTenantId.get();
    }

    public static void clear() {
        currentTenantId.remove();
    }
}
