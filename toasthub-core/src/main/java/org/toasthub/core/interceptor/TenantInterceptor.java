package org.toasthub.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.toasthub.core.general.utils.TenantContext;

public class TenantInterceptor extends HandlerInterceptorAdapter {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String TENANT_HEADER_NAME = "X-TENANT-ID";
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		TenantContext.clear();
		logger.info("clear tenantContext ");
		super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		/*TenantContext.setURLDomain(request.getServerName());
		logger.info("urlDomain " + request.getServerName());
		
		String tenantId = request.getHeader(TENANT_HEADER_NAME);
		if (tenantId == null) {
			tenantId = "internet";
		}
		TenantContext.setTenantId(tenantId);
		*/
		return super.preHandle(request, response, handler);
	}
}
