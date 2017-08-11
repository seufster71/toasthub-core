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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.AppCacheMenuUtil;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.MenuItem;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.AppCachePageUtil;

@Service("PublicSvc")
public class PublicSvc implements ServiceProcessor {

	@Autowired 
	UtilSvc utilSvc;
	
	@Autowired 
	MailSvc mailSvc;
	
	@Autowired 
	EntityManagerMainSvc entityManagerMainSvc;
	
	@Autowired 
	@Qualifier("MenuSvc")
	MenuSvc menuSvc;
	
	@Autowired 
	AppCacheMenuUtil appCacheMenuUtil;
	
	@Autowired 
	AppCachePageUtil appCachePageUtil;

	// Constructors
	public PublicSvc() {}
	
	// Processor
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParams().get(BaseEntity.ACTION);
		
		this.setupDefaults(request);
		appCachePageUtil.getPageInfo(request,response);
		switch (action) {
		case "INIT": 
			this.init(request, response);
			break;
		case "INIT_MENU":
			this.initMenu(request, response);
			break;
		default:
			break;
		}
		
	
	}
	
	public void init(RestRequest request, RestResponse response) {
		response.addParam(BaseEntity.PAGELAYOUT,entityManagerMainSvc.getPublicLayout());
		response.addParam(BaseEntity.APPNAME,entityManagerMainSvc.getAppName());
		response.addParam(BaseEntity.HTMLPREFIX, entityManagerMainSvc.getHTMLPrefix());
		// default language code
		response.addParam("userLang", appCachePageUtil.getDefaultLang());
		
	}
	
	public void initMenu(RestRequest request, RestResponse response){
		Map<Integer,MenuItem> menu = null;
		Map<String,Map<Integer,MenuItem>> menuList = new HashMap<String,Map<Integer,MenuItem>>();
		
		ArrayList<String> mylist = (ArrayList<String>) request.getParam(BaseEntity.MENUNAMES);
		for (String menuName : mylist) {
			menu = appCacheMenuUtil.getMenu(menuName,(String)request.getParam(BaseEntity.MENUAPIVERSION),(String)request.getParam(BaseEntity.MENUAPPVERSION),(String)request.getParam(BaseEntity.LANG));
			menuList.put(menuName, menu);
		}
		
		if (!menuList.isEmpty()){
			response.addParam(RestResponse.MENUS, menuList);
		} else {
			utilSvc.addStatus(RestResponse.INFO, RestResponse.SUCCESS, "Menu Issue", response);
		}
	}
	
	public void setupDefaults(RestRequest request){
		
		if (!request.containsParam(BaseEntity.MENUAPIVERSION)){
			request.addParam(BaseEntity.MENUAPIVERSION, "1.0");
		}

		if (!request.containsParam(BaseEntity.MENUAPPVERSION)){
			request.addParam(BaseEntity.MENUAPPVERSION, "1.0");
		}
		
		if (!request.containsParam(BaseEntity.MENUNAMES)){
			ArrayList<String> myList = new ArrayList<String>();
			myList.add("PUBLIC_MENU_LEFT");
			myList.add("PUBLIC_MENU_RIGHT");
			request.addParam(BaseEntity.MENUNAMES, myList);
		}
	}
	
}