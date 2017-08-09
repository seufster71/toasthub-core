package org.toasthub.core.general.utils;

public class TenantContext {

	private static ThreadLocal<String> tenant = new ThreadLocal<String>();
	private static ThreadLocal<String> hostname = new ThreadLocal<String>();
	
	public static void setTenantId(String tenantId) {
		tenant.set(tenantId);
	}
	public static String getTenantId() {
		return tenant.get();
	}
	
	public static void setURLDomain(String name){
		hostname.set(name);
	}
	public static String getURLDomain(){
    	return hostname.get();
    }
	
	public static void clear() {
		tenant.remove();
		hostname.remove();
	}
	
	
}
