package org.toasthub.core.general.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;
import org.toasthub.core.general.utils.TenantContext;

public class TenantInterceptor extends GenericFilterBean {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private static final String TENANT_HEADER_NAME = "X-TENANT-ID";
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		
		TenantContext.setURLDomain(request.getServerName());
		logger.info("filter urlDomain " + request.getServerName());
		
		String tenantId = request.getHeader(TENANT_HEADER_NAME);
		if (tenantId == null) {
			tenantId = "internet";
		}
		TenantContext.setTenantId(tenantId);
		
		chain.doFilter(req, res);
	}

}
