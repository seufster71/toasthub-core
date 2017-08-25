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

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("AppCacheClientDomains")
@Scope("singleton")
public class AppCacheClientDomains implements Serializable {

	private static final long serialVersionUID = 1L;
	private Map<String,ClientDomain> clientDomains = new ConcurrentHashMap<String,ClientDomain>();
	
	// Constructor
	public AppCacheClientDomains(){
	}
	
	public void clearCache(){
		this.setClientDomains(new ConcurrentHashMap<String,ClientDomain>());
	}

	// Client Domains
	public Map<String,ClientDomain> getClientDomains() {
		return clientDomains;
	}

	public void setClientDomains(Map<String,ClientDomain> clientDomains) {
		this.clientDomains = clientDomains;
	}

	public void clearClientDomainCache(){
		this.clientDomains = null;
		this.clientDomains = new ConcurrentHashMap<String,ClientDomain>();
	}
	
	public ClientDomain getClientDomain(String url){
		
		ClientDomain cdomain = null;
		if (url != null && this.clientDomains.containsKey(url)){
			// domain exist in cache 
			cdomain = this.clientDomains.get(url);
		}
		return cdomain;
	}
}
