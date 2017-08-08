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

package org.toasthub.core.system.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.toasthub.core.system.service.ClientDomainSvc;

@Component("AppCacheClientDomainsUtil")
public class AppCacheClientDomainsUtil {

	@Autowired
	@Qualifier("ClientDomainSvc")
	ClientDomainSvc clientDomainSvc;
	
	@Autowired
	AppCacheClientDomains appCacheClientDomains;
	
	public void loadCache(){
		List<ClientDomain> clientDomains = clientDomainSvc.loadCache();
		if (clientDomains != null){
			// add it to cache
			Map<String,ClientDomain> cacheItems = appCacheClientDomains.getClientDomains();
			for (Iterator<ClientDomain> iterator = clientDomains.iterator(); iterator.hasNext();) {
				ClientDomain clientDomain = (ClientDomain) iterator.next();
				cacheItems.put(clientDomain.getURLDomain(), clientDomain);
			}
			//appCacheClientDomains.setClientDomains(cacheItems);
		}
	}
	
	public List<String> getTenantList() {
		List<String> items = new ArrayList<String>();
		Map<String,ClientDomain> cacheItems = appCacheClientDomains.getClientDomains();
		
		for (Map.Entry<String, ClientDomain> pair : cacheItems.entrySet()) {
		    String domain = ((ClientDomain) pair.getValue()).getAPPDomain();
		    if (!items.contains(domain)) {
		    	items.add(domain);
		    }
		}
	    
	    return items;
	}
}
