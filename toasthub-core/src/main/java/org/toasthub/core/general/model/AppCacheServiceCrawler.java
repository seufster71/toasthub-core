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

package org.toasthub.core.general.model;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.utils.TenantContext;
import org.toasthub.core.serviceCrawler.ServiceCrawlerSvc;
import org.toasthub.core.system.model.AppCacheClientDomains;

@Component("ServiceCrawler")
@Scope("singleton")
public class AppCacheServiceCrawler implements Serializable {

	private static final long serialVersionUID = 1L;
	// appDomain -> category -> service-apiVersion-appVersion
	private Map<String,Map<String,Map<String,ServiceClass>>> services = new ConcurrentHashMap<String,Map<String,Map<String,ServiceClass>>>();
	
	@Autowired
	@Qualifier("ServiceCrawlerSvc")
	protected ServiceCrawlerSvc serviceCrawlerSvc;
	
	@Autowired
	protected ApplicationContext context;
	
	@Autowired
	AppCacheClientDomains appCacheClientDomains;
	
	// Constructor
	public AppCacheServiceCrawler(){
	}
	
	// This is used by frontend web service and should only pull what is in memory.
	public ServiceClass getServiceClass(String category, String key, String apiVersion, String appVersion){
		String tenant = appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getCustDomain();
		ServiceClass serviceClass = null;
		//String categoryKey = category + "-" + appDomain;
		String apiKey = key  + "-" + apiVersion + "-" + appVersion;
		if ( this.services != null && this.services.get(tenant) != null && this.services.get(tenant).get(category) != null && this.services.get(tenant).get(category).containsKey(apiKey) ){
			// Pull from memory
			serviceClass = this.services.get(tenant).get(category).get(apiKey);
			if ("LOCAL".equals(serviceClass.getLocation())) {
				serviceClass.setServiceProcessor((ServiceProcessor) context.getBean(serviceClass.getClassName()));
			}
		} else {
			// do not pull from db it will cause load issue if not available. Also all services should have been loaded.
			serviceClass = null;
		}
		return serviceClass;
	}
	
	// This is used by save or delete is issued and the cache needs to be reloaded
	public void reloadServiceCache() {
		String tenant = appCacheClientDomains.getClientDomain(TenantContext.getURLDomain()).getCustDomain();
		loadServiceCache(tenant);
	};
	
	// This is used by the initializeCache
	public void loadServiceCache(String tenant) {
		if (this.services != null && this.services.containsKey(tenant)) {
			this.services.remove(tenant);
		}
		this.services.put(tenant, serviceCrawlerSvc.getServices());
	}
	
}
