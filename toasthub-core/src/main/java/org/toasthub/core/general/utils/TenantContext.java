package org.toasthub.core.general.utils;

public class TenantContext {

	private static final ThreadLocal<String> CONTEXT = new ThreadLocal<String>();
	private static final ThreadLocal<String> hostname = new ThreadLocal<String>();
	private static final ThreadLocal<String> contextPath = new ThreadLocal<String>();
	
	public static void setTenantId(String tenantId) {
		CONTEXT.set(tenantId);
	}
	public static String getTenantId() {
		return CONTEXT.get();
	}
	
	public static void setURLDomain(String name){
		hostname.set(name);
	}
	public static String getURLDomain(){
    	return hostname.get();
    }
	
	public static String getContextpath() {
		return contextPath.get();
	}
	public static void setContextPath(String name){
		contextPath.set(name);
	}
}
