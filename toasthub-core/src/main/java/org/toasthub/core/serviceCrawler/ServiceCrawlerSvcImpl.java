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

package org.toasthub.core.serviceCrawler;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.model.ServiceClass;

@Service("ServiceCrawlerSvc")
public class ServiceCrawlerSvcImpl implements ServiceProcessor, ServiceCrawlerSvc {

	@Autowired
	@Qualifier("ServiceCrawlerDao")
	ServiceCrawlerDao serviceCrawlerDao;
	
	@Autowired
	protected UtilSvc utilSvc;
	
	@Override
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		
		//Long count = 0l;
		switch (action) {
		case "LIST":
			
			break;
		case "SHOW":
			
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, "Action not available", response);
			break;
		}
	}
	
	// get services
	public Map<String,Map<String,ServiceClass>> getServices() {
		return serviceCrawlerDao.getServices();
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) {
		try {
			serviceCrawlerDao.itemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Count failed", response);
			e.printStackTrace();
		}
	}

	@Override
	public void item(RestRequest request, RestResponse response) {
		try {
			serviceCrawlerDao.item(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Item failed", response);
			e.printStackTrace();
		}
	}

	@Override
	public void items(RestRequest request, RestResponse response) {
		try {
			serviceCrawlerDao.items(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "List failed", response);
			e.printStackTrace();
		}
	}

	
	protected void initParams(RestRequest request) {
		if (!request.containsParam(GlobalConstant.SEARCHCOLUMN)){
			request.addParam(GlobalConstant.SEARCHCOLUMN, "serviceName");
		}
		if (!request.containsParam(GlobalConstant.ITEMNAME)){
			request.addParam(GlobalConstant.ITEMNAME, "ServiceClass");
		}
		if (!request.containsParam(GlobalConstant.ORDERCOLUMN)) {
			request.addParam(GlobalConstant.ORDERCOLUMN, "category,serviceName");
		}
		if (!request.containsParam(GlobalConstant.ORDERDIR)) {
			request.addParam(GlobalConstant.ORDERDIR, "ASC");
		}
	}

}
