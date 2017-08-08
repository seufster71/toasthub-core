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
import org.toasthub.core.general.service.ServiceCrawlerSvc;

@Component("ServiceCrawler")
@Scope("singleton")
public class ServiceCrawler implements Serializable {

	private static final long serialVersionUID = 1L;
	// appDomain -> category -> service-apiVersion-appVersion
	private Map<String,Map<String,Map<String,ServiceClass>>> services = null;
	
	@Autowired
	@Qualifier("ServiceCrawlerSvc")
	protected ServiceCrawlerSvc serviceCrawlerSvc;
	
	@Autowired
	protected ApplicationContext context;
	
	// Constructor
	public ServiceCrawler(){
	}
	
	//@Schedule(hour="12")
	public void clearCache(){
		this.setServices(null);
	}
	
	//
	//@Lock(LockType.READ)
	public ServiceProcessor getService(String category, String key, String apiVersion, String appVersion, String appDomain){
		if (this.services == null || !this.services.containsKey(appDomain)) {
			this.getServices(appDomain);
		}
		//String categoryKey = category + "-" + appDomain;
		String apiKey = key  + "-" + apiVersion + "-" + appVersion;
		if ( this.services != null && this.services.get(appDomain) != null && this.services.get(appDomain).get(category) != null && this.services.get(appDomain).get(category).containsKey(apiKey) ){
			ServiceClass sc = this.services.get(appDomain).get(category).get(apiKey);
			if (sc.getServiceProcessor() == null){
				try {
					this.getProcessor(sc);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return sc.getServiceProcessor();
		} else {
			return null;
		}
	}
	
	private void setServices(Map<String,Map<String,Map<String,ServiceClass>>> services) {
		this.services = services;
	}
	
	//@Lock(LockType.WRITE)
	private void getServices(String appDomain){
		if (this.services == null){
			this.services = new ConcurrentHashMap<String,Map<String,Map<String,ServiceClass>>>(); 
		}
		this.services.put(appDomain, serviceCrawlerSvc.getServices());
	}
	
	//@Lock(LockType.WRITE)
	private void getProcessor(ServiceClass sc) throws ClassNotFoundException{
		if (sc.getServiceProcessor() == null){
			sc.setServiceProcessor((ServiceProcessor) context.getBean(sc.getClassName()));
		}
	}
	
	//@Lock(LockType.WRITE)
	public void clearServicesCache(){
		this.setServices(null);
	}
	
	
}
