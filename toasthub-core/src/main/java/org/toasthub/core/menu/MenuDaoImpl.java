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

import java.util.List;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.Menu;
import org.toasthub.core.general.model.MenuItem;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;

@Repository("MenuDao")
@Transactional("TransactionManagerData")
public class MenuDaoImpl implements MenuDao {

	@Autowired
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;

	public List<MenuItem> getMenuItems(String menuName,String lang) throws Exception {
		List<MenuItem> results = entityManagerDataSvc.getInstance().createQuery("from MenuItem where menu.code = :menuName")
				.setParameter("menuName", menuName)
				.getResultList(); 
		return results;
	}

	public Menu getMenu(String menuName, String apiVersion, String appVersion, String lang) throws Exception {
		
		return (Menu) entityManagerDataSvc.getInstance().createQuery("SELECT DISTINCT m FROM Menu AS m JOIN FETCH m.menuItems AS items JOIN FETCH items.values AS values WHERE m.code = :menuName AND m.apiVersion = :apiVersion AND m.appVersion = :appVersion AND values.lang =:lang ")
				.setParameter("menuName", menuName)
				.setParameter("apiVersion", apiVersion)
				.setParameter("appVersion", appVersion)
				.setParameter("lang", lang)
				.getSingleResult();
	}
	
	public void getMenuItemCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		String HQLQuery = "SELECT COUNT(*) FROM MenuItem AS m WHERE ";
		if (request.containsParam(GlobalConstant.PARENTID)) {
			HQLQuery += "m.parent.id = :parentId ";
		} else {
			HQLQuery += "m.menu.id = :menuId AND m.parent.id = null";
		}
		query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		if (request.containsParam(GlobalConstant.PARENTID)) {
			query.setParameter("parentId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)));
		} else {
			query.setParameter("menuId", new Long((Integer)request.getParam(Menu.ID)));
		}
		response.addParam(GlobalConstant.ITEMCOUNT, (Long) query.getSingleResult());
	}
	
	public void getMenuItems(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT DISTINCT m FROM MenuItem as m JOIN FETCH m.values WHERE ";
		if (request.containsParam(GlobalConstant.PARENTID)) {
			HQLQuery += "m.parent.id = :parentId ";
		} else {
			HQLQuery += "m.menu.id = :menuId AND m.parent.id = null";
		}
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		if (request.containsParam(GlobalConstant.PARENTID)) {
			query.setParameter("parentId", new Long((Integer)request.getParam(GlobalConstant.PARENTID)));
		} else {
			query.setParameter("menuId", new Long((Integer)request.getParam(Menu.ID)));
		}
		List<MenuItem> results = query.getResultList(); 
		response.addParam(GlobalConstant.ITEMS, results);
	}
	
	public void getMenus(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT m FROM Menu AS m JOIN FETCH m.title AS t JOIN FETCH t.langTexts AS l WHERE m.category =:category ";
		if ( !(request.containsParam(GlobalConstant.SHOWALL) && (Boolean)request.getParam(GlobalConstant.SHOWALL)) ){
			HQLQuery += "AND m.active =:active ";
		}
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			HQLQuery += "AND l.lang =:lang AND l.text LIKE :searchValue "; 
		}
		HQLQuery += "ORDER BY m.code ASC";
		
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery).setParameter("category", (String) request.getParam("category"));

		if ( !(request.containsParam(GlobalConstant.SHOWALL) && (Boolean)request.getParam(GlobalConstant.SHOWALL)) ){
			if (request.containsParam(GlobalConstant.ACTIVE)) {
				query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
			} else {
				query.setParameter("active", true);
			}
		}
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
			query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		}
		if (request.containsParam(GlobalConstant.PAGELIMIT) && (Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		
		List<?> results = query.getResultList();
		for(Object r : results){
			((Menu) r).setMenuItems(null); 
		}
		response.addParam(GlobalConstant.ITEMS, (List<Menu>) results);
		
	}
	
	public void getMenuCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		String HQLQuery = "SELECT COUNT(*) FROM Menu AS m JOIN m.title AS t JOIN t.langTexts AS l WHERE m.category =:category ";
		if ( !(request.containsParam(GlobalConstant.SHOWALL) && (Boolean)request.getParam(GlobalConstant.SHOWALL)) ){
			HQLQuery += "AND m.active =:active ";
		}
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			HQLQuery += "AND l.lang =:lang AND l.text LIKE :searchValue "; 
		}
		query = entityManagerDataSvc.getInstance().createQuery(HQLQuery).setParameter("category", (String) request.getParam("category"));
		if ( !(request.containsParam(GlobalConstant.SHOWALL) && (Boolean)request.getParam(GlobalConstant.SHOWALL)) ){
			if (request.containsParam(GlobalConstant.ACTIVE)) {
				query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
			} else {
				query.setParameter("active", true);
			}
		}
		if (request.containsParam(GlobalConstant.SEARCHVALUE) && !request.getParam(GlobalConstant.SEARCHVALUE).equals("")){
			query.setParameter("searchValue", "%"+((String)request.getParam(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
			query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		}
		response.addParam(GlobalConstant.ITEMCOUNT, (Long) query.getSingleResult());
	}

	public void getMenu(RestRequest request, RestResponse response) throws Exception {
		String HQLQuery = "SELECT DISTINCT items FROM MenuItem AS items JOIN FETCH items.values AS values WHERE items.menu.id = :id AND ((:parentId is null and items.parent.id is null) or items.parent.id = :parentId) AND values.lang =:lang order by items.order ";
		Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
		query.setParameter("id",new Long((Integer) request.getParam(Menu.ID))).setParameter("lang", request.getParam(GlobalConstant.LANG));
		if (request.containsParam(GlobalConstant.PARENTID)){
			query.setParameter("parentId", new Long((Integer) request.getParam(GlobalConstant.PARENTID)));
		} else {
			query.setParameter("parentId",null);
		}
		List<MenuItem> items = query.getResultList();
		response.addParam(GlobalConstant.ITEMS, items);
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
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)) );
			response.addParam(GlobalConstant.ITEM, query.getSingleResult());
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
	}
	
}
