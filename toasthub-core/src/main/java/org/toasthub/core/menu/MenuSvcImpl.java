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

package org.toasthub.core.menu;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.Menu;
import org.toasthub.core.general.model.MenuItem;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.AppCachePageUtil;


@Service("MenuSvc")
public class MenuSvcImpl implements ServiceProcessor, MenuSvc {

	@Autowired 
	@Qualifier("MenuDao")
	MenuDao menuDao;
	
	@Autowired
	protected UtilSvc utilSvc;
	
	@Autowired
	protected AppCachePageUtil appCachePageUtil;
	
	// Constructors
	public MenuSvcImpl() {}
	
	// Processor
	@Override
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		
		Long count = 0l;
		switch (action) {
		case "INIT": 
			appCachePageUtil.getPageInfo(request,response);
			//appCachePage.getGlobalInfo(request,response);
			
			this.initParams(request);
			this.itemColumns(request, response);
			
			this.getMenuCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.getMenus(request, response);
			}

			break;
		case "LIST":
			request.addParam("appPageParamLoc", "response");
			appCachePageUtil.getPageInfo(request,response);
			
			this.initParams(request);
			this.itemColumns(request, response);
			
			this.getMenuCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.getMenus(request, response);
			}

			break;
		case "SHOW":
			this.getMenu(request, response);
			if (request.containsParam(GlobalConstant.PARENTID)){
				response.addParam(GlobalConstant.PARENTID,(Integer) request.getParam(GlobalConstant.PARENTID));
			}
			response.addParam(Menu.ID,(Integer) request.getParam(Menu.ID));
			response.addParam(GlobalConstant.ID,request.getParam(GlobalConstant.ID));
			break;
		default:
			break;
		}
		
		
	}
	
	public void getMenu(RestRequest request, RestResponse response) {
		try {
			menuDao.getMenu(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Item failed", response);
			e.printStackTrace();
		}
		//List<MenuItem> items = (List<MenuItem>) response.getParam("menuItems");
		//response.addParam("menuItems", items);
	}
	
	public void getMenus(RestRequest request, RestResponse response) {
		try {
			menuDao.getMenus(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "List failed", response);
			e.printStackTrace();
		}
	}
	
	public void getMenuCount(RestRequest request, RestResponse response) {
		try {
			menuDao.getMenuCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Count failed", response);
			e.printStackTrace();
		}
	}
	
	public void getMenuItemCount(RestRequest request, RestResponse response) {
		try {
			menuDao.getMenuItemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Count failed", response);
			e.printStackTrace();
		}
	}
	
	public void getMenuItems(RestRequest request, RestResponse response) {
		try {
			menuDao.getMenuItems(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "List failed", response);
			e.printStackTrace();
		}
		// update menu items with parentId
		List<MenuItem> items = (List<MenuItem>) response.getParam(GlobalConstant.ITEMS);
		for(MenuItem m : items){
			if (m.getParent() != null) {
				m.setParentId(m.getParent().getId());
				m.setMenuId(m.getMenu().getId());
			}
		}
	}
	
	public List<MenuItem> getMenuItems(String menuName, String lang) {
		try {
			return menuDao.getMenuItems(menuName, lang);
		} catch (Exception e) {
			return null;
		}
	}

	public Map<Integer,MenuItem> getMenu(String menuName, String apiVersion, String appVersion, String lang) {
		Menu menu = null;
		try {
			menu = menuDao.getMenu(menuName,apiVersion, appVersion, lang);
		} catch (Exception e) {
			return null;
		}
		return this.orgMenu(menu);
	}

	protected Map<Integer,MenuItem> orgMenu(Menu menu) {
		if (menu != null) {
			Set<MenuItem> items = menu.getMenuItems();
			Map<Integer,MenuItem> topRow = new HashMap<Integer,MenuItem>();
			for(MenuItem m : items){
				if (m.getParent() == null){
					// parent
					Map<Integer,MenuItem> children = this.findChildren(m, items);
					m.setChildren(children);
					topRow.put(m.getOrder(), m);
				}
				
			}
			return topRow;
		} else {
			return null;
		}
	}
	
	protected Map<Integer,MenuItem> findChildren(MenuItem parent,Set<MenuItem> items) {
		Map<Integer,MenuItem> myItems = null;
		for(MenuItem m : items){
			if (m.getParent() == parent){
				if (myItems == null){
					myItems = new HashMap<Integer,MenuItem>();
				}
				myItems.put(m.getOrder(), m);
				m.setChildren(this.findChildren(m,items));
			}
		}
		return myItems;
	}

	protected void itemColumns(RestRequest request, RestResponse response) {
		String itemName = (String) request.getParam(GlobalConstant.ITEMNAME);
		if (itemName != null && itemName.equals("Menu")) {
			request.addParam(GlobalConstant.COLUMNS, Menu.columns);
			request.addParam(GlobalConstant.DATATYPES, Menu.dataTypes);
			response.addParam(GlobalConstant.COLUMNS, request.getParam(GlobalConstant.COLUMNS));
			response.addParam(GlobalConstant.DATATYPES, request.getParam(GlobalConstant.DATATYPES));
		}
	}
	
	protected void initParams(RestRequest request) {
		if (!request.containsParam(GlobalConstant.SEARCHCOLUMN)){
			request.addParam(GlobalConstant.SEARCHCOLUMN, "title");
		}
		if (!request.containsParam(GlobalConstant.ITEMNAME)){
			request.addParam(GlobalConstant.ITEMNAME, "Menu");
		}
	}
}
