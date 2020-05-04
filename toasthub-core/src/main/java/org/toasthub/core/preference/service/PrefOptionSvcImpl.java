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

package org.toasthub.core.preference.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.PrefCache;
import org.toasthub.core.preference.model.PrefOptionName;
import org.toasthub.core.preference.repository.PrefOptionDao;

@Service("PrefOptionSvc")
public class PrefOptionSvcImpl implements ServiceProcessor, PrefOptionSvc {

	@Autowired
	@Qualifier("PrefOptionDao")
	PrefOptionDao prefOptionDao;
	
	@Autowired
	UtilSvc utilSvc;
	
	@Autowired
	PrefCache prefCache;
	
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParam(GlobalConstant.ACTION);
		
		Long count = 0l;
		switch (action) {
		case "LIST":
			itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
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
			prefOptionDao.itemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Count failed", response);
			e.printStackTrace();
		}
	}
	
	public void items(RestRequest request, RestResponse response) {
		try {
			prefOptionDao.items(request, response);
			// null values because they are lazy loaded but empty
			List<PrefOptionName> items = (List<PrefOptionName>) response.getParam(GlobalConstant.ITEMS);
			for (PrefOptionName item : items) {
				item.setValues(null);
			}
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "List failed", response);
			e.printStackTrace();
		}
	}
	
	public void item(RestRequest request, RestResponse response) {
		try {
			prefOptionDao.item(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Item Failed", response);
			e.printStackTrace();
		}
	}
	
}
