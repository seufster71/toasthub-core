package org.toasthub.core.common;

import javax.persistence.EntityManager;

public interface EntityManagerMainSvc {

	public EntityManager getEntityMgrMain();
	public String getAppName();
	public String getAppDomain();
	public String getHTMLPrefix();
	public String getPublicLayout();
	public String getAdminLayout();
	public String getMemberLayout();
	public String getSysAdminLayout();
}
