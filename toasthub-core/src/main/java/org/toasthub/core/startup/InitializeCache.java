/*
 * Copyright (C) 2016 The ToastHub Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.toasthub.core.startup;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.toasthub.core.general.model.AppCacheMenu;
import org.toasthub.core.general.utils.TenantContext;
import org.toasthub.core.preference.model.AppCachePageUtil;
import org.toasthub.core.system.model.AppCacheClientDomainsUtil;

@Component("InitializeCache")
public class InitializeCache {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AppCacheClientDomainsUtil appCacheClientDomainUtil;
	
	@Autowired
	AppCachePageUtil appCachePageUtil;
	
	@Autowired
	AppCacheMenu appCacheMenu;
	
	@EventListener(ContextRefreshedEvent.class)
	public void contextRefreshedEvent() {
		logger.info(" Pre Loading client domain");
		appCacheClientDomainUtil.loadCache();
		
		List<String> tenantList = appCacheClientDomainUtil.getTenantList();
		for (String tenant : tenantList) {
			TenantContext.setURLDomain(null);
			TenantContext.setTenantId(tenant);
			logger.info(" Pre loading language cache " + tenant);
			appCachePageUtil.loadLanguageCache(tenant);
			
			logger.info(" Pre loading language cache " + tenant);
			appCacheMenu.loadMenuCache(tenant);
			
			
		}
	}
}
