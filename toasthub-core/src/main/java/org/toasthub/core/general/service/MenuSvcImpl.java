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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.toasthub.core.general.handler.ServiceProcessor;
import org.toasthub.core.general.model.BaseEntity;
import org.toasthub.core.general.model.Menu;
import org.toasthub.core.general.model.MenuItem;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.general.repository.MenuDao;
import org.toasthub.core.preference.model.AppCachePage;

@Service("MenuSvc")
public class MenuSvcImpl implements ServiceProcessor, MenuSvc {

	@Autowired 
	@Qualifier("MenuDao")
	MenuDao menuDao;
	
	@Autowired
	protected UtilSvc utilSvc;
	
	@Autowired
	protected AppCachePage appCachePage;
	
	// Constructors
	public MenuSvcImpl() {}
	
	// Processor
	@Override
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParams().get(BaseEntity.ACTION);
		
		Long count = 0l;
		switch (action) {
		case "INIT": 
			appCachePage.getPageInfo(request,response);
			//appCachePage.getGlobalInfo(request,response);
			
			this.initParams(request);
			this.itemColumns(request, response);
			
			this.getMenuCount(request, response);
			count = (Long) response.getParam(BaseEntity.ITEMCOUNT);
			if (count != null && count > 0){
				this.getMenus(request, response);
			}

			break;
		case "LIST":
			request.addParam("appPageParamLoc", "response");
			appCachePage.getPageInfo(request,response);
			
			this.initParams(request);
			this.itemColumns(request, response);
			
			this.getMenuCount(request, response);
			count = (Long) response.getParam(BaseEntity.ITEMCOUNT);
			if (count != null && count > 0){
				this.getMenus(request, response);
			}

			break;
		case "SHOW":
			this.getMenu(request, response);
			if (request.containsParam(BaseEntity.PARENTID)){
				response.addParam(BaseEntity.PARENTID,(Integer) request.getParam(BaseEntity.PARENTID));
			}
			response.addParam(Menu.ID,(Integer) request.getParam(Menu.ID));
			response.addParam(BaseEntity.ID,request.getParam(BaseEntity.ID));
			break;
		default:
			break;
		}
		
		
	}
	
	public void getMenu(RestRequest request, RestResponse response) {
		menuDao.getMenu(request, response);
		//List<MenuItem> items = (List<MenuItem>) response.getParam("menuItems");
		//response.addParam("menuItems", items);
	}
	
	public void getMenus(RestRequest request, RestResponse response) {
		menuDao.getMenus(request, response);
	}
	
	public void getMenuCount(RestRequest request, RestResponse response) {
		menuDao.getMenuCount(request, response);
	}
	
	public void getMenuItemCount(RestRequest request, RestResponse response) {
		menuDao.getMenuItemCount(request, response);
	}
	
	public void getMenuItems(RestRequest request, RestResponse response) {
		menuDao.getMenuItems(request, response);
		// update menu items with parentId
		List<MenuItem> items = (List<MenuItem>) response.getParam(BaseEntity.ITEMS);
		for(MenuItem m : items){
			if (m.getParent() != null) {
				m.setParentId(m.getParent().getId());
				m.setMenuId(m.getMenu().getId());
			}
		}
	}
	
	public List<MenuItem> getMenuItems(String menuName, String lang) {
		return menuDao.getMenuItems(menuName, lang);
	}

	public Map<Integer,MenuItem> getMenu(String menuName, String apiVersion, String appVersion, String lang) {
		Menu menu = menuDao.getMenu(menuName,apiVersion, appVersion, lang);
		return this.orgMenu(menu);
	}

	protected Map<Integer,MenuItem> orgMenu(Menu menu) {
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
		String itemName = (String) request.getParam(BaseEntity.ITEMNAME);
		if (itemName != null && itemName.equals("Menu")) {
			request.addParam(BaseEntity.COLUMNS, Menu.columns);
			request.addParam(BaseEntity.DATATYPES, Menu.dataTypes);
			response.addParam(BaseEntity.COLUMNS, request.getParam(BaseEntity.COLUMNS));
			response.addParam(BaseEntity.DATATYPES, request.getParam(BaseEntity.DATATYPES));
		}
	}
	
	protected void initParams(RestRequest request) {
		if (!request.containsParam(BaseEntity.SEARCHCOLUMN)){
			request.addParam(BaseEntity.SEARCHCOLUMN, "title");
		}
		if (!request.containsParam(BaseEntity.ITEMNAME)){
			request.addParam(BaseEntity.ITEMNAME, "Menu");
		}
	}
}
