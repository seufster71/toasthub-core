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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerMemberSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.Menu;
import org.toasthub.core.general.model.MenuItem;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

@Repository("MenuDao")
@Transactional("TransactionManagerMember")
public class MenuDaoImpl implements MenuDao {

	@Autowired
	protected EntityManagerMemberSvc entityManagerSvc;
	@Autowired
	protected UtilSvc utilSvc;

	@SuppressWarnings("unchecked")
	@Override
	public List<MenuItem> subItems(String menuName,String lang) throws Exception {
		List<MenuItem> results = entityManagerSvc.getInstance().createQuery("from MenuItem where menu.code = :menuName")
				.setParameter("menuName", menuName)
				.getResultList(); 
		return results;
	}

	@Override
	public Menu item(String menuName, String apiVersion, String appVersion, String lang) throws Exception {
		
		return (Menu) entityManagerSvc.getInstance().createQuery("SELECT DISTINCT m FROM Menu AS m JOIN FETCH m.menuItems AS items JOIN FETCH items.values AS values WHERE m.code = :menuName AND m.apiVersion = :apiVersion AND m.appVersion = :appVersion AND values.lang =:lang ")
				.setParameter("menuName", menuName)
				.setParameter("apiVersion", apiVersion)
				.setParameter("appVersion", appVersion)
				.setParameter("lang", lang)
				.getSingleResult();
	}
	
	
	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String HQLQuery = "";
			if (request.containsParam(GlobalConstant.ITEMTYPE) && "menu".equals(request.getParam(GlobalConstant.ITEMTYPE))){
				// menu 
				HQLQuery = "SELECT m FROM Menu AS m JOIN FETCH m.title AS t JOIN FETCH t.langTexts WHERE m.id = :id ";
			} else if (request.containsParam(GlobalConstant.ITEMTYPE) && ( "subItem".equals(request.getParam(GlobalConstant.ITEMTYPE)) || "subSub".equals(request.getParam(GlobalConstant.ITEMTYPE))) ){
				// menu item
				HQLQuery = "SELECT m FROM MenuItem AS m JOIN FETCH m.values AS values WHERE m.id = :id ";
			} else {
				utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing Item Type", response);
				return;
			}
			Query query = entityManagerSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("id", request.getParamLong(GlobalConstant.ITEMID));
			response.addParam(GlobalConstant.ITEM, query.getSingleResult());
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT DISTINCT m FROM Menu AS m JOIN FETCH m.title AS t JOIN FETCH t.langTexts AS lt WHERE lt.lang =:lang ";
		if (request.containsParam(GlobalConstant.ACTIVE)){
			queryStr += "AND m.active =:active ";
		}
		
		// search
		ArrayList<LinkedHashMap<String,String>> searchCriteria = null;
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")) {
			if (request.getParam(GlobalConstant.SEARCHCRITERIA) instanceof Map) {
				searchCriteria = new ArrayList<>();
				searchCriteria.add((LinkedHashMap<String, String>) request.getParam(GlobalConstant.SEARCHCRITERIA));
			} else {
				searchCriteria = (ArrayList<LinkedHashMap<String, String>>) request.getParam(GlobalConstant.SEARCHCRITERIA);
			}
			
			// Loop through all the criteria
			boolean or = false;
			
