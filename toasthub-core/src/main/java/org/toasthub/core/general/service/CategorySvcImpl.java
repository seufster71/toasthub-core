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

package org.toasthub.core.general.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.repository.CategoryDao;
import org.toasthub.core.preference.model.AppCachePage;

@Service("CategorySvcImpl")
public class CategorySvcImpl implements ServiceProcessor, CategorySvc {

	@Autowired 
	@Qualifier("CategoryDao")
	CategoryDao categoryDao;
	
	@Autowired 
	UtilSvc utilSvc;
	
	@Autowired
	AppCachePage appCachePage;
	
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParam(GlobalConstant.ACTION);
		// get option from main
		request.addParam("sysPageFormName", "MEMBER_CATEGORY");
		request.addParam("sysPageLabelName", "MEMBER_CATEGORY");
		request.addParam("sysPageTextName", "MEMBER_CATEGORY");
		
		Long count = 0l;
		switch (action) {
		case "LIST":
			itemCount(request, response);
			count = (Long) response.getParam("count");
			if (count != null && count > 0){
				items(request, response);
			}
			break;
		case "SHOW":
			item(request, response);
			break;
		default:
			utilSvc.addStatus(RestResponse.INFO, RestResponse.ACTIONNOTEXIST, "Action not available", response);
			break;
		}
	}
	
	public void itemCount(RestRequest request, RestResponse response) {
		try {
			categoryDao.itemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Count failed", response);
			e.printStackTrace();
		}
	}
	
	public void items(RestRequest request, RestResponse response) {
		try {
			categoryDao.items(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "List failed", response);
			e.printStackTrace();
		}
	}
	
	public void item(RestRequest request, RestResponse response) {
		try {
			categoryDao.item(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Item Failed", response);
			e.printStackTrace();
		}
	}
	
}
