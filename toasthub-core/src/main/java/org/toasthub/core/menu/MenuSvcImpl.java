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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
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
import org.toasthub.core.preference.model.PrefCacheUtil;

@Service("MenuSvc")
public class MenuSvcImpl implements ServiceProcessor, MenuSvc {

	@Autowired 
	@Qualifier("MenuDao")
	MenuDao menuDao;
	
	@Autowired
	protected UtilSvc utilSvc;
	
	@Autowired
	protected PrefCacheUtil prefCacheUtil;
	
	// Constructors
	public MenuSvcImpl() {}
	
	// Processor
	@Override
	public void process(RestRequest request, RestResponse response) {
		String action = (String) request.getParams().get(GlobalConstant.ACTION);
		
		Long count = 0l;
		switch (action) {
		case "INIT": 
			prefCacheUtil.getPrefInfo(request,response);
			
			this.initParams(request);
			
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}

			break;
		case "LIST":
			request.addParam(PrefCacheUtil.PREFPARAMLOC, PrefCacheUtil.RESPONSE);
			prefCacheUtil.getPrefInfo(request,response);
			
			this.initParams(request);
			
			this.itemCount(request, response);
			count = (Long) response.getParam(GlobalConstant.ITEMCOUNT);
			if (count != null && count > 0){
				this.items(request, response);
			}

			break;
		case "ITEM":
			this.item(request, response);
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
	
	@Override
	public void item(RestRequest request, RestResponse response) {
		try {
			menuDao.item(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Item failed", response);
			e.printStackTrace();
		}
		//List<MenuItem> items = (List<MenuItem>) response.getParam("menuItems");
		//response.addParam("menuItems", items);
	}
	
	@Override
	public void items(RestRequest request, RestResponse response) {
		try {
			menuDao.items(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "List failed", response);
			e.printStackTrace();
		}
	}
	
	@Override
	public void itemCount(RestRequest request, RestResponse response) {
		try {
			menuDao.itemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Count failed", response);
			e.printStackTrace();
		}
	}
	
	@Override
	public void subItem(RestRequest request, RestResponse response) {
		try {
			menuDao.subItem(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Item failed", response);
			e.printStackTrace();
		}
	}
	
	@Override
	public void subItemCount(RestRequest request, RestResponse response) {
		try {
			menuDao.subItemCount(request, response);
		} catch (Exception e) {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Count failed", response);
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void subItems(RestRequest request, RestResponse response) {
		try {
			menuDao.subItems(request, response);
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
	
	public List<MenuItem> subItems(String menuName, String lang) {
		try {
			return menuDao.subItems(menuName, lang);
		} catch (Exception e) {
			return null;
		}
	}

	public List<MenuItem> item(String menuName, String apiVersion, String appVersion, String lang) {
		Menu menu = null;
		try {
			menu = menuDao.item(menuName,apiVersion, appVersion, lang);
		} catch (Exception e) {
			return null;
		}
		return this.orgMenu(menu);
	}

	protected List<MenuItem> orgMenu(Menu menu) {
		if (menu != null) {
			Set<MenuItem> items = menu.getMenuItems();
			List<MenuItem> topRow = new ArrayList<MenuItem>();
			for(MenuItem m : items){
				m.setMenuId(m.getId());
				if (m.getParent() == null){
					// parent
					List<MenuItem> children = this.findChildren(m, items);
					m.setChildren(children);
					topRow.add(m);
				}
			}
			// order top row
			if (topRow != null) {
				topRow.sort(Comparator.comparing(MenuItem::getOrder));
			}
			return topRow;
		} else {
			return null;
		}
	}
	
	protected List<MenuItem> findChildren(MenuItem parent,Set<MenuItem> items) {
		List<MenuItem> myItems = null;
		for(MenuItem m : items){
			if (m.getParent() == parent){
				if (myItems == null){
					myItems = new ArrayList<MenuItem>();
				}
				myItems.add(m);
				m.setChildren(this.findChildren(m,items));
			}
		}
		if (myItems != null) {
			myItems.sort(Comparator.comparing(MenuItem::getOrder));
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