			String lookupStr = "";
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "lt.lang =:lang AND lt.text LIKE :titleValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_CODE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.code LIKE :codeValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_CATEGORY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.category LIKE :categoryValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_APIVERSION")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.apiVersion LIKE :apiValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_APPVERSION")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.appVersion LIKE :appValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_LANGUAGE_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.active LIKE :statusValue"; 
						or = true;
					}
				}
			}
			if (!"".equals(lookupStr)) {
				queryStr += " AND ( " + lookupStr + " ) ";
			}
			
		}
		// order by
		ArrayList<LinkedHashMap<String,String>> orderCriteria = null;
		StringBuilder orderItems = new StringBuilder();
		if (request.containsParam(GlobalConstant.ORDERCRITERIA) && !request.getParam(GlobalConstant.ORDERCRITERIA).equals("")) {
			if (request.getParam(GlobalConstant.ORDERCRITERIA) instanceof Map) {
				orderCriteria = new ArrayList<>();
				orderCriteria.add((LinkedHashMap<String, String>) request.getParam(GlobalConstant.ORDERCRITERIA));
			} else {
				orderCriteria = (ArrayList<LinkedHashMap<String, String>>) request.getParam(GlobalConstant.ORDERCRITERIA);
			}
			
			// Loop through all the criteria
			boolean comma = false;
			
			for (LinkedHashMap<String,String> item : orderCriteria) {
				if (item.containsKey(GlobalConstant.ORDERCOLUMN) && item.containsKey(GlobalConstant.ORDERDIR)) {
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_MENU_TABLE_NAME")){
						if (comma) { orderItems.append(","); }
						orderItems.append("lt.text ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_MENU_TABLE_CODE")){
						if (comma) { orderItems.append(","); }
						orderItems.append("m.code ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_MENU_TABLE_CATEGORY")){
						if (comma) { orderItems.append(","); }
						orderItems.append("m.category ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_MENU_TABLE_APIVERSION")){
						if (comma) { orderItems.append(","); }
						orderItems.append("m.apiVersion ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_MENU_TABLE_APPVERSION")){
						if (comma) { orderItems.append(","); }
						orderItems.append("m.appVersion ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
					if (item.get(GlobalConstant.ORDERCOLUMN).equals("ADMIN_MENU_TABLE_STATUS")){
						if (comma) { orderItems.append(","); }
						orderItems.append("m.active ").append(item.get(GlobalConstant.ORDERDIR));
						comma = true;
					}
				}
			}
		}
		if (!"".equals(orderItems.toString())) {
			queryStr += " ORDER BY ".concat(orderItems.toString());
		} else {
			// default order
			queryStr += " ORDER BY lt.text";
		}
		
		Query query = entityManagerSvc.getInstance().createQuery(queryStr);
		
		query.setParameter("lang",request.getParam(GlobalConstant.LANG));

		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_NAME")){
						query.setParameter("titleValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_CODE")){
						query.setParameter("codeValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_CATEGORY")){
						query.setParameter("categoryValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_APIVERSION")){
						query.setParameter("apiValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_APPVERSION")){
						query.setParameter("appValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_STATUS")){
						if ("active".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", true);
						} else if ("disabled".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", false);
						}
					}
				}
			}
		}
		if (request.containsParam(GlobalConstant.LISTLIMIT) && (Integer) request.getParam(GlobalConstant.LISTLIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.LISTSTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.LISTLIMIT));
		}
		
		List<?> results = query.getResultList();
		for(Object r : results){
			((Menu) r).setMenuItems(null); 
		}
		response.addParam(GlobalConstant.ITEMS, (List<Menu>) results);
		
	}
	
	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT COUNT(DISTINCT m) FROM Menu AS m JOIN m.title AS t JOIN t.langTexts AS lt ";
		boolean and = false;
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			if (!and) { queryStr += " WHERE "; }
			queryStr += "m.active =:active ";
			and = true;
		}
		
		ArrayList<LinkedHashMap<String,String>> searchCriteria = null;
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")) {
			if (request.getParam(GlobalConstant.SEARCHCRITERIA) instanceof Map) {
				searchCriteria = new ArrayList<>();
				searchCriteria.add((LinkedHashMap<String, String>) request.getParam(GlobalConstant.SEARCHCRITERIA));
			} else {
				searchCriteria = (ArrayList<LinkedHashMap<String, String>>) request.getParam(GlobalConstant.SEARCHCRITERIA);
			}
			
			// Loop through all the criteria
			boolean or = false;
			
			String lookupStr = "";
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_NAME")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "lt.lang =:lang AND lt.text LIKE :titleValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_CODE")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.code LIKE :codeValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_CATEGORY")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.category LIKE :categoryValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_APIVERSION")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.apiVersion LIKE :apiValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_APPVERSION")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.appVersion LIKE :apiValue"; 
						or = true;
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_STATUS")){
						if (or) { lookupStr += " OR "; }
						lookupStr += "m.active LIKE :statusValue"; 
						or = true;
					}
				}
			}
			if (!"".equals(lookupStr)) {
				if (!and) { 
					queryStr += " WHERE ( " + lookupStr + " ) ";
				} else {
					queryStr += " AND ( " + lookupStr + " ) ";
				}
			}
			
		}

		Query query = entityManagerSvc.getInstance().createQuery(queryStr);
		
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} 
		
		if (searchCriteria != null){
			for (LinkedHashMap<String,String> item : searchCriteria) {
				if (item.containsKey(GlobalConstant.SEARCHVALUE) && !"".equals(item.get(GlobalConstant.SEARCHVALUE)) && item.containsKey(GlobalConstant.SEARCHCOLUMN)) {  
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_NAME")){
						query.setParameter("titleValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
						query.setParameter("lang",request.getParam(GlobalConstant.LANG));
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_CODE")){
						query.setParameter("codeValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_CATEGORY")){
						query.setParameter("categoryValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_APIVERSION")){
						query.setParameter("apiValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_APPVERSION")){
						query.setParameter("appValue", "%"+((String)item.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
					}
					if (item.get(GlobalConstant.SEARCHCOLUMN).equals("ADMIN_MENU_TABLE_STATUS")){
						if ("active".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", true);
						} else if ("disabled".equalsIgnoreCase((String)item.get(GlobalConstant.SEARCHVALUE))) {
							query.setParameter("statusValue", false);
						}
					}
				}
			}
		}
		
		Long count = (Long) query.getSingleResult();
		if (count == null){
			count = 0l;
		}
		response.addParam(GlobalConstant.ITEMCOUNT, count);
	}

	public void subItem(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT DISTINCT items FROM MenuItem AS items JOIN FETCH items.values AS values WHERE items.menu.id = :id AND ((:parentId is null and items.parent.id is null) or items.parent.id = :parentId) AND values.lang =:lang order by items.order ";
		Query query = entityManagerSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id",request.getParamLong(Menu.ID)).setParameter("lang", request.getParam(GlobalConstant.LANG));
		if (request.containsParam(GlobalConstant.PARENTID)){
			query.setParameter("parentId", request.getParamLong(GlobalConstant.PARENTID));
		} else {
			query.setParameter("parentId",null);
		}
		List<?> items = query.getResultList();
		response.addParam(GlobalConstant.ITEMS, items);
	}
	
	public void subItems(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT DISTINCT m FROM MenuItem as m JOIN FETCH m.values WHERE ";
		if (request.containsParam(GlobalConstant.PARENTID)) {
			HQLQuery += "m.parent.id = :parentId ";
		} else {
			HQLQuery += "m.menu.id = :menuId AND m.parent.id = null";
		}
		Query query = entityManagerSvc.getInstance().createQuery(HQLQuery);
		if (request.containsParam(GlobalConstant.PARENTID)) {
			query.setParameter("parentId", request.getParamLong(GlobalConstant.PARENTID));
		} else {
			query.setParameter("menuId", request.getParamLong(Menu.ID));
		}
		List<?> results = query.getResultList(); 
		response.addParam(GlobalConstant.ITEMS, results);
	}
	
	public void subItemCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		String HQLQuery = "SELECT COUNT(*) FROM MenuItem AS m WHERE ";
		if (request.containsParam(GlobalConstant.PARENTID)) {
			HQLQuery += "m.parent.id = :parentId ";
		} else {
			HQLQuery += "m.menu.id = :menuId AND m.parent.id = null";
		}
		query = entityManagerSvc.getInstance().createQuery(HQLQuery);
		if (request.containsParam(GlobalConstant.PARENTID)) {
			query.setParameter("parentId", request.getParamLong(GlobalConstant.PARENTID));
		} else {
			query.setParameter("menuId", request.getParamLong(Menu.ID));
		}
		response.addParam(GlobalConstant.ITEMCOUNT, (Long) query.getSingleResult());
	}

	
}
