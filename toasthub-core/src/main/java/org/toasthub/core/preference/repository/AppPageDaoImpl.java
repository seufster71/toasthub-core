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

package org.toasthub.core.preference.repository;

import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.toasthub.core.common.EntityManagerDataSvc;
import org.toasthub.core.common.UtilSvc;
import org.toasthub.core.general.model.GlobalConstant;
import org.toasthub.core.general.model.RestRequest;
import org.toasthub.core.general.model.RestResponse;
import org.toasthub.core.preference.model.AppPageName;

@Repository("AppPageDao")
@Transactional("TransactionManagerData")
public class AppPageDaoImpl implements AppPageDao {

	@Autowired 
	protected EntityManagerDataSvc entityManagerDataSvc;
	@Autowired
	protected UtilSvc utilSvc;
	
	public AppPageName getPageName(Long id) {
		AppPageName pagename = (AppPageName) entityManagerDataSvc.getInstance()
			.createQuery("FROM AppPageName where id = :id")
			.setParameter("id",id)
			.getSingleResult();
		return pagename;
	}

	public AppPageName getPageName(String name) {
		AppPageName pagename = (AppPageName) entityManagerDataSvc.getInstance()
			.createQuery("FROM AppPageName where name = :name")
			.setParameter("name",name)
			.getSingleResult();
		return pagename;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void items(RestRequest request, RestResponse response) throws Exception {
		String queryStr = "SELECT p FROM AppPageName AS p JOIN FETCH p.title AS t JOIN FETCH t.langTexts AS l WHERE p.active =:active";
		// limit by catetory
		if (request.containsParam("category")){
			queryStr += " AND p.category =:category";
		}
		// Search criteria
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")){
			Map<String,Object> searchCriteria = (Map<String, Object>) request.getParam(GlobalConstant.SEARCHCRITERIA);
		
			if (searchCriteria.containsKey(GlobalConstant.SEARCHVALUE) && !searchCriteria.get(GlobalConstant.SEARCHVALUE).equals("")){
				queryStr += " AND l.lang =:lang AND l.text LIKE :searchValue"; 
			} else {
				queryStr += " AND l.lang =:lang";
			}
		} else {
			queryStr += " AND l.lang =:lang"; 
		}
		// order criteria
		if (request.containsParam(GlobalConstant.ORDERCRITERIA) && !"".equals(request.getParam(GlobalConstant.ORDERCRITERIA)) ){
			queryStr += " ORDER BY";
			List<Map<String,String>> orderCriteria = (List<Map<String,String>>) request.getParam(GlobalConstant.ORDERCRITERIA);
			int count = 0;
			for (Map<String,String> item : orderCriteria) {
				if (item.containsKey(GlobalConstant.ORDERCOLUMN) && !"".equals(item.get(GlobalConstant.ORDERCOLUMN)) ) {
					if ("ADMIN_PREFERENCE_TABLE_CATEGORY".equals(item.get(GlobalConstant.ORDERCOLUMN))) {
						if (count > 0) {
							queryStr += ",";
						}
						queryStr += " p.category";
					} else if ("ADMIN_PREFERENCE_TABLE_CODE".equals(item.get(GlobalConstant.ORDERCOLUMN))) {
						if (count > 0) {
							queryStr += ",";
						}
						queryStr += " p.name";
					}
				}
				if (item.containsKey(GlobalConstant.ORDERDIR) && !"".equals(item.get(GlobalConstant.ORDERDIR)) ) {
					if ("ASC".equals(item.get(GlobalConstant.ORDERDIR))) {
						queryStr += " ASC";
					} else {
						queryStr += " DESC";
					}
				}
				count++;
			}
		} else {
			queryStr += " ORDER BY p.category ASC, p.name ASC";
		}
		
		Query query = entityManagerDataSvc.getInstance().createQuery(queryStr);
	
		if (request.containsParam("category")){
			query.setParameter("category", (String) request.getParam("category"));
		}
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		// search criteria
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")){
			Map<String,Object> searchCriteria = (Map<String, Object>) request.getParam(GlobalConstant.SEARCHCRITERIA);
			if (searchCriteria.containsKey(GlobalConstant.SEARCHVALUE) && !searchCriteria.get(GlobalConstant.SEARCHVALUE).equals("")){
				query.setParameter("searchValue", "%"+((String)searchCriteria.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
				query.setParameter("lang",request.getParam(GlobalConstant.LANG));
			} else {
				query.setParameter("lang",request.getParam(GlobalConstant.LANG));
			}
		} else {
			query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		}
		// paging
		if (request.containsParam(GlobalConstant.PAGELIMIT) && (Integer) request.getParam(GlobalConstant.PAGELIMIT) != 0){
			query.setFirstResult((Integer) request.getParam(GlobalConstant.PAGESTART));
			query.setMaxResults((Integer) request.getParam(GlobalConstant.PAGELIMIT));
		}
		
		List<?> results = query.getResultList();
		
		response.addParam(GlobalConstant.ITEMS, (List<AppPageName>) results);
		
	}

	@Override
	public void itemCount(RestRequest request, RestResponse response) throws Exception {
		Query query = null;
		String queryStr = "SELECT COUNT(*) FROM AppPageName AS p JOIN p.title AS t JOIN t.langTexts AS l WHERE p.active =:active";
		if (request.containsParam("category")){
			queryStr += " AND p.category =:category";
		}		
		// search criteria
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")){
			Map<String,Object> searchCriteria = (Map<String, Object>) request.getParam(GlobalConstant.SEARCHCRITERIA);
		
			if (searchCriteria.containsKey(GlobalConstant.SEARCHVALUE) && !searchCriteria.get(GlobalConstant.SEARCHVALUE).equals("")){
				queryStr += " AND l.lang =:lang AND l.text LIKE :searchValue"; 
			} else {
				queryStr += " AND l.lang =:lang";
			}
		} else {
			queryStr += " AND l.lang =:lang"; 
		}
		query = entityManagerDataSvc.getInstance().createQuery(queryStr);
		if (request.containsParam("category")){
			query.setParameter("category", (String) request.getParam("category"));
		}
		if (request.containsParam(GlobalConstant.ACTIVE)) {
			query.setParameter("active", (Boolean) request.getParam(GlobalConstant.ACTIVE));
		} else {
			query.setParameter("active", true);
		}
		// search criteria
		if (request.containsParam(GlobalConstant.SEARCHCRITERIA) && !request.getParam(GlobalConstant.SEARCHCRITERIA).equals("")){
			Map<String,Object> searchCriteria = (Map<String, Object>) request.getParam(GlobalConstant.SEARCHCRITERIA);
			if (searchCriteria.containsKey(GlobalConstant.SEARCHVALUE) && !searchCriteria.get(GlobalConstant.SEARCHVALUE).equals("")){
				query.setParameter("searchValue", "%"+((String)searchCriteria.get(GlobalConstant.SEARCHVALUE)).toLowerCase()+"%");
				query.setParameter("lang",request.getParam(GlobalConstant.LANG));
			} else {
				query.setParameter("lang",request.getParam(GlobalConstant.LANG));
			}
		} else {
			query.setParameter("lang",request.getParam(GlobalConstant.LANG));
		}
		response.addParam(GlobalConstant.ITEMCOUNT, (Long) query.getSingleResult());
	
	}

	@Override
	public void item(RestRequest request, RestResponse response) throws Exception {
		if (request.containsParam(GlobalConstant.ITEMID) && !"".equals(request.getParam(GlobalConstant.ITEMID))) {
			String HQLQuery = "SELECT p FROM AppPageName AS p JOIN FETCH p.title AS t JOIN FETCH t.langTexts WHERE p.id = :id ";
			
			Query query = entityManagerDataSvc.getInstance().createQuery(HQLQuery);
			query.setParameter("id", new Long((Integer) request.getParam(GlobalConstant.ITEMID)) );
			response.addParam(GlobalConstant.ITEM, query.getSingleResult());
		} else {
			utilSvc.addStatus(RestResponse.ERROR, RestResponse.ACTIONFAILED, "Missing ID", response);
		}
		
		
		
	}

}
